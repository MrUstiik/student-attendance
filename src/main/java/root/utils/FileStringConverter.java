package root.utils;

import javafx.util.StringConverter;

import java.io.File;

public class FileStringConverter extends StringConverter<File> {
    private String path = "";

    public FileStringConverter() {
        super();
    }

    public FileStringConverter(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        this.path = path;
    }

    @Override
    public String toString(File file) {
        return FileUtils.fileName(file);
    }

    @Override
    public File fromString(String string) {
        return new File(path + string);
    }
}
