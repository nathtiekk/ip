package kif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Storage {

    private static final String FILE_PATH = "tasks.txt";
    public static final String KEYWORD = "kifReservedKeyword";

    /**
     * Loads user tasks from the saved file.
     */
    public static void initialiseUserTasks() {
        File file = new File(FILE_PATH);

        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                parseAndAddTask(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println("No saved task file.");
        } catch (KifException e) {
            System.err.println("Error reading task file: " + e.getMessage());
        }
    }

    private static void parseAndAddTask(String line) throws KifException {
        String[] details = line.split(KEYWORD);
        Task task;

        switch (details.length) {
        case 2 -> task = new Task.ToDo(details[1]);
        case 3 -> task = new Task.Deadline(details[1], details[2]);
        case 4 -> task = new Task.Event(details[1], details[2], details[3]);
        default -> throw new KifException("Invalid task format: " + line);
        }

        task.isDone = Boolean.parseBoolean(details[0].trim());
        Task.addTask(task);
    }

    /**
     * Edits a specific task in the file (mark, unmark, or delete).
     */
    public static void editTaskTxt(int lineNumber, Kif.UserCommand operation) {
        List<String> lines = readAllLines();

        if (lineNumber < 1 || lineNumber > lines.size()) {
            throw new IndexOutOfBoundsException("Invalid line number");
        }

        switch (operation) {
        case MARK -> lines.set(lineNumber - 1, lines.get(lineNumber - 1).replaceFirst("false", "true"));
        case UNMARK -> lines.set(lineNumber - 1, lines.get(lineNumber - 1).replaceFirst("true", "false"));
        case DELETE -> lines.remove(lineNumber - 1);
        }

        writeAllLines(lines);
    }

    /**
     * Reads all lines from the task file.
     */
    private static List<String> readAllLines() {
        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Task file not found.");
        }
        return lines;
    }

    /**
     * Writes all lines back to the task file (used for editing and deleting tasks).
     */
    private static void writeAllLines(List<String> lines) {
        try (FileWriter fw = new FileWriter(FILE_PATH, false)) {
            for (String line : lines) {
                fw.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating task file.", e);
        }
    }

    /**
     * Writes a new task to the file.
     */
    public static void writeTask(Object t) {
        Task task = (Task) t;
        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(formatTask(task) + System.lineSeparator());
            Task.addTask(task);
        } catch (IOException e) {
            throw new RuntimeException("Error writing task to file.", e);
        }
    }

    private static String formatTask(Task task) {
        return switch (task.type) {
            case TODO -> task.isDone + KEYWORD + task.description;
            case DEADLINE -> {
                Task.Deadline deadline = (Task.Deadline) task;
                yield deadline.isDone + KEYWORD + deadline.description + KEYWORD + deadline.getDeadline();
            }
            case EVENT -> {
                Task.Event event = (Task.Event) task;
                yield event.isDone + KEYWORD + event.description + KEYWORD + event.getStart() + KEYWORD + event.getEnd();
            }
        };
    }
}
