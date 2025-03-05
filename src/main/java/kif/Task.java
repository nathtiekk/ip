package kif;

import static kif.Ui.formatMessage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Functions like an abstract class;
 * Has three subclasses: Todo, Deadline, and Event.
 */
abstract class Task {
    private static final List<Task> userTasks = new ArrayList<>();
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    /**
     * Constructs a new {@code Task}.
     *
     * @param description The task description.
     * @param type The type of the task (TODO, DEADLINE, or EVENT).
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return The total task count.
     */
    public static int getTotalTasks() {
        return userTasks.size();
    }

    /**
     * Clears all tasks from the list for the purpose of unit testing.
     */
    public static void clearTasks() {
        userTasks.clear();
    }

    /**
     * Represents the types of tasks available.
     */
    public enum TaskType {TODO, DEADLINE, EVENT}

    public static void addTask(Task task) {
        userTasks.add(task);
    }

    /**
     * Creates a formatted message confirming task addition.
     *
     * @param task The task that was added.
     * @return A formatted confirmation message.
     */
    public static String createTaskMsg(Task task) {
        return formatMessage("Got it. I've added this task:", task.toString(), printTotalTasks());
    }

    /**
     * Returns "X" if the Task is done, or " " otherwise.
     *
     * @return String "X" or " ".
     */
    public String getStatusIcon() {
        // mark done task with X
        return (isDone ? "X" : " ");
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), description);
    }

    /**
     * Prints the number of task the user currently has thus far.
     */
    public static String printTotalTasks() {
        return "Now you have " + userTasks.size() + " tasks in the list.";
    }

    /**
     * Number and list all the tasks a user has.
     */
    public static String listUserTask() {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < userTasks.size(); i++) {
            response.append(System.lineSeparator()).append(i + 1).append(". ").append(userTasks.get(i));
        }
        return formatMessage(response.toString());
    }

    /**
     * Marks the task specified by the user as done
     * before showing a success message and the details of the marked task.
     *
     * @param index The position of the Task in the list from the user's perspective.
     */
    public static String markTask(int index) {
        Task task = getTask(index);
        task.isDone = true;
        Storage.editTaskTxt(index, Kif.UserCommand.MARK);
        return formatMessage("Nice! I've marked this task as done:", task.toString());
    }

    /**
     * Unmarks the task specified by the user as undone
     * before showing a success message and the details of the unmarked task.
     *
     * @param index The position of the Task in the list from the user's perspective.
     */
    public static String unmarkTask(int index) {
        Task task = getTask(index);
        task.isDone = false;
        Storage.editTaskTxt(index, Kif.UserCommand.UNMARK);
        return formatMessage("OK, I've marked this task as not done yet:", task.toString());
    }

    /**
     * Deletes the task specified by the user before showing a success message,
     * and the details of the deleted task.
     *
     * @param index The position of the Task in the list from the user's perspective.
     */
    public static String deleteTask(int index) {
        Task task = getTask(index);
        userTasks.remove(index - 1);
        Storage.editTaskTxt(index, Kif.UserCommand.DELETE);
        return formatMessage("Noted. I've removed this task:", task.toString(), printTotalTasks());
    }

    /**
     * Retrieves a task at the specified index.
     *
     * @param index The task index (1-based).
     * @return The task at the given index.
     */
    public static Task getTask(int index) {
        return userTasks.get(index - 1);
    }

    /**
     * Retrieves the index of a specific task.
     *
     * @param t The task to find.
     * @return The 1-based index of the task, or -1 if not found.
     */
    public static int getTaskIndex(Task t) {
        for (int index = 0; index < userTasks.size(); index++) {
            if (userTasks.get(index).equals(t)) {
                return index + 1;
            }
        }
        // not found
        return -1;
    }

    /**
     * Represents a Task created by the user that has a deadline.
     * Has an additional attribute, by, to store the dateline set by the user.
     */
    public static class Deadline extends Task {
        private final LocalDate by;


        public Deadline(String description, String by) throws KifException {
            super(description, TaskType.DEADLINE);
            this.by = Parser.parseDate(by);
        }

        public LocalDate getDeadline() {
            return this.by;
        }

        public static Deadline create(String description) throws KifException {
            String[] details = Parser.extractDeadlineDetails(description);
            Deadline deadlineTask = new Deadline(details[0], details[1]);
            Storage.writeTask(deadlineTask);
            return deadlineTask;
        }

        public static Deadline create(Deadline task) throws KifException {
            String byDateString = String.valueOf(task.by);
            Deadline deadlineTask = new Deadline(task.description, byDateString);
            Storage.writeTask(deadlineTask);
            return deadlineTask;
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + " (by: " + Parser.formatDate(by) + ")";
        }
    }

    /**
     * Represents a Task created by the user that has a start and end date or time.
     */
    public static class Event extends Task {
        private final String start;
        private final String end;

        public Event(String description, String start, String end) {
            super(description, TaskType.EVENT);
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return this.start;
        }

        public String getEnd() {
            return this.end;
        }

        public static Event create(String description) {
            String[] details = Parser.extractEventDetails(description);
            Event eventTask = new Event(details[0], details[1], details[2]);
            Storage.writeTask(eventTask);
            return eventTask;
        }

        public static Event create(Event task) {
            Event eventTask = new Event(task.description, task.start, task.end);
            Storage.writeTask(eventTask);
            return eventTask;
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
        }
    }

    /**
     * Represents a Task created by the user.
     */
    public static class ToDo extends Task {

        public ToDo(String description) {
            super(description, TaskType.TODO);
        }

        public static ToDo create(String description) throws KifException {
            ToDo toDoTask = new ToDo(Parser.extractToDoDescription(description));
            Storage.writeTask(toDoTask);
            return toDoTask;
        }

        public static ToDo create(ToDo task) {
            ToDo toDoTask = new ToDo(task.description);
            Storage.writeTask(toDoTask);
            return toDoTask;
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }
}