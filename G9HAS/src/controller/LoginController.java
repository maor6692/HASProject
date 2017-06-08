package controller;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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



public class LoginController extends Application implements Initializable {

	@FXML
	private Label lblWrongUser,lblConnection; //use to pop up login connections issues
	@FXML
	private PasswordField tfPassword;
	@FXML
	public TextField tfUserName,tfPort,tfHost;
	
	public static HashMap<String, ArrayList<String>> msg;
	public static UserClient userClient;
	
/**
 * change user status to 'offline' in DB
 */
	static void logout(){
		HashMap<String, ArrayList<String>> msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(UserClient.userName);
		msg.put("logout",arr);
		LoginController.userClient.sendServer(msg);
	}
	/**
	 * change user status to 'online' in DB and open appropriate window due to user_type
	 */
	@FXML
	void signinHandler(ActionEvent event) {
		lblWrongUser.setVisible(false);
		lblConnection.setVisible(false);
		try {
			userClient = new UserClient(tfHost.getText(),Integer.parseInt(tfPort.getText()));
		} catch (Exception e1) {
			lblConnection.setVisible(false);
		}
		msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> userInfo = new ArrayList<String>();
		userInfo.add(tfUserName.getText());
		userInfo.add(tfPassword.getText());
		msg.put("login",userInfo);
		userClient.sendServer(msg);//send to server user info to verify user details 
		syncWithServer();
		if(UserClient.ans!=null && UserClient.ans.size()!=0){//if user details are OK
			userClient.fullName = UserClient.ans.get(1)+" "+UserClient.ans.get(2);//first+last name
			userClient.userName = tfUserName.getText();
			String user_type = UserClient.ans.get(0);
			Parent nextWindow;
			try {
				nextWindow = FXMLLoader.load(getClass().getResource("../gui/"+user_type+".fxml"));//Prepare appropriate window due to user_type
				Scene nextScene = new Scene(nextWindow);
				Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
				stage.setScene(nextScene);
				stage.show();  //change to user_type window
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			lblWrongUser.setVisible(true);
		}
	}
	/**
	 * this method waits for new answer from server
	 */
	public static void syncWithServer()
	{
		synchronized(userClient)
		{
			while(!userClient.isready())
			{
				try{
					userClient.wait();	
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			UserClient.setFlagFalse();
		}
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../gui/loginWindow.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show(); 
		} catch(Exception e) {
			e.printStackTrace();
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
