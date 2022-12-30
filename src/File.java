import java.util.ArrayList;

/**
 * Класс File - описание файлов
 */
public class File {
    /**
     *     name - имя файла
     *     depends_on - списко файлов, от которых зависит данный файл
     *     color - цвет вершины (0 - пустая, 1 - вошли в вершину, 2 - вышли из вершины) - нужно для dfs
     *     content - содержимое файла, весь текст
     */
    private final String name;
    private final ArrayList<File> depends_on;
    int color;
    String content;

    File(String name) {
        this.name = name;
        this.depends_on = new ArrayList<>();
        this.color = 0;
        this.content = "";
    }

    /**
     * @return имя файла
     */
    public String getName() {
        return name;
    }

    /**
     * @param col - цвет файла
     */
    public void setColor(int col){
        color = col;
    }

    /**
     * @return содержимое файла
     */
    public String getContent(){
        return content;
    }

    /**
     * @return цвет файла
     */
    public int getColor(){
        return color;
    }

    /**
     * @return список файлов, от которых зависит данный файл
     */

    public ArrayList<File> getDependedFiles(){
        return depends_on;
    }

    /**
     * @param depended_file - файл, который нужно добавить в список зависимых файлов
     */
    public void setDepended(File depended_file) {
        depends_on.add(depended_file);
    }
}
