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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SecretaryController implements Initializable{
	ComboBoxTableCell cb = new ComboBoxTableCell();

	private ObservableList<ClassInCourse> classesInCourse = FXCollections.observableArrayList();
	
	
	int newYear,newSemester;
	String currSemester="";

	@FXML
	private Pane paneRemoveStudent,paneChangeAppointment,paneCreateSemester,paneDefineClass,paneAddStudent;

	@FXML
	private Label lblUser,lblSemester;

	@FXML
	private ComboBox<String> cmbCourse;

	private ComboBox<String> cmbTeacher;

	@FXML
	private ListView<String> lvClasses;

	@FXML
	private TableView<ClassInCourse> tblClassTeacher;

	@FXML
	private TableView<String> tblExceptions;

	@FXML
	private Button btnAssign;


	@FXML
	private TableColumn<ComboBoxTableCell,String> teachers;

	@FXML
	private TableColumn<String,String> classes;

    @FXML
    private Button btnMoveRight;

    @FXML
    private Button btnMoveLeft;
    ObservableList<String> selectedClasses;
    
    private String currCourseBox=null;
    
  //out hashmap <teaching unit id,map of teachers in this teaching unit>;
    //inner hashmaps <teacher id,arraylist of teacher name and max hours>
    private HashMap<Integer,HashMap<Integer,ArrayList<String>>> teachersInfo; 
    
	private boolean checkClassExists(String className){ // checks if class is exists in right table
		for(ClassInCourse temp : classesInCourse){
			if(temp.getClassInCourse().equals(className))
				return true;
		}
		return false;
	}
	  @FXML
	  
	  
	  
	    void addClassesToCourseHandler(ActionEvent event) {
		  if(cmbCourse.getValue() == null)
			  return;
		  selectedClasses = lvClasses.getSelectionModel().getSelectedItems(); // get selected elements from the left
		 
		  for(String temp : selectedClasses){ 
			  if(checkClassExists(temp)) continue; // returns true if class exists
			  classesInCourse.add(new ClassInCourse(temp));
		  }
		  
		  tblClassTeacher.setItems(classesInCourse); // set table from starter
	    }

	    @FXML
	    void removeClassesFromCourseHandler(ActionEvent event) {
	    	ClassInCourse selectedClass = tblClassTeacher.getSelectionModel().getSelectedItem();
	    	if(selectedClass == null)
	    		return;
	    	ObservableList<ClassInCourse> tempClassesInCourse = FXCollections.observableArrayList();
	    	for(ClassInCourse temp : classesInCourse){
	    		if(selectedClass.equals(temp)) continue;
	    		tempClassesInCourse.add(temp);
	    	}
	    	classesInCourse = tempClassesInCourse;
			  tblClassTeacher.setItems(classesInCourse); // set table from starter
	    }

	
	@FXML
	void listViewEditChoise(ActionEvent event) {

	}

	@FXML
	void classClickHandler(ActionEvent event) {//enable multiple choice
		
	}


	@FXML
	void chooseCourseHandler(ActionEvent event) {
		currCourseBox = cmbCourse.getValue();
		// clean class in course table
    	classesInCourse =  FXCollections.observableArrayList();
    	tblClassTeacher.setItems(classesInCourse); // set table from starter
    	
	}

	@FXML
	void chooseYearHandler(ActionEvent event) {

	}

	@FXML
	void chooseSemesterHandler(ActionEvent event) {

	}
	@FXML
	void assignClassesAndTeachersHandler(ActionEvent event) {

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
	void setPane(Pane pane){
		paneRemoveStudent.setVisible(false);
		paneChangeAppointment.setVisible(false);
		paneCreateSemester.setVisible(false);
		paneDefineClass.setVisible(false);
		paneAddStudent.setVisible(false);
		pane.setVisible(true);
	}

	@FXML
	void createSemesterHandler(ActionEvent event) {
		setPane(paneCreateSemester);
	}
	@FXML
	void addStudentToCourseHandler(ActionEvent event) {
		setPane(paneAddStudent);
	}
	@FXML
	void removeStudentFromCourseHandler(ActionEvent event) {
		setPane(paneRemoveStudent);
	}
	@FXML
	void changeAppointmentHandler(ActionEvent event) {
		setPane(paneChangeAppointment);
	}
	@FXML
	void defineClassHandler(ActionEvent event) {
		setPane(paneDefineClass);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblUser.setText(UserClient.fullName);
		initializeCreateSemester();

	}

	void initializeCreateSemester(){
		HashMap<String, ArrayList<String>>	msg = new HashMap<String, ArrayList<String>>();
		msg.put("getCurrentSemester",null);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		msg.clear();
		tblClassTeacher.setPlaceholder(new Label("Select A Course And Add classes"));
		currSemester=(String) UserClient.ans;
		if(currSemester.equals("")){
			LocalDate localDate = LocalDate.now();
			newYear=Integer.parseInt(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDate).substring(0, 4));
			newSemester=1;
		}else{
			newYear=Integer.parseInt(currSemester.substring(0, 4));
			newSemester=currSemester.charAt(4);
			if(newSemester==2){
				newSemester=1;
				newYear++;
			}else newSemester = 1;
		}
		char sem = newSemester > 1 ? 'B' : 'A';
		lblSemester.setText("The new semester : "+newYear+"/"+sem);
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(String.valueOf(newYear));
		arr.add(String.valueOf(newSemester));
		msg.put("getCurrentCourses",arr);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		ArrayList<String> courses = (ArrayList<String>) UserClient.ans;
		for(String course: courses)
			cmbCourse.getItems().add(course);
		msg.clear();
		
		msg.put("getCurrentClasses",arr);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		ArrayList<String> comboClasses = (ArrayList<String>) UserClient.ans;
		for(String comboClass: comboClasses)
			lvClasses.getItems().add(comboClass);
		lvClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		classes.setCellValueFactory(new PropertyValueFactory<>("classInCourse"));
		
		//get teachers
		msg.put("getTeachers",arr);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		msg.clear();
		
		//teachersInfo=(HashMap<Integer,HashMap<Integer,String>>) UserClient.ans;
		
		teachers.setCellFactory(ComboBoxTableCell.forTableColumn("avi sofer ben zona","Malki","Ilya"));
		tblClassTeacher.setItems(classesInCourse);

	}
	public static class ClassInCourse {

		private final SimpleStringProperty classInCourse;

		private ClassInCourse(String classInCourse) {
			this.classInCourse = new SimpleStringProperty(classInCourse);

		}

		public String ClassInCourse() {
			return classInCourse.get();
		}

		public void ClassInCourse(String class1) {
			classInCourse.set(class1);
		}

		public String getClassInCourse() {
			return classInCourse.get();
		}
		
		public boolean equals(ClassInCourse a){
			if(this.getClassInCourse().equals(a.getClassInCourse())){
				return true;
			}
			return false;
		}
	}

} 



