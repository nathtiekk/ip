package kif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private static final String FILE_PATH = "tasks.txt";
    private static final String KEYWORD = "kifReservedKeyword";
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
                    Task t = new Task.ToDo(description[1]);
                    t.isDone = Boolean.parseBoolean(description[0].trim());
                }
                else if (description.length == 4) {
                    Task t = new Task.Event(description[1], description[2], description[3]);
                    t.isDone = Boolean.parseBoolean(description[0].trim());
                }
                else if (description.length == 3) {
                    Task t = new Task.Deadline(description[1], description[2]);
                    t.isDone = Boolean.parseBoolean(description[0].trim());
                }
            }
        } catch (FileNotFoundException e) {
            //do nothing
        } catch (KifException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void editTaskTxt(int lineNumber, Kif.UserCommand operation) {
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
                } else {
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

    public static void writeTask(Object taskObject) {
        FileWriter fw;
        Task task = (Task) taskObject;
        try {
            fw = new FileWriter(FILE_PATH, true);
            if (task.type == Task.TaskType.TODO) {
                fw.write("\n" + task.isDone + KEYWORD);
            } else if (task.type == Task.TaskType.EVENT) {
                if (task instanceof Task.Event eventTask) {
                    fw.write("\n" + eventTask.isDone + KEYWORD + eventTask.start
                            + KEYWORD + eventTask.end);
                }
            } else if (task.type == Task.TaskType.DEADLINE) {
                if (task instanceof Task.Deadline deadlineTask) {
                    fw.write("\n" + deadlineTask.isDone + KEYWORD +
                            Parser.parseDateToString(deadlineTask.by));
                }
            }
            Task.addTaskToList(task);
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
