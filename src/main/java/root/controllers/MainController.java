package root.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import root.models.Lecture;
import root.models.Student;
import root.models.Subject;
import root.models.enums.Attendance;
import root.models.enums.LectureType;
import root.utils.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

//todo shortcuts
//todo inout in rows of tables
//todo adding/deleting students/lectures (pop-up)
//todo implements Initializable
public class MainController {
    @FXML private MenuItem                      menuItemQuickLecture;
    @FXML private TextField                     textFieldSubject;
    @FXML private TextField                     textFieldLecturer;
    @FXML private TextField                     textFieldPracticeLecturer;
    @FXML private TextField                     textFieldGroup;
    @FXML private GridPane                      gridPaneInfo;
    @FXML private ScrollBar                     scrollBar;
    @FXML private TableView<Student>            tableLectures;
    @FXML private TableView<Student>            tablePractical;
    @FXML private TableView<Student>            tableStudents;
    @FXML private TableColumn<Student, Student> colNum;
    @FXML private TableColumn<Student, String>  colStudents;
    private       Button                        btnChangeSource;

    private FileChooser                 fileChooser = new FileChooser();
    private FileChooser.ExtensionFilter excelFilter =
            new FileChooser.ExtensionFilter("Excel file (*.xlsx)", "*.xlsx");
    private FileChooser.ExtensionFilter xmlFilter   =
            new FileChooser.ExtensionFilter("XML file (*.xml)", "*.xml");
    private FileChooser.ExtensionFilter pdfFilter   =
            new FileChooser.ExtensionFilter("PDF file (*.pdf)", "*.pdf");

    private Subject                 subject  = new Subject();
    private ObservableList<Student> students = FXCollections.observableArrayList();

    private        Boolean isTabLectures = null;
    private static boolean isEasyExpand  = false;

    private void scrollTo(int value){
        tableStudents.scrollTo(value);
        tableLectures.scrollTo(value);
        tablePractical.scrollTo(value);
    }

    @FXML
    private void initialize() throws IOException {
        isEasyExpand = false;

        FileUtils.initStructure();

        //subject info init
        textFieldSubject.textProperty().bindBidirectional(subject.titleProperty());
        textFieldLecturer.textProperty().bindBidirectional(subject.lecturerProperty());
        textFieldPracticeLecturer.textProperty().bindBidirectional(subject.practicalLecturerProperty());
        textFieldGroup.textProperty().bindBidirectional(subject.groupProperty());

//        scrollBar init
        TableUtils.initScrollBar(scrollBar, 0);
        students.addListener(new ListSizeChangedListener(scrollBar));

        //tables init
        tableStudents.setItems(students);
        tableLectures.setItems(students);
        tablePractical.setItems(students);
        tableStudents.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableLectures.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tablePractical.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableStudents.addEventFilter(ScrollEvent.ANY, Event::consume);
        tableLectures.addEventFilter(ScrollEvent.ANY, Event::consume);
        tablePractical.addEventFilter(ScrollEvent.ANY, Event::consume);
        //init tableStudents
        TableUtils.initAutonumColumn(colNum);
        colStudents.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStudents.setCellFactory(TextFieldTableCell.forTableColumn());

        TableUtils.initShortcuts(tableLectures);
        TableUtils.initShortcuts(tablePractical);

        btnChangeSource = constructButtonChangeSource();

        //init fileChooser
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(excelFilter, xmlFilter, pdfFilter);

        //test
    }

    public void onNew() throws IOException {
        if (!saveBefore()) {
            return;
        }
        clear();
    }

