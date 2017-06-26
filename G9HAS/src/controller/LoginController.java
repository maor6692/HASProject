package controller;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public class LoginController extends Application implements Initializable {

	@FXML
	private Label lblWrongUser,lblConnection,lblWarning; //use to pop up login connections issues
	@FXML
	private PasswordField tfPassword;
	@FXML
	public TextField tfUserName,tfPort,tfHost;
	public ArrayList<String> arrans;
	public static HashMap<String, ArrayList<String>> msg;
	public static UserClient userClient;

	/**
	 * change user status to 'offline' in DB
	 */
	static void logout(){
		if(!userClient.isConnected()) return;
		HashMap<String, ArrayList<String>> msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(UserClient.userName);
		msg.put("logout",arr);
		LoginController.userClient.sendServer(msg);
		syncWithServer();
		try {
			
			userClient.closeConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * change user status to 'online' in DB and open appropriate window due to user_type
	 */
	@FXML
	void signinHandler(ActionEvent event) {

		lblWrongUser.setText("Wrong User Name or Password!");
		lblWrongUser.setVisible(false);
		lblConnection.setVisible(false);
		try {
			userClient = new UserClient(tfHost.getText(),Integer.parseInt(tfPort.getText()));
		} catch (Exception e1) {
			lblConnection.setVisible(true);
			return;
		}

		msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> userInfo = new ArrayList<String>();
		userInfo.add(tfUserName.getText());
		userInfo.add(tfPassword.getText());
		msg.put("login",userInfo);
		//	if(userClient==null)
		userClient.sendServer(msg);//send to server user info to verify user details 
		syncWithServer();
		arrans =  (ArrayList<String>)(UserClient.ans);
		if(arrans!=null && arrans.size()!=0){//if user details are OK
			userClient.fullName = arrans.get(1)+" "+arrans.get(2);//first+last name
			userClient.userName = tfUserName.getText();
			String user_type = arrans.get(0);
			Parent nextWindow;
			//			if(arrans.get(3).equals("online")){
			//				lblWrongUser.setText("User Already Connected!");
			//				lblWrongUser.setVisible(true);
			//				return;
			//			}

			try {
				if(user_type.equals("Manager"))user_type="SchoolManager";
				nextWindow = FXMLLoader.load(getClass().getResource("../gui/"+user_type+".fxml"));//Prepare appropriate window due to user_type
				Scene nextScene = new Scene(nextWindow);
				Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
				stage.setScene(nextScene);
				stage.show();  //change to user_type window
				stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent t) {
						LoginController.logout();
						Platform.exit();
						System.exit(0);
					}
				});

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
			UserClient.setFlagFalse();

			while(!userClient.isready())
			{
				try{
					userClient.wait();	
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		//	UserClient.setFlagFalse();
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
