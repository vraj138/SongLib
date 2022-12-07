/* Authors: Deep Parekh and Vraj Patel */

package MainApplication;

import javafx.application.Application;
import javafx.stage.Stage;
import View.Controller;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXMLLoader;

public class SongLib extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("../View/hellooFX.fxml"));
		GridPane root = (GridPane)loader.load();
		
		// Set up controller
		Controller songController = loader.getController();
		songController.start(primaryStage);
		
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}


    public static void main(String[] args) {
        launch(args);
    }
}