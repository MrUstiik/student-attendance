package root.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    private static String groupsPath = "./xml/groups";

    public static String getGroupsPath() {
        return groupsPath;
    }

    public static void initStructure() throws IOException {
        File groupsDir = new File(groupsPath);
        if (!groupsDir.exists()) {
            groupsDir.mkdir();
        }
    }

    public static String constructFilename(String subjectTitle) {
        return subjectTitle + ".xml";
    }

    public static File addGroupIfNotExist(String groupTitle) {
        File file = new File(groupsPath, groupTitle);
        if (!groupExist(groupTitle)) {
            file.mkdirs();
        }
        return file;
    }

    public static File addSubjectIfNotExist(File groupDir, String subject) throws IOException {
        File file = new File(groupDir, constructFilename(subject));
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    public static boolean groupExist(String title) {
        return Arrays.asList(getGroupNames()).contains(title);
    }

    public static String fileName(File file) {
        return fileName(file.getName());
    }

    public static String fileName(String path) {
        int startIndex = path.lastIndexOf('/');
        int endIndex   = path.lastIndexOf('.');
        return endIndex == -1? path.substring(startIndex+1) : path.substring(startIndex + 1, endIndex);

    }

    public static String[] getGroupNames() {
        File     groupsDir = new File(groupsPath);
        String[] groups    = groupsDir.list((dir, name) -> new File(dir, name).isDirectory());
        return groups;
    }

    public static List<File> groupsFileList() {
        File   groupsDir = new File(groupsPath);
        File[] groups    = groupsDir.listFiles((dir, name) -> new File(dir, name).isDirectory());
        return Arrays.asList(groups);
    }

    public static List<File> subjectsFileLest(File groupDir) {
        File[] subjects = groupDir.listFiles(pathname -> pathname.getName().endsWith(".xml"));
        return Arrays.asList(subjects);
    }
}
