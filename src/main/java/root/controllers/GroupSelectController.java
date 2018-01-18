package root.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import root.utils.FileStringConverter;
import root.utils.FileUtils;

import java.io.File;
import java.util.List;

public class GroupSelectController {
    @FXML private ComboBox<File> comboBoxGroup;
    @FXML private ComboBox<File> comboBoxSubject;

    private static File selectedFile;

    public static File getSelectedFile() {
        return selectedFile;
    }

    @FXML
    private void initialize() {
        FileStringConverter fileStringConverter = new FileStringConverter();
        comboBoxGroup.setConverter(fileStringConverter);
        comboBoxSubject.setConverter(fileStringConverter);
        initGroupItems();
        selectedFile = null;
    }

    public void onGroupSelected() {
        initSubjectItems(comboBoxGroup.getValue());
    }

    public void onSubjectSelected(ActionEvent event) {
        selectedFile = comboBoxSubject.getValue();
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    private void initGroupItems() {
        List<File> groups = FileUtils.groupsFileList();
        if (groups != null) {
            comboBoxGroup.setItems(FXCollections.observableList(groups));
        }
    }

    private void initSubjectItems(File group) {
        List<File> subjects = FileUtils.subjectsFileLest(group);
        if (subjects != null) {
            comboBoxSubject.setItems(FXCollections.observableList(subjects));
        }
    }
}
