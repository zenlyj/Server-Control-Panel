package Logic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {
    private static final String incorrectIPFormatMessage = "Please make sure that the ip address follows the format xxx.xxx.xxx.xxx";
    private static final String invalidIPMessage = "Please make sure that the input ip address is made of integers (1-255) separated by '.'";
    private static final String fileNotFoundMessage = "File cannot be found!";
    private static final String incorrectEntryMessage = "Please make sure that all required fields are filled!";

    public static String parseIPAddress(String input) throws ParserException {
        input = input.strip();
        String[] tokens = input.split("\\.");
        if (tokens.length != 4) {
            // incorrect format
            throw new ParserException(incorrectIPFormatMessage);
        }
        try {
            for (int i = 0; i < 4; i++) {
                String segment = tokens[i];
                int num = Integer.parseInt(segment);
                if (num < 0 || num > 255) {
                    throw new ParserException(invalidIPMessage);
                }
            }
        } catch (NumberFormatException ex) {
            throw new ParserException(invalidIPMessage);
        }
        return input;
    }

    public static List<List<String>> parseCSV(File importFile) throws ParserException {
        List<List<String>> serversToAdd = new ArrayList<>();
        try {
            Scanner sc = new Scanner(importFile);
            while (sc.hasNextLine()) {
                String serverInfo = sc.nextLine();
                String[] tokensComma = serverInfo.split(",");
                String[] tokensSemi = serverInfo.split(";");
                if (tokensComma.length < 4 && tokensSemi.length < 4) {
                    throw new ParserException(incorrectEntryMessage);
                }
                String[] tokens = tokensComma.length == 4 ? tokensComma : tokensSemi;
                String ipAddress = parseIPAddress(tokens[3]);
                tokens[3] = ipAddress;
                serversToAdd.add(Arrays.asList(tokens));
            }
        } catch (FileNotFoundException ex) {
            throw new ParserException(fileNotFoundMessage);
        }
        return serversToAdd;
    }

    public static LocalDateTime parseDateTime(String dateTime) throws NumberFormatException, ArrayIndexOutOfBoundsException, DateTimeParseException {
        String[] tokens = dateTime.split(" ");
        int year;
        int month;
        int day;
        int hour;
        int minute;
        int second;
        String yearInput = tokens[0];
        String timeInput = tokens[1];

        String[] yearTokens = yearInput.split("/");
        month = Integer.parseInt(yearTokens[0]);
        day = Integer.parseInt(yearTokens[1]);
        year = Integer.parseInt(yearTokens[2]);

        String[] timeTokens = timeInput.split(":");
        hour = Integer.parseInt(timeTokens[0]);
        minute = Integer.parseInt(timeTokens[1]);
        second = Integer.parseInt(timeTokens[2]);

        return LocalDateTime.of(year, month, day, hour, minute, second);
    }
}
