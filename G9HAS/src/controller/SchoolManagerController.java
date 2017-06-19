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
	private ListView<String> lvStudents;//list view to display blocked students in current semester

	@FXML
	private ComboBox<String> cbStudents,cbClasses;//combo box for class and students in current semester

	@FXML
	private Button btnReturnAccess,btnBlock;

	HashMap<String, ArrayList<String>>	msg ;
	private ObservableList<String> studentsDetails = FXCollections.observableArrayList();//saves student details from db
	private ObservableList<String> classesDetails = FXCollections.observableArrayList();//saves class details from db
	private ObservableList<String> blockedStudents = FXCollections.observableArrayList();//saves blocked students in current semester
	ArrayList<String> arr ;//uses to send parameters to server
	int newYear;
	char newSemester;
	HashMap<Integer, String> classes;//saves all classes in current semester
	HashMap<String, ArrayList<String>> studentsInClass;//saves all students of chosen class in combo box
	HashMap<String, ArrayList<String>> blockedStudentsHM;

/**
 * change visible user window to appropriate window
 * @param pane
 */
	@FXML
	void setBlockParentAccessPaneHandler(ActionEvent event) {
		setPane(blockParentPane);
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	@FXML
	void setGetStatisticReportPaneHandler(ActionEvent event) {
		setPane(blockParentPane);
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	@FXML
	void setViewAllInformationPaneHandler(ActionEvent event) {
		setPane(blockParentPane);
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	@FXML
	void setAnswerRequestPaneHandler(ActionEvent event) {
		setPane(blockParentPane);
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	void setPane(Pane pane){
		blockParentPane.setVisible(false);
		pane.setVisible(true);
	}
	/**
	 * fills students cb with chosen class cb
	 * @param chosen class from cb
	 */
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
		if(studentsInClass!=null) studentsInClass.clear();
		studentsInClass = (HashMap<String, ArrayList<String>>)UserClient.ans;
		studentsDetails.clear();
		for(String id: studentsInClass.keySet())
			studentsDetails.addAll(id+" - "+studentsInClass.get(id).get(0)+" "+studentsInClass.get(id).get(1));
		cbStudents.setItems(studentsDetails);
	}
	/**
	 * change student access to "hidden" by his parent
	 * @param student from cb
	 */
	@FXML
	void blockAccessHandler(ActionEvent event){
		if(blockedStudents.contains(cbStudents.getSelectionModel().getSelectedItem())) return;
		if(cbClasses.getSelectionModel().getSelectedItem()!=null && cbStudents.getSelectionModel().getSelectedItem()!=null){
			arr.clear();
			arr.add(cbStudents.getSelectionModel().getSelectedItem().split("\\s+")[0]);
			msg.put("blockAccess",arr);
			LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
			LoginController.syncWithServer();
			msg.clear();
			blockedStudents.add(cbStudents.getSelectionModel().getSelectedItem());
			lvStudents.setItems(blockedStudents);
		}
	}
	/**
	 * change student access to "visible" by his parent
	 * @param student from list view
	 */
	@FXML
	void returnAccessHandler(ActionEvent event){
		if(lvStudents.getSelectionModel().getSelectedItem()!=null){
			arr.clear();
			arr.add(lvStudents.getSelectionModel().getSelectedItem().split("\\s+")[0]);
			msg.put("returnAccess",arr);
			LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
			LoginController.syncWithServer();
			msg.clear();
			blockedStudents.remove(lvStudents.getSelectionModel().getSelectedItem());
			lvStudents.setItems(blockedStudents);
		}
	}


	/**
	 * change user status to "offline" and go back to login window
	 * @param 
	 */
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
	/**
	 * get and display all necessary data from db for blockParent window
	 * @param 
	 */
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
		if(newSemester == 'B'){//if next semester is 'B' current is 'A' and current year is the same
		arr.add("1");
		arr.add(String.valueOf(newYear));
		}else{
			arr.add("2");//if next semester is 'A' current is 'B' and current year is new year - 1 
			arr.add(String.valueOf(--newYear));
		}
		msg.clear();
		msg.put("getCurrentClasses",arr);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		msg.clear();
		classes = (HashMap<Integer,String>)UserClient.ans;
		classesDetails.addAll(((HashMap<Integer,String>)UserClient.ans).values());
		cbClasses.setItems(classesDetails);
		refreshListView();
	}
	/**
	 * display all blocked students in list view
	 */
	void refreshListView(){
		if(blockedStudents!=null) blockedStudents.clear();
		for(int cid: classes.keySet()){
			arr.clear();
			arr.add(String.valueOf(cid));
			msg.clear();
			msg.put("getStudentsInClass",arr);
			LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
			LoginController.syncWithServer();
			if(blockedStudentsHM!=null) blockedStudentsHM.clear();
			blockedStudentsHM = (HashMap<String, ArrayList<String>>)UserClient.ans;
		
			for(String id: blockedStudentsHM.keySet())
				if(blockedStudentsHM.get(id).get(2)!= null) 
					if(blockedStudentsHM.get(id).get(2).equals("1"))
					blockedStudents.add(id+" - "+blockedStudentsHM.get(id).get(0)+" "+blockedStudentsHM.get(id).get(1));
			
		}
		lvStudents.setItems(blockedStudents);

	}
}
