package application;

import controller.movieController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/movieUI.fxml"));
            Parent root = loader.load();
            movieController controller = loader.getController();
            controller.setStage(primaryStage);
            primaryStage.setScene(new Scene(root));
            primaryStage.initStyle(StageStyle.UTILITY);
            primaryStage.show();

        } catch(Exception e) {
			e.printStackTrace();
		}
	}
}
