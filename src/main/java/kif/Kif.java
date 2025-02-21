package kif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Handles the chatbot's interaction with the user.
 * Also includes methods to start up, and end the interaction.
 */
public class Kif {

    private static void bootUp() {
        Storage.initialiseUserTasks();
        Ui.introduction();
        Task.listUserTask();
    }

    public enum UserCommand {
        LIST,
        MARK,
        UNMARK,
        DEADLINE,
        EVENT,
        TODO,
        DELETE,
        BYE
    }

    private static boolean isTerminate(String code) {
        return code.equals("bye");
    }

    public static String getResponse(String userMessage) {
        StringBuilder response = new StringBuilder();
        String[] splitMessage = Parser.parseUserInput(userMessage);

        UserCommand command = UserCommand.valueOf(splitMessage[0].toUpperCase());
        switch (command) {
        case LIST:
            response.append(Task.listUserTask());
            break;
        case MARK:
            response.append(Task.markUserTask(Integer.parseInt(splitMessage[1])));
            break;
        case UNMARK:
            response.append(Task.unmarkUserTask(Integer.parseInt(splitMessage[1])));
            break;
        case DEADLINE:
            try {
                response.append(Task.Deadline.createDeadline(userMessage));
            } catch (KifException e) {
                response.append(e.getMessage());
            }
            break;
        case EVENT:
            response.append(Task.Event.createEvent(userMessage));
            break;
        case DELETE:
            response.append(Task.delete(Integer.parseInt(splitMessage[1])));
            break;
        case TODO:
            try {
                response.append(Task.ToDo.createToDo(userMessage));
            } catch (KifException e) {
                response.append(e.getMessage());
            }
            break;
        case BYE:
            response.append(Ui.goodbye());
            Ui.closeGui();
            break;
        default:
            response.append(
                    """
                    ____________________________________________________________
                    OOPS!!! I'm sorry, but I don't know what that means :-(
                    ____________________________________________________________"""
            );
            break;
        }
        return response.toString();
    }

    //outdated console interaction
    private static void run() throws IOException {
        bootUp();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String userMessage = reader.readLine();
        String[] splitMessage = Parser.parseUserInput(userMessage);

        while (!isTerminate(splitMessage[0])) {
            UserCommand command = UserCommand.valueOf(splitMessage[0].toUpperCase());
            switch (command) {
            case LIST:
                Task.listUserTask();
                break;
            case MARK:
                Task.markUserTask(Integer.parseInt(splitMessage[1]));
                break;
            case UNMARK:
                Task.unmarkUserTask(Integer.parseInt(splitMessage[1]));
                break;
            case DEADLINE:
                try {
                    Task.Deadline.createDeadline(userMessage);
                } catch (KifException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case EVENT:
                Task.Event.createEvent(userMessage);
                break;
            case DELETE:
                Task.delete(Integer.parseInt(splitMessage[1]));
                break;
            case TODO:
                try {
                    Task.ToDo.createToDo(userMessage);
                } catch (KifException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                System.out.println(
                        """
                        ____________________________________________________________
                        OOPS!!! I'm sorry, but I don't know what that means :-(
                        ____________________________________________________________"""
                );
                break;
            }

            userMessage = reader.readLine();
            splitMessage = Parser.parseUserInput(userMessage);
        }
        Ui.goodbye();
    }

    public static void main(String[] args) throws IOException {
        //Kif.run();
    }
}
