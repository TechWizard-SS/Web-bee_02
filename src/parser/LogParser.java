package parser;

import model.LogEntry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.*;

/**
 * Парсер строк логов в объекты LogEntry.
 * Поддерживает форматы:
 * - [дата] пользователь operation сумма
 * - [дата] пользователь operation сумма to/from пользователь
 */

public class LogParser {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "\\[(.*?)\\]\\s+(\\w+)\\s+(balance inquiry|transferred|withdrew|received)\\s+([\\d.,]+)(?:\\s+(to|from)\\s+(\\w+))?"
    );


    /**
     * Парсит строку лога в объект LogEntry.
     *
     * @param line Строка лога
     * @return LogEntry
     * @throws IllegalArgumentException При неверном формате строки
     */
    public static LogEntry parse(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid log line: " + line);
        }

        // Извлечение данных из regex-групп
        LocalDateTime timestamp = LocalDateTime.parse(matcher.group(1), formatter);
        String user = matcher.group(2);
        String operationStr = matcher.group(3);
        String amountStr = matcher.group(4).replace(',', '.'); // Исправление для чисел с запятыми
        String direction = matcher.group(5);
        String otherUser = matcher.group(6);

        // Парсим сумму, заменяя запятые на точки для корректного преобразования
        double amount = Double.parseDouble(amountStr);

        // Определяем тип операции и получателя
        LogEntry.Operation operation = parseOperation(operationStr);
        String targetUser = parseTargetUser(operation, direction, otherUser);

        // Создаем новый объект записи лога
        return new LogEntry(timestamp, user, operation, amount, targetUser, line);
    }

    /**
     * Преобразует строковое представление операции в enum Operation.
     * @param operationStr Строка с названием операции (case-insensitive)
     * @return Соответствующий enum Operation
     * @throws IllegalArgumentException При неизвестном типе операции
     */
    private static LogEntry.Operation parseOperation(String operationStr) {
        switch (operationStr.toLowerCase()) {
            case "balance inquiry": return LogEntry.Operation.BALANCE_INQUIRY;
            case "transferred": return LogEntry.Operation.TRANSFERRED;
            case "withdrew": return LogEntry.Operation.WITHDREW;
            case "received": return LogEntry.Operation.RECEIVED;
            default: throw new IllegalArgumentException("Unknown operation: " + operationStr);
        }
    }

    /**
     * Определяет получателя транзакции на основе типа операции и направления.
     * @param operation Тип операции (TRANSFERRED/RECEIVED)
     * @param direction Направление ("to" или "from")
     * @param otherUser Имя пользователя из строки лога
     * @return Имя получателя или null, если операция не требует получателя
     */
    private static String parseTargetUser(LogEntry.Operation operation, String direction, String otherUser) {
        // Для переводов: проверяем направление "to"
        if (operation == LogEntry.Operation.TRANSFERRED && "to".equals(direction)) {
            return otherUser;
        }
        // Для получений: проверяем направление "from"
        else if (operation == LogEntry.Operation.RECEIVED && "from".equals(direction)) {
            return otherUser;
        }
        // Для операций без получателя
        return null;
    }
}