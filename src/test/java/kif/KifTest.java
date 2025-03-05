package kif;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KifTest {

    @BeforeEach
    void setUp() {
        // Reset static variables before each test to ensure test independence
        Kif.previousCommand = null;
        Kif.previousTask = null;
        Task.clearTasks(); // Ensure task list is empty before each test
    }

    @Test
    void undoPrevCommand_ShouldUndoUnmarkCommand() throws KifException {
        Task task = new Task.ToDo("Sample Task");
        Task.addTask(task);
        Kif.previousTask = task;
        Kif.previousCommand = Kif.UserCommand.UNMARK;
        task.isDone = false; // Simulating the task being unmarked

        String result = Kif.undoPrevCommand();

        assertTrue(task.isDone, "Undoing unmark should mark the task.");
        assertEquals(Task.markTask(Task.getTaskIndex(task)), result, "Undo should match expected mark message.");
    }

    @Test
    void undoPrevCommand_ShouldUndoDeadlineCommand() throws KifException {
        Kif.previousTask = Task.Deadline.create("deadline Submit report /by 2025-03-03");
        Kif.previousCommand = Kif.UserCommand.DEADLINE;

       Kif.undoPrevCommand();

        assertEquals(0, Task.getTotalTasks(), "Deleting a task should remove it.");
    }

    @Test
    void undoPrevCommand_ShouldUndoDeleteCommand() throws KifException {
        Task task = new Task.ToDo("Deleted Task");
        Task.addTask(task);
        int taskIndex = Task.getTaskIndex(task);
        Task.deleteTask(taskIndex); // Simulating deletion
        Kif.previousTask = task;
        Kif.previousCommand = Kif.UserCommand.DELETE;

        String result = Kif.undoPrevCommand();

        assertEquals(Task.createTaskMsg(task), result, "Undo should match expected restoration message.");
    }

    @Test
    void undoPrevCommand_ShouldReturnCannotUndoMessage_OnInvalidCommand() throws KifException {
        Kif.previousCommand = Kif.UserCommand.BYE;

        String result = Kif.undoPrevCommand();

        assertEquals(Ui.getCannotUndoMessage(), result, "Undoing an invalid command should return a failure message.");
    }

    @Test
    void handleCommand_ShouldListTasks() throws KifException {
        Task.addTask(new Task.ToDo("Task 1"));
        Task.addTask(new Task.Deadline("deadline Project", "2025-03-03"));

        String result = Kif.handleCommand(Kif.UserCommand.LIST, new String[]{"LIST"}, "list");

        assertEquals(Task.listUserTask(), result, "Listing tasks should match the expected output.");
    }

    @Test
    void handleCommand_ShouldMarkTask() throws KifException {
        Task task = new Task.ToDo("Task to mark");
        Task.addTask(task);
        int taskIndex = Task.getTaskIndex(task);

        String result = Kif.handleCommand(Kif.UserCommand.MARK, new String[]{
                "MARK", String.valueOf(taskIndex)}, "mark " + taskIndex);

        assertTrue(task.isDone, "Task should be marked as done.");
        assertEquals(Task.markTask(taskIndex), result, "Marking a task should return the correct message.");
    }

    @Test
    void handleCommand_ShouldUnmarkTask() throws KifException {
        Task task = new Task.ToDo("Task to unmark");
        Task.addTask(task);
        int taskIndex = Task.getTaskIndex(task);
        task.isDone = true; // Mark the task first

        String result = Kif.handleCommand(Kif.UserCommand.UNMARK, new String[]{"UNMARK", String.valueOf(taskIndex)}, "unmark " + taskIndex);

        assertFalse(task.isDone, "Task should be unmarked (not done).");
        assertEquals(Task.unmarkTask(taskIndex), result, "Unmarking a task should return the correct message.");
    }

    @Test
    void handleCommand_ShouldCreateDeadlineTask() throws KifException {
        String userMessage = "deadline Submit project /by 2025-06-06";
        String[] splitMessage = userMessage.split(" ");

        String result = Kif.handleCommand(Kif.UserCommand.DEADLINE, splitMessage, userMessage);
        Task lastTask = Task.getTask(Task.getTotalTasks());

        assertNotNull(lastTask, "Deadline task should be created.");
        assertInstanceOf(Task.Deadline.class, lastTask, "Created task should be a deadline task.");
        assertEquals(Task.createTaskMsg(lastTask), result, "Task creation message should match expected output.");
    }

    @Test
    void handleCommand_ShouldCreateEventTask() throws KifException {
        String userMessage = "event Birthday Party /from Monday /to Sunday";
        String[] splitMessage = userMessage.split(" ");

        String result = Kif.handleCommand(Kif.UserCommand.EVENT, splitMessage, userMessage);
        Task lastTask = Task.getTask(Task.getTotalTasks());

        assertNotNull(lastTask, "Event task should be created.");
        assertInstanceOf(Task.Event.class, lastTask, "Created task should be an event task.");
        assertEquals(Task.createTaskMsg(lastTask), result, "Task creation message should match expected output.");
    }

    @Test
    void handleCommand_ShouldDeleteTask() throws KifException {
        Task task = new Task.ToDo("Task to delete");
        Task.addTask(task);
        int taskIndex = Task.getTaskIndex(task);

        Kif.handleCommand(Kif.UserCommand.DELETE, new String[]{
                "DELETE", String.valueOf(taskIndex)}, "delete " + taskIndex);

        assertEquals(0, Task.getTotalTasks(), "Deleting a task should remove it.");
    }

    @Test
    void handleCommand_ShouldExitApplication() throws KifException {
        String result = Kif.handleCommand(Kif.UserCommand.BYE, new String[]{"BYE"}, "bye");

        assertEquals(Ui.getGoodbyeMessage(), result, "Exiting should return the goodbye message.");
    }

    @Test
    void handleCommand_ShouldUndoLastAction() throws KifException {
        Kif.previousCommand = Kif.UserCommand.TODO;

        String result = Kif.handleCommand(Kif.UserCommand.UNDO, new String[]{"UNDO"}, "cannot Redo");

        assertEquals(Ui.getCannotUndoMessage(), result, "Undo message should match expected output.");
    }
}