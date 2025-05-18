package parser;

import static org.junit.jupiter.api.Assertions.*;

import model.LogEntry;
import model.LogEntry.Operation;
import org.junit.jupiter.api.Test;
import parser.LogParser;

import static org.junit.jupiter.api.Assertions.*;

public class LogParserTest{
    private final String TEST_LINE_WITHDRAW =
            "[2025-05-10 09:06:00] user001 withdrew 200.75";
    private final String TEST_LINE_BALANCE =
            "[2025-05-10 09:00:22] user005 balance inquiry 1500.00";

    @Test       // Проверяем парсинг строки с операцией перевода денег

    public void testTransferParsing() {
        String TEST_LINE_TRANSFER = "[2025-05-10 09:05:44] user001 transferred 100.50 to user002";
        LogEntry entry = LogParser.parse(TEST_LINE_TRANSFER);
        assertAll(
                () -> assertEquals("user001", entry.getUser()),
                () -> assertEquals(Operation.TRANSFERRED, entry.getOperation()),
                () -> assertEquals(100.50, entry.getAmount(), 0.001),
                () -> assertEquals("user002", entry.getTargetUser())
        );
    }

    @Test       // Проверяем парсинг строки с операцией снятия средств

    public void testWithdrawParsing() {
        LogEntry entry = LogParser.parse(TEST_LINE_WITHDRAW);
        assertAll(
                () -> assertEquals(Operation.WITHDREW, entry.getOperation()),
                () -> assertEquals(200.75, entry.getAmount(), 0.001),
                () -> assertNull(entry.getTargetUser())
        );
    }

    @Test       // Проверяем парсинг строки с операцией запроса баланса
    public void testBalanceInquiryParsing() {
        LogEntry entry = LogParser.parse(TEST_LINE_BALANCE);
        assertAll(
                () -> assertEquals("user005", entry.getUser()),
                () -> assertEquals(Operation.BALANCE_INQUIRY, entry.getOperation()),
                () -> assertEquals(1500.0, entry.getAmount(), 0.001),
                () -> assertNull(entry.getTargetUser())
        );
    }

    @Test       // Проверяем, что при некорректной строке возникает исключение
    void testInvalidLineThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            LogParser.parse("invalid log line");
        });
    }
}