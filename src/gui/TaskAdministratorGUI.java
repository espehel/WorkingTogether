package gui;

import agents.TaskAdministrator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by espen on 10/02/15.
 */
public class TaskAdministratorGUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "view.fxml"
                )
        );

        loader.setController(TaskAdministrator.getInstance());
        Parent root = loader.load();


        Scene scene = new Scene(root);


        stage.setScene(scene);
        stage.show();
    }
    public static void run(String[] args){
        launch(args);

    }
}
