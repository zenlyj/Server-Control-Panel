package Logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {
    private static final String incorrectIPFormatMessage = "Please make sure that the ip address follows the format xxx.xxx.xxx.xxx\n";
    private static final String invalidIPMessage = "Please make sure that the input ip address is made of integers (1-255) separated by '.'\n";
    private static final String fileNotFoundMessage = "File cannot be found!\n";
    private static final String incorrectEntryMessage = "Please make sure that all required fields are filled!\n";

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
                String[] tokens = serverInfo.split(",");
                if (tokens.length < 4) {
                    throw new ParserException(incorrectEntryMessage);
                }
                String ipAddress = parseIPAddress(tokens[3]);
                tokens[3] = ipAddress;
                serversToAdd.add(Arrays.asList(tokens));
            }
        } catch (FileNotFoundException ex) {
            throw new ParserException(fileNotFoundMessage);
        }
        return serversToAdd;
    }
}
