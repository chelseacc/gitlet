package gitlet;

import java.io.File;

/** Parse and interpret commands.
 * @author Chelsea Chen, Alan Song
 */

public class Command {

    /** Outlines process of Gitlet program G, when inputting String ARGS.
     *
     * @param g
     * @param args
     */
    public void process(Gitlet g, String... args) {

        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String order = args[0];
        String[] detail = new String[args.length - 1];
        System.arraycopy(args, 1, detail, 0, args.length - 1);

        switch (order) {
        case "init":
            init(g, detail);
            break;
        case "add":
            add(g, detail);
            break;
        case "commit":
            commit(g, detail);
            break;
        case "rm":
            rm(g, detail);
            break;
        case "log":
            log(g, detail);
            break;
        case "global-log":
            globallog(g, detail);
            break;
        case "find":
            find(g, detail);
            break;
        case "status":
            status(g, detail);
            break;
        case "checkout":
            checkout(g, detail);
            break;
        case "branch":
            branch(g, detail);
            break;
        case "rm-branch":
            rmbranch(g, detail);
            break;
        case "reset":
            reset(g, detail);
            break;
        case "merge":
            merge(g, detail);
            break;
        default:
            System.out.println("No command with that name exists.");
            System.exit(0);
            break;
        }
    }

    /** Tells program Gitlet G how to execute an
     * Init command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void init(Gitlet g, String[] detail) {
        if (detail.length != 0) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.init();
    }

    /** Tells program Gitlet G how to execute an
     * Add command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void add(Gitlet g, String[] detail) {
        if (detail.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.add(detail[0]);
    }

    /** Tells program Gitlet G how to execute a
     * Commit command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void commit(Gitlet g, String[] detail) {
        if (detail.length == 0 || detail[0].equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (detail.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.commit(detail[0]);
    }

    /** Tells program Gitlet G how to execute a
     * RM command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void rm(Gitlet g, String[] detail) {
        if (detail.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.rm(detail[0]);
    }

    /** Tells program Gitlet G how to execute a
     * Log command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void log(Gitlet g, String[] detail) {
        if (detail.length != 0) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.log();
    }

    /** Tells program Gitlet G how to execute a
     * global_log command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void globallog(Gitlet g, String[] detail) {
        if (detail.length != 0) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.globallog();
    }

    /** Tells program Gitlet G how to execute a
     * Find command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void find(Gitlet g, String[] detail) {
        if (detail.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.find(detail[0]);
    }
    /** Tells program Gitlet G how to execute an
     * Status command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void status(Gitlet g, String[] detail) {
        if (detail.length != 0) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.status();
    }
    /** Tells program Gitlet G how to execute a
     * Checkout command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void checkout(Gitlet g, String[] detail) {
        if (detail.length == 0 || 3 < detail.length) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (detail.length == 1) {
            if (!g.getBranch().getBranches().containsKey(detail[0])) {
                System.out.println("No such branch exists.");
                System.exit(0);
            }
            if (g.getBranch().getActiveBranch().equals(detail[0])) {
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }

            if (g.checkFailureCase(detail[0])) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it or add it first.");
                System.exit(0);
            }
            g.checkout(detail[0]);
        } else if (detail.length == 2) {
            if (!detail[0].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            if (!g.getHeadpointer().getBlobs().containsKey(detail[1])) {
                System.out.println("File does not exist in that commit");
                System.exit(0);
            }
            g.checkout(g.getHeadpointer(), detail[1]);
        } else {
            if (!detail[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            File givenCommitID = new File(g.getCommits(), detail[0]);
            if (!givenCommitID.exists()) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            Commit givenCommit = (Commit) Utils.deserialize(givenCommitID);
            if (!givenCommit.getBlobs().containsKey(detail[2])) {
                System.out.println("File does not exist in that commit");
                System.exit(0);
            }
            g.checkout(givenCommit, detail[2]);
        }
    }
    /** Tells program Gitlet G how to execute a
     * Branch command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void branch(Gitlet g, String[] detail) {
        if (detail.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.branch(detail[0]);
    }
    /** Tells program Gitlet G how to execute a
     * Rm_branch command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void rmbranch(Gitlet g, String[] detail) {
        if (detail.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.rmbranch(detail[0]);
    }

    /** Tells program Gitlet G how to execute a
     * Reset command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void reset(Gitlet g, String[] detail) {
        if (detail.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.reset(detail[0]);
    }
    /** Tells program Gitlet G how to execute a
     * Merge command with optional arguments DETAIL.
     *
     * @param g
     * @param detail
     */
    private void merge(Gitlet g, String[] detail) {
        if (detail.length != 0) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        g.merge(detail[0]);
    }
}
