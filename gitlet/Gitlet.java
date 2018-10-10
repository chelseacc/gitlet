package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** Understand, execute each command.
 * @author Chelsea Chen
 */

public class Gitlet {

    /**
     * File object for .gitlet/ directory.
     */
    private File gitlet;

    /**
     * String of filenames in working directory.
     */
    private String workingdir;

    /**
     * File object for commits directory.
     */
    private File commits;

    /**
     * File object for logs directory.
     */
    private File logs;

    /**
     * File object for heads directory.
     */
    private File heads;

    /**
     * File object for blobs directory.
     */
    private File blobs;

    /**
     * File object for HEAD file with sha1code of current commit.
     */
    private File head;

    /**
     * File object for Staging file containing byte array of Staging area.
     */
    private File staging;

    /**
     * File object for Master file.
     */
    private File master;

    /**
     * File object for Branch file containing byte array of Branches.
     */
    private File branches;

    /**
     * Commit object of headpointer pointing to current commit.
     */
    private Commit headpointer;

    /**
     * Staging object for staging area.
     */
    private Staging stage;

    /**
     * Branch object for branches.
     */
    private Branch branch;

    /**
     * Date object for logging date in commit objects.
     */
    private Date date;

    /**
     * String object for name of current branch.
     */
    private String currentBranch;


    /**
     * Working directory.
     */
    public Gitlet() {
        gitlet = new File(".gitlet/");
        commits = new File(gitlet, "commits");
        blobs = new File(gitlet, "blobs");
        logs = new File(gitlet, "logs");

        if (gitlet.exists()) {
            workingdir = gitlet.getAbsoluteFile().getParent();

            heads = new File(logs, "heads");
            head = new File(logs, "HEAD");
            staging = new File(logs, "Staging");
            master = new File(heads, "MASTER");
            branches = new File(gitlet, "Branch");

            File headPointer
                    = new File(commits, Utils.readContentsAsString(head));
            headpointer = (Commit) Utils.deserialize(headPointer);

            File staging = new File(logs, "Staging");
            stage = (Staging) Utils.deserialize(staging);

            File branchh = new File(gitlet, "Branch");
            branch = (Branch) Utils.deserialize(branchh);

        }
    }

    /** Access private field branch.
     *
     * @return branch
     */
    public Branch getBranch() {
        return branch;
    }

    /** Access private field headpointer.
     *
     * @return headpointer
     */
    public Commit getHeadpointer() {
        return headpointer;
    }

    /** Access private field commits.
     *
     * @return commits
     */
    public File getCommits() {
        return commits;
    }

    /**
     * Init function.
     */
    public void init() {

        gitlet = new File(".gitlet/");

        if (gitlet.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);

        }
        gitlet.mkdir();

        File commits = new File(gitlet, "commits");
        commits.mkdir();
        File blobs = new File(gitlet, "blobs");
        blobs.mkdir();
        File logs = new File(gitlet, "logs");
        logs.mkdir();
        File heads = new File(logs, "heads");
        heads.mkdir();

        Commit initialCommit
                = new Commit("initial commit", new Date(1),
                "", new HashMap<>());
        initialCommit.commitsave(commits);

        File head = new File(logs, "HEAD");
        File stagingfile = new File(logs, "Staging");
        File master = new File(heads, "MASTER");
        Staging staging = new Staging();
        File branchfile = new File(gitlet, "Branch");
        Branch branch = new Branch();
        branch.addBranch("master", initialCommit.getFilename());
        branch.setActiveBranch("master");

        try {
            master.createNewFile();
            head.createNewFile();
            stagingfile.createNewFile();
            branchfile.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeObject(master, initialCommit);
        Utils.writeContents(head, initialCommit.getFilename());
        Utils.writeObject(stagingfile, staging);
        Utils.writeObject(branchfile, branch);
    }

    /** Add filename FILE.
     *
     * @param file
     */
    public void add(String file) {

        List<String> wdfiles = Utils.plainFilenamesIn(workingdir);

        if (!wdfiles.contains(file)) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        HashMap<String, String> filestoadd = stage.getFilesAdd();
        HashSet<String> filestorm = stage.getFilesRm();

        File addfile = new File(workingdir, file);
        Blob addfile1 = new Blob(addfile);

        if (filestorm.contains(file)) {
            filestorm.remove(file);
        }

        if (headpointer.getBlobs().containsKey(file)) {
            if (headpointer.getBlobs().get(file).equals(addfile1.sha1())) {
                filestoadd.remove(file);
            } else {
                filestoadd.put(file, addfile1.getContents());
            }
        } else {
            filestoadd.put(file, addfile1.getContents());
        }
        Utils.writeObject(staging, stage);
    }