    public void onLoadExcel() throws IOException {
        if (students.size() != 0) {
            clear();
            if (!saveBefore()) {
                return;
            }
        }
        fileChooser.setSelectedExtensionFilter(excelFilter);
        Window owner = tableStudents.getScene().getWindow();
        File   file  = fileChooser.showOpenDialog(owner);
        if (file == null) {
            return;
        }
        fileChooser.setInitialDirectory(new File(file.getParent()));
        String group = FileUtils.fileName(file);
        textFieldGroup.setText(group);
        List<String> studentNames;
        try {
            studentNames = ExcelUtils.readGroup(file);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Не вдалося прочитати дані з файлу.");
            alert.setContentText("Перевірте коректність даних та повторіть спробу.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        for (String studName : studentNames) {
            students.add(new Student(studName));
        }
        if (tableLectures.getColumns().size() != 0 || tablePractical.getColumns().size() != 0) {
            addExistingClassesToNewStudents(tableLectures);
        }
    }

    private void addExistingClassesToNewStudents(TableView<Student> table) {
        List<Lecture> generatedLectures = ScheduleController.getGeneratedLectures();
        if (generatedLectures.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Не вдалося завантажені згенеровані лекції.");
            alert.setContentText("Створіть список лекцій заново.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        for (Student student : students) {
            student.getAttendance().clear();
            for (Lecture lecture : generatedLectures) {
                student.getAttendance().put(lecture, new SimpleObjectProperty<>(Attendance.NULL));
            }
        }
    }

    public void onLoadXml() throws IOException {
        if (!saveBefore()) {
            return;
        }
        fileChooser.setSelectedExtensionFilter(xmlFilter);
        Window owner = tableStudents.getScene().getWindow();
        File   file  = fileChooser.showOpenDialog(owner);
        if (file == null) {
            return;
        }
        fileChooser.setInitialDirectory(new File(file.getParent()));
        XmlUtils.loadXml(file, students, subject);
        if (students.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!! ");
            alert.setHeaderText("Не вдалося прочитати дані з файлу.");
            alert.setContentText("Перевірте коректність даних та повторіть спробу.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        clear();
        Set<Lecture> lectures = students.get(0).getAttendance().keySet();
        loadLectures(lectures, true);
        File groupDir = FileUtils.addGroupIfNotExist(file.getName().replace(".xml", ""));
        FileUtils.addSubjectIfNotExist(groupDir, subject.getTitle());
    }

    public void onSaveXml() throws IOException {
        if(textFieldGroup.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Вкажіть групу.");
            alert.setContentText("Перевірте коректність введених даних та повторіть спробу.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        if(textFieldSubject.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Вкажіть предмет.");
            alert.setContentText("Перевірте коректність введених даних та повторіть спробу.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        File group = FileUtils.addGroupIfNotExist(subject.getGroup());
        File file  = FileUtils.addSubjectIfNotExist(group, subject.getTitle());
        boolean saved  = XmlUtils.saveXml(file, students, subject, true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація");
        alert.setHeaderText("Файл " + (saved ? "успішно " : "не ") + "збережено!!");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public void onExportData(ActionEvent event) {
        fileChooser.setSelectedExtensionFilter(xmlFilter);
        Window owner = tableStudents.getScene().getWindow();
        File   file  = fileChooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }
        fileChooser.setInitialDirectory(new File(file.getParent()));

        boolean result = XmlUtils.saveXml(file, students, subject, false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація");
        alert.setHeaderText("Файл " + (result ? "успішно " : "не ") + "збережено!!");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public void onExit() throws IOException {
        saveBefore();
        Platform.exit();
    }

    public void onGenerateSchedule(ActionEvent event) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Створення розкладу");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/root/views/ScheduleWindow.fxml"));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Неможливо відкрити нове вікно!!");
            alert.setContentText("Виправіть помилку самостійно))!!");
            alert.showAndWait();
            return;
        }
        dialog.setScene(new Scene(root));
        Window owner = ((MenuItem) event.getSource()).getParentPopup().getScene().getWindow();
        dialog.initOwner(owner);
//        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
        if (!ScheduleController.isGenerate()) {
            return;
        }
        List<Lecture> generatedLectures = ScheduleController.getGeneratedLectures();
        if (generatedLectures.size() == 0) {
            return;
        }
        boolean clear = true;
        if (tableLectures.getColumns().size() <= 1 && tablePractical.getColumns().size() <= 1
                && tableStudents.getItems().size() != 0) {
            clear = false;
        }

//        if (generatedLectures.size() != 0 && tableLectures.getColumns().size() != 0 && tablePractical.getColumns().size() != 0) {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Зберегти попередні дані??");
//            alert.setHeaderText("Додати лекції до існуючих, чи перезаписати дані??");
//            alert.setContentText("Старі дані буде втрачено.");
//
//            ButtonType buttonTypeOne = new ButtonType("Зберегти");
//            ButtonType buttonTypeTwo = new ButtonType("Оновити");
//
//            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
//
//            Optional<ButtonType> result = alert.showAndWait();
//            if (result.get() == buttonTypeOne) {
//                clear = false;
//            }
//        }
        for (Student student : students) {
            if (clear) {
                student.getAttendance().clear();
            }
            for (Lecture lecture : generatedLectures) {
                student.getAttendance().putIfAbsent(lecture, new SimpleObjectProperty<>(Attendance.NULL));
            }
        }

        loadLectures(generatedLectures, clear);
    }

    public void onSaveReport() throws FileNotFoundException {
        fileChooser.setSelectedExtensionFilter(pdfFilter);
        Window owner = tableStudents.getScene().getWindow();
        File   file  = fileChooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }
        try {
            PdfUtils.saveReport(file, students, subject);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Не можу зберегти звіт!!");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація");
        alert.setHeaderText("Файл успішно збережено!!");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public void onHelp() {
        //todo help
    }

    public void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Про програму");
        alert.setHeaderText("Програма для обліку відвідуваності студентів. Версія: 1.0.");
        alert.setContentText("Розроблено Устименко Вадимом (mr.ustiik@gmail.com).");
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }

    public void onTabChanged() {
        if (isTabLectures == null) {
            isTabLectures = true;
        } else {
            isTabLectures = !isTabLectures;
        }
        if (isTabLectures) {
            menuItemQuickLecture.setText("Швидка лекція");
            textFieldPracticeLecturer.setVisible(false);
            textFieldLecturer.setVisible(true);
        } else {
            menuItemQuickLecture.setText("Швидка практика");
            textFieldLecturer.setVisible(false);
            textFieldPracticeLecturer.setVisible(true);
        }

    }

    public void onTableMouseClicked(MouseEvent event) {
        TableUtils.rowSelection(event, tableStudents, tableLectures, tablePractical);
        if (event.getSource() != tableStudents) {
            TableUtils.expandableComboBoxes((TableView<Student>) event.getSource(), event, isEasyExpand);
        }
    }

    public void onEasyExpand() {
        isEasyExpand = !isEasyExpand;
//        ((CheckMenuItem)event.getTarget()).isSelected();
    }

    public void onQuickLecture() {
        if (isTabLectures) {
            TableUtils.quickLecture(tableLectures, LectureType.LECTURE);
        } else {
            TableUtils.quickLecture(tablePractical, LectureType.PRACTICAL);
        }
        updateStudentsHeader(true);
    }

    public void onActionButtonChange(ActionEvent event) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Вибір групи та предмету");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/root/views/GroupSelectionWindow.fxml"));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!!");
            alert.setHeaderText("Неможливо відкрити нове вікно!!");
            alert.setContentText("Виправіть помилку самостійно :))!!");
            alert.showAndWait();
            e.printStackTrace();
            return;
        }
        dialog.setScene(new Scene(root));
        Window owner = ((Button) event.getSource()).getScene().getWindow();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();

        File source = GroupSelectController.getSelectedFile();
        if (source == null) {
            return;
        }
        try {
            if (!saveBefore()) {
                return;
            }
        } catch (IOException e) {
            return;
        }
        clear();
        boolean loaded = XmlUtils.loadXml(source, students, subject);
        if (!loaded) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка!! ");
            alert.setHeaderText("Не вдалося завантажити дані .");
            alert.setContentText("Перевірте коректність даних в файлі " + source.getPath() + " та повторіть спробу.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }
        if (students.size() !=0){
            Set<Lecture> lectures = students.get(0).getAttendance().keySet();
            loadLectures(lectures, true);
        }
    }

    public static boolean isEasyExpand() {
        return isEasyExpand;
    }

    private Button constructButtonChangeSource() {
        Button button = new Button();
        button.setText("Змінити джерело");
        button.setOnAction(this::onActionButtonChange);
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Завантажити нові дані групи та предмету");
        button.setTooltip(tooltip);
        button.setAlignment(Pos.CENTER);
        gridPaneInfo.add(button, 2, 1, 2, 1);
        return button;
    }

    private boolean saveBefore() throws IOException {
        if (students.size() == 0 && subject.getTitle().isEmpty()) {
            return true;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Вікно підтвердження");
        alert.setHeaderText("Зберегти зміни??");
        alert.setContentText("Дані вже могло бути збережено. Це страхувальне вікно.");
        alert.initStyle(StageStyle.UTILITY);

        ButtonType buttonTypeOne    = new ButtonType("Так");
        ButtonType buttonTypeTwo    = new ButtonType("Ні");
        ButtonType buttonTypeCancel = new ButtonType("Відмнити", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
//            File fileToSave;
////            if (comboBoxAllGroups.getSelectionModel().getSelectedIndex() == -1) {
////                fileToSave = new File(groupsPath, );
////            } else {
////
////            }
            onSaveXml();
            return true;
        } else if (result.get() == buttonTypeTwo) {
            return true;
        } else {
            return false;
        }
    }

    private void loadLectures(Collection<Lecture> lectures, boolean clear) {
        lectures = new ArrayList<>(lectures);
        Collections.sort((List<Lecture>) lectures);
        List<Lecture> lectureList   = new LinkedList<>();
        List<Lecture> practicalList = new LinkedList<>();
        lectures.forEach(lecture -> {
            if (lecture.getType() == LectureType.LECTURE) {
                lectureList.add(lecture);
            } else {
                practicalList.add(lecture);
            }
        });
        TableUtils.initTableWithLectures(tableLectures, lectureList, clear);
        TableUtils.initTableWithLectures(tablePractical, practicalList, clear);
//        TableUtils.updateHeader(colStudents);
        //todo update studentTable header
        updateStudentsHeader(true);
    }

    private void updateStudentsHeader(boolean increase) {
        if (increase) {
            colStudents.setText("\n" + colStudents.getText().trim() + "\n\n");
            colNum.setText("\n" + colNum.getText().trim() + "\n\n");
        } else {
            colStudents.setText(colStudents.getText().trim());
            colNum.setText(colNum.getText().trim());
        }
    }

    private void clear() {
        subject.clear();
        students.clear();
        tableLectures.getColumns().clear();
        tablePractical.getColumns().clear();
        colStudents.setText(colStudents.getText().trim());
        colNum.setText(colNum.getText().trim());
        updateStudentsHeader(false);
    }

    private void onStartScroll(List<Lecture> lectures) {
        try {
            LocalDateTime now           = LocalDateTime.now();
            Integer       firstLecture  = null;
            Integer       firstPractice = null;
            for (int i = 0; i < lectures.size(); i++) {
                Lecture lecture = lectures.get(i);
                if (!lecture.getDateTime().isBefore(now)) {
                    if (lecture.getType() == LectureType.LECTURE) {
                        if (firstLecture == null) {
                            firstLecture = i;
                        }
                    } else {
                        if (firstPractice == null) {
                            firstPractice = i;
                        }
                    }
                }
                if (firstLecture != null && firstPractice != null) {
                    break;
                }
            }

            double tableWidthL = tableLectures.getWidth();
            double tableWidthP = tablePractical.getWidth();

            double colWidthL = tableLectures.getColumns().get(0).getWidth();
            double colWidthP = tablePractical.getColumns().get(0).getWidth();

            tableLectures.scrollTo((int) (tableWidthL / colWidthL * firstLecture));
            tablePractical.scrollTo((int) (tableWidthP / colWidthP * firstPractice));
        } catch (Exception e) {
        }
    }

}