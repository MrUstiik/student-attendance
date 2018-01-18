package root.utils;

import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import root.models.Lecture;
import root.models.ScheduleLecture;
import root.models.Student;
import root.models.enums.Attendance;
import root.models.enums.LectureType;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class TableUtils {
    public static <R> void initAutonumColumn(TableColumn<R, R> column) {
        column.setCellValueFactory(p -> new ReadOnlyObjectWrapper(p.getValue()));
        column.setCellFactory(col -> new TableCell<R, R>() {
            @Override
            protected void updateItem(R item, boolean empty) {
                super.updateItem(item, empty);
                if (this.getTableRow() != null && item != null) {
                    setText(this.getTableRow().getIndex() + 1 + "");
                } else {
                    setText("");
                }
            }
        });
    }

    public static void initScrollBar(ScrollBar bar, int size) {
        double visibleDiff = 16;
        double realSize    = size - visibleDiff;
        bar.setMax(realSize);
//        GridPane          parent = (GridPane) bar.getParent();
//        ColumnConstraints column = parent.getColumnConstraints().get(1);
//        if (realSize <= 0) {
//            bar.setVisible(false);
//        } else {
//            bar.setVisible(true);
//        }
    }

    public static TableColumn<Student, Attendance> constructColumnAttendance(Lecture lecture) {
        TableColumn<Student, Attendance> column = new TableColumn<>();
        column.setText(lecture.toString());
//        column.setText(headerText(lecture.toString()));
        column.setCellValueFactory(cellData -> cellData.getValue().getAttendance().get(lecture));
        column.setCellFactory(ComboBoxTableCell.forTableColumn(Attendance.values()));
//        column.setCellFactory(param -> {
//            ComboBoxTableCell<Student, Attendance> cell = new ComboBoxTableCell<>(Attendance.values());
//            ComboBox<Student> comboBox = (ComboBox<Student>) cell.getChildrenUnmodifiable().get(0);
//            comboBox.setOnMouseClicked(event -> {
//                System.out.println();
//            });
//            return cell;
//        });
        column.addEventHandler(KeyEvent.ANY, System.out::println);
        return column;
    }

    public static void initTableWithLectures(TableView<Student> table, List<Lecture> lectures, boolean clear) {
        if (clear) {
            table.getColumns().clear();
        }
        for (Lecture lecture : lectures) {
            table.getColumns().add(constructColumnAttendance(lecture));
        }
//        TableUtils.updateHeader(table);
    }

    //todo click on Group (net of table) case
    public static void expandableComboBoxes(TableView<?> table, MouseEvent event, boolean easyExpand) {
        //todo do not works for one row cells
        if (table.getEditingCell() == null) {
            int row = table.getSelectionModel().getFocusedIndex();
            //int row = tableSchedule.getSelectionModel().getSelectedIndex();
            ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
            if (selectedCells.size() == 0) {
                return;
            }
            TableColumn col = selectedCells.get(0).getTableColumn();
            table.edit(row, col);
            Object cellData = col.getCellData(0);
            if (cellData instanceof Enum) {
                EventTarget       target = event.getTarget();
                ComboBoxTableCell cell   = null;
                if (target instanceof ComboBoxTableCell) {
                    cell = (ComboBoxTableCell) event.getTarget();
                } else {
                    if (target instanceof LabeledText) {
                        try {
                            Field labeled = target.getClass().getDeclaredField("labeled");
                            labeled.setAccessible(true);
                            cell = (ComboBoxTableCell) labeled.get(target);
                        } catch (Exception e) {
                            ;
                        }
                    } else {
                        return;
                    }
                }
                //todo autoexpand combobox (working after removing)
                if (easyExpand) {
                    Node     node     = cell.getChildrenUnmodifiable().get(0);
                    if(node instanceof ComboBox ){
                        ComboBox comboBox = (ComboBox) node;
                        comboBox.show();
                    }
                }
            }
        }
    }


    public static void multiChoice(TableView<Student> table, MouseEvent event) {
        if (table.getEditingCell() != null) {
            ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
            //todo multichoise
        }
    }

    public static void rowSelection(MouseEvent event, TableView<Student> tableStudents,
                                    TableView<Student> tableLectures, TableView<Student> tablePractical)
    {
        TableView<Student>      source          = (TableView<Student>) event.getSource();
        ObservableList<Integer> selectedIndices = source.getSelectionModel().getSelectedIndices();
        if (source == tableStudents) {
            tableLectures.getSelectionModel().clearSelection();
            selectedIndices.forEach(tableLectures.getSelectionModel()::select);

            tablePractical.getSelectionModel().clearSelection();
            selectedIndices.forEach(tablePractical.getSelectionModel()::select);
        } else {
            tableStudents.getSelectionModel().clearSelection();
            selectedIndices.forEach(tableStudents.getSelectionModel()::select);
        }
    }

    public static void initShortcuts(TableView<Student> table) {

        table.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            if (event.isControlDown()) {
                ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
                if (selectedCells.size() == 0 || table.getColumns().size() == 0 || table.getItems().size() == 0) {
                    return;
                }
                TableColumn column = null;
                int         row    = -1;
                for (TablePosition tablePosition : selectedCells) {
                    column = tablePosition.getTableColumn();
                    row = tablePosition.getRow();
//                    table.getItems().get(row).getAttendance().get(lecture).
//                            setValue(Attendance.ATTEND);
                }
                if (column != null) {
                    table.getSelectionModel().clearAndSelect(row + 1, column);
                }
            }
        });
    }

    public static void quickLecture(TableView<Student> table, LectureType type) {
        if (table.getColumns().size() == 0) {
            LocalTime nowTime          = LocalTime.now();
            LocalTime classesEndTime   = LocalTime.of(20, 00);
            LocalTime classerStartTime = LocalTime.of(8, 00);
            if (nowTime.isAfter(classesEndTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка!!");
                alert.setHeaderText("Занадто пізно для занять!!");
                alert.setContentText("Створіть бажаний розклад за допомогою функції \"Функції - Створити розклад\"");
                alert.showAndWait();
                return;
            }
            if (nowTime.isBefore(classerStartTime)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка!!");
                alert.setHeaderText("Занадто рано для занять!!");
                alert.setContentText("Створіть бажаний розклад за допомогою функції \"Функції - Створити розклад\"");
                alert.showAndWait();
                return;
            }
            LocalDate lectureDate = LocalDate.now();
            Lecture   lecture     = new Lecture();
            lecture.setType(type);
//            switch (dateTime.getDayOfWeek()){
//                case SATURDAY:
//                    dateTime = dateTime.plusDays(2);
//                    break;
//                case SUNDAY:
//                    dateTime = dateTime.plusDays(1);
//                    break;
//            }
            LocalTime lectureTime = LocalTime.from(LectureTimes.valuesTime()[0]);
            for (LocalTime localTime : LectureTimes.valuesTime()) {
                if (localTime.isBefore(nowTime)) {
                    lectureTime = localTime;
                } else {
                    break;
                }
            }
            lecture.setDateTime(LocalDateTime.of(lectureDate, lectureTime));
            lecture.setAudience("-");
            table.getColumns().add(constructColumnAttendance(lecture));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("В таблиці вже є " + (type == LectureType.LECTURE ? "лекції" :
                    "практичні заняття") + "!!");
            alert.setContentText("Дана функція лише для пустої таблиці");
            alert.showAndWait();
        }
    }

    public static Callback<TableColumn<ScheduleLecture, String>, TableCell<ScheduleLecture, String>>
    constructTextTableCell() {
        //todo autocommit textFields (not only table)
        return null;
    }

    //    public static void updateHeader(TableColumn column){
//        column.setText(TableUtils.headerText(column.getText()));
//    }
//
//    public static void updateHeader(TableView table){
//        ObservableList columns = table.getColumns();
//        if(columns.size() != 0){
//            updateHeader((TableColumn) columns.get(0));
//        }
//    }
//
//    public static String headerText(String text) {
//        int count = text.split("\n").length;
//        switch (count) {
//            case 2:
//                return text + "\n";
//            case 3:
//                return text;
//            default:
//                return "\n" + text + "\n\n";
//        }
//    }
}
