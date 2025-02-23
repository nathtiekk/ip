package kif;

/**
 * Handles the chatbot's interaction with the user.
 * Also includes methods to start up, and end the interaction.
 */
public class Kif {

    static UserCommand previousCommand;
    static Task previousTask;

    public enum UserCommand {
        LIST,
        MARK,
        UNMARK,
        DEADLINE,
        EVENT,
        TODO,
        DELETE,
        BYE,
        UNDO,
    }

    private static String undoPrevCommand(UserCommand previousCommand, Task previousTask)
            throws KifException {
        switch (previousCommand) {
        case MARK:
            return Task.unmarkUserTask(Task.getTaskIndex(previousTask));
            //Fallthrough
        case UNMARK:
            return Task.markUserTask(Task.getTaskIndex(previousTask));
            //Fallthrough
        case DEADLINE, TODO, EVENT:
            return Task.delete(Task.getTaskIndex(previousTask));
            //Fallthrough
        case DELETE:
            if (previousTask instanceof Task.Deadline) {
                Task.Deadline task = Task.Deadline.createDeadline((Task.Deadline) previousTask);
                if (previousTask.isDone) {
                    Task.markUserTask(Task.getTaskIndex(task));
                }
                return Task.createTaskMsg(task);
            } else if (previousTask instanceof Task.ToDo) {
                Task.ToDo task = Task.ToDo.createToDo((Task.ToDo) previousTask);
                if (previousTask.isDone) {
                    Task.markUserTask(Task.getTaskIndex(task));
                }
                return Task.createTaskMsg(task);
            } else if (previousTask instanceof Task.Event) {
                Task.Event task = Task.Event.createEvent((Task.Event) previousTask);
                if (previousTask.isDone) {
                    Task.markUserTask(Task.getTaskIndex(task));
                }
                return Task.createTaskMsg(task);
            }
            break;
        }
        return Ui.cannotUndoMsg();
    }

    public static String getResponse(String userMessage) {
        StringBuilder response = new StringBuilder();
        String[] splitMessage = Parser.parseUserInput(userMessage);

        UserCommand command = UserCommand.valueOf(splitMessage[0].toUpperCase());
        switch (command) {
        case LIST:
            response.append(Task.listUserTask());
            previousCommand = UserCommand.LIST;
            break;
        case MARK:
            response.append(Task.markUserTask(Integer.parseInt(splitMessage[1])));
            previousCommand = UserCommand.MARK;
            previousTask = Task.getTask(Integer.parseInt(splitMessage[1]));
            break;
        case UNMARK:
            response.append(Task.unmarkUserTask(Integer.parseInt(splitMessage[1])));
            previousCommand = UserCommand.UNMARK;
            previousTask = Task.getTask(Integer.parseInt(splitMessage[1]));
            break;
        case DEADLINE:
            try {
                Task.Deadline deadLineTask = Task.Deadline.createDeadline(userMessage);
                response.append(Task.createTaskMsg(deadLineTask));
                previousTask = deadLineTask;
                previousCommand = UserCommand.DEADLINE;
            } catch (KifException e) {
                response.append(e.getMessage());
            }
            break;
        case EVENT:
            Task.Event eventTask = Task.Event.createEvent(userMessage);
            response.append(Task.createTaskMsg(eventTask));
            previousTask = eventTask;
            previousCommand = UserCommand.EVENT;
            break;
        case DELETE:
            previousTask = Task.getTask(Integer.parseInt(splitMessage[1]));
            response.append(Task.delete(Integer.parseInt(splitMessage[1])));
            previousCommand = UserCommand.DELETE;
            break;
        case TODO:
            try {
                Task.ToDo toDoTask = Task.ToDo.createToDo(userMessage);
                response.append(Task.createTaskMsg(toDoTask));
                previousTask = toDoTask;
                previousCommand = UserCommand.TODO;
            } catch (KifException e) {
                response.append(e.getMessage());
            }
            break;
        case BYE:
            response.append(Ui.goodbye());
            Ui.closeGui();
            break;
        case UNDO:
            try {
                response.append(undoPrevCommand(previousCommand, previousTask));
            } catch (KifException e) {
                response.append(e.getMessage());
            }
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
}
