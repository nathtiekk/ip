import java.util.ArrayList;

class Task {
    private static final ArrayList<Task> userTasks = new ArrayList<>();
    protected String description;
    protected boolean isDone;
    protected int key;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        Task.userTasks.add(this);
        this.key = Task.userTasks.size();
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.description;
    }

    public static void printTotalTasks() {
        System.out.println("Now you have " + Task.userTasks.size() + " tasks in the list.");
    }

    public static void listUserTask() {
        System.out.println("____________________________________________________________");
        System.out.println("Here are the tasks in your list:");
        for(Task userTask: Task.userTasks) {
            System.out.println(userTask.key + "." + userTask);
        }
        System.out.println("____________________________________________________________");
    }

    public static void markUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = true;
        System.out.println("____________________________________________________________");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(currentTask);
        System.out.println("____________________________________________________________");
    }

    public static void unmarkUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = false;
        System.out.println("____________________________________________________________");
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(currentTask);
        System.out.println("____________________________________________________________");
    }

    public static void delete(int key) {
        Task currentTask = userTasks.get(key - 1);
        userTasks.remove(key - 1);
        System.out.println("____________________________________________________________");
        System.out.println("Noted. I've removed this task:");
        System.out.println(currentTask);
        Task.printTotalTasks();
        System.out.println("____________________________________________________________");
    }

    public static class Deadline extends Task {

        protected String by;

        public Deadline(String description, String by) {
            super(description);
            this.by = by;
        }

        public static void createDeadline(String description) {
            String[] information = description.split("/by");
            Task deadlineTask = new Deadline(information[0].replace("deadline ", ""), information[1]);
            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            System.out.println(deadlineTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        @Override
        public String toString() {
            return "[D]" + super.toString() + "(by:" + by + ")";
        }
    }

    public static class Event extends Task {

        protected String start;
        protected String end;

        public Event(String description, String start, String end) {
            super(description);
            this.start = start;
            this.end = end;
        }

        public static void createEvent(String description) {
            String[] information = description.split("/from | /to");
            Task eventTask = new Event(information[0].replace("event ", ""), information[1], information[2]);
            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            System.out.println(eventTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        @Override
        public String toString() {
            return "[E]" + super.toString() + "(from: " + start + " to:" + end + ")";
        }
    }

    public static class ToDo extends Task {

        public ToDo(String description) {
            super(description);
        }

        public static void createToDo(String description) throws KifException {
            String cleanInput = description.replace("todo", "");
            if(cleanInput.trim().isEmpty()) {
                throw new KifException(
                        """
                        ____________________________________________________________
                        OOPS!!! The description of a todo cannot be empty.
                        ____________________________________________________________"""
                );
            }

            Task toDoTask = new ToDo(cleanInput.trim());
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
