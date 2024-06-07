import java.io.IOException;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/*
 * Leo Ramirez - lrami7
 * Arwa Mazher - amazh2
 * Project 4: Chat Application
 */
public class GuiServer extends Application{
	TextField s1,s2,s3,s4, c1;
	Button serverChoice,clientChoice,b1;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox;
	VBox clientBox;
	Scene startScene;
	BorderPane startPane;
	Server serverConnection;
	Client clientConnection;
	MessageInfo messageInfo = new MessageInfo();
	ListView<String> listItems, listItems2;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Chat Application");

		startScene = createInitialGui();
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
		primaryStage.setScene(startScene);
		primaryStage.show();
		
	}

	public Scene createInitialGui() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MyFXML0.fxml"));
		Parent root = loader.load();
		MyController myctr = loader.getController();
		root.getStylesheets().add("Style0.css");
		return new Scene(root);
	}
}
