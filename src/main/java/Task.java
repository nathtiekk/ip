import java.util.ArrayList;

class Task {
    private static ArrayList<Task> userTasks = new ArrayList<Task>();
    protected String description;
    protected boolean isDone;
    private static int counter = 0;
    protected int key;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        Task.counter++;
        this.key = Task.counter;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public static void listUserTask() {
        System.out.println("____________________________________________________________");
        System.out.println("Here are the tasks in your list:");
        for(Task userTask: Task.userTasks) {
            System.out.println(userTask.key + ".[" + userTask.getStatusIcon() + "] " + userTask.description);
        }
        System.out.println("____________________________________________________________");
    }

    public static void addUserTask(String userTask) {
        Task createdTask = new Task(userTask);
        Task.userTasks.add(createdTask);
        System.out.println("____________________________________________________________");
        System.out.println("added: " + createdTask.description);
        System.out.println("____________________________________________________________");
    }

    public static void markUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = true;
        System.out.println("____________________________________________________________");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("[X] " + currentTask.description);
        System.out.println("____________________________________________________________");
    }

    public static void unmarkUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = false;
        System.out.println("____________________________________________________________");
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("[ ] " + currentTask.description);
        System.out.println("____________________________________________________________");
    }


}
