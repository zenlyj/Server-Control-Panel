package Logic;

public class Parser {
    private static final String incorrectIPFormatMessage = "Please make sure that the ip address follows the format xxx.xxx.xxx.xxx\n";
    private static final String invalidIPMessage = "Please make sure that the input ip address is made of integers (1-255) separated by '.'\n";


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
}
