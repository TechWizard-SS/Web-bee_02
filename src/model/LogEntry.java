package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Модель записи лога с информацией о транзакции.
 * Содержит: временную метку, пользователя, тип операции, сумму и получателя.
 */

public class LogEntry{
    public enum Operation {
        BALANCE_INQUIRY,  // Запрос баланса
        TRANSFERRED,      // Перевод средств
        WITHDREW,         // Снятие средств
        RECEIVED          // Получение средств
    }

    private final LocalDateTime timestamp;
    private final String user;
    private final Operation operation;
    private final double amount;
    private final String targetUser;
    private final String rawLine;

    /**
     * Конструктор записи лога.
     *
     * @param timestamp Время операции
     * @param user Инициатор операции
     * @param operation Тип операции
     * @param amount Сумма (всегда положительная)
     * @param targetUser Получатель (для TRANSFERRED/RECEIVED)
     * @param rawLine Оригинальная строка лога
     */
    public LogEntry(LocalDateTime timestamp, String user, Operation operation,
                    double amount, String targetUser, String rawLine) {
        this.timestamp = timestamp;
        this.user = user;
        this.operation = operation;
        this.amount = amount;
        this.targetUser = targetUser;
        this.rawLine = rawLine;
    }

    // Геттеры
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getUser() { return user; }
    public Operation getOperation() { return operation; }
    public double getAmount() { return amount; }
    public String getTargetUser() { return targetUser; }
    public String getRawLine() {
        return rawLine;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("] ");
        sb.append(user).append(" ").append(operation).append(" ");
        sb.append(String.format(Locale.ENGLISH, "%.2f", amount));
        if (targetUser != null && !targetUser.isEmpty()) {
            sb.append(" to ").append(targetUser);
        }
        return sb.toString();
    }

}