package service;

import static org.junit.jupiter.api.Assertions.*;

import model.LogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import service.LogFileSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LogFileSaverTest {
    private final LogFileSaver saver = new LogFileSaver();

    @Test        // Проверяем, что для каждого пользователя создается свой лог-файл и он содержит записи
    public void testSaveFilesWithDifferentUsers(@TempDir Path tempDir) throws IOException {
        // Подготовка данных
        List<LogEntry> user1Logs = Arrays.asList(
                createLog("user1", "balance inquiry", 1000.0),
                createLog("user1", "transferred", 200.0, "user2")
        );

        List<LogEntry> user2Logs = Collections.singletonList(
                createLog("user2", "received", 200.0, "user1")
        );

        Map<String, List<LogEntry>> userLogs = Map.of(
                "user1", user1Logs,
                "user2", user2Logs
        );

        // Выполнение
        saver.saveUserLogs(userLogs, tempDir);

        // Проверка
        Path user1File = tempDir.resolve("transactions_by_users/user1.log");
        Path user2File = tempDir.resolve("transactions_by_users/user2.log");

        assertAll(
                () -> assertTrue(Files.exists(user1File)),
                () -> assertTrue(Files.exists(user2File)),
                () -> assertTrue(Files.readAllLines(user1File).size() >= 2),
                () -> assertTrue(Files.readAllLines(user2File).size() >= 1)
        );
    }

    @Test       // Проверяем, что лог-файл создается даже при отсутствии операций (только финальный баланс)
    public void testEmptyLogList(@TempDir Path tempDir) throws IOException {
        // Выполнение с пустым списком
        saver.saveUserLogs(Map.of("emptyUser", Collections.emptyList()), tempDir);

        // Проверка
        Path userFile = tempDir.resolve("transactions_by_users/emptyUser.log");
        assertTrue(Files.exists(userFile));
        assertEquals(1, Files.readAllLines(userFile).size()); // Только финальный баланс
    }

    private LogEntry createLog(String user, String operation, double amount) {
        return createLog(LocalDateTime.now().toString(), user, operation, amount, null);
    }

    private LogEntry createLog(String user, String operation, double amount, String targetUser) {
        return createLog(LocalDateTime.now().toString(), user, operation, amount, targetUser);
    }

    private LogEntry createLog(String timestamp, String user, String operation, double amount, String targetUser) {
        String rawLine = String.format(Locale.ENGLISH, "[%s] %s %s %.2f%s",
                timestamp, user, operation, amount,
                targetUser != null ? " to " + targetUser : "");

        return new LogEntry(
                LocalDateTime.parse(timestamp.replace(" ", "T")),
                user,
                LogEntry.Operation.valueOf(operation.toUpperCase().replace(" ", "_")),
                amount,
                targetUser,
                rawLine
        );
    }
}