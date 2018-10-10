package gitlet;

import java.io.Serializable;
import java.io.File;


/** Store content of each file.
 * @author Chelsea Chen, Alan Song
 */

public class Blob implements Serializable {

    /** Hashcode.
     *
     */
    private int hashCode;

    /** File contents.
     *
     */
    private String contents;


    /** Store content for Blob as FILE object.
      * @param file
     */
    public Blob(File file) {
        this.contents = Utils.readContentsAsString(file);
    }

    /**
     * Generates the SHA-1 for the Blob.
     * @return The SHA-1.
     */
    public String sha1() {
        return Utils.sha1(contents);
    }

    /** Access contents.
     *
     * @return contents
     */
    public String getContents() {
        return contents;
    }

    /** Hashcode.
     *
     * @return hashcode
     */
    public int hashCode() {
        return this.sha1().hashCode();
    }

    /** Equals method for object OBJ.
     *
     * @param obj
     * @return boolean
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Blob other = (Blob) obj;

        return (this.sha1().equals(other.sha1()));
    }
}
