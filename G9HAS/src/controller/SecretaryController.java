package controller;



import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.DefaultStringConverter;

public class SecretaryController implements Initializable{
	ComboBoxTableCell cb = new ComboBoxTableCell();

	private ObservableList<String> students = FXCollections.observableArrayList();
	private ObservableList<Row> classesInCourse = FXCollections.observableArrayList();
	private ObservableList<String> teachersnames = FXCollections.observableArrayList();

	int newYear,newSemester;
	String currSemester="";

	@FXML
	private Pane paneRemoveStudent,paneChangeAppointment,paneCreateSemester,paneDefineClass,paneAddStudent;

	@FXML
	private Label lblUser,lblSemester;

	@FXML
	private TextField tfClassId,tfClassName,tfStudentId;

	@FXML
	private ComboBox<String> cmbCourse;

	private ComboBox<String> cmbTeacher;

	@FXML
	private ListView<String> lvClasses,lvStudents;

	@FXML
	private TableView<Row> tblClassTeacher,tblExceptions;

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
	private HashMap<String,HashMap<String,ArrayList<String>>> teachersInfo; 

	private boolean checkClassExists(String className){ // checks if class is exists in right side of table
		for(Row temp : classesInCourse){
			if(temp.getRow().equals(className))
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
			classesInCourse.add(new Row(temp));
		}

		tblClassTeacher.setItems(classesInCourse); // set table from starter
	}
	@FXML
	void removeClassesFromCourseHandler(ActionEvent event) {
		Row selectedClass = tblClassTeacher.getSelectionModel().getSelectedItem();
		if(selectedClass == null)
			return;
		ObservableList<Row> tempClassesInCourse = FXCollections.observableArrayList();
		for(Row temp : classesInCourse){
			if(selectedClass.equals(temp)) continue;
			tempClassesInCourse.add(temp);
		}
		classesInCourse = tempClassesInCourse;
		tblClassTeacher.setItems(classesInCourse); // set table from starter
	}
	@FXML
	void inputChangedHandler(ActionEvent event) {
		event.getSource();
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
		//*****************************************************************************************************************
		HashMap<String,ArrayList<String>> values = teachersInfo.get("unit");//insert teaching unit of course instead "unit"
		//******************************************************************************************************************
		if(values!=null)
			for(ArrayList<String> lists: values.values()){
				if(!teachersnames.contains(lists.get(0)+" "+lists.get(1))) teachersnames.add(lists.get(0)+" "+lists.get(1));
			}
		Collections.sort(teachersnames);
		teachers.setCellFactory(ComboBoxTableCell.forTableColumn(teachersnames));

	}
	@FXML
	void createClassHandler(ActionEvent event) {

	}

	@FXML
	void addStudentToTableHandler(ActionEvent event) {
		if(students.contains(tfStudentId.getText())) return;
		students.add(tfStudentId.getText());
		lvStudents.setItems(students);
	}

	@FXML
	void removeStudentFromTableHandler(ActionEvent event) {
		if(lvStudents.getSelectionModel().getSelectedItem()==null){
			if(!students.contains(tfStudentId.getText())) return;
			students.remove(tfStudentId.getText());
		}
		else students.remove(lvStudents.getSelectionModel().getSelectedItem());
		lvStudents.setItems(students);
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
		msg.clear();
		ArrayList<String> comboClasses = (ArrayList<String>) UserClient.ans;
		for(String comboClass: comboClasses)
			lvClasses.getItems().add(comboClass);
		lvClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		classes.setCellValueFactory(new PropertyValueFactory<>("row"));
		//teachers.setCellValueFactory(new PropertyValueFactory<>("teacher"));

		//get teachers
		msg.put("getTeachers",new ArrayList<String>());
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		teachersInfo=(HashMap<String,HashMap<String,ArrayList<String>>>) UserClient.ans;
		tblClassTeacher.setItems(classesInCourse);

	}
	public static class Row {

		private final SimpleStringProperty row;

		private Row(String row) {
			this.row = new SimpleStringProperty(row);

		}

		public void setRow(String row) {
			this.row.set(row);
		}

		public String getRow() {
			return row.get();
		}

		public boolean equals(Row row){
			if(this.getRow().equals(row.getRow())){
				return true;
			}
			return false;
		}
	}

} 



