package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;

import controller.SecretaryController.CourseComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SchoolManagerController implements Initializable{

	@FXML
	private Hyperlink linkLogout;

	@FXML
	private Label lblUser;

	
	   @FXML
		Pane blockParentPane;
	   
	   @FXML
	    private ListView<String> lvStudents;

	    @FXML
	    private ComboBox<String> cbStudents,cbClasses;

	    @FXML
	    private Button btnReturnAccess,btnBlock;

		HashMap<String, ArrayList<String>>	msg ;
		private ObservableList<String> studentsDetails = FXCollections.observableArrayList();
		private ObservableList<String> classesDetails = FXCollections.observableArrayList();
		ArrayList<String> arr ;
		int newYear;
		char newSemester;
		HashMap<Integer, String> classes;
		HashMap<String, ArrayList<String>> students;


	    @FXML
	    void setBlockParentAccessPaneHandler(ActionEvent event) {
	    	setPane(blockParentPane);
	    }

	    @FXML
	    void setGetStatisticReportPaneHandler(ActionEvent event) {
	    	setPane(blockParentPane);
	    }

	    @FXML
	    void setViewAllInformationPaneHandler(ActionEvent event) {
	    	setPane(blockParentPane);
	    }

	    @FXML
	    void setAnswerRequestPaneHandler(ActionEvent event) {
	    	setPane(blockParentPane);
	    }

	    void setPane(Pane pane){
			blockParentPane.setVisible(false);
			pane.setVisible(true);
		}
	    
	    @FXML
	    void onClassChoosedHandler(ActionEvent event){
	    	int classId = 0;
	    	if(cbClasses.getSelectionModel().getSelectedItem()==null) return;
	    	for(int cid: classes.keySet())
	    		if(classes.get(cid).equals(cbClasses.getSelectionModel().getSelectedItem())) {
	    			classId=cid;
	    			break;
	    		}
	    	
	    	arr.clear();
	    	arr.add(String.valueOf(classId));
	    	msg.put("getStudentsInClass",arr);
			LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
			LoginController.syncWithServer();
			msg.clear();
			 students = (HashMap<String, ArrayList<String>>)UserClient.ans;
			for(String id: students.keySet())
				studentsDetails.addAll(id+":"+students.get(id).get(0)+" "+students.get(id).get(1));
			cbClasses.setItems(studentsDetails);
	    }


	@FXML
	void logoutHandler(ActionEvent event) {//goes back to login window
		Parent nextWindow;
		try {
			LoginController.logout();
			nextWindow = FXMLLoader.load(getClass().getResource("../gui/loginWindow.fxml"));
			Scene nextScene = new Scene(nextWindow);
			Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
			stage.setScene(nextScene);
			stage.show();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					LoginController.logout();
					Platform.exit();
					System.exit(0);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblUser.setText(UserClient.fullName);
		initializeBlockParent();
	}
	void initializeBlockParent(){

		msg = new HashMap<String, ArrayList<String>>();
		arr = new ArrayList<>();

		msg.put("getCurrentSemester",null);
		LoginController.userClient.sendServer(msg);//ask from server next semester details
		LoginController.syncWithServer();
		msg.clear();
		
		ArrayList<Object> currSemester=(ArrayList<Object>) UserClient.ans;
		int newYear=(int) currSemester.get(0);
		char newSemester=(char) currSemester.get(1);
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(String.valueOf(newYear));
		arr.add(String.valueOf(newSemester == 'A' ? 1 : 2));
		msg.clear();
		msg.put("getCurrentClasses",arr);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		msg.clear();
	    classes = (HashMap<Integer,String>)UserClient.ans;
	    classesDetails.addAll(((HashMap<Integer,String>)UserClient.ans).values());
		cbClasses.setItems(classesDetails);
	}
}
