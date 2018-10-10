package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;


/** Capture all necessary information needed at each commit.
 * @author Chelsea Chen, Alan Song
 */

public class Commit implements Serializable {


    /** Filename.
     *
     */
    private String filename;

    /** Access private field filename.
     *
     * @return string filename
     */
    public String getFilename() {
        return filename;
    }

    /** Log message. */
    private String message;

    /** Timestamp. */
    private Date timestamp;

    /** The hash map of filename-sha1. */
    private HashMap<String, String> blobs;

    /** sha1code of Parent commit. */
    private String parent;

    /** Date.
     *
     */
    private Date date;

    /** Commit string ID.
     *
     */
    private String id;

    /**
     * Creates a commit with a set of Blobs.
     * @param messages
     *            The commit message.
     * @param date
     *            The date time.
     * @param parent
     *            The parent commit.
     * @param blobs
     *            The blobs involved in the commit.
     */


    public Commit(String messages, Date date,
                  String parent, HashMap<String, String> blobs) {
        if (messages.isEmpty() || messages == null || messages.equals("")) {
            throw new IllegalArgumentException(
                    "Please enter a commit message.");
        }
        this.parent = parent;
        this.message = messages;
        this.timestamp = date;
        this.blobs = blobs;
    }

    /**
     * @return parent
     */
    public String getParent() {
        return this.parent;
    }

    /**
     * @return commit message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return date
     */
    public Date getDate() {
        return this.timestamp;
    }

    /**
     * @return the blobs filename-sha1
     */
    public HashMap<String, String> getBlobs() {
        return this.blobs;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /** Saving commits to DIR.
     *
     * @param dir
     */
    public void commitsave(File dir) {
        filename = Utils.sha1(this.toString());
        File file = new File(dir, Utils.sha1(this.toString()));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Utils.writeContents(file, Utils.serialize(this));
    }

    /** Tostring method.
     *
     * @return string
     */
    public String toString() {
        return super.toString()
                + this.message + this.timestamp.toString()
                + this.blobs.toString() + this.parent;
    }

    /** Get date and time of commit in correct format.
     *
     * @return string
     */
    public String commdate() {
        Format format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z");
        String printDate = format.format(timestamp);
        return printDate;
    }

}
