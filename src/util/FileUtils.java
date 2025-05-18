package util;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

// Вспомогательный утилитный класс для работы с файлами
public class FileUtils {

    /**
     * Рекурсивно находит все .log файлы в заданной директории.
     *
     * @param dir Корневая директория для поиска
     * @return Список путей к найденным .log файлам
     */
    public static List<Path> findLogFiles(Path dir) throws IOException {
        List<Path> result = new ArrayList<>();
        Files.walk(dir, FileVisitOption.FOLLOW_LINKS)       // Рекурсивный обход с переходом по символьным ссылкам
                .filter(path -> path.toString().toLowerCase().endsWith(".log"))     // Фильтрация по расширению .log
                .forEach(result::add);      // Добавление подходящих путей в список
        return result;
    }
}