import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Kif {

    private static void introduce() {
        String introduce =
                """
                ____________________________________________________________
                Hello! I'm Kif
                What can I do for you?
                ____________________________________________________________
                """;
        System.out.println(introduce);
    }

    private static void goodbye() {
        String goodbye =
                """
                ____________________________________________________________
                Kif: Bye. Hope to see you again soon!
                ____________________________________________________________""";
        System.out.println(goodbye);
    }

    private static void echo(String userMessage) {
        String echoMessage =
                "____________________________________________________________\n" +
                 "Kif: " + userMessage + "\n" +
                "____________________________________________________________\n";
        System.out.println(echoMessage);
    }

    private static boolean isTerminate(String code) {
        return code.equals("bye");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        /*
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);
         */
        introduce();
        String userMessage = reader.readLine();
        while(!isTerminate(userMessage)) {
            if(userMessage.equals("list")) {
                Storage.listUserTexts();
            }
            else {
                Storage.addUserTexts(userMessage);
            }
            userMessage = reader.readLine();
        }
        goodbye();
    }
}
