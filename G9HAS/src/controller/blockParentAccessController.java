package controller;

import java.awt.Button;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import common.SClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class blockParentAccessController implements Initializable{

	@FXML
	private Hyperlink linkLogout;

	@FXML
	private Label lblUser;
	
    @FXML
    private ComboBox<?> cmbName;

    @FXML
    private Label lblSuccess;


    @FXML
    private ComboBox<?> cmbClass;

    @FXML
    private Label lblErr;

    @FXML
    private Button btnBlock;
    
    private HashMap<String,String> studentsInClass;
    @FXML
    void blockHandler(ActionEvent event) {

    }




	@FXML
	void logoutHandler(ActionEvent event) {
		
		Parent user_parent;
		try {
			user_parent = FXMLLoader.load(getClass().getResource("../gui/loginWindow.fxml"));//
			Scene user_scene = new Scene(user_parent);
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			stage.setScene(user_scene);
			stage.show();  

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblUser.setText(UserClient.userName);
		
		System.out.println("intiallize block parent user");
		
    	//**** get information for the page
		// we need all classes only id maybe name
		//we need all students names and in which classes they are
		
    	HashMap<String,Object> msg=new HashMap<String,Object>();
    	msg.put("get info for blockParentAccess", null);
    	try{
    	LoginController.userClient.sendToServer(msg);
    	//syncWithServer(); ----- need??
    	}catch(Exception ex){
    		System.out.println("");
    	}
    	if(UserClient.ans == null ) // no DB info!
    		return;
    	
    	
    }
	
}
