package root.controllers;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import javafx.util.converter.DefaultStringConverter;
import root.models.Lecture;
import root.models.ScheduleLecture;
import root.models.enums.LectureRotation;
import root.models.enums.LectureType;
import root.utils.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ScheduleController {
    private static List<Lecture> generatedLectures = new LinkedList<>();
    private static boolean       isGenerate        = false;

    public static List<Lecture> getGeneratedLectures() {
        return generatedLectures;
    }

    public static boolean isGenerate() {
        return isGenerate;
    }

    private ObservableList<root.models.ScheduleLecture> lectures = FXCollections.observableArrayList();

    @FXML private DatePicker datePickerFrom;
    @FXML private DatePicker datePickerTo;

    @FXML private TableView<ScheduleLecture>                    tableSchedule;
    @FXML private TableColumn<ScheduleLecture, ScheduleLecture> colNum;
    @FXML private TableColumn<ScheduleLecture, LectureType>     colType;
    @FXML private TableColumn<ScheduleLecture, DayOfWeek>       colDay;
    @FXML private TableColumn<ScheduleLecture, LectureRotation> colRotation;
    @FXML private TableColumn<ScheduleLecture, String>          colTime;
    @FXML private TableColumn<ScheduleLecture, String>          colAudience;

    @FXML
    private void initialize() {
        //table initialization
        tableSchedule.setItems(lectures);
        tableSchedule.getSelectionModel().setCellSelectionEnabled(true);
        tableSchedule.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        //columns initialization
        TableUtils.initAutonumColumn(colNum);

        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setCellFactory(ComboBoxTableCell.forTableColumn(LectureType.values()));

        colDay.setCellValueFactory(new PropertyValueFactory<>("day"));
        colDay.setCellFactory(ComboBoxTableCell.forTableColumn(new DayOfWeekStringConverter(), DayOfWeek.values()));

        colRotation.setCellValueFactory(new PropertyValueFactory<>("rotation"));
        colRotation.setCellFactory(ComboBoxTableCell.forTableColumn(LectureRotation.values()));

        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colTime.setCellFactory(ComboBoxTableCell.forTableColumn(LectureTimes.valuesString()));

        colAudience.setCellValueFactory(new PropertyValueFactory<>("audience"));
        colAudience.setCellFactory(TextFieldTableCell.forTableColumn());
//        colAudience.setCellFactory(arg -> {
//            TextFieldTableCell<ScheduleLecture, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());
//            cell.focusedProperty().addListener((observable, oldValue, newValue) -> {
//                if (oldValue) {
//                    cell.fireEvent(new TableColumn.CellEditEvent<>(tableSchedule, tableSchedule.getEditingCell(), ));
//                }
//            });
//
//            return cell;
//        });

        //datePickers initialize
        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(LocalDate.now());

        isGenerate = false;
    }


    public void addLecture() {
        lectures.add(new ScheduleLecture());
    }

    public void deleteLecture() {
//        if(tableSchedule.getSelectionModel().getSelectedItems().size() == 0){
//
//        }else{
//            for (ScheduleLecture sl : tableSchedule.getSelectionModel().getSelectedItems()) {
//                lectures.remove(sl);
//            }
//        }
        boolean removed = lectures.removeAll(tableSchedule.getSelectionModel().getSelectedItems());
        if (!removed) {
            lectures.remove(lectures.size() - 1);
        }
    }

    public void generateSchedule(ActionEvent event) throws Exception {
        if (!noDuplicates()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Таблиця містить однакові заняття!!");
            alert.setContentText("Перевірте дані!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        if (!validAudiences()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Не для всіх лекцій вказано аудиторію!!");
            alert.setContentText("Доповніть дані!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        LocalDate from = datePickerFrom.getValue();
        LocalDate to   = datePickerTo.getValue();
        if (from.isAfter(to)) {
//            throw new Exception("Incorrect date range!!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Невірний часовий діапазон!!");
            alert.setContentText("Виправте дати початку та кінця занять!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        int week = checkWeek(from);
        if (week == 0) {
            return;
        }
        List<Lecture> lectures = LectureGenerator.generateLectures(new ArrayList<>(this.lectures), from, to, week);
        if (lectures.size() != 0) {
            generatedLectures.clear();
            generatedLectures = lectures;
            isGenerate = true;
            ((Node) (event.getSource())).getScene().getWindow().hide();
        }
    }

    private boolean validAudiences() {
        for (ScheduleLecture lecture : lectures) {
            if (lecture.getAudience().equals("")) {
                return false;
            }
        }
        return true;
    }

    private boolean noDuplicates() {
        for (ScheduleLecture i : lectures) {
            for (ScheduleLecture j : lectures) {
                if (i != j) {
                    if (i.atOneTime(j)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // 0 - cancel
    // 1 - first week
    // 2 - second week
    private int checkWeek(LocalDate from) {
        boolean hasRotates = false;
        for (ScheduleLecture lecture : lectures) {
            if (lecture.getRotation() != LectureRotation.EVERY_WEEK) {
                hasRotates = true;
                break;
            }
        }
        if (!hasRotates) {
            return 1;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Вкажіть тиждень");
        alert.setHeaderText("Який тиждень відповідає даті " + DTF.formatDate(from) + "??");
        alert.setContentText("Оберіть варіант.");
        alert.initStyle(StageStyle.UTILITY);

        ButtonType buttonTypeOne    = new ButtonType("Перший");
        ButtonType buttonTypeTwo    = new ButtonType("Другий");
        ButtonType buttonTypeCancel = new ButtonType("Відміна", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            return 1;
        } else if (result.get() == buttonTypeTwo) {
            return 2;
        } else {
            return 0;
        }
    }

    public void onTableMouseClicked(MouseEvent event) {
        TableUtils.expandableComboBoxes(tableSchedule, event, MainController.isEasyExpand());
    }
}
