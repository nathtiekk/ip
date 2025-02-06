package kif;

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

    private static void bootUp() {
        Task.initialiseUserTasks();
        introduce();
        Task.listUserTask();
    }

    public enum UserCommand {
        LIST,
        MARK,
        UNMARK,
        DEADLINE,
        EVENT,
        TODO,
        DELETE
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
        bootUp();
        String userMessage = reader.readLine();
        String[] splitMessage = userMessage.split(" ");
        while(!isTerminate(splitMessage[0])) {
            UserCommand command = UserCommand.valueOf(splitMessage[0].toUpperCase());
            switch (command) {
                case LIST -> Task.listUserTask();
                case MARK -> Task.markUserTask(Integer.parseInt(splitMessage[1]));
                case UNMARK -> Task.unmarkUserTask(Integer.parseInt(splitMessage[1]));
                case DEADLINE -> {
                    try {
                        Task.Deadline.createDeadline(userMessage);
                    } catch (KifException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case EVENT -> Task.Event.createEvent(userMessage);
                case DELETE -> Task.delete(Integer.parseInt(splitMessage[1]));
                case TODO -> {
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
