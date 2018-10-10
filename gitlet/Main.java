package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Chelsea Chen, Mian Zhong, Alan Song
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {

        Gitlet g = new Gitlet();
        Command command = new Command();
        command.process(g, args);
    }
}

