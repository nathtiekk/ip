package kif;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {

    public static LocalDate parseDate(String inputDate) throws KifException {
        LocalDate deadline;
        try {
            deadline = LocalDate.parse(inputDate.trim());
        } catch (DateTimeParseException e) {
            String errorMessage =
                    """
                            ____________________________________________________________
                            Kif: Please format "/by" value to yyyy-MM-dd and try again
                            ____________________________________________________________""";

            throw new KifException(errorMessage);
        }
        return deadline;
    }

    public static String[] parseUserInput(String input) {
        return input.split(" ");
    }

    public static String parseDateToString(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    public static String[] parseDeadlineTask(String userInput) {
        String[] information = userInput.split("/by");
        information[0] = information[0].replace("deadline ", "");
        return information;
    }

    public static String[] parseEventTask(String userInput) {
        String[] information = userInput.split("/from | /to");
        information[0] = information[0].replace("event ", "");
        return information;
    }

    public static String parseToDoTask(String userInput) throws KifException {
        String parsed = userInput.replace("todo", "");

        if(parsed.trim().isEmpty()) {
            throw new KifException(
                    """
                    ____________________________________________________________
                    OOPS!!! The description of a todo cannot be empty.
                    ____________________________________________________________"""
            );
        }
        return parsed.trim();
    }
}
