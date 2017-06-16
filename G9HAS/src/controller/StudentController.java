package controller;

import java.awt.List;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StudentController implements Initializable {


	@FXML
	private Hyperlink linkLogout;

	@FXML
	private Label lblCPersonalInfo;

	@FXML
	private Label lblUser;
	@FXML
	private TextArea taTasks;

	@FXML
	private ComboBox<String> cbTask;

	@FXML
	private Label lblShowUsername;

	@FXML
	private ComboBox<String> cbCourseInClass;

	@FXML
	private Hyperlink hlPersonalInfo;

	@FXML
	private Label lblCourseAvg;

	@FXML
	private Label lblTaskGrade;

	@FXML
	private Label lblTask;

	@FXML
	private Label lblAvg;

	@FXML
	private Label lblUsername;

	@FXML
	private Label lblClass;

	@FXML
	private Label lblShowClass;

	@FXML
	void logoutHandler(ActionEvent event) {
		Parent nextWindow;
		try {
			HashMap<String, ArrayList<String>> msg = new HashMap<String, ArrayList<String>>();
			ArrayList<String> arr = new ArrayList<String>();
			arr.add(UserClient.userName);
			msg.put("logout",arr);
			LoginController.userClient.sendServer(msg);
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

	@FXML
	void cbCourseInClassHandler(ActionEvent event) {
		float gradeAvg=0;
		taTasks.setText("");
		taTasks.setVisible(true);
		lblCourseAvg.setVisible(true);
		ArrayList<String> arr = new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm= new HashMap<String,ArrayList<String>>();
		arr.add(LoginController.userClient.userName);
		String temp = "";
		for(int i=0;i<3;i++)
		{
			temp+= cbCourseInClass.getValue().charAt(i);
		}
		arr.add(temp);
		hm.put("get grades for student in course",arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null){
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				taTasks.setText(taTasks.getText()+ "Task no. "+ (i+1) + " Grade: "+((ArrayList<String>)LoginController.userClient.ans).get(i)+"\n");
				gradeAvg +=Float.parseFloat(((ArrayList<String>)LoginController.userClient.ans).get(i));
			}
		}
		lblCourseAvg.setText("Course Average: "+ gradeAvg/((ArrayList<String>)LoginController.userClient.ans).size());
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ArrayList<String> arr=new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm= new HashMap<String,ArrayList<String>>();
		lblUser.setText(UserClient.fullName);
		lblShowUsername.setText(UserClient.userName);
		arr.add(UserClient.userName);
		hm.put("Get student class", arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null){	
			lblShowClass.setText(((ArrayList<String>)LoginController.userClient.ans).get(0));
			((ArrayList<String>)LoginController.userClient.ans).clear();
		}
		else lblShowClass.setText("No class");
		hm.remove("Get student class");
		//System.out.println(arr.toString());
		/////work//////
		hm.put("get course in class", arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		//System.out.println(((ArrayList<String>)LoginController.userClient.ans).toString());
		arr.clear();
		if(LoginController.userClient.ans != null){
			for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
				arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
		}
		//work//
		hm.remove("get course in class");
		hm.put("get course id",arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		arr.clear();
		if(LoginController.userClient.ans != null){
			for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
				arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
		}
		//System.out.println(((ArrayList<String>)LoginController.userClient.ans).toString());
		hm.remove("get course id");
		hm.put("Search for course name",arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		//System.out.println(((ArrayList<String>)LoginController.userClient.ans).toString()+"!!!!!");
		if(LoginController.userClient.ans != null){
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				cbCourseInClass.getItems().add(i,arr.get(i)+" - "+((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
			}
		}
		////CHECKING AVERAGE GRADE FOR STUDENT IN CURRENT SEMESTER
		arr.clear();
		int year=0,semester = 0;
		char sem = ' ';
		hm.remove("Search for course name");
		hm.put("getCurrentSemester",null);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null){
			year = ((ArrayList<Integer>)LoginController.userClient.ans).get(0);
			sem = ((ArrayList<Character>)LoginController.userClient.ans).get(1);
			if(sem=='A')
			{
				year = year--;
				semester = 2;
			}
			else
			{
				semester = 1;
			}
		}
		arr.clear();
		arr.add(LoginController.userClient.userName);
		arr.add(String.valueOf(year));
		arr.add(String.valueOf(semester));
		hm.remove("getCurrentSemester");
		hm.put("get course in class",arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		
		
		ArrayList<String> arr1 = new ArrayList<String>();
		arr1.add(String.valueOf(year));
		arr1.add(String.valueOf(semester));
		arr1.add(LoginController.userClient.userName);
		if(LoginController.userClient.ans != null){
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				arr1.add((i+3),((ArrayList<String>)LoginController.userClient.ans).get(i));
			}
		}
		
		hm.remove("get course in class");
		hm.put("get student average for current semester",arr1);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		float sumgrades = 0;
		if(LoginController.userClient.ans != null){
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				sumgrades += Float.parseFloat(((ArrayList<String>)LoginController.userClient.ans).get(i));
			}
		}
		//System.out.println(sumgrades/((ArrayList<String>)LoginController.userClient.ans).size());
		lblAvg.setText(String.valueOf((Float)(sumgrades/((ArrayList<String>)LoginController.userClient.ans).size())));
//		hm.remove("get course in class");
//		hm.put("get course id",((ArrayList<String>)LoginController.userClient.ans));
//		LoginController.userClient.sendServer(hm);
//		LoginController.syncWithServer();
        
	}

}

