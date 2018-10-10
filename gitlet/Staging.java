package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/** Staging Class.
 * @author Chelsea Chen
 */
public class Staging implements Serializable {

    /** HashMap of files added.
     *
     */
    private HashMap<String, String> filesAdd;

    /** HashSet of tracked files.
     *
     */
    private HashSet<String> filesRm;

    /** Constructur for staging.
     *
     */
    public Staging() {
        filesAdd = new HashMap<>();
        filesRm = new HashSet<>();
    }

    /** Get staging area.
     *
     * @return HashMap
     */
    public HashMap<String, String> getFilesAdd() {
        return filesAdd;
    }

    /** Get tracked files.
     *
     * @return HashSet
     */
    public HashSet<String> getFilesRm() {
        return filesRm;
    }

    /** Clear staging area.
     *
     */
    public void clear() {
        filesAdd.clear();
        filesRm.clear();
    }
}
