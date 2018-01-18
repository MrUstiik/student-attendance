package root;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

//todo remove all row selection
//todo normal scroll
//todo installer & exe
//todo note for lectures
//todo attendance for all students for lecture
//todo on today lecture
public class StudentAttendance extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/MainWindow.fxml"));
        primaryStage.setTitle("Облік відвідуваності студентів");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        root.requestFocus();
        primaryStage.setMaxHeight(primaryStage.getHeight());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
    }
}
