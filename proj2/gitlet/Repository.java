package gitlet;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

public class Repository implements Serializable {
    /** The current working directory. */
    /** 当前工作目录。*/
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    /** .gitlet 目录（存放所有版本数据）。*/
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    private Map<String, String> branches= new TreeMap<>();//<,分支名字符串,
    // 一串 commitId（就是 commits 文件夹里那个文件名）>
    // 当前正在使用的分支名
    private String currentBranch;
    // 暂存区
    private Map<String, String> staging= new TreeMap<>();//<Filename,blobid>
    // 待删除清单
    private Set<String> toRemove=new TreeSet<>();//Filename
    Map<String,String>remotes= new TreeMap<>();
    public Repository(){
         this.branches= new TreeMap<>();//<,分支名字符串,
        // 一串 commitId（就是 commits 文件夹里那个文件名）>
        // 当前正在使用的分支名
        this.currentBranch="master";
        // 暂存区
        this.staging= new TreeMap<>();//<Filename,blobid>
        // 待删除清单
        this.toRemove=new TreeSet<>();//Filename
        this.remotes= new TreeMap<>();
    }

    public void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        Commit c = new Commit();
        c.saveCommit();
        
        this.branches.put("master", c.getId());
        this.currentBranch="master";
       Utils.writeObject(join(GITLET_DIR,"repo"),this);
    }
    public void add(String fileName) {
        File addOne=Utils.join(CWD,fileName);
        if(!addOne.exists()){
            System.out.println("File does not exist.");
            return;
        }
        if(toRemove.contains(fileName)){
            toRemove.remove(fileName);
        }
        String content= Utils.readContentsAsString(addOne);
        String commitid=branches.get(currentBranch);
        if(commitid==null){
            return;
        }
        File Latest=Utils.join(COMMITS_DIR,commitid);
        Commit c= Utils.readObject(Latest, Commit.class);
        Map<String, String> map=c.getFileNameToBlobId();
        Blob b=new Blob(content);
        Utils.writeObject(join(BLOBS_DIR,b.getBlobId()), (Serializable) b);
        String blobname=b.getBlobId();
        if(!map.containsKey(fileName)){
            staging.put(fileName,blobname);
        }
        else{
            String oldBlobId = map.get(fileName);
            File oldBlobFile =Utils.join(BLOBS_DIR, oldBlobId);
            String oldContent =Utils.readContentsAsString(oldBlobFile);
            if(content.equals(oldContent)){
                staging.remove(fileName);
            }
            else{
                staging.put(fileName,blobname);
            }
        }
        Utils.writeObject(join(GITLET_DIR, "repo"), this);
    }
    public void commit(String message) {
        if(message.isEmpty()){
            System.out.println("Please enter a commit message.");
           return;
        }
        if(staging.isEmpty()){
            System.out.println("No changes added to the commit.");
            return;
        }
        String parentcommitid=branches.get(currentBranch);
        File Latest=Utils.join(COMMITS_DIR,parentcommitid);
        Commit parentcommit=Utils.readObject(Latest, Commit.class);
        Map<String, String> map=parentcommit.getFileNameToBlobId();
        Map<String, String> newmap = new TreeMap<>(map);
        newmap.putAll(staging);
        for(String s:toRemove){
            newmap.remove(s);
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        String time = sdf.format(date);
        Commit newCommit=new Commit(message,parentcommitid,null,time,newmap);
        newCommit.saveCommit();
        staging.clear();
        toRemove.clear();
        branches.put(currentBranch,newCommit.getId());
        Utils.writeObject(join(GITLET_DIR, "repo"), this);
    }
    public void rm(String fileName) {
        if(staging.containsKey(fileName)){
            staging.remove(fileName);
            return;
        }
        String commitid=branches.get(currentBranch);
        File Latest=Utils.join(COMMITS_DIR,commitid);
        Commit c=Utils.readObject(Latest, Commit.class);
        Map<String, String> map=c.getFileNameToBlobId();
        if(map.containsKey(fileName)){
            toRemove.add(fileName);
            File f=new File(fileName);
            f.delete();
            return;
        }
       Utils.writeObject(join(GITLET_DIR,"repo"),this);
        System.out.println("No reason to remove the file.");
    }
    public void log() {
        // 先拿到当前最新的提交
        String currentId = branches.get(currentBranch);
        // 开始回溯打印
        while (currentId != null) {
            // 1. 读取当前的提交对象
            File commitFile = new File(COMMITS_DIR, currentId);
            Commit commit = Utils.readObject(commitFile, Commit.class);

            // 2. 开始打印 ====================
            System.out.println("===");
            System.out.println("commit " + currentId); // 完整哈希

            // 如果是合并提交（有第二个父节点），打印 Merge 行
            if (commit.getParent2() != null) {
                String p1 = commit.getParent().substring(0, 7);
                String p2 = commit.getParent2().substring(0, 7);
                System.out.println("Merge: " + p1 + " " + p2);
            }

            System.out.println("Date: " + commit.getTime());
            System.out.println(commit.getMessage());
            System.out.println(); // 空行

            // 3. 往前回溯（只看第一个父提交）
            currentId = commit.getParent();
        }
    }
    public void globalLog() {
        List<String> ListOfcommitid =Utils.plainFilenamesIn(COMMITS_DIR);
        if (ListOfcommitid != null) {
            for(String s:ListOfcommitid){
                File Latest=Utils.join(COMMITS_DIR,s);
                Commit c=Utils.readObject(Latest, Commit.class);
                System.out.println("===");
                System.out.println("commit " + s); // 完整哈希

                // 如果是合并提交（有第二个父节点），打印 Merge 行
                if (c.getParent2() != null) {
                    String p1 = c.getParent().substring(0, 7);
                    String p2 = c.getParent2().substring(0, 7);
                    System.out.println("Merge: " + p1 + " " + p2);
                }

                System.out.println("Date: " + c.getTime());
                System.out.println(c.getMessage());
                System.out.println();
            }
        }
    }
    public void find(String message) {
        List<String> ListOfcommitid =Utils.plainFilenamesIn(COMMITS_DIR);
        boolean found=false;
        if (ListOfcommitid != null) {
            for(String s:ListOfcommitid){
                File Latest=Utils.join(COMMITS_DIR,s);
                Commit c=Utils.readObject(Latest, Commit.class);
                if(c.getMessage().equals(message)){
                    found=true;
                    System.out.println(s);
                }
            }
        }
        if(!found){
            System.out.println("Found no commit with that message.");
        }
    }
    public void status() {
       List<String> Listofbranches= new ArrayList<>( branches.keySet());
       Listofbranches.sort(null);
       for(String s:Listofbranches){
           if(s.equals(currentBranch)){
               System.out.print("*");
           }
           System.out.println(s);
       }
        System.out.println();
       List<String> Listofstagingfilename= new ArrayList<>(staging.keySet());
       Listofstagingfilename.sort(null);
       for(String s:Listofstagingfilename){
           System.out.println(s);
       }
        System.out.println();
        List<String> Listoftoremove = new ArrayList<>(toRemove);
        Listoftoremove.sort(null);
        for(String s:Listoftoremove){
            System.out.println(s);
        }
        System.out.println();
    }
    // checkout 统一入口 处理三种格式
    public void checkout(String[] args) {
        int n=args.length;
        if(n==3 && args[1].equals("--")){
            String currentId = branches.get(currentBranch);
            File commitFile = new File(COMMITS_DIR, currentId);
            Commit commit = Utils.readObject(commitFile, Commit.class);
            Map<String, String> filenametobid=commit.getFileNameToBlobId();
            if(!filenametobid.containsKey(args[2])){
                System.out.println("File does not exist in that commit.");
            }
            else{
                String blobid=filenametobid.get(args[2]);
                File BlobFile =Utils.join(BLOBS_DIR, blobid);
                String Content =Utils.readContentsAsString(BlobFile);
                File f=new File(args[2]);
               Utils.writeContents(f,Content);
            }
        }
        else if(n==4){
            String commitid=args[1];
            String filename=args[3];
            List<String> ListOfcommitid =Utils.plainFilenamesIn(COMMITS_DIR);
            int cnt=0;
            String totalcommitid="";
            for(String Commitid:ListOfcommitid){
                if(Commitid.startsWith(commitid)){
                    cnt++;
                    totalcommitid=Commitid;
                }
                if(cnt>1){
                    return;
                }
            }
            if(cnt==0){
                System.out.println("No commit with that id exists.");
                return;
            }
            File commitFile=Utils.join(COMMITS_DIR,totalcommitid);
            Commit commit = Utils.readObject(commitFile, Commit.class);
            Map<String, String> filenametobid=commit.getFileNameToBlobId();
            if(!filenametobid.containsKey(filename)){
                System.out.println("File does not exist in that commit.");
                return;
            }
            String blobid=filenametobid.get(filename);
        File BlobFile = Utils.join(BLOBS_DIR, blobid);
            String Content =Utils.readContentsAsString(BlobFile);
            File f=new File(filename);
            Utils.writeContents(f,Content);
        }
        else{
            String branchname=args[1];
            if(!branches.containsKey(branchname)){
                System.out.println("No such branch exists.");
                return;
            }
            if(branchname.equals(currentBranch)){
                System.out.println("No need to checkout the current branch.");
                return;
            }
            String targetCid = branches.get(branchname);
            Commit targetCommit = Utils.readObject(join(COMMITS_DIR, targetCid), Commit.class);
            Set<String> targetFiles = targetCommit.getFileNameToBlobId().keySet();
            List<String> workFiles =Utils.plainFilenamesIn(new File("."));
            boolean hasDanger = false;
            for (String fileName : workFiles) {
                // 条件1：是未跟踪文件
                boolean isUntracked = true;
                for (String cid :Utils.plainFilenamesIn(COMMITS_DIR)) {
                    Commit c = Utils.readObject(join(COMMITS_DIR, cid), Commit.class);
                    if (c.getFileNameToBlobId().containsKey(fileName)) {
                        isUntracked = false;
                        break;
                    }
                }
                // 条件2：目标分支里有这个文件
                if (isUntracked && targetFiles.contains(fileName)) {
                    hasDanger = true;
                    break;
                }
            }

            if (hasDanger) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
            for(String fn:targetFiles){
                String blobid=targetCommit.getFileNameToBlobId().get(fn);
                File BlobFile =Utils.join(BLOBS_DIR, blobid);
                String Content =Utils.readContentsAsString(BlobFile);
                File f=new File(fn);
               Utils.writeContents(f,Content);
            }
            for(String s:workFiles){
                if(!targetFiles.contains(s)){
                    new File(s).delete();
                }
            }
            staging.clear();
            toRemove.clear();
            currentBranch=branchname;
        }
        Utils.writeObject(join(GITLET_DIR,"repo"),this);
    }

    public void branch(String branchName) {
        if(branches.containsKey(branchName)){
            System.out.println("A branch with that name already exists.");
            return;
        }
        String commitid=branches.get(currentBranch);
        branches.put(branchName,commitid);
        Utils.writeObject(join(GITLET_DIR,"repo"),this);
    }
    public void rmBranch(String branchName) {
        if(!branches.containsKey(branchName)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if(branchName.equals(currentBranch)){
            System.out.println("Cannot remove the current branch.");
            return;
        }
        branches.remove(branchName);
        Utils.writeObject(join(GITLET_DIR,"repo"),this);
    }
    public void reset(String commitId) {
        List<String> ListOfcommitid =Utils.plainFilenamesIn(COMMITS_DIR);
        int cnt=0;
        String totalcommitid="";
        if (ListOfcommitid != null) {
            for(String Commitid:ListOfcommitid){
                if(Commitid.startsWith(commitId)){
                    cnt++;
                    totalcommitid=Commitid;
                }
                if(cnt>1){
                    return;
                }
            }
        }
        if(cnt==0){
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit targetCommit = Utils.readObject(join(COMMITS_DIR, totalcommitid), Commit.class);
        Set<String> targetFiles = targetCommit.getFileNameToBlobId().keySet();
        List<String> workFiles =Utils.plainFilenamesIn(new File("."));
        boolean hasDanger = false;
        for (String fileName : workFiles) {
            // 条件1：是未跟踪文件
            boolean isUntracked = true;
            for (String cid :Utils.plainFilenamesIn(COMMITS_DIR)) {
                Commit c = Utils.readObject(join(COMMITS_DIR, cid), Commit.class);
                if (c.getFileNameToBlobId().containsKey(fileName)) {
                    isUntracked = false;
                    break;
                }
            }
            // 条件2：目标分支里有这个文件
            if (isUntracked && targetFiles.contains(fileName)) {
                hasDanger = true;
                break;
            }
        }

        if (hasDanger) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        for(String fn:targetFiles){
            String blobid=targetCommit.getFileNameToBlobId().get(fn);
            File BlobFile =Utils.join(BLOBS_DIR, blobid);
            String Content =Utils.readContentsAsString(BlobFile);
            File f=new File(fn);
           Utils.writeContents(f,Content);
        }
        for(String s:workFiles){
            if(!targetFiles.contains(s)){
                new File(s).delete();
            }
        }
        branches.put(currentBranch,totalcommitid);
        staging.clear();
        toRemove.clear();
       Utils.writeObject(join(GITLET_DIR,"repo"),this);
    }
    public void merge(String branchName) {
        if(!staging.isEmpty()){
            System.out.println("You have uncommitted changes.");
            return;
        }
        if(!branches.containsKey(branchName)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if(branchName.equals(currentBranch)){
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        String currcommitid=branches.get(currentBranch);
        String othercommitid=branches.get(branchName);
        String parentcommitid=findcloseCommonParent(currcommitid,othercommitid);
        if(parentcommitid.equals(othercommitid)){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        Commit targetCommit = Utils.readObject(join(COMMITS_DIR, othercommitid), Commit.class);
        Set<String> targetFiles = targetCommit.getFileNameToBlobId().keySet();
        List<String> workFiles =Utils.plainFilenamesIn(".");
        if(parentcommitid.equals(currcommitid)) {
            branches.put(currentBranch,othercommitid);
            for(String fn:targetFiles){
                String blobid=targetCommit.getFileNameToBlobId().get(fn);
                File BlobFile =Utils.join(BLOBS_DIR, blobid);
                String Content =Utils.readContentsAsString(BlobFile);
                File f=new File(fn);
               Utils.writeContents(f,Content);
            }
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        Commit currCommit = Utils.readObject(join(COMMITS_DIR, currcommitid), Commit.class);
        Set<String> currFiles = currCommit.getFileNameToBlobId().keySet();
        Commit otherCommit = Utils.readObject(join(COMMITS_DIR, othercommitid), Commit.class);
        Set<String> otherFiles = otherCommit.getFileNameToBlobId().keySet();
        Commit splitCommit = Utils.readObject(join(COMMITS_DIR, parentcommitid), Commit.class);
        Set<String> splitFiles = splitCommit.getFileNameToBlobId().keySet();

        for (String f : workFiles) {
            // 未跟踪 = 不在当前提交里
            boolean untracked = !currFiles.contains(f);

            // 会被覆盖 = 这个文件在 目标分支 或 分叉点 里存在
            boolean willBeOverwritten = otherFiles.contains(f) || splitFiles.contains(f);

            // 如果 未跟踪 + 会被覆盖 → 直接报错
            if (untracked && willBeOverwritten) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }
        Map<String, String> curr_s=currCommit.getFileNameToBlobId();
        Map<String, String> parent_s=splitCommit.getFileNameToBlobId();
        Map<String, String> other_s=otherCommit.getFileNameToBlobId();
        Set<String> allFileName = new TreeSet<>();
        allFileName.addAll(curr_s.keySet());
        allFileName.addAll(parent_s.keySet());
        allFileName.addAll(other_s.keySet());
        boolean conflictFlag = false;
        for(String filename:allFileName){

            String splitblob=parent_s.get(filename);
            String currblob=curr_s.get(filename);
            String otherblob=other_s.get(filename);
            if(currblob.equals(otherblob)){

            }
            else if(currblob.equals(splitblob)){
                staging.put(filename,otherblob);
                byte[] content = Utils.readContents(Utils.join(BLOBS_DIR, otherblob));
                Utils.writeContents(new File(filename), content);
            }
            else if (!otherblob.equals(splitblob) && !currblob.equals(splitblob)){
                byte[] currContent = Utils.readContents(Utils.join(BLOBS_DIR, currblob));
                String currStr = new String(currContent);
                byte[] otherContent = Utils.readContents(Utils.join(BLOBS_DIR, otherblob));
                String otherStr = new String(otherContent);
                String conflictText = "<<<<<<< HEAD\n"
                        + currStr
                        + "=======\n"
                        + otherStr
                        + ">>>>>>>\n";
                Utils.writeContents(new File(filename), conflictText);
                staging.put(filename,"CONFLICT");
                conflictFlag = true;
            }
            if (conflictFlag) {
                System.out.println("Encountered a merge conflict.");
            }
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        String time = sdf.format(date);
        Commit c=new Commit("Merged "+branchName+" into "+currentBranch,currcommitid,
                othercommitid,time,staging);
        String newCommitId = Utils.sha1(Utils.serialize(c));
       Utils.writeObject(join(COMMITS_DIR,newCommitId), (Serializable) c);
        branches.put(currentBranch, newCommitId);
       Utils.writeObject(join(GITLET_DIR,"repo"),this);

    }
    private  Set<String> getAllancestors(String commitid){
        Set<String> ancestors = new TreeSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(commitid);

        while (!queue.isEmpty()) {
            String cid = queue.poll();
            if (cid == null || ancestors.contains(cid)) {
                continue;
            }
            ancestors.add(cid);

            // 读取这个提交
            Commit c = Utils.readObject(Utils.join(COMMITS_DIR, cid), Commit.class);

            // 把父1加入队列
            queue.add(c.getParent());

            // 如果是合并提交，把父2也加入队列
            if (c.getParent2() != null) {
                queue.add(c.getParent2());
            }
        }
        return ancestors;
    }
    private String findcloseCommonParent(String currcommitid,String othercommitid){
        Set<String> otherAncestors = getAllancestors(othercommitid);
        Queue<String> queue = new LinkedList<>();
        queue.add(currcommitid);

        while (!queue.isEmpty()) {
            String cid = queue.poll();

            // 如果这个提交也在另一个分支的祖先里 → 就是分叉点！
            if (otherAncestors.contains(cid)) {
                return cid;
            }

            Commit c = Utils.readObject(Utils.join(COMMITS_DIR, cid), Commit.class);

            // 继续往上找爹
            if (c.getParent() != null) {
                queue.add(c.getParent());
            }
            if (c.getParent2() != null) {
                queue.add(c.getParent2());
            }
        }
        return null; // 永远不会到这里
    }
    // 远程附加命令（可选）
    public void addRemote(String remoteName, String path) {
        if(remotes.containsKey(remoteName)){
            System.out.println("A remote with that name already exists.");
            return;
        }
        remotes.put(remoteName,path);
    }
    public void rmRemote(String remoteName) {
        if(!remotes.containsKey(remoteName)){
            System.out.println("A remote with that name does not exist.");
            return;
        }
        remotes.remove(remoteName);
    }
    public void push(String remoteName, String branchName) {
        if(!remotes.containsKey(remoteName)){
            System.out.println("Remote directory not found.");
            return;
        }
        String path=remotes.get(remoteName);
        File remoteDir = new File(path);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteBranchesFile = Utils.join(path, "branches");

        Map<String, String> remoteBranches = new TreeMap<>();
        String content = Utils.readContentsAsString(remoteBranchesFile);
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(":", 2);
            String branchNa = parts[0].trim();
            String commitId = parts[1].trim();
            remoteBranches.put(branchNa, commitId);
        }
        String currcommitid=branches.get(currentBranch);
        if(remoteBranches.containsKey(branchName)) {
            String remotecommitid = remoteBranches.get(branchName);
            Set<String> allancestors = getAllancestors(currcommitid);
            if (!allancestors.contains(remotecommitid)) {
                System.out.println("Please pull down remote changes before pushing.");
                return;
            }
        }
        List<String> ListOfcommitid =plainFilenamesIn(COMMITS_DIR);
        File Fileofremote = Utils.join(path, "commits");
        Fileofremote.mkdirs();
        List<String> ListOfremotecommitid =plainFilenamesIn(Fileofremote);
        for(String s:ListOfcommitid){
            if(!ListOfremotecommitid.contains(s)){
                // 1. 构建本地提交文件
                File localCommitFile = Utils.join(COMMITS_DIR, s);
                // 2. 构建远程存放文件
                File remoteCommitFile = Utils.join(Fileofremote, s);
                // 3. 读取本地完整内容
                byte[] contents = Utils.readContents(localCommitFile);
                // 4. 原样写入远程文件
                Utils.writeContents(remoteCommitFile, contents);
            }
        }
        List<String> ListOfblobid =plainFilenamesIn(BLOBS_DIR);
        File Fileofblobremote = Utils.join(path, "blobs");
        Fileofblobremote.mkdirs();
        List<String> ListOfremoteblobid =plainFilenamesIn(Fileofblobremote);
        for(String s:ListOfblobid){
            if(!ListOfremoteblobid.contains(s)){
                // 1. 构建本地提交文件
                File localblobFile = Utils.join(BLOBS_DIR, s);
                // 2. 构建远程存放文件
                File remoteblobFile = Utils.join(Fileofblobremote, s);
                // 3. 读取本地完整内容
                byte[] contents = Utils.readContents(localblobFile);
                // 4. 原样写入远程文件
                Utils.writeContents(remoteblobFile, contents);
            }
        }
        remoteBranches.put(branchName,currcommitid);
        StringBuilder sb = new StringBuilder();
        for (String name : remoteBranches.keySet()) {
            sb.append(name).append(": ").append(remoteBranches.get(name)).append("\n");
        }
        Utils.writeContents(remoteBranchesFile, sb.toString());
    }
    public void fetch(String remoteName, String branchName) {
        if(!remotes.containsKey(remoteName)){
            System.out.println("Remote directory not found.");
            return;
        }
        String path=remotes.get(remoteName);
        File remoteDir = new File(path);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            return;
        }
        File remoteBranchesFile = Utils.join(path, "branches");

        Map<String, String> remoteBranches = new HashMap<>();
        if (remoteBranchesFile.exists()) {
            String content = Utils.readContentsAsString(remoteBranchesFile);
            String[] lines = content.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(":", 2);
                String branchNa = parts[0].trim();
                String commitId = parts[1].trim();
                remoteBranches.put(branchNa, commitId);
            }
        }
        if(!remoteBranches.containsKey(branchName)){
            System.out.println("That remote does not have that branch.");
            return;
        }
        String remoteCommitId = remoteBranches.get(branchName);
        COMMITS_DIR.mkdirs();
    List<String> ListOfcommitid =Utils.plainFilenamesIn(COMMITS_DIR);
        File Fileofremote = Utils.join(path, "commits");
        Fileofremote.mkdirs();
        List<String> ListOfremotecommitid =Utils.plainFilenamesIn(Fileofremote);
        for(String s:ListOfremotecommitid){
            if(!ListOfcommitid.contains(s)){
                File remoteCommitFile = Utils.join(Fileofremote, s);
                File localCommitFile = Utils.join(COMMITS_DIR, s);
                byte[] contents = Utils.readContents(remoteCommitFile);
                Utils.writeContents(localCommitFile, contents);
            }
        }
        BLOBS_DIR.mkdirs();
        List<String> ListOfblobid =Utils.plainFilenamesIn(BLOBS_DIR);
        File Fileofblobremote = Utils.join(path, "blobs");
        Fileofblobremote.mkdirs();
        List<String> ListOfremoteblobid =Utils.plainFilenamesIn(Fileofblobremote);
        for(String s:ListOfremoteblobid){
            if(!ListOfblobid.contains(s)){
                File localblobFile = Utils.join(BLOBS_DIR, s);
                File remoteblobFile = Utils.join(Fileofblobremote, s);
                byte[] contents = Utils.readContents(remoteblobFile);
                Utils.writeContents(localblobFile, contents);
            }
        }
        String localBranchName = remoteName + "/" + branchName;
        branches.put(localBranchName, remoteCommitId);

        File localBranchFile = Utils.join(GITLET_DIR, "branches");
        StringBuilder sb = new StringBuilder();
        for (String name : branches.keySet()) {
            sb.append(name).append(": ").append(branches.get(name)).append("\n");
        }
        Utils.writeContents(localBranchFile, sb.toString());
    }
    public void pull(String remoteName, String branchName) {
        fetch(remoteName,branchName);
        String localBranchName = remoteName + "/" + branchName;
        merge(localBranchName);
    }

}
