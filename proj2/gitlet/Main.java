package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  Gitlet 的驱动类，Gitlet 是 Git 版本控制系统的一个子集。
 *  @author TODO
 *  @author 作者名
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  用法：java gitlet.Main 参数列表，其中参数列表包含
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  <命令> <操作数1> <操作数2> ...
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        // TODO: 如果参数数组为空该怎么办？
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        Repository repo = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                // TODO: 处理 `init` 命令
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                // TODO: 处理 `add [文件名]` 命令
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            // TODO: 填充剩余的命令
            case "commit":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.commit(args[1]);
                break;
            case "rm":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.rm(args[1]);
                break;
            case "log":
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.log();
                break;
            case "global-log":
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.globalLog();
                break;
            case "find":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.find(args[1]);
                break;
            case "status":
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.status();
                break;
            case "checkout":
                if (!(args.length == 2 || args.length == 3 || args.length == 4)) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.checkout(args);
                break;
            case "branch":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.branch(args[1]);
                break;
            case "rm-branch":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                repo.rmBranch(args[1]);
                break;
            case "reset":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                repo.reset(args[1]);
                break;
            case "merge":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                repo.merge(args[1]);
                break;
            case "add-remote":
                if(args.length!=3){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                repo.addRemote(args[1],args[2]);
                break;
            case "rm-remote":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                repo.rmRemote(args[1]);
                break;
            case "push":
                if(args.length!=3){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                repo.push(args[1],args[2]);
                break;
            case "fetch":
                if(args.length!=3){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                repo.fetch(args[1],args[2]);
                break;
            case "pull":
                if(args.length!=3){
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }

                repo.pull(args[1],args[2]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
