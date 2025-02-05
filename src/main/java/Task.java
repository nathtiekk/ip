import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Task {
    private static final ArrayList<Task> userTasks = new ArrayList<>();
    protected String description;
    protected boolean isDone;
    private static final String FILEPATH = "tasks.txt";
    protected taskType type;
    private static final String KEYWORD = "kifReservedKeyword";

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        Task.userTasks.add(this);
        this.type = taskType.TODO;
    }

    public enum taskType {TODO, DEADLINE, EVENT;}

    public static void initialiseUserTasks () {
        try {
            File f = new File(FILEPATH); // create a File for the given file path
            Scanner s = new Scanner(f); // create a Scanner using the File as the source
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] description = line.split(KEYWORD);
                if (description.length == 2) {
                    Task t = new ToDo(description[1]);
                    t.isDone = Boolean.parseBoolean(description[0].trim());
                }
                else if (description.length == 4) {
                    Task t = new Event(description[1], description[2], description[3]);
                    t.isDone = Boolean.parseBoolean(description[0].trim());
                }
                else if (description.length == 3) {
                    Task t = new Deadline(description[1], description[2]);
                    t.isDone = Boolean.parseBoolean(description[0].trim());
                }
            }
        } catch (FileNotFoundException e) {
            //do nothing
        }
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

    private static void editTaskTXT (int lineNumber, Kif.UserCommand operation) {
        ArrayList<String> preItems = new ArrayList<>();
        ArrayList<String> postItems = new ArrayList<>();
        ArrayList<String> editLine = new ArrayList<>();
        try {
            File f = new File(FILEPATH); // create a File for the given file path
            Scanner s = new Scanner(f); // create a Scanner using the File as the source
            int counter = 1;
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (counter == lineNumber) {
                    editLine.add(line);
                }
                else if (counter < lineNumber) {
                    preItems.add(line);
                }
                else {
                    postItems.add(line);
                }
                counter++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FileWriter fw = null; // create a FileWriter in append mode
        try {
            fw = new FileWriter(FILEPATH, true);
            for (String line : preItems) {
                fw.write("\n" + line);
            }
            switch (operation) {
                case MARK -> {
                    String newLine = editLine.get(0).replaceFirst("false", "true");
                    fw.write("\n" + newLine);
                }
                case UNMARK -> {
                    String newLine = editLine.get(0).replaceFirst("true", "false");
                    fw.write("\n" + newLine);
                }
                case DELETE -> {
                    String newLine = "";
                    fw.write(newLine);
                }
            }
            for (String line : postItems) {
                fw.write("\n" + line);
            }
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeTask(Task task) {
        FileWriter fw = null; // create a FileWriter in append mode
        try {
            fw = new FileWriter(FILEPATH, true);
            if (task.type == taskType.TODO) {
                fw.write("\n" + task.isDone + Task.KEYWORD);
            } else if (task.type == taskType.EVENT) {
                if (task instanceof Event eventTask) {  // Ensure it's actually a Deadline
                    fw.write("\n" + eventTask.isDone + Task.KEYWORD + eventTask.start + Task.KEYWORD + eventTask.end);
                }
            } else if (task.type == taskType.DEADLINE) {
                if (task instanceof Deadline deadlineTask) {  // Ensure it's actually a Deadline
                    fw.write("\n" + deadlineTask.isDone + Task.KEYWORD + deadlineTask.by);
                }
            }
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void listUserTask() {
        System.out.println("____________________________________________________________");
        System.out.println("Here are the tasks in your list:");
        int counter = 1;
        for (Task task: userTasks) {
            System.out.println(Integer.toString(counter) + "." + task);
            counter++;
        }
        System.out.println("____________________________________________________________");
    }

    public static void markUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = true;
        editTaskTXT(key, Kif.UserCommand.MARK);
        System.out.println("____________________________________________________________");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(currentTask);
        System.out.println("____________________________________________________________");
    }

    public static void unmarkUserTask(int key) {
        Task currentTask = userTasks.get(key - 1);
        currentTask.isDone = false;
        editTaskTXT(key, Kif.UserCommand.UNMARK);
        System.out.println("____________________________________________________________");
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(currentTask);
        System.out.println("____________________________________________________________");
    }

    public static void delete(int key) {
        Task currentTask = userTasks.get(key - 1);
        userTasks.remove(key - 1);
        editTaskTXT(key, Kif.UserCommand.DELETE);
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
            this.type = taskType.DEADLINE;
        }

        public static void createDeadline(String description) {
            String[] information = description.split("/by");
            Task deadlineTask = new Deadline(information[0].replace("deadline ", ""), information[1]);
            Task.writeTask(deadlineTask);
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
            this.type = taskType.EVENT;
        }

        public static void createEvent(String description) {
            String[] information = description.split("/from | /to");
            Task eventTask = new Event(information[0].replace("event ", ""), information[1], information[2]);
            Task.writeTask(eventTask);
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
            this.type = taskType.TODO;
        }

        public static void createToDo(String description) throws KifException {
            Task toDoTask = new ToDo(extractDetails(description));
            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            Task.writeTask(toDoTask);
            System.out.println(toDoTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        private static String extractDetails (String description) throws KifException {
            String cleanInput = description.replace("todo", "");
            if(cleanInput.trim().isEmpty()) {
                throw new KifException(
                        """
                        ____________________________________________________________
                        OOPS!!! The description of a todo cannot be empty.
                        ____________________________________________________________"""
                );
            }
            return cleanInput.trim();
        }

        @Override
        public String toString() {
            return "[T]" + super.toString();
        }
    }
}
