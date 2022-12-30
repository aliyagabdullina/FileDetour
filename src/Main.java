import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    /**
     * PATH_NAME - название папки, откуда начинается поиск
     * all_files - список из всех имеющихся файлов
     * all_files_names - список имен всех имеющихся файлов
     * if_cycle - показывает есть ли цикл
     * sorted_files - список отсортированных нужным образом файлов
     * if_broken - показывает есть ли неверные ссылки на файлы
     * broken_file_name - если есть ссылка на несуществующий файл, это его имя
     * broken_file_directory - имя файла, где есть ссылка на несуществующий файл
     */

    public static final String PATH_NAME = "C:\\Users\\User\\Downloads\\Archive\\BasicExample";
    public static ArrayList<File> all_files;
    public static ArrayList<String> all_files_names;
    public static boolean if_cycle = false;
    public static ArrayList<File> sorted_files;
    public static boolean if_broken = false;
    public static String broken_file_name;
    public static String broken_file_directory;

    /**
     * Основная логика программы: обход корневой папки, чтение всех файлов, обход файлов
     * dfs-ом, обработка случаев, вывод ответа
     *
     * @param args -
     */
    public static void main(String[] args) {
        all_files = new ArrayList<>();
        all_files_names = new ArrayList<>();
        sorted_files = new ArrayList<>();
        detour();
        for (int i = 0; i < all_files.size(); i++) {
            read(i);
        }

        for (File all_file : all_files) {
            if (all_file.getColor() == 0) {
                dfs(all_file);
            }
        }

        if (if_broken) {
            System.out.println("Ошибка: указатель на несуществующий файл");
            System.out.println("Файла " + broken_file_name + ", указанного в файле " +
                    broken_file_directory + "не существует");
        } else {
            if (if_cycle) {
                System.out.println("Ошибка: в папке обнаружена циклическая зависимость");
            } else {
                System.out.println("Все хорошо. Вот файлы в нужном порядке:");
                for (int i = sorted_files.size() - 1; i >= 0; i--) {
                    System.out.println(sorted_files.get(i).getName() + " ");
                }
                //конкатенация строк в файл и вывод на консоль
                String total_note = sorted_files.get(sorted_files.size() - 1).getContent();
                for (int i = sorted_files.size() - 2; i >= 0; i--) {
                    total_note = String.join("\n", total_note, sorted_files.get(i).getContent());
                }
                System.out.println("Вот все содержимое:");
                System.out.println(total_note);
            }
        }
    }

    /**
     * Метод обходит все файлы в корневой папке и запоминает все файлы, которые обошли. Выкидывает
     * Exception при ошибке
     */
    private static void detour() {
        Path root = Paths.get(PATH_NAME);

        if (Files.notExists(root)) {
            System.out.println("Папка " + root.toString() + " не существует");
            return;
        }

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    File new_file = new File(file.toString());
                    new_file.content = new String(Files.readAllBytes(Paths.get(file.toString())));
                    all_files.add(new_file);
                    all_files_names.add(new_file.getName());
                    System.out.println("Посетили файл " + file.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("При обходе дерева папок произошла ошибка");
        }
    }

    /**
     * Читает файл, находит все зависимости, записывает нужную информацию
     * @param index - индекс файла в списке всех файлов all_files
     */

    private static void read(int index) {
        try {
            String file_name = all_files.get(index).getName();
            FileReader file_reader = new FileReader(file_name);
            Scanner file_scanner = new Scanner(file_reader);

            while (file_scanner.hasNext()) {
                String line = file_scanner.next();
                if (line.equals("require")) {
                    String file_depend_on = readNameOfFile(file_scanner);
                    if (all_files_names.contains(file_depend_on)) {
                        File file_depend = findExistedFile(file_depend_on);
                        all_files.get(index).setDepended(file_depend);
                    } else {
                        if_broken = true;
                        broken_file_name = file_depend_on;
                        broken_file_directory = file_name;
                    }
                }
            }

            file_reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает файл с указанным именем
     * @param file_name - имя файла, который ищем
     * @return - файл с указанным именем
     */
    private static File findExistedFile(String file_name) {
        for (File all_file : all_files) {
            if (all_file.getName().equals(file_name)) {
                return all_file;
            }
        }
        return null;
    }

    /**
     * Читает имя файла, заданное в формате 'file name'
     * @param file_scanner - сканнер
     * @return - имя файла
     */
    private static String readNameOfFile(Scanner file_scanner) {
        String file_depend_on = file_scanner.next();
        String start_symbol = "'";
        while (!file_depend_on.endsWith(start_symbol)) {
            file_depend_on = String.join(" ", file_depend_on, file_scanner.next());
        }
        if (file_depend_on.startsWith(start_symbol)) {
            file_depend_on = file_depend_on.substring(1, file_depend_on.length() - 1);
        }
        return file_depend_on;
    }

    /**
     * Топологическая сортировка, использующая поиск в глубину: помечаем файл цифрой 1, когда вошли в него,
     * цифрой 2 - когда вышли.
     * Если попали в вершину с цифрой 1 - значит образовался цикл.
     * Перед выходом из рекурсии добавляем файл в список sorted_files
     * @param file_for_dfs - файл, из которого выполняем поиск в глубину
     */
    static void dfs(File file_for_dfs) {
        file_for_dfs.setColor(1);
        for (int i = 0; i < file_for_dfs.getDependedFiles().size(); i++) {
            File to_file = file_for_dfs.getDependedFiles().get(i);

            if (to_file.getColor() == 0) {
                dfs(to_file);
            } else {
                if (to_file.getColor() == 1) {
                    if_cycle = true;
                }
            }
        }
        file_for_dfs.setColor(2);
        sorted_files.add(file_for_dfs);
    }

}
