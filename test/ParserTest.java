import Logic.util.Parser;
import Logic.util.ParserException;
import org.junit.Test;

import java.time.DateTimeException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
    private static final String incorrectIPFormatMessage = "Please make sure that the ip address follows the format xxx.xxx.xxx.xxx";
    private static final String invalidIPMessage = "Please make sure that the input ip address is made of integers (1-255) separated by '.'";

    @Test
    public void blankIPTest() {
        String ipAddress = "       ";
        ParserException ex = assertThrows(ParserException.class, () -> {
            Parser.parseIPAddress(ipAddress);
        });
        assertEquals(incorrectIPFormatMessage, ex.getMessage());
    }

    @Test
    public void invalidIntegerIPTest() {
        String ipAddress = "172.3.4.1000";
        ParserException ex = assertThrows(ParserException.class, () -> {
            Parser.parseIPAddress(ipAddress);
        });
        assertEquals(invalidIPMessage, ex.getMessage());
    }

    @Test
    public void nonIntegerIPTest() {
        String ipAddress = "172.3.4.abc";
        ParserException ex = assertThrows(ParserException.class, () -> {
            Parser.parseIPAddress(ipAddress);
        });
        assertEquals(invalidIPMessage, ex.getMessage());
    }

    @Test
    public void validDateTimeTest() {
        String dateTime = "12/17/2021 17:11:55";
        LocalDateTime dt = Parser.parseDateTime(dateTime);
        assertEquals(dt, LocalDateTime.of(2021, 12, 17, 17, 11, 55));
    }

    @Test
    public void invalidMonthParseTest() {
        String dateTime = "17/12/2021 17:11:55";
        DateTimeException ex = assertThrows(DateTimeException.class, () -> {
           Parser.parseDateTime(dateTime);
        });
        assertEquals(ex.getMessage(), "Invalid value for MonthOfYear (valid values 1 - 12): 17");
    }
}
