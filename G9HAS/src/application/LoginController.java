package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;


//
public class LoginController extends Application implements Initializable {

	@FXML
	private ImageView HASLogo,ivBackground;
	@FXML
	private Label lblWelcome;
	@FXML
	private Button btnSignIn;
	@FXML
	private PasswordField tfPassword;
	@FXML
	public TextField tfUserName;
	
	public static String userName;

	@FXML
	void signinHandler(ActionEvent event) {
		if(tfUserName.getText().length()>0 && tfPassword.getText().equals("123456")) {
			userName=tfUserName.getText();
			Parent user_parent;
			try {
				user_parent = FXMLLoader.load(getClass().getResource("../gui/pattern.fxml"));
				Scene user_scene = new Scene(user_parent);
				Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
				stage.setScene(user_scene);
				stage.show();  
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../gui/loginWindow.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show(); 
		} catch(Exception e) {
			e.printStackTrace()   ;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
}
