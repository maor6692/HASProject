
package server;


import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Main extends Application {
	@FXML
	private TextField txtBoxDBpassword;

	@FXML
	private Button btnConnect;
	@FXML
	private TextField txtBoxport;
	@FXML
	private TextField txtBoxHost;

	@FXML
	private TextField txtBoxDBuser;

	@FXML
	private TextField txtBoxDBname;

	@FXML
	private Label lblConnected;
	@FXML
	void connectHandler(ActionEvent event) {

		int port = 0; // Initialize port

		try{
			port = Integer.parseInt(txtBoxport.getText()); // Get port from command line
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid input, please enter port number");
		}
		EchoServer sv = new EchoServer(port);
		//connect to data base.
		if(sv.connect(txtBoxHost.getText(), txtBoxDBname.getText(), txtBoxDBuser.getText(), txtBoxDBpassword.getText()) == 1)
		{
			lblConnected.setVisible(true);
			btnConnect.setVisible(false);
		}

	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("server.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

	}

	public static void main(String[] args) {
		launch(args);


	}
}
