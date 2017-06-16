package controller;



import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
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
	private ObservableList<TeacherComboBox> teachersBoxValues = FXCollections.observableArrayList();

	int newYear;
	char newSemester;
	ArrayList<Object> currSemester;

	@FXML
	private Pane paneRemoveStudent,paneChangeAppointment,paneCreateSemester,paneDefineClass,paneAddStudent;

	@FXML
	private Label lblStudentsPre,lblWarning,lblUser,lblSemester,lblWarningNoStudents,lblClassCreated,lblTooLongInput,lblWarningNoStudent,lblWarningEmptyFields,lblWarningStudentAlreadyAssigned,lblWarningClassIsAlreadyExist;

	@FXML
	private TextField tfClassName,tfStudentId;

	@FXML
	private ComboBox<CourseComboBox> cmbCourse;

	private ComboBox<String> cmbTeacher;

	@FXML
	private ListView<String> lvClasses,lvStudents;

	@FXML
	private TableView<Row> tblClassTeacher,tblExceptions;

	@FXML
	private Button btnAssign;


	@FXML
	private TableColumn<ComboBoxTableCell,TeacherComboBox> teachers;

	@FXML
	private TableColumn<String,String> classes;

	@FXML
	private Button btnMoveRight;

	@FXML
	private Button btnMoveLeft;
	ObservableList<String> selectedClasses;
	private HashMap<Integer,HashMap<Integer,String>> courses;
	HashMap<String, ArrayList<String>>	msg ;
	ArrayList<String> params;
	private CourseComboBox currCourseBox=null;

	
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
		//TODO : teachers appoinment will not change after adding more courses
		if(cmbCourse.getValue() == null)
			return;
		selectedClasses = lvClasses.getSelectionModel().getSelectedItems(); // get selected elements from the left

		for(String temp : selectedClasses){ 
			if(checkClassExists(temp)) continue; // check if class exists
			tblClassTeacher.getItems().add(new Row(temp)); //classesInCourse.add(new Row(temp));
		}
		//Row
		
		
		//tblClassTeacher.setItems(classesInCourse); // set table from starter
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
		teachersBoxValues = FXCollections.observableArrayList();
		tblClassTeacher.setItems(classesInCourse); // set table from starter
		//*****************************************************************************************************************
		HashMap<String,ArrayList<String>> teachersOfTeachingUnit = teachersInfo.get(String.valueOf(currCourseBox.getTeachingUnit()));//insert teaching unit of course instead "unit"
		//TODO : handle no teachers
		//******************************************************************************************************************
		Collection<String> teachersOfTeachingUnitKeySet = teachersOfTeachingUnit.keySet();
		for(String tid : teachersOfTeachingUnitKeySet){ // foreach teacher id in this teaching unit
			ArrayList<String> teacherAtt = teachersOfTeachingUnit.get(tid); // get teacher's info
			TeacherComboBox teacherObj = new TeacherComboBox(currCourseBox.getTeachingUnit(),tid,teacherAtt.get(0)+" "+teacherAtt.get(1),Integer.parseInt(teacherAtt.get(2)));
			if(teachersBoxValues.isEmpty())
				teachersBoxValues.add(teacherObj);
			else{
				if(!teacherInComboBox(teacherObj))
					teachersBoxValues.add(teacherObj);
					
					}
			
			
					
		}
		//**


		

		teachers.setCellFactory(ComboBoxTableCell.forTableColumn(teachersBoxValues));


	}

public boolean teacherInComboBox(TeacherComboBox teacherObj){
	for(TeacherComboBox temp : teachersBoxValues){
		if(temp.getTeacherId().equals(teacherObj.getTeacherId()))
			return true;
	}
	return false;
}
	
	/**
	 * create the new class with by user input
	 */
	@FXML
	void createClassHandler(ActionEvent event) {
		hideLabels();
		if(tfClassName.getText().equals("")){
			lblWarningEmptyFields.setVisible(true);
			return;
		}
		if( tfClassName.getText().length()>11){
			lblTooLongInput.setVisible(true);
			return;
		}
		msg = new HashMap<String, ArrayList<String>>();
		params = new ArrayList<String>();
		if(!(lvStudents.getItems().size()==0)){			
			msg.put("getCurrentSemester",null);
			LoginController.userClient.sendServer(msg);//ask from server to return the next semester details
			LoginController.syncWithServer();
			msg.clear();
			currSemester=(ArrayList<Object>) UserClient.ans;
			newYear=(int) currSemester.get(0);
			newSemester=(char) currSemester.get(1);
			int sem = (newSemester == 'A' ? 1 : 2);

			params.add(tfClassName.getText());
			params.add(String.valueOf(newYear));
			params.add(String.valueOf(sem));
			msg.put("checkClassIsNotExist",params);
			LoginController.userClient.sendServer(msg);//ask from server to check that there is no such class already
			LoginController.syncWithServer();
			msg.clear();
			params.clear();
			if(UserClient.ans.equals("no")){ 
				lblWarningClassIsAlreadyExist.setVisible(true);
				return;
			}

			params.add(tfClassName.getText());
			params.add(String.valueOf(newYear));
			params.add(String.valueOf(sem));
			msg.put("createClass",params);
			LoginController.userClient.sendServer(msg);//ask from server to create the  new class
			LoginController.syncWithServer();
			msg.clear();
			params.clear();

			params.add((String) UserClient.ans);
			params.addAll(lvStudents.getItems());
			msg.put("assignStudentsToCourseInClass",params);
			LoginController.userClient.sendServer(msg);//ask from server to assign students to the new class 
			LoginController.syncWithServer();
			msg.clear();
			params.clear();
			lblClassCreated.setVisible(true);
			lblWarningNoStudents.setVisible(false);
			students.clear();
			tfClassName.setText("");
			tfStudentId.setText("");
			lvStudents.setItems(students);
		}
		else lblWarningNoStudents.setVisible(true);
	}
	
	
	/**
	 * add student to lvStudents by tfStudentId input
	 */
	@FXML
	void addStudentToTableHandler(ActionEvent event) {
		hideLabels();
		if(tfStudentId.getText().equals("")) return;
		if(tfStudentId.getText().length()>45){
			lblTooLongInput.setVisible(true);
			return;
		}
		msg = new HashMap<String, ArrayList<String>>();
		params = new ArrayList<String>();
		params.add(tfStudentId.getText());
		msg.put("isStudent",params);
		LoginController.userClient.sendServer(msg);//ask from server to assign students to the new class 
		LoginController.syncWithServer();
		msg.clear();
		params.clear();
		if(UserClient.ans.equals("no")){ 
			lblWarningNoStudent.setVisible(true);
			return;
		}
		if(UserClient.ans.equals("alreadyAssigned")){ 
			lblWarningStudentAlreadyAssigned.setVisible(true);
			return;
		}
		if(students.contains(tfStudentId.getText())) return;
		
		students.add(tfStudentId.getText());
		lvStudents.setItems(students);
	}

	/**
	 * remove selected student from lvStudents
	 */
	@FXML
	void removeStudentFromTableHandler(ActionEvent event) {
		hideLabels();
		if(lvStudents.getSelectionModel().getSelectedItem()==null){
			if(!students.contains(tfStudentId.getText())) return;
			students.remove(tfStudentId.getText());
		}
		else students.remove(lvStudents.getSelectionModel().getSelectedItem());
		lvStudents.setItems(students);
	}
