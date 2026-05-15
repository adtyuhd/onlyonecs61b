package gitlet;

// TODO: any imports you need here
// TODO: 在这里添加你需要的导入语句

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Date; // TODO: 你很可能会在这个类中使用它
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.COMMITS_DIR;

/** Represents a gitlet commit object.
 *  表示一个 gitlet 提交对象。
 *
 *  TODO: It's a good idea to give a description here of what else this Class
 *  TODO: 最好在这里描述一下这个类
 *  does at a high level.
 *  主要还做了些什么（高层次概述）。
 *
 *  @author TODO
 *  @author 作者名
 */
public class Commit {
    /**
     * TODO: add instance variables here.
     * TODO: 在这里添加实例变量。
     *
     * List all instance variables of the Commit class here with a useful
     * 在这里列出 Commit 类的所有实例变量，
     * comment above them describing what that variable represents and how that
     * 并在每个变量上方写一个有用的注释，描述该变量代表什么、
     * variable is used. We've provided one example for `message`.
     * 如何使用。我们已经为 `message` 提供了一个示例。
     */

    /** The message of this Commit. */
    /** 本次提交的说明信息。 */
    private String message;
    private String time;
    private  String filename;
    private String parent1;
    private String parent2;
    private Map<String, String> fileNameToBlobId;


    public Commit(String message, String parent1,String parent2, String timestamp,Map<String, String>map) {
        this.message = message;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.time = timestamp;
        this.fileNameToBlobId = map;
    }
    public String getId() {
        // 用 SHA-1 生成唯一 ID
        return Utils.sha1(this);
    }
    public Map<String, String> getFileNameToBlobId(){
        return fileNameToBlobId;
    }
    public void saveCommit(Commit c){
        // 1. 拿到 commit 的唯一 ID
        String commitId = c.getId();

        // 2. 拼接路径：.gitlet/commits/xxx（commitId）
        File commitFile = Utils.join(COMMITS_DIR, commitId);

        // 3. 把 commit 对象保存到文件里（必须用 writeObject）
        Utils.writeObject(commitFile, (Serializable) c);
    }
    public String getParent() {
        return parent1;
    }

    public String getParent2() {
        return parent2; // 合并提交才有第二个爸爸
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
        /* TODO: fill in the rest of this class. */
    /* TODO: 补全这个类剩余的部分。 */
}
