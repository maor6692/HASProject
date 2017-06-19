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
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
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
	private ObservableList<ClassInCourseRow> classesInCourse = FXCollections.observableArrayList();
	private ObservableList<String> teachersnames = FXCollections.observableArrayList();
	private ObservableList<TeacherComboBox> teachersBoxValues = FXCollections.observableArrayList();

	int newYear;
	char newSemester;
	ArrayList<Object> currSemester;

	@FXML
	private Pane paneRemoveStudent,paneChangeAppointment,paneCreateSemester,paneDefineClass,paneAddStudent;

	@FXML
	private Label lblStudentsPre,lblWarning,lblUser,lblSemester,lblWarningNoStudents,lblClassCreated,lblTooLongInput,lblWarningNoStudent,lblWarningEmptyFields,lblWarningStudentAlreadyAssigned,lblWarningClassIsAlreadyExist,lblWarningClassId;

	@FXML
	private TextField tfClassId,tfClassName,tfStudentId;

	@FXML
	private ComboBox<CourseComboBox> cmbCourse;

	private ComboBox<String> cmbTeacher;

	@FXML
	private ListView<String> lvStudents;
	
	
	@FXML
	private ListView<ClassesInListView> lvClasses;

	@FXML
	private TableView<Row> tblExceptions;

	@FXML
	private Button btnAssign;

	@FXML
	private TableView<ClassInCourseRow> tblClassTeacher;

	@FXML
	private TableColumn<ClassInCourseRow,TeacherComboBox> teachers;

	@FXML
	private TableColumn<ClassInCourseRow,String> classes;

	@FXML
	private Button btnMoveRight;

	@FXML
	private Button btnMoveLeft;
	ObservableList<ClassesInListView> selectedClasses;
	private HashMap<Integer,HashMap<Integer,String>> courses;
	HashMap<String, ArrayList<String>>	msg ;
	ArrayList<String> params;
	private CourseComboBox currCourseBox=null;
	HashMap<Integer,String> comboClasses;
	
	//out hashmap <teaching unit id,map of teachers in this teaching unit>;
	//inner hashmaps <teacher id,arraylist of teacher name and max hours>
	private HashMap<String,HashMap<String,ArrayList<String>>> teachersInfo;

	private HashMap<Integer, Integer> coursesWeeklyHours; 

	private boolean checkClassExists(ClassesInListView className){ // checks if class is exists in right side of table
		for(ClassInCourseRow temp : classesInCourse){
			if(temp.getClassName().equals(className.getClassName()))
				return true;
		}
		return false;
	}
	@FXML



	void addClassesToCourseHandler(ActionEvent event) {
		lblWarning.setVisible(false);
		if(cmbCourse.getValue() == null)
			return;
		selectedClasses = lvClasses.getSelectionModel().getSelectedItems(); // get selected elements from the left
		
		
		for(ClassesInListView temp : selectedClasses){ 
			if(checkClassExists(temp)) continue; // check if class exists
			tblClassTeacher.getItems().add(new ClassInCourseRow(temp.getClassName())); //classesInCourse.add(new Row(temp));
		}
		//Row
		
		
		//tblClassTeacher.setItems(classesInCourse); // set table from starter
	}
	@FXML
	void removeClassesFromCourseHandler(ActionEvent event) {
		lblWarning.setVisible(false);
		ClassInCourseRow selectedClass = tblClassTeacher.getSelectionModel().getSelectedItem();
		if(selectedClass == null)
			return;
		ObservableList<ClassInCourseRow> tempClassesInCourse = FXCollections.observableArrayList();
		for(ClassInCourseRow temp : classesInCourse){
			if(selectedClass.equals(temp)) continue;
			tempClassesInCourse.add(temp);
		}
		classesInCourse = tempClassesInCourse;
		tblClassTeacher.setItems(classesInCourse); // set table from starter
	}


	@FXML
	void chooseCourseHandler(ActionEvent event) {
		lblWarning.setVisible(false);
		currCourseBox = cmbCourse.getValue();
		// clean class in course table
		classesInCourse =  FXCollections.observableArrayList();
		teachersBoxValues = FXCollections.observableArrayList();

		HashMap<String,ArrayList<String>> teachersOfTeachingUnit = teachersInfo.get(String.valueOf(currCourseBox.getTeachingUnit()));//
		
		
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
		
		classes.setCellValueFactory(new PropertyValueFactory<>("className"));
		teachers.setCellValueFactory(new PropertyValueFactory<>("teacher"));
	
	       teachers.setOnEditCommit((CellEditEvent<ClassInCourseRow, TeacherComboBox> event2) -> {
	            TablePosition<ClassInCourseRow, TeacherComboBox> pos = event2.getTablePosition();
	 
	            TeacherComboBox newTeacherComboBox = event2.getNewValue();
	 
	            int row = pos.getRow();
	            ClassInCourseRow newRow = event2.getTableView().getItems().get(row);
	 
	            newRow.setTeacher(newTeacherComboBox.getTeacherName(),newTeacherComboBox);
	        });
		
		tblClassTeacher.setItems(classesInCourse);
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
	if( tfClassName.getText().length()>5){
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
	void assignClassesAndTeachersHandler(ActionEvent event) {
		lblWarning.setVisible(false);
		//validating choosing classes and teacher to any class:

		if(currCourseBox == null){
			lblWarning.setText("Choose course");
			lblWarning.setVisible(true);
			return;
		} else 	if(tblClassTeacher.getItems().isEmpty()){
			lblWarning.setText("Choose classes from left table");
			lblWarning.setVisible(true);
			return;
		}
		for(ClassInCourseRow t : tblClassTeacher.getItems()){
			if(t.getTeacherComboBox() == null){
				lblWarning.setText("Choose teacher for every class");
				lblWarning.setVisible(true);
				return;
			}
		}
		//get pre courses with class_in_course_id's of class who studied him
		HashMap<String, ArrayList<String>>	msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> tempPreCourses = new ArrayList<>();
		tempPreCourses.add(String.valueOf(currCourseBox.getCourseId()));
		msg.put("getPreCourses",tempPreCourses);
		LoginController.userClient.sendServer(msg); 
		LoginController.syncWithServer();
		msg.clear();
		HashMap <String,ArrayList<Integer>> preCourses = (HashMap <String,ArrayList<Integer>>) UserClient.ans; // for each pre course we have an array list of course_in_class that studied this course
		//--
		//get current working hours of teachers
		ArrayList<String> teachersId = new ArrayList<>();
		for(ClassInCourseRow t : tblClassTeacher.getItems()){
			teachersId.add(t.getTeacherComboBox().getTeacherId());
		}
		msg.put("getTeachersWorkingHours",teachersId);
		LoginController.userClient.sendServer(msg); 
		LoginController.syncWithServer();
		msg.clear();
		HashMap<String,Integer> teachersWorkingHours = (HashMap<String,Integer>) UserClient.ans;
		//--
		int weeklyhours = coursesWeeklyHours.get(currCourseBox.getCourseId());
		
		//check if teachers working hours + course weekly hours > max hours ----> then return an error
		//comment: cause we can assign the same teacher to different classes we need to sum classes * teaching hours of this course
		HashMap<String,Integer> teachersCounter = new HashMap<>();
		for(ClassInCourseRow t : tblClassTeacher.getItems()){ // for each row in this table, count times each teacher assigned
			if(teachersCounter.get(t.getTeacherComboBox().getTeacherId()) == null)
				teachersCounter.put(t.getTeacherComboBox().getTeacherId(),1);
			else{
				int cnt = teachersCounter.get(t.getTeacherComboBox().getTeacherId())+1;
				teachersCounter.replace(t.getTeacherComboBox().getTeacherId(),cnt);
			}
		}
		//start checking limit hours

		
		for(ClassInCourseRow t : tblClassTeacher.getItems()){ // for each row
			String teacherId = t.getTeacherComboBox().getTeacherId();
			if(t.getTeacherComboBox().getMaxHours() <  weeklyhours*teachersCounter.get(teacherId)+teachersWorkingHours.get(teacherId)){
				lblWarning.setText("teacher: "+t.getTeacher()+"exceeding max hours");
				lblWarning.setVisible(true);
				return;
			}
		}
		//--
		//preCourses = [courseid,[course_in_class_id,course_in_class_id],...]
		//next will be on few stages
		//first preActions: make conversion table, class name --> class id
		HashMap <String,String> classConvertToId = new HashMap<>();
		for(ClassesInListView c: lvClasses.getItems()){
			classConvertToId.put(c.getClassName(), String.valueOf(c.getClassId()));
		}
		//--
		//1. foreach right table row do:
		for(ClassInCourseRow t : tblClassTeacher.getItems()){
			ArrayList<String> class_in_courseRow = new ArrayList<>();
			class_in_courseRow.add(String.valueOf(currCourseBox.getCourseId()));
			class_in_courseRow.add(classConvertToId.get(t.getClassName()));
			class_in_courseRow.add(t.getTeacherComboBox().getTeacherId());
			//add a row
			msg.put("addClass_in_courseRow",class_in_courseRow);
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			msg.clear();
		}
		//
		//
		//

		
		
		//we need choosen course
		System.out.println("current Course"+currCourseBox.getCourseId()+" "+currCourseBox.getCourseName());
		for(ClassInCourseRow t : tblClassTeacher.getItems()){
				System.out.println(t.getClassName()+" "+t.getTeacher());
				System.out.println(""+t.getTeacherComboBox().getTeacherName());
				System.out.println(" "+t.getTeacherComboBox().getTeacherId()+" "+t.getTeacherComboBox().getMaxHours());
				
				
				
		}
		
		
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
		//TeachingUnit-->[Map<courseId,courseName>]
		ArrayList<Object> tempAns = (ArrayList<Object>)UserClient.ans;
		courses  = (HashMap<Integer,HashMap<Integer,String>>) tempAns.get(0);
		coursesWeeklyHours = (HashMap<Integer,Integer>) tempAns.get(1);
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
		/*// current(old) way of getting course:(needs to be changed for getting course id)
		ArrayList<String> comboClasses = (ArrayList<String>) UserClient.ans;
		for(String comboClass: comboClasses)
			lvClasses.getItems().add(comboClass);
		lvClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//--*/
		//current new way of getting course:
		comboClasses = (HashMap<Integer,String>) UserClient.ans;
		Collection<Integer> classesKeySet = comboClasses.keySet();
		for(int classId:classesKeySet){
			String className = comboClasses.get(classId);
			ClassesInListView newClass = new ClassesInListView(classId,className);
			lvClasses.getItems().add(newClass);
		}
		lvClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		//--
		//get teachers
		msg.put("getTeachers",new ArrayList<String>());
		LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
		LoginController.syncWithServer();
		teachersInfo=(HashMap<String,HashMap<String,ArrayList<String>>>) UserClient.ans;
		

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
	public static class ClassInCourseRow {

		private final SimpleStringProperty className;
		private final SimpleStringProperty teacher;
		private TeacherComboBox teacherComboBox=null;

		private ClassInCourseRow(String className,String teacher) {
			this.className = new SimpleStringProperty(className);
			this.teacher = new SimpleStringProperty(teacher);
		}
		private ClassInCourseRow(String className) {
			this.className = new SimpleStringProperty(className);
			this.teacher = new SimpleStringProperty("");
		}

		public void setClassName(String row) {
			this.className.set(row);
		}

		public void setTeacher(String t,TeacherComboBox tcb) {
			this.teacher.set(t);
			this.teacherComboBox = tcb;
		}		
		public TeacherComboBox getTeacherComboBox(){
			return teacherComboBox;
		}
		public String getClassName() {
			return className.get();
		}
		public String getTeacher() {
			return teacher.get();
		}
		public boolean equals(ClassInCourseRow r){
			if(this.getClassName().equals(r.getClassName())){
				return true;
			}
			return false;
		}
	}
	class ClassesInListView {
		private String className;
		private int classId;
		public ClassesInListView(int id,String name){
			className = name;
			classId  = id;
		}
		public String getClassName(){
			return className;
		}
		public int getClassId(){
			return classId;
		}
		public String toString(){
			return className;
		}
		public void setClassName(String name){
			className = name;
		}
		public void setClassId(int id){
			classId = id;
		}
	}

} 



