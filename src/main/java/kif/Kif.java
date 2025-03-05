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

    static String undoPrevCommand() throws KifException {
        if (previousCommand == null || previousTask == null) {
            return Ui.getCannotUndoMessage();
        }

        return switch (previousCommand) {
            case MARK -> Task.unmarkTask(Task.getTaskIndex(previousTask));
            case UNMARK -> Task.markTask(Task.getTaskIndex(previousTask));
            case DEADLINE, TODO, EVENT -> Task.deleteTask(Task.getTaskIndex(previousTask));
            case DELETE -> restoreDeletedTask();
            default -> Ui.getCannotUndoMessage();
        };
    }

    private static String restoreDeletedTask() throws KifException {
        assert previousTask != null : "Previous task should not be null when restoring";

        Task restoredTask;

        if (previousTask instanceof Task.Deadline) {
            restoredTask = Task.Deadline.create((Task.Deadline) previousTask);
        } else if (previousTask instanceof Task.ToDo) {
            restoredTask = Task.ToDo.create((Task.ToDo) previousTask);
        } else if (previousTask instanceof Task.Event) {
            restoredTask = Task.Event.create((Task.Event) previousTask);
        } else {
            return Ui.getCannotUndoMessage();
        }

        if (previousTask.isDone) {
            Task.markTask(Task.getTaskIndex(restoredTask));
        }

        return Task.createTaskMsg(restoredTask);
    }

    public static String getResponse(String userMessage) {
        StringBuilder response = new StringBuilder();
        String[] splitMessage = Parser.splitUserInput(userMessage);

        try {
            UserCommand command = UserCommand.valueOf(splitMessage[0].toUpperCase());
            response.append(handleCommand(command, splitMessage, userMessage));
        } catch (IllegalArgumentException | KifException e) {
            response.append(Ui.getUnknownCommandMessage());
        }
        return response.toString();
    }

    static String handleCommand(UserCommand command, String[] splitMessage, String userMessage) throws KifException {
        if (command == UserCommand.LIST) {
            previousCommand = command;
            previousTask = null;
        }

        return switch (command) {
            case LIST -> Task.listUserTask();
            case MARK -> updateTaskStatus(Integer.parseInt(splitMessage[1]), true);
            case UNMARK -> updateTaskStatus(Integer.parseInt(splitMessage[1]), false);
            case DEADLINE -> createTask(Task.Deadline.create(userMessage));
            case EVENT -> createTask(Task.Event.create(userMessage));
            case TODO -> createTask(Task.ToDo.create(userMessage));
            case DELETE -> deleteTask(Integer.parseInt(splitMessage[1]));
            case BYE -> exitApplication();
            case UNDO -> undo();
        };
    }

    private static String undo() throws KifException {
        assert previousCommand != null : "Previous command should not be null before undo";
        String result = undoPrevCommand();
        previousCommand = UserCommand.UNDO;
        previousTask = null;
        return result;
    }

    private static String updateTaskStatus(int index, boolean isMarking) {
        assert index >= 0 : "Task index should be non-negative";

        previousTask = Task.getTask(index);
        assert previousTask != null : "Previous task should not be null when updating";

        previousCommand = isMarking ? UserCommand.MARK : UserCommand.UNMARK;
        return isMarking ? Task.markTask(index) : Task.unmarkTask(index);
    }

    private static String createTask(Task task) {
        assert task != null : "Task should not be null when creating";

        previousTask = task;
        previousCommand = determineTaskCommand(task);
        return Task.createTaskMsg(task);
    }

    private static UserCommand determineTaskCommand(Task task) {
        assert task != null : "Task cannot be null when determining command";

        if (task instanceof Task.Deadline) {
            return UserCommand.DEADLINE;
        } else if (task instanceof Task.Event) {
            return UserCommand.EVENT;
        } else {
            return UserCommand.TODO;
        }
    }

    private static String deleteTask(int index) {
        assert index >= 0 : "Task index should be non-negative";

        previousTask = Task.getTask(index);
        assert previousTask != null : "Previous task should not be null when deleting";

        previousCommand = UserCommand.DELETE;
        return Task.deleteTask(index);
    }

    private static String exitApplication() {
        Ui.closeGui();
        return Ui.getGoodbyeMessage();
    }
}