/**
 * set visible of all warnings labels to false
 */
	void hideLabels(){
		lblClassCreated.setVisible(false);
		lblWarningStudentAlreadyAssigned.setVisible(false);
		lblWarningClassIsAlreadyExist.setVisible(false);
		lblWarningNoStudents.setVisible(false);
		lblWarningEmptyFields.setVisible(false);
		lblWarningNoStudent.setVisible(false);
		lblTooLongInput.setVisible(false);
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

	void initializeDefineCourse(){

	}

	void initializeCreateSemester(){
		HashMap<String, ArrayList<String>>	msg = new HashMap<String, ArrayList<String>>();
		msg.put("getCurrentSemester",null);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		msg.clear();
		lblWarning.setVisible(false);
		lblStudentsPre.setVisible(false);
		tblExceptions.setVisible(false);
		tblClassTeacher.setPlaceholder(new Label("Select A Course And Add classes"));
		currSemester=(ArrayList<Object>) UserClient.ans;
		newYear=(int) currSemester.get(0);
		newSemester=(char) currSemester.get(1);
		lblSemester.setText("The new semester : "+newYear+"/"+newSemester);
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(String.valueOf(newYear));
		arr.add(String.valueOf(newSemester == 'A' ? 1 : 2));
		//Get Courses
		msg.put("getCurrentCourses",arr);
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		courses  = (HashMap<Integer,HashMap<Integer,String>>) UserClient.ans;
		Collection<Integer> teachingUnits = courses.keySet();
		//Collection<HashMap<Integer,String>> allCoursesMapsCollection = courses.values();
		//Collection<String>  courseCollection = courseCollectionMap.values();
		for(int singleTeachUnit : teachingUnits){ // foreach teaching unit
			HashMap<Integer,String> coursesOfTeachingUnit = courses.get(singleTeachUnit); // get all course of single teaching unit
			Collection<Integer> coursesIdOfSingleTeachUnit = coursesOfTeachingUnit.keySet();
			for(int singleCourseId : coursesIdOfSingleTeachUnit){ // foreach course in teaching unit
				CourseComboBox cmbRow;
				String courseName = coursesOfTeachingUnit.get(singleCourseId);
				cmbRow = new CourseComboBox(singleTeachUnit,singleCourseId,courseName);
				cmbCourse.getItems().add(cmbRow);
			}
		}
		
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
	public class CourseComboBox {
		private int teachingUnit;
		private int courseId;
		private String courseName;
		public CourseComboBox(int tu,int cid,String cname){
			teachingUnit = tu;
			courseId = cid;
			courseName = cname;
		}
		public int getTeachingUnit() {
			// TODO Auto-generated method stub
			return teachingUnit;
		}
		public int getCourseId() {
			// TODO Auto-generated method stub
			return courseId;
		}
		public String getCourseName() {
			// TODO Auto-generated method stub
			return courseName;
		}
		public String toString(){
			return ""+courseName;
		}
	}

	public class TeacherComboBox {
		private int teachingUnit;
		private String teacherId;
		private String teacherName;
		private int maxHours;
		public TeacherComboBox(int tu,String tid,String tname,int h){
			teachingUnit = tu;
			teacherId = tid;
			teacherName = tname;
			maxHours = h;
		}
		public int getTeachingUnit() {
			
			return teachingUnit;
		}
		public int getMaxHours() {
			
			return maxHours;
		}
		public String getTeacherId() {
			// TODO Auto-generated method stub
			return teacherId;
		}
		public String getTeacherName() {
			// TODO Auto-generated method stub
			return teacherName;
		}
		public String toString(){
			return ""+teacherName;
		}
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



