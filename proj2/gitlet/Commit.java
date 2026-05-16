package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import static gitlet.Repository.COMMITS_DIR;

public class Commit implements Serializable {
    private String message;
    private String time;
    private String parent1;
    private String parent2;
    private Map<String, String> fileNameToBlobId;
    private String id;

    public Commit() {
        this.message = "initial commit";

        SimpleDateFormat sdf =
                new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.time = sdf.format(new Date(0));

        this.parent1 = null;
        this.parent2 = null;
        this.fileNameToBlobId = new TreeMap<>();
        this.id = computeId();
    }

    public Commit(String message, String parent1, String parent2,
                  String timestamp, Map<String, String> map) {
        this.message = message;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.time = timestamp;
        this.fileNameToBlobId = new TreeMap<>(map);
        this.id = computeId();
    }

    private String computeId() {
        String p1 = parent1 == null ? "" : parent1;
        String p2 = parent2 == null ? "" : parent2;
        return Utils.sha1(message, time, p1, p2, Utils.serialize((Serializable) fileNameToBlobId));
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getFileNameToBlobId() {
        return fileNameToBlobId;
    }

    public void saveCommit() {
        File commitFile = Utils.join(COMMITS_DIR, id);
        Utils.writeObject(commitFile, this);
    }

    public String getParent() {
        return parent1;
    }

    public String getParent2() {
        return parent2;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
