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
    public static String printTotalTasks() {
        return "Now you have " + Task.userTasks.size() + " tasks in the list.";
    }

    /**
     * Numbers and list all the tasks a user has.
     */
    public static String listUserTask() {
        StringBuilder response = new StringBuilder();
        response.append("____________________________________________________________")
                .append(System.lineSeparator());
        response.append("Here are the tasks in your list:").append(System.lineSeparator());;
        int counter = 1;
        for (Task task: userTasks) {
            response.append(counter).append(".").append(task).append(System.lineSeparator());;
            counter++;
        }
        response.append("____________________________________________________________");
        return response.toString();
    }

    /**
     * Marks the task specified by the user as done
     * before showing a success message and the details of the marked task.
     *
     * @param key The position of the Task in the list from the user's perspective.
     */
    public static String markUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = true;
        Storage.editTaskTxt(key, Kif.UserCommand.MARK);

        return "____________________________________________________________" +
                System.lineSeparator() +
                "Nice! I've marked this task as done:" + System.lineSeparator() +
                currentTask + System.lineSeparator() +
                "____________________________________________________________";
    }

    /**
     * Unmarks the task specified by the user as undone
     * before showing a success message and the details of the unmarked task.
     *
     * @param key The position of the Task in the list from the user's perspective.
     */
    public static String unmarkUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = false;
        Storage.editTaskTxt(key, Kif.UserCommand.UNMARK);

        return "____________________________________________________________" +
                System.lineSeparator() +
                "OK, I've marked this task as not done yet:" + System.lineSeparator() +
                currentTask + System.lineSeparator() +
                "____________________________________________________________";
    }

    /**
     * Deletes the task specified by the user before showing a success message,
     * and the details of the deleted task.
     *
     * @param key The position of the Task in the list from the user's perspective.
     */
    public static String delete(int key) {
        Task currentTask = userTasks.get(key - 1);
        userTasks.remove(key - 1);
        Storage.editTaskTxt(key, Kif.UserCommand.DELETE);

        return "____________________________________________________________" +
                System.lineSeparator() +
                "Noted. I've removed this task:" + System.lineSeparator() +
                currentTask + System.lineSeparator() + Task.printTotalTasks() +
                System.lineSeparator() +
                "____________________________________________________________";
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

        public static String createDeadline(String description) throws KifException {
            String[] information = Parser.parseDeadlineTask(description);
            Task deadlineTask;
            deadlineTask = new Deadline(information[0], information[1]);
            Storage.writeTask(deadlineTask);

            return "____________________________________________________________" +
                    System.lineSeparator() +
                    "Got it. I've added this task:" + System.lineSeparator() +
                    deadlineTask + System.lineSeparator() + Task.printTotalTasks() +
                    System.lineSeparator() +
                    "____________________________________________________________";
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

        public static String createEvent(String description) {
            String[] information = Parser.parseEventTask(description);
            Task eventTask = new Event(information[0], information[1], information[2]);
            Storage.writeTask(eventTask);

            return "____________________________________________________________" +
                    System.lineSeparator() +
                    "Got it. I've added this task:" + System.lineSeparator() +
                    eventTask + System.lineSeparator() + Task.printTotalTasks() +
                    System.lineSeparator() +
                    "____________________________________________________________";
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

        public static String createToDo(String description) throws KifException {
            Task toDoTask = new ToDo(Parser.parseToDoTask(description));
            Storage.writeTask(toDoTask);

            return "____________________________________________________________" +
                    System.lineSeparator() +
                    "Got it. I've added this task:" + System.lineSeparator() +
                    toDoTask + System.lineSeparator() + Task.printTotalTasks() +
                    System.lineSeparator() +
                    "____________________________________________________________";
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }
}
