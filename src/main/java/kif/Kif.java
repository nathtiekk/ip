package kif;

/**
 * Handles the chatbot's interaction with the user.
 * Also includes methods to start up, and end the interaction.
 */
public class Kif {

    static UserCommand previousCommand;
    static Task previousTask;

    /**
     * Enum representing possible user commands.
     */
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

    /**
     * Undoes the previous user command if possible.
     *
     * @return A response message indicating the undo result.
     * @throws KifException If an error occurs while undoing the command.
     */
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

    /**
     * Restores a previously deleted task.
     *
     * @return A response message indicating the restored task or an error message.
     * @throws KifException If an error occurs while restoring the task.
     */
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

    /**
     * Processes the user message and returns an appropriate response.
     *
     * @param userMessage The message input by the user.
     * @return The chatbot's response based on the user's command.
     */
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

    /**
     * Handles user commands and executes corresponding actions.
     *
     * @param command The parsed user command.
     * @param splitMessage The split message components from user input.
     * @param userMessage The full raw user input.
     * @return A response message indicating the outcome of the command.
     * @throws KifException If an error occurs while processing the command.
     */
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

    /**
     * Undoes the last user action.
     *
     * @return A response message indicating the undo result.
     * @throws KifException If an error occurs while undoing.
     */
    private static String undo() throws KifException {
        assert previousCommand != null : "Previous command should not be null before undo";
        String result = undoPrevCommand();
        previousCommand = UserCommand.UNDO;
        previousTask = null;
        return result;
    }

    /**
     * Updates the status of a task (mark/unmark).
     *
     * @param index The index of the task to update.
     * @param isMarking Whether the task is being marked as done or not.
     * @return A response message indicating the updated task status.
     */
    private static String updateTaskStatus(int index, boolean isMarking) {
        assert index >= 0 : "Task index should be non-negative";

        previousTask = Task.getTask(index);
        assert previousTask != null : "Previous task should not be null when updating";

        previousCommand = isMarking ? UserCommand.MARK : UserCommand.UNMARK;
        return isMarking ? Task.markTask(index) : Task.unmarkTask(index);
    }

    /**
     * Creates a new task and adds it to the task list.
     *
     * @param task The task to be created.
     * @return A response message indicating the created task details.
     */
    private static String createTask(Task task) {
        assert task != null : "Task should not be null when creating";

        previousTask = task;
        previousCommand = determineTaskCommand(task);
        return Task.createTaskMsg(task);
    }

    /**
     * Determines the corresponding user command for a given task.
     *
     * @param task The task for which the command is being determined.
     * @return The corresponding UserCommand (DEADLINE, EVENT, TODO).
     */
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
