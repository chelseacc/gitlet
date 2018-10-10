package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Branch class.
 * @author Chelsea Chen, Alan Song
 */
public class Branch implements Serializable {

    /** Hashmap of all branches and commit ID.
     *
     */
    private HashMap<String, String> branches;

    /** Name of current branch.
     *
     */
    private String activeBranch;

    /** Constructor for branch to get its hashmap.
     *
     */
    public Branch() {
        branches = new HashMap<>();
    }

    /** Access private field branches.
     *
     * @return branches hashmap
     */
    public HashMap<String, String> getBranches() {
        return branches;
    }

    /** Access private field activebranch.
     *
     * @return string activebranch
     */
    public String getActiveBranch() {
        return activeBranch;
    }

    /** Adding a new branch with BRANCHNAME and COMMID.
     *
     * @param branchname
     * @param commid
     */
    public void addBranch(String branchname, String commid) {
        branches.put(branchname, commid);
    }

    /** Removing a branch with BRANCHNAME.
     *
     * @param branchname
     */
    public void removeBranch(String branchname) {
        branches.remove(branchname);
    }

    /** Set active branch to BRANCHNAME.
     *
     * @param branchname
     */
    public void setActiveBranch(String branchname) {
        activeBranch = branchname;
    }

}
