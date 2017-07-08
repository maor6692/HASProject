package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SystemManagerController implements Initializable{
	public static HashMap<String, ArrayList<String>> msg;
	@FXML
	private Hyperlink linkLogout;
	@FXML
	private Pane defineCoursePane;
	@FXML
	private TextField tfWeeklyHours;


	@FXML
	private Label lblYear;

	@FXML
	private TextField tfSemester;

	@FXML
	private Label lblDefineCourse;

	@FXML
	private Label lblCourseID;

	@FXML
	private Label lblCourseName;

	@FXML
	private TextField tfYear;

	@FXML
	private Label lblUser; 
	@FXML
	private Label lblError;

	@FXML
	private Label lblTeachingUnit;

	@FXML
	private TextField tfCourseID;

	@FXML
	private Label lblWeeklyHours;

	@FXML
	private Label lblSemester;

	@FXML
	private TextField tfCourseName;

	@FXML
	private ComboBox<String> cbTeachingUnit;

	@FXML
	private Button btnCreateCourse;
	/**
	 * sets the pane of the controller to define course
	 * @param event
	 */
	@FXML
	void defineCourseHandler(ActionEvent event) {
		setPane(defineCoursePane);
	}
	/**
	 * create new course for selected teaching unit
	 * @param event
	 */
	@FXML
	void createCourseHandler(ActionEvent event) {

		lblError.setText("Course already exist");
		lblError.setVisible(false);

		if (isEmptyFields(tfCourseID.getText(),tfCourseName.getText(), tfWeeklyHours.getText() , cbTeachingUnit.getValue()))
		{
			lblError.setText("Please fill all the fields");
			lblError.setVisible(true);
			return;
		}
		if (!checkWeeklyHours(tfWeeklyHours.getText())){
			lblError.setText("illegal hours");
			lblError.setVisible(true);
			return;
		};
		msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> courseInfo = new ArrayList<String>();
		String tu[];
		//try{
		if(checkCourseID(tfCourseID.getText()))	courseInfo.add(String.valueOf(Integer.parseInt(tfCourseID.getText())));
		else{
			lblError.setText("course id must be 3 digits");
			lblError.setVisible(true);
		//	JOptionPane.showMessageDialog(null, "Illegal value");
			return;
		}
		tu = cbTeachingUnit.getValue().split("\\s");
		int year=0,semester = 0;
		char sem = ' ';
		msg.clear();
		msg.put("getCurrentSemester",null);
		LoginController.userClient.sendServer(msg);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null){
			year = ((ArrayList<Integer>)LoginController.userClient.ans).get(0);
			semester = ((ArrayList<Character>)LoginController.userClient.ans).get(1)=='A'?1:2;

			if (isCourseAlreadyExist(tfCourseID.getText(), tfCourseName.getText(), cbTeachingUnit.getValue(), tfWeeklyHours.getText(), String.valueOf(year), String.valueOf(semester),LoginController.userClient))
			{
				lblError.setVisible(true);
				return;
			}
			else
			{
				courseInfo.clear();
				courseInfo.add(String.valueOf(Integer.parseInt(tfCourseID.getText())));
				courseInfo.add(tfCourseName.getText());
				courseInfo.add(tu[0]);
				courseInfo.add(tfWeeklyHours.getText());
				courseInfo.add(String.valueOf(year));
				courseInfo.add(String.valueOf(semester));
				lblError.setVisible(false);
				msg.clear();	
				msg.put("Create Course",courseInfo);
				LoginController.userClient.sendServer(msg);
				LoginController.syncWithServer();
				lblError.setText("Course added successfully");
				lblError.setVisible(true);
			//	JOptionPane.showMessageDialog(null, "Course added successfully");
				return;
			}
		}
	}
	public static boolean isEmptyFields(String courseID,String courseName, String weeklyHours , String teachingUnit ) {
		if (courseID.equals("") || courseName.equals("") || weeklyHours.equals("") || teachingUnit==null) return true;
		else return false;
	}

	public static boolean checkWeeklyHours(String weeklyHours){
		try{
			if((Integer.parseInt(weeklyHours))<0) return false;
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}

	public static boolean checkCourseID(String courseID){
		try{
			if(courseID.length()!=3 || (Integer.parseInt(courseID))<0) return false;
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}

	public static boolean isCourseAlreadyExist(String courseName ,String courseID,String teachingUnit,String weeklyHours,String year,String semester,UserClient userClient){
		ArrayList<String> courseInfo = new ArrayList<String>();
		HashMap<String, ArrayList<String>> msg = new HashMap<String, ArrayList<String>>();
		courseInfo.add(courseID);
		courseInfo.add(courseName);
		courseInfo.add(teachingUnit.split("\\s")[0]);
		courseInfo.add(weeklyHours);
		courseInfo.add(year);
		courseInfo.add(semester);
		msg.put("check if course exists",courseInfo);
		userClient.sendServer(msg); 
		
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
		}
		if(userClient.ans != null &&((String)userClient.ans).equals("exist")) return true;
		return false;
	}


	/**
	 * logout handler, logout the user if X is pressed and change the window to login window.
	 * @param event
	 */
	@FXML
	void logoutHandler(ActionEvent event) {
		Parent nextWindow;
		try {
			LoginController.logout();
			nextWindow = FXMLLoader.load(getClass().getResource("/gui/loginWindow.fxml"));
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
	/**
	 * sets the pane to the selected parameter
	 * @param pane - selected pane to display
	 * 
	 */
	void setPane(Pane pane){
		pane.setVisible(true);
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		lblUser.setText(UserClient.fullName);
		msg = new HashMap<String, ArrayList<String>>();
		msg.put("Get teaching unit",new ArrayList<String>());
		LoginController.userClient.sendServer(msg);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				cbTeachingUnit.getItems().add(i,((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
			}
		}

	}
}
