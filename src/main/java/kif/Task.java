package kif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Scanner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.ArrayList;

/**
 * Functions like an abstract class;
 * Has three subclasses: Todo, Deadline, and Event.
 */
class Task {
    private static final ArrayList<Task> userTasks = new ArrayList<>();
    protected String description;
    protected boolean isDone;
    private static final String FILE_PATH = "tasks.txt";
    protected TaskType type;
    private static final String KEYWORD = "kifReservedKeyword";

    public Task(String description) {
        this.description = description;
        this.isDone = false;
        this.type = TaskType.TODO;
    }

    public enum TaskType {TODO, DEADLINE, EVENT}

    /**
     * Loads up all the tasks saved previously by the user
     * and catches FileNotFoundException, and KifException when file is not found,
     * and when the file contents happen to not be readable respectively.
     */
    public static void initialiseUserTasks() {
        try {
            File f = new File(FILE_PATH);
            Scanner s = new Scanner(f);
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
        } catch (KifException e) {
            System.out.println(e.getMessage());
        }
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

    private static void editTaskTxt(int lineNumber, Kif.UserCommand operation) {
        ArrayList<String> preItems = new ArrayList<>();
        ArrayList<String> postItems = new ArrayList<>();
        ArrayList<String> editLines = new ArrayList<>();
        try {
            File f = new File(FILE_PATH);
            Scanner s = new Scanner(f);
            int counter = 1;
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (counter == lineNumber) {
                    editLines.add(line);
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

        FileWriter fw;
        try {
            fw = new FileWriter(FILE_PATH, true);
            for (String line : preItems) {
                fw.write("\n" + line);
            }

            String newLine = "";
            switch (operation) {
            case MARK:
                newLine = editLines.get(0).replaceFirst("false", "true");
                fw.write("\n" + newLine);
                break;
            case UNMARK:
                newLine = editLines.get(0).replaceFirst("true", "false");
                fw.write("\n" + newLine);
                break;
            case DELETE:
                fw.write(newLine);
                break;
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
        FileWriter fw;
        try {
            fw = new FileWriter(FILE_PATH, true);
            if (task.type == TaskType.TODO) {
                fw.write("\n" + task.isDone + Task.KEYWORD);
            } else if (task.type == TaskType.EVENT) {
                if (task instanceof Event eventTask) {
                    fw.write("\n" + eventTask.isDone + Task.KEYWORD + eventTask.start
                            + Task.KEYWORD + eventTask.end);
                }
            } else if (task.type == TaskType.DEADLINE) {
                if (task instanceof Deadline deadlineTask) {
                    fw.write("\n" + deadlineTask.isDone + Task.KEYWORD +
                            deadlineTask.by.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            }
            Task.userTasks.add(task);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        editTaskTxt(key, Kif.UserCommand.MARK);

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
        editTaskTxt(key, Kif.UserCommand.UNMARK);

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
        editTaskTxt(key, Kif.UserCommand.DELETE);

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

            try {
                this.by = LocalDate.parse(by.trim());
            } catch (DateTimeParseException e) {
                String errorMessage =
                        """
                        ____________________________________________________________
                        Kif: Please format "/by" value to yyyy-MM-dd and try again
                        ____________________________________________________________""";

                throw new KifException(errorMessage);
            }
            this.type = TaskType.DEADLINE;
        }

        public static void createDeadline(String description) throws KifException {
            String[] information = description.split("/by");
            Task deadlineTask;
            deadlineTask = new Deadline(information[0].replace("deadline ", ""),
                    information[1]);
            Task.writeTask(deadlineTask);

            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            System.out.println(deadlineTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        @Override
        public String toString() {
            return "[D]" + super.toString()
                    + "(by: " + by.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
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
            String[] information = description.split("/from | /to");
            Task eventTask = new Event(information[0].replace("event ", ""),
                    information[1], information[2]);
            Task.writeTask(eventTask);

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
            Task toDoTask = new ToDo(extractDetails(description));
            Task.writeTask(toDoTask);

            System.out.println("____________________________________________________________");
            System.out.println("Got it. I've added this task:");
            System.out.println(toDoTask);
            Task.printTotalTasks();
            System.out.println("____________________________________________________________");
        }

        private static String extractDetails(String description) throws KifException {
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
