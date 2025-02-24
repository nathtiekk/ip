package kif;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {

    private static final String DATE_FORMAT_ERROR =
            """
            ____________________________________________________________
            Kif: Please format "/by" value to yyyy-MM-dd and try again
            ____________________________________________________________""";

    private static final String EMPTY_TODO_ERROR =
            """
            ____________________________________________________________
            OOPS!!! The description of a todo cannot be empty.
            ____________________________________________________________""";

    private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Parses a date string in "yyyy-MM-dd" format to a LocalDate object.
     * @param inputDate The date string to parse.
     * @return The parsed LocalDate object.
     * @throws KifException If the input format is invalid.
     */
    public static LocalDate parseDate(String inputDate) throws KifException {
        try {
            return LocalDate.parse(inputDate.trim());
        } catch (DateTimeParseException e) {
            throw new KifException(DATE_FORMAT_ERROR);
        }
    }

    /**
     * Splits user input into individual words.
     * @param input The user input string.
     * @return An array of words from the input.
     */
    public static String[] splitUserInput(String input) {
        return input.trim().split("\\s+");
    }

    /**
     * Converts a LocalDate object to a formatted string (e.g., "Jan 01 2024").
     * @param date The LocalDate object to format.
     * @return The formatted date string.
     */
    public static String formatDate(LocalDate date) {
        return date.format(OUTPUT_DATE_FORMATTER);
    }

    /**
     * Parses a deadline task input into description and deadline.
     * @param userInput The full user input string.
     * @return A string array containing the task description and deadline.
     */
    public static String[] extractDeadlineDetails(String userInput) {
        String[] parts = userInput.split(" /by ", 2);
        parts[0] = parts[0].replaceFirst("^deadline ", "").trim();
        return parts;
    }

    /**
     * Parses an event task input into description, start time, and end time.
     * @param userInput The full user input string.
     * @return A string array containing the task description, start time, and end time.
     */
    public static String[] extractEventDetails(String userInput) {
        String[] parts = userInput.split(" /from | /to ", 3);
        parts[0] = parts[0].replaceFirst("^event ", "").trim();
        return parts;
    }

    /**
     * Extracts the description from a ToDo task input.
     * @param userInput The full user input string.
     * @return The task description.
     * @throws KifException If the description is empty.
     */
    public static String extractToDoDescription(String userInput) throws KifException {
        String description = userInput.replaceFirst("^todo", "").trim();
        if (description.isEmpty()) {
            throw new KifException(EMPTY_TODO_ERROR);
        }
        return description;
    }
}