    /** Commit method with commit message MSG.
     *
     * @param msg
     */
    public void commit(String msg) {

        Commit newc
                = new Commit(msg, new Date(System.currentTimeMillis()),
                Utils.readContentsAsString(head), headpointer.getBlobs());

        HashMap<String, String> filestoadd = stage.getFilesAdd();
        HashSet<String> filestorm = stage.getFilesRm();

        if (filestoadd.isEmpty() && filestorm.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else {
            for (String key : filestoadd.keySet()) {
                newc.getBlobs().put(key, Utils.sha1(filestoadd.get(key)));
                File blobfile
                        = new File(blobs, Utils.sha1(filestoadd.get(key)));
                try {
                    blobfile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Utils.writeContents(blobfile, filestoadd.get(key));
            }
        }

        newc.commitsave(commits);
        Utils.writeContents(head, newc.getFilename());
        branch.getBranches().put(branch.getActiveBranch(), newc.getFilename());

        stage.clear();
        Utils.writeObject(staging, stage);
        Utils.writeObject(branches, branch);
    }

    /** Remove filename FILE.
     *
     * @param file
     */
    public void rm(String file) {

        boolean rm = false;

        HashMap<String, String> filestoadd = stage.getFilesAdd();
        HashSet<String> filestorm = stage.getFilesRm();

        if (filestoadd.containsKey(file)) {
            filestoadd.remove(file);
            rm = true;
        }

        if (headpointer.getBlobs().containsKey(file)) {
            List<String> wdfiles = Utils.plainFilenamesIn(workingdir);
            if (wdfiles.contains(file)) {
                File rmfile = new File(workingdir, file);
                rmfile.delete();
                rm = true;
            } else {
                rm = true;
            }
            filestorm.add(file);
        }

        if (!rm) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        Utils.writeObject(staging, stage);
    }

    /** Log method.
     *
     */
    public void log() {

        while (!headpointer.getParent().equals("")) {
            System.out.println("===");
            System.out.println("commit " + headpointer.getFilename());
            System.out.println("Date: " + headpointer.commdate());
            System.out.println(headpointer.getMessage());
            System.out.println("");
            File parent = new File(commits, headpointer.getParent());
            headpointer = (Commit) Utils.deserialize(parent);
        }
        System.out.println("===");
        System.out.println("commit " + headpointer.getFilename());
        System.out.println("Date: " + headpointer.commdate());
        System.out.println(headpointer.getMessage());
        System.out.println("");
    }

    /** Global log.
     *
     */
    public void globallog() {
        List<String> dir = Utils.plainFilenamesIn(commits);

        for (String child : dir) {
            File cfile = new File(commits, child);
            Commit c = (Commit) Utils.deserialize(cfile);
            System.out.println("===");
            System.out.println("commit " + c.getFilename());
            System.out.println("Date: " + c.commdate());
            System.out.println(c.getMessage());
            System.out.println("");

        }
    }

    /** Find commit with given commit CMSG.
     *
     * @param cmsg
     */
    public void find(String cmsg) {
        List<String> dir = Utils.plainFilenamesIn(commits);
        int count = 0;

        for (String child : dir) {
            File cfile = new File(commits, child);
            Commit c = (Commit) Utils.deserialize(cfile);
            if (cmsg.equals(c.getMessage())) {
                System.out.println(child);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("Found no commit with that message.");
        }

    }

    /** Status method.
     *
     */
    public void status() {

        HashMap<String, String> existingbranches = branch.getBranches();
        Map<String, String> sorted = new TreeMap<>(existingbranches);

        HashMap<String, String> filestoadd = stage.getFilesAdd();
        Map<String, String> sorted1 = new TreeMap<>(filestoadd);

        HashSet<String> filestorm = stage.getFilesRm();

        System.out.println("=== Branches ===");
        for (Map.Entry entry : sorted.entrySet()) {
            if (entry.getKey().equals(branch.getActiveBranch())) {
                System.out.println("*" + entry.getKey());
            } else {
                System.out.println(entry.getKey());
            }
        }
        System.out.println("");

        System.out.println("=== Staged Files ===");
        for (Map.Entry entry : sorted1.entrySet()) {
            System.out.println(entry.getKey());
        }
        System.out.println("");

        System.out.println("=== Removed Files ===");
        for (String entry : filestorm) {
            System.out.println(entry);
        }
        System.out.println("");

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("");

        System.out.println("=== Untracked Files ===");
        System.out.println("");

    }

    /** Checkout FILENAME in COMMIT.
     *
     * @param commit
     * @param filename
     */
    public void checkout(Commit commit, String filename) {

        String prevsha1 = commit.getBlobs().get(filename);
        File prevV = new File(blobs, prevsha1);

        String filecontent = Utils.readContentsAsString(prevV);
        File file = new File(workingdir, filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Utils.writeContents(file, filecontent);
    }

    /** Checkout BRANCHNAME branch.
     *
     * @param branchname
     */
    public void checkout(String branchname) {

        String commid = branch.getBranches().get(branchname);
        File commfile = new File(commits, commid);
        Commit c = (Commit) Utils.deserialize(commfile);


        ArrayList<String> filenamestoAdd
                = new ArrayList<>(c.getBlobs().keySet());
        ArrayList<String> wdirkeys
                = new ArrayList<>(Utils.plainFilenamesIn(workingdir));

        for (String filename : wdirkeys) {
            if (!filenamestoAdd.contains(filename)) {
                File filetoDelete = new File(workingdir, filename);
                filetoDelete.delete();
            }
        }
        for (String name : filenamestoAdd) {
            File file = new File(workingdir, name);
            if (!wdirkeys.contains(name)) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File blob = new File(blobs, c.getBlobs().get(name));
            String content = Utils.readContentsAsString(blob);
            Utils.writeContents(file, content);
        }

        stage.clear();

        File headPointer = new File(logs, "HEAD");
        Utils.writeContents(headPointer, commid);
        branch.setActiveBranch(branchname);

        Utils.writeObject(staging, stage);
        Utils.writeObject(branches, branch);
    }

    /** Checking for failure cases when adding new branch BRANCHNAME.
     *
     * @param branchname
     * @return
     */
    public boolean checkFailureCase(String branchname) {

        String commid = branch.getBranches().get(branchname);
        File commfile = new File(commits, commid);
        Commit c = (Commit) Utils.deserialize(commfile);
        ArrayList<String> trackedKeys = new ArrayList<>(c.getBlobs().keySet());

        ArrayList<String> workingKeys =
                new ArrayList<>(Utils.plainFilenamesIn(workingdir));
        for (String key : workingKeys) {
            if (!headpointer.getBlobs().containsKey(key)) {
                if (trackedKeys.contains(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Adding new branch BRANCHNAME.
     *
     * @param branchname
     */
    public void branch(String branchname) {
        if (branch.getBranches().containsKey(branchname)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branch.addBranch(branchname, headpointer.getFilename());
        Utils.writeObject(branches, branch);
    }

    /** Remove BRANCHNAME.
     *
     * @param branchname
     */
    public void rmbranch(String branchname) {
        if (!branch.getBranches().containsKey(branchname)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branch.getActiveBranch().equals(branchname)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branch.removeBranch(branchname);
        Utils.writeObject(branches, branch);
    }

    /** Equals method for objects O.
     *
     * @param o
     * @return true or false
     */
    public boolean equals(Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        return Utils.sha1(this).equals(Utils.sha1(o));
    }

    /** Hashcode method.
     *
     * @return hashcode
     */
    public int hashcode() {
        return Utils.sha1(this).hashCode();
    }

    /** Reset to commit with COMMID.
     *
     * @param commid
     */
    public void reset(String commid) {

        ArrayList<String> wdirkeys
                = new ArrayList<>(Utils.plainFilenamesIn(workingdir));
        ArrayList<String> commfile
                = new ArrayList<>(Utils.plainFilenamesIn(commits));

        if (!wdirkeys.equals(commfile) && wdirkeys.size() > commfile.size()) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            System.exit(0);
        } else if (!Utils.plainFilenamesIn(commits).contains(commid)) {
            System.out.print("No commit with that id exists.");
        } else {
            File commfile1 = new File(commits, commid);
            Commit c = (Commit) Utils.deserialize(commfile1);

            ArrayList<String> filenamestoAdd
                    = new ArrayList<>(c.getBlobs().keySet());

            for (String filename : wdirkeys) {
                if (!filenamestoAdd.contains(filename)) {
                    File filetoDelete = new File(workingdir, filename);
                    filetoDelete.delete();
                }
            }
            for (String name : filenamestoAdd) {
                File file = new File(workingdir, name);
                if (!wdirkeys.contains(name)) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            File headPointer = new File(logs, "HEAD");
            Utils.writeContents(headPointer, commid);
            stage.clear();
        }
    }
    /**
     * Merging BRANCHNAME into current branch.
     */
    public void merge(String branchname) {

        if (stage.getFilesRm().isEmpty() || stage.getFilesAdd().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!branch.getBranches().keySet().contains(branchname)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branch.getActiveBranch().equals(branchname)) {
            System.out.println("Cannot merge a branch with itself.");
        }
    }
}
