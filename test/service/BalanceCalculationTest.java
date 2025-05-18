package service;

import model.LogEntry;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BalanceCalculationTest {

    private final LogFileSaver saver = new LogFileSaver();

    @Test
    public void testBalanceCalculation() {


        List<LogEntry> logs = Arrays.asList(
                createLog("balance inquiry", 1000.0),
                createLog("transferred", 200.0),
                createLog("received", 150.0),
                createLog("withdrew", 50.0),
                createLog("balance inquiry", 500.0)
        );

        double balance = saver.calculateBalance(logs);
        assertEquals(900.0, balance, 0.001);
    }

    private LogEntry createLog(String operation, double amount) {
        return new LogEntry(
                LocalDateTime.now(),
                "testUser",
                LogEntry.Operation.valueOf(operation.toUpperCase().replace(" ", "_")),
                amount,
                null,
                "[test] raw line"
        );
    }
}
