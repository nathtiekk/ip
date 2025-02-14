package kif;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Functions like an abstract class;
 * Has three subclasses: Todo, Deadline, and Event.
 */
class Task {
    private static final ArrayList<Task> userTasks = new ArrayList<>();
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.type = TaskType.TODO;
    }

    public enum TaskType {TODO, DEADLINE, EVENT}

    public static void addTaskToList(Task task) {
        userTasks.add(task);
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
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    /**
     * Prints the number of task the user currently has thus far.
     */
    public static void printTotalTasks() {
        System.out.println("Now you have " + Task.userTasks.size() + " tasks in the list.");
    }

    /**
     * Numbers and list all the tasks a user has.
     */
    public static void listUserTask() {
        System.out.println("____________________________________________________________");
        System.out.println("Here are the tasks in your list:");
        int counter = 1;
        for (Task task: userTasks) {
            System.out.println(counter + "." + task);
            counter++;
        }
        System.out.println("____________________________________________________________");
    }

    /**
     * Marks the task specified by the user as done
     * before showing a success message and the details of the marked task.
     *
     * @param key The position of the Task in the list from the user's perspective.
     */
    public static void markUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = true;
        Storage.editTaskTxt(key, Kif.UserCommand.MARK);

        System.out.println("____________________________________________________________");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(currentTask);
        System.out.println("____________________________________________________________");
    }

    /**
     * Unmarks the task specified by the user as undone
     * before showing a success message and the details of the unmarked task.
     *
     * @param key The position of the Task in the list from the user's perspective.
     */
    public static void unmarkUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = false;
        Storage.editTaskTxt(key, Kif.UserCommand.UNMARK);

        System.out.println("____________________________________________________________");
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(currentTask);
        System.out.println("____________________________________________________________");
    }

    /**
     * Deletes the task specified by the user before showing a success message,
     * and the details of the deleted task.
     *
     * @param key The position of the Task in the list from the user's perspective.
     */
    public static void delete(int key) {
        Task currentTask = userTasks.get(key - 1);
        userTasks.remove(key - 1);
        Storage.editTaskTxt(key, Kif.UserCommand.DELETE);

        System.out.println("____________________________________________________________");
        System.out.println("Noted. I've removed this task:");
        System.out.println(currentTask);
        Task.printTotalTasks();
        System.out.println("____________________________________________________________");
    }

    /**
     * Represents a Task created by the user that has a deadline.
     * Has an additional attribute, by, to store the dateline set by the user.
     */
    public static class Deadline extends Task {
        protected LocalDate by;

        public Deadline(String description, String by) throws KifException {
            super(description);
            this.by = Parser.parseDate(by);
            this.type = TaskType.DEADLINE;
        }

        public static void createDeadline(String description) throws KifException {
            String[] information = Parser.parseDeadlineTask(description);
            Task deadlineTask;
            deadlineTask = new Deadline(information[0], information[1]);
            Storage.writeTask(deadlineTask);

            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            System.out.println(deadlineTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        @Override
        public String toString() {
            return "[D]" + super.toString()
                    + "(by: " + Parser.parseDateToString(this.by) + ")";
        }
    }

    /**
     * Represents a Task created by the user that has a start and end date or time.
     */
    public static class Event extends Task {
        protected String start;
        protected String end;

        public Event(String description, String start, String end) {
            super(description);
            this.start = start;
            this.end = end;
            this.type = TaskType.EVENT;
        }

        public static void createEvent(String description) {
            String[] information = Parser.parseEventTask(description);
            Task eventTask = new Event(information[0], information[1], information[2]);
            Storage.writeTask(eventTask);

            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            System.out.println(eventTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        @Override
        public String toString() {
            return "[E]" + super.toString()
                    + "(from: " + start + " to:" + end + ")";
        }
    }

    /**
     * Represents a Task created by the user.
     */
    public static class ToDo extends Task {

        public ToDo(String description) {
            super(description);
            this.type = TaskType.TODO;
        }

        public static void createToDo(String description) throws KifException {
            Task toDoTask = new ToDo(Parser.parseToDoTask(description));
            Storage.writeTask(toDoTask);

            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            System.out.println(toDoTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }
}
