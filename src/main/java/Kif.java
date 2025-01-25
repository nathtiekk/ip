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

    private static boolean isTerminate(String code) {
        return code.equals("bye");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        introduce();
        String userMessage = reader.readLine();
        String[] splitMessage = userMessage.split(" ");
        while(!isTerminate(splitMessage[0])) {
            switch (splitMessage[0]) {
                case "list" -> Task.listUserTask();
                case "mark" -> Task.markUserTask(Integer.parseInt(splitMessage[1]));
                case "unmark" -> Task.unmarkUserTask(Integer.parseInt(splitMessage[1]));
                case "deadline" -> Task.Deadline.createDeadline(userMessage);
                case "event" -> Task.Event.createEvent(userMessage);
                case "todo" -> {
                    try {
                        Task.ToDo.createToDo(userMessage);
                    } catch (KifException e) {
                        System.out.println(e.getMessage());
                    }
                }
                default -> System.out.println(
                                    """
                                    ____________________________________________________________
                                    OOPS!!! I'm sorry, but I don't know what that means :-(
                                    ____________________________________________________________"""
                );
            }
            userMessage = reader.readLine();
            splitMessage = userMessage.split(" ");
        }
        goodbye();
    }
}
