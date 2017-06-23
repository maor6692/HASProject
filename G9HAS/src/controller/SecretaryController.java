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

import javax.swing.JOptionPane;

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
	private ArrayList<String> studentCourse = new ArrayList<String>();
	ArrayList<Object> currSemester;

	
	@FXML
	private Pane paneRemoveStudent,paneChangeAppointment,paneCreateSemester,paneDefineClass,paneAddStudent;

	@FXML
	private Label lblStudentsPre,lblWarning,lblUser,lblSemester,lblWarningNoStudents,lblClassCreated,lblTooLongInput,lblWarningNoStudent,lblWarningEmptyFields,lblWarningStudentAlreadyAssigned,lblWarningClassIsAlreadyExist,lblWarningClassId;

	@FXML
	private TextField tfClassId,tfClassName,tfStudentId;
    @FXML
    private Label lblChooseStudentRS;
	@FXML
	private ComboBox<CourseComboBox> cmbCourse;
	
	private ComboBox<String> cmbTeacher;
	@FXML
	private ComboBox<ClassesInListView> cmbChooseStudentClassAS;
	
	@FXML
	private ListView<String> lvStudents;
    @FXML
    private Button btnSendRequestToManager;
    @FXML
    private Button btnAddAS;
    @FXML
    private ComboBox<String> cbClass;
    @FXML
    private ComboBox<String> cbCourse;
 
    @FXML
    private ComboBox<String> cbChooseCourseRS;

    @FXML
    private ComboBox<String> cbChooseStudentRS;
	
	@FXML
	private ListView<ClassesInListView> lvClasses;
	
	@FXML
	private ComboBox<StudentsInClassAS> cmbChooseStudentAS;
	@FXML
	private TableView<StudentsExp> tblExceptions;

	@FXML
	private ComboBox<CourseComboBox> cmbChooseCourseAS;
	@FXML
	private Button btnAssign;
    @FXML
    private Label lblSuccessAS,lblErrAS;
    @FXML
    private TableColumn<StudentsExp, String> colSidExp;
    @FXML
    private TableColumn<StudentsExp, String> colSnExp;
    @FXML
    private TableColumn<StudentsExp, String> colCnExp;
    @FXML
    private TableColumn<StudentsExp, String> colCidExp;
	@FXML
	private TableView<ClassInCourseRow> tblClassTeacher;
	

    @FXML
    private TableColumn<ViewInboxTbl, String> colIdVI;
    
    @FXML
    private TableView<ViewInboxTbl> tblViewInbox;
    

    @FXML
    private TableColumn<ViewInboxTbl, String> colMsgVI;
    
    private ObservableList <ViewInboxTbl> secretaryInbox;

	@FXML
	private TableColumn<ClassInCourseRow,TeacherComboBox> teachers;

	@FXML
	private TableColumn<ClassInCourseRow,String> classes;

	@FXML
	private Button btnMoveRight;

	@FXML
	private Button btnMoveLeft;
	@FXML
	private ComboBox<ClassInCourseAS> cmbClassInCourseAS;
	
	ObservableList<ClassesInListView> selectedClasses;
	private HashMap<Integer,HashMap<Integer,String>> courses;
	HashMap<String, ArrayList<String>>	msg ;
	ArrayList<String> params;
	private CourseComboBox currCourseBox=null;
	private CourseComboBox currCourseAS=null;
	HashMap<Integer,String> comboClasses;
	ArrayList<String> currSemAS;
	ArrayList<String> args = new ArrayList<String>();///kobi//
	private ObservableList<StudentsExp> ExceptionStudents = FXCollections.observableArrayList();
	//out hashmap <teaching unit id,map of teachers in this teaching unit>;
	//inner hashmaps <teacher id,arraylist of teacher name and max hours>
	private HashMap<String,HashMap<String,ArrayList<String>>> teachersInfo;
	private HashMap<String,ArrayList<String>> tCTA;

	private HashMap<Integer, Integer> coursesWeeklyHours;
	@FXML
	private Pane paneViewInbox; 

	/**
	 * send adding request of student to a course in class 
	 * @param event, course, course in class, student
	 */
	@SuppressWarnings("unchecked")
	/////////////////FOR CHANGE TEACHER'S APPOINTMENT///////////////
	   @FXML
	    void cbChooseCourseHandler(ActionEvent event) {
		   if(cbCourse.getSelectionModel().getSelectedItem()==null) return;
		   cbClass.getItems().clear();
/*		   HashMap<Integer,String> cls=new HashMap<Integer,String>();
		   HashMap<String,ArrayList<String>> ms=new HashMap<String,ArrayList<String>>();
			ms.clear();
			ms.put("getCurrentClasses",args);
			LoginController.userClient.sendServer(ms);//
			LoginController.syncWithServer();
			cls=(HashMap<Integer,String>)(LoginController.userClient.ans);	
			ArrayList<Integer> cid=new ArrayList<Integer>();
			String temp2;
			for(Integer key : cls.keySet()){
				temp2=cls.get(key);
				
				cbClass.getItems().add(key+" - "+temp2);
			}
			ms.clear();
			ms.put("get classes", value)*/
			String currCourse = cbCourse.getSelectionModel().getSelectedItem();
			msg = new HashMap<String, ArrayList<String>>();
			
			ArrayList<String> p = new ArrayList<>();
			p.add(cbCourse.getValue());
			msg.put("get course id from course name",p);
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			p.clear();
			String cid="";
			cid+=((ArrayList<String>)LoginController.userClient.ans).get(0);
			p.add(args.get(0));
			p.add(args.get(1));
			p.add(cid);
			msg.clear();
			msg.put("getClassOfCourseAS",p); // p=[year,semester,course_id]
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			msg.clear();
//			if(UserClient.ans instanceof HashMap<?,?>){
			//recieve Map<String,ArrayList<String>> -- [class_in_course_id,[class_id,class_name]]	
			 tCTA =(HashMap<String,ArrayList<String>>) UserClient.ans;
			for(ArrayList<String> classes : tCTA.values())
				if(classes!=null)
				cbClass.getItems().add(classes.get(0)+"-"+classes.get(1));
		
//	    }
			
	}
	    @FXML
	    void cbClassHandler(ActionEvent event) {
	    	HashMap <String,ArrayList<String>> hm=new HashMap <String,ArrayList<String>>();
	    	HashMap <String,ArrayList<String>> hm1=new HashMap <String,ArrayList<String>>();
	    	ArrayList<String> crs = new ArrayList<>();
	    	//for(int i=0;i<cbClass.getItems().size();i++)
	    		//crs.add(cbClass.getItems().get(i));
	    	String chosenClass=cbClass.getValue();
	    	String classid="";
	    	int i=0;
	    	while(chosenClass.charAt(i)!='-')
	    		classid+=chosenClass.charAt(i);
	    	crs.add(classid);
	    	hm.put("getStudentsInClass", crs);
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			
			
	    	

	    }
/////////////////FOR CHANGE TEACHER'S APPOINTMENT///////////////
	@FXML
	void addStudentASHandler(ActionEvent event){
		HashMap<String, ArrayList<String>>	msg = new HashMap<String, ArrayList<String>>();
		
		lblErrAS.setVisible(false);
		lblSuccessAS.setVisible(false);
		
		if(cmbChooseStudentAS.getSelectionModel().getSelectedItem() == null || cmbClassInCourseAS.getSelectionModel().getSelectedItem() == null) {
			lblErrAS.setVisible(true);
			return;
		}
		ArrayList<String> args = new ArrayList<>();
		args.add(cmbClassInCourseAS.getSelectionModel().getSelectedItem().getClassInCourseId());
		args.add(cmbChooseStudentAS.getSelectionModel().getSelectedItem().getStudentId());
		msg.put("check if student in class in course",args);
		LoginController.userClient.sendServer(msg);//
		LoginController.syncWithServer();
		msg.clear();
		if((boolean)UserClient.ans == true){
			lblErrAS.setText("Student already in this course!");
			lblErrAS.setVisible(true);
			return;
		}
		
		String detailes = "2:"+lblUser.getText()+":";
		detailes+=cmbChooseStudentAS.getSelectionModel().getSelectedItem().getStudentFirstName()+" ";
		detailes+=cmbChooseStudentAS.getSelectionModel().getSelectedItem().getStudentLastName()+":";
		detailes+=cmbChooseStudentAS.getSelectionModel().getSelectedItem().getStudentId()+":";
		detailes+=cmbClassInCourseAS.getSelectionModel().getSelectedItem().getClassInCourseId()+":";
		detailes+=cmbChooseCourseAS.getSelectionModel().getSelectedItem().getCourseName();
		ArrayList<String> reqArr = new ArrayList<>();
		reqArr.add(LoginController.userClient.userName);
		reqArr.add(detailes);
		
		msg.put("send add request to manager",reqArr);
		LoginController.userClient.sendServer(msg);//
		LoginController.syncWithServer();
		msg.clear();
		lblSuccessAS.setVisible(true);
		
	}
	
	
	/**
	 * prompting in next combobox the all students of this class
	 * @param event
	 */
	@FXML
	void chooseStudentClassASHandler(ActionEvent event){
		lblErrAS.setVisible(false);
		lblSuccessAS.setVisible(false);
		cmbChooseStudentAS.getItems().clear();
		ClassesInListView studentsClass = cmbChooseStudentClassAS.getSelectionModel().getSelectedItem();
		msg = new HashMap<String, ArrayList<String>>();
		ArrayList <String> s = new ArrayList<>();
		s.add(String.valueOf(studentsClass.getClassId()));
		msg.put("getStudentsInClass",s);
		LoginController.userClient.sendServer(msg);
		LoginController.syncWithServer();
		msg.clear();
		HashMap<String, ArrayList<String>> students = (HashMap<String, ArrayList<String>>) UserClient.ans;
		//[student_id,[first_name,last_name,pblocked]]
		//

		for(String sid : students.keySet()){
			ArrayList<String> st = students.get(sid);
			cmbChooseStudentAS.getItems().add(new StudentsInClassAS(sid,st.get(0),st.get(1)));
		}
		
	}
	@FXML
	void chooseCourseASHandler(ActionEvent event) {
		lblErrAS.setVisible(false);
		lblSuccessAS.setVisible(false);
		cmbClassInCourseAS.getItems().clear();
		currCourseAS = cmbChooseCourseAS.getSelectionModel().getSelectedItem();
		msg = new HashMap<String, ArrayList<String>>();
		ArrayList<String> p = new ArrayList<>();
		p.add(currSemAS.get(0));
		p.add(currSemAS.get(1));
		p.add(String.valueOf(currCourseAS.getCourseId()));
		msg.put("getClassOfCourseAS",p); // p=[year,semester,course_id]
		LoginController.userClient.sendServer(msg);
		LoginController.syncWithServer();
		msg.clear();
		//recieve Map<String,ArrayList<String>> -- [class_in_course_id,[class_id,class_name]]
		
		HashMap<String,ArrayList<String>> t =(HashMap<String,ArrayList<String>>) UserClient.ans;
		for(String cicid : t.keySet()){
			ArrayList<String> cl = t.get(cicid);
			cmbClassInCourseAS.getItems().add(new ClassInCourseAS(cl.get(0),cl.get(1),cicid));
		}
	}
	
	
	
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
		tblExceptions.setVisible(false);
		lblStudentsPre.setVisible(false);
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
		tblExceptions.setVisible(false);
		lblStudentsPre.setVisible(false);
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
		tblExceptions.setVisible(false);
		lblStudentsPre.setVisible(false);
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
		ExceptionStudents.clear();
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
		HashMap <String,ArrayList<String>> preCourses = (HashMap <String,ArrayList<String>>) UserClient.ans; // for each pre course we have an array list of course_in_class that studied this course
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
				lblWarning.setText("teacher: "+t.getTeacher()+" exceeding max hours");
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
			// add class_in_course row and get id
			ArrayList<String> class_in_courseRow = new ArrayList<>();
			class_in_courseRow.add(String.valueOf(currCourseBox.getCourseId()));
			class_in_courseRow.add(classConvertToId.get(t.getClassName()));
			class_in_courseRow.add(t.getTeacherComboBox().getTeacherId());
			//add a row
			msg.clear();
			msg.put("addClass_in_courseRow",class_in_courseRow);
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			msg.clear();
			int currCourse_in_classId = (int)UserClient.ans;
			//--
			// update teacher working hours
			ArrayList<String> teacherUpdate = new ArrayList<>();
			teacherUpdate.add(t.getTeacherComboBox().getTeacherId());
			teacherUpdate.add(String.valueOf(weeklyhours));
			msg.put("updateTeacherWorkingHours",teacherUpdate);
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			msg.clear();
			//--
			//get all student in this class
			msg.clear();
			ArrayList<String> studentsIdtemp = new ArrayList<>();
			studentsIdtemp.add(String.valueOf(classConvertToId.get(t.getClassName())));
			msg.put("getStudentsOfClass",studentsIdtemp);
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			msg.clear();
			HashMap<String,String> studentsId = (HashMap<String,String>) UserClient.ans;
			ArrayList<String> studentsToAssign = new ArrayList<>();
			studentsToAssign.add(String.valueOf(currCourse_in_classId)); // first elemnt will be class_in_course_id
			for(String sid : studentsId.keySet()){ // foreach student in this class
				boolean preReq=true;
				for(String cid : preCourses.keySet()){ // foreach preCourse for this course
					//check if he has this course:
					ArrayList<String> pack = new ArrayList<>(preCourses.get(cid));
					pack.add(0,sid);
					msg.put("checkPreCourseFromArray",pack);
					LoginController.userClient.sendServer(msg);
					LoginController.syncWithServer();
					msg.clear();
					preReq = (boolean) UserClient.ans;
					//--
					if(!preReq){
						ExceptionStudents.add(new StudentsExp(sid,studentsId.get(sid),classConvertToId.get(t.getClassName()),t.getClassName()));
						break;
					}
				}
				if(!preReq) continue; // if student has no pre classes than skip and move to next iteration
				studentsToAssign.add(sid);
			}
			msg.put("AssignStudentsToClassInCourse",studentsToAssign);
			LoginController.userClient.sendServer(msg);
			LoginController.syncWithServer();
			msg.clear();
		}
		String s = "Successfully Assigned classes,teachers and students to course";
		if(!ExceptionStudents.isEmpty()){
			tblExceptions.setItems(ExceptionStudents);
			tblExceptions.setVisible(true);
			lblStudentsPre.setVisible(true);
			s+=" with students exception list who doesnt fill pre courses";
		}
		JOptionPane.showMessageDialog(null, s);
		
		//
		//
		//

		
		
		
		
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
		paneViewInbox.setVisible(false);
		
		pane.setVisible(true);
	}

	@FXML
	void createSemesterHandler(ActionEvent event) {
		setPane(paneCreateSemester);
	}
	@FXML
	void viewInboxHandler(ActionEvent event) {
		setPane(paneViewInbox);
		HashMap<String,ArrayList<String>>msg = new HashMap<>();
		tblViewInbox.setPlaceholder(new Label("No Messages to Show"));
		secretaryInbox = FXCollections.observableArrayList();
		ArrayList<String> tos = new ArrayList<>();
		tos.add(LoginController.userClient.userName);
		msg.put("getSecretayInbox",tos);
		LoginController.userClient.sendServer(msg);//
		LoginController.syncWithServer();
		
		HashMap<String,String> msgMap=(HashMap<String,String>) UserClient.ans;
		
		colMsgVI.setCellValueFactory(new PropertyValueFactory<>("messageContent"));
		colIdVI.setCellValueFactory(new PropertyValueFactory<>("messageId"));
		for(String mid: msgMap.keySet()){
			String msgc = msgMap.get(mid);
			secretaryInbox.add(new ViewInboxTbl(mid,msgc));
		}
		tblViewInbox.setItems(secretaryInbox);

	}
	
	@FXML
	void addStudentToCourseHandler(ActionEvent event) {
		setPane(paneAddStudent);
		initializeAddStudent();
	}
	@FXML
	void removeStudentFromCourseHandler(ActionEvent event) {
		setPane(paneRemoveStudent);
		cbChooseCourseRS.getItems().clear();
		
		char sem;
		int semester=0,year=0;
		HashMap <String,ArrayList<String>> hm = new HashMap <String,ArrayList<String>>();
		ArrayList<String> arr = new ArrayList<String>();
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
		arr.add(String.valueOf(year));
		arr.add(String.valueOf(semester));
		hm.remove("getCurrentSemester");
		hm.put("get courses for semester",arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null){
			for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				
				if(!cbChooseCourseRS.getItems().contains(((ArrayList<String>)LoginController.userClient.ans).get(i)))
				{
					cbChooseCourseRS.getItems().add(((ArrayList<String>)LoginController.userClient.ans).get(i));
				}
				
			}
		}


		
		
	}
    @FXML
    void ChooseCourseRSHandler(ActionEvent event) {
    	cbChooseStudentRS.setVisible(true);
    	cbChooseStudentRS.getItems().clear();
    	studentCourse.clear();
    	String currStud= "";
    	int spacecnt=0;
		HashMap <String,ArrayList<String>> hm = new HashMap <String,ArrayList<String>>();
		ArrayList<String> arr = new ArrayList<String>();
		arr.add(cbChooseCourseRS.getValue().substring(2, 5));
		hm.put("get students for course", arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null){
			for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				currStud = "";
				spacecnt=0;
				
				for(int j=0;j<((ArrayList<String>)LoginController.userClient.ans).get(i).length();j++)
				{
					System.out.println(((ArrayList<String>)LoginController.userClient.ans).get(i));
					studentCourse.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
					if(((ArrayList<String>)LoginController.userClient.ans).get(i).charAt(j) == ' ')
					{
						spacecnt++;
					}
					if(spacecnt==2)
					{
						currStud = ((ArrayList<String>)LoginController.userClient.ans).get(i).substring(j+1, ((ArrayList<String>)LoginController.userClient.ans).get(i).length());
						break;
					}
				}
				if(!cbChooseStudentRS.getItems().contains(currStud))
				{
					cbChooseStudentRS.getItems().add(currStud);
				}
				
			}
			
			
		}
		lblChooseStudentRS.setVisible(true);
		cbChooseStudentRS.setVisible(true);
		
    }

    @FXML
    void ChooseStudentRSHandler(ActionEvent event) {

    		
    		btnSendRequestToManager.setVisible(true);
    		
    }
    @FXML
    void sendRequestToManagerHandler(ActionEvent event) {
		ArrayList<String> arr = new ArrayList<String>();
		HashMap <String,ArrayList<String>> hm = new HashMap <String,ArrayList<String>>();
		String sfullname="";
		int spacecnt=0;
		String class_in_course_id="", studName="";
		String arr1[];
		//
		
		for(int i=0;i<studentCourse.size();i++)
		{
			arr1 = studentCourse.get(0).split(" - ");
			if(arr1[1].equals(cbChooseStudentRS.getValue()))
				class_in_course_id = arr1[0];
		}
				arr.add(cbChooseStudentRS.getValue());		
				hm.put("get user full name", arr);		
				LoginController.userClient.sendServer(hm);		
				LoginController.syncWithServer();		
				sfullname = ((ArrayList<String>)LoginController.userClient.ans).get(0);		
				arr.clear();		
				hm.clear();		
						
				arr.add(LoginController.userClient.userName);		
				arr.add(LoginController.userClient.fullName);		
				arr.add(sfullname);		
				arr.add(cbChooseStudentRS.getValue());		
				arr.add(class_in_course_id);		
				arr.add(cbChooseCourseRS.getValue().substring(8));		
				hm.put("send remove request to manager", arr);		
				LoginController.userClient.sendServer(hm);		
				LoginController.syncWithServer();		
				/*
		arr.add(LoginController.userClient.userName);
		arr.add(class_in_course_id);
		arr.add(cbChooseStudentRS.getValue());
		hm.put("send remove request to manager", arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		*/
		
    }

	@FXML
	void changeAppointmentHandler(ActionEvent event) {
		setPane(paneChangeAppointment);
		cbCourse.getItems().clear();
///////////change teacher's appointment///////////
		HashMap<Integer,String> cls=new HashMap<Integer,String>();
		ArrayList<String> courses=new ArrayList<String>();
		 HashMap<String, ArrayList<String>>	msg = new HashMap<String, ArrayList<String>>();
		 HashMap<Integer,Integer>	coursesWeeklyHours = new HashMap<Integer,Integer>();
		 HashMap<Integer,HashMap<Integer,String>>	currCourses = new HashMap<Integer,HashMap<Integer,String>>();
		   msg.put("getCurrentSemester", null);
			int year=0,semester = 0;
			char sem = ' ';
			LoginController.userClient.sendServer(msg);//
			LoginController.syncWithServer();
			msg.clear();
			year=(((ArrayList<Integer>)LoginController.userClient.ans).get(0));
			sem=(((ArrayList<Character>)LoginController.userClient.ans).get(1));
			
			HashMap<Integer, String> temp = new HashMap<Integer, String>();
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
			args.add(String.valueOf(year));
			args.add(String.valueOf(semester));
			msg.put("getCurrentCourses",args);
			LoginController.userClient.sendServer(msg);//
			LoginController.syncWithServer();
			currCourses=(HashMap<Integer,HashMap<Integer,String>>)(((ArrayList<Object>)(LoginController.userClient.ans)).get(0));
			for(Integer key : currCourses.keySet()){
				temp=currCourses.get(key);
				for(Integer key2 : temp.keySet())
					courses.add(temp.get(key2));
			}
			if(courses!=null){
				for(int i=0;i<courses.size();i++)
					cbCourse.getItems().add(courses.get(i));
			}
			/////////courses in semester////////
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
/**
 * setting first values, all courses and all class
 */
	void initializeAddStudent(){
		HashMap<String, ArrayList<String>>	msg = new HashMap<String, ArrayList<String>>();
		
		lblErrAS.setVisible(false);
		lblSuccessAS.setVisible(false);
		//get current semester
		msg.put("getCurrentSemester",null);
			LoginController.userClient.sendServer(msg);//ask from server next semester details
			LoginController.syncWithServer();
			msg.clear();
			ArrayList<Object> currSemester=(ArrayList<Object>) UserClient.ans;
			int newYear=(int) currSemester.get(0);
			char newSemester=(char) currSemester.get(1);
			ArrayList<String> arr = new ArrayList<String>();
			if(newSemester == 'B'){//if next semester is 'B' current is 'A' and current year is the same
				arr.add(String.valueOf(newYear));
				arr.add("1");
			}else{ 
				arr.add(String.valueOf(--newYear));
				arr.add("2");//if next semester is 'A' current is 'B' and current year is new year - 1
			}
			msg.clear();
			//--
			currSemAS = new ArrayList<>(arr);
			//get current courses
			msg.put("getCurrentCourses",arr);
			LoginController.userClient.sendServer(msg);// 
			LoginController.syncWithServer();
			//TeachingUnit-->[Map<courseId,courseName>]
			ArrayList<Object> tempAns = (ArrayList<Object>)UserClient.ans;
			courses  = (HashMap<Integer,HashMap<Integer,String>>) tempAns.get(0);
			
			Collection<Integer> teachingUnits = courses.keySet();

			for(int singleTeachUnit : teachingUnits){ // foreach teaching unit
				HashMap<Integer,String> coursesOfTeachingUnit = courses.get(singleTeachUnit); // get all course of single teaching unit
				Collection<Integer> coursesIdOfSingleTeachUnit = coursesOfTeachingUnit.keySet();
				for(int singleCourseId : coursesIdOfSingleTeachUnit){ // foreach course in teaching unit
					CourseComboBox cmbRow;
					String courseName = coursesOfTeachingUnit.get(singleCourseId);
					cmbRow = new CourseComboBox(singleTeachUnit,singleCourseId,courseName);
					cmbChooseCourseAS.getItems().add(cmbRow);
				}
			}
			msg.clear();
			//--
			//get all current classes
			msg.put("getCurrentClasses",arr);
			LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
			LoginController.syncWithServer();
			msg.clear();
			//current new way of getting course:
			HashMap<Integer,String> currClasses = (HashMap<Integer,String>) UserClient.ans;
			Collection<Integer> classesKeySet = currClasses.keySet();
			for(int classId:classesKeySet){
				String className = currClasses.get(classId);
				cmbChooseStudentClassAS.getItems().add(new ClassesInListView(classId,className));
			}
			//--
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
		
		colSidExp.setCellValueFactory(new PropertyValueFactory<>("id"));
		colSnExp.setCellValueFactory(new PropertyValueFactory<>("name"));
		colCidExp.setCellValueFactory(new PropertyValueFactory<>("classId"));
		colCnExp.setCellValueFactory(new PropertyValueFactory<>("className"));
		
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
	
	class ClassInCourseAS {
		private String className;
		private String classId;
		private String classInCourseId;
		public ClassInCourseAS(String id,String name,String cicid){
			className = name;
			classId  = id;
			classInCourseId = cicid;
		}
		public String getClassName(){
			return className;
		}
		public String getClassId(){
			return classId;
		}
		public String getClassInCourseId(){
			return classInCourseId;
		}
		public String toString(){
			return className;
		}
		public void setClassName(String name){
			className = name;
		}
		public void setClassId(String id){
			classId = id;
		}
		public void setClassInCourseId(String cicid){
			classInCourseId = cicid;
		}
	}
	
	class StudentsInClassAS {
		private String studentId;
		private String studentFirstName;
		private String studentLastName;
		
		public StudentsInClassAS(String id,String fname,String lname){
			studentId = id;
			studentFirstName  = fname;
			studentLastName = lname;
		}
		public String getStudentId(){
			return studentId;
		}
		
		public String getStudentFirstName(){
			return studentFirstName;
		}
		public String getStudentLastName(){
			return studentLastName;
		}
		public String toString(){
			return studentFirstName+" "+studentLastName;
		}
		
		
		public void setStudentFirstName(String name){
			studentFirstName = name;
		}
		public void setStudentLastName(String name){
			studentLastName = name;
		}
		public void setStudentId(String id){
			studentId= id;
		}
	}
	
	public class StudentsExp {
		private String name;
		private String id;
		private String classId;
		private String className;
		
		public StudentsExp(String i,String n,String clsId,String cls){
			name=n;
			id=i;
			classId=clsId;
			className=cls;
		}
		public String getName(){
			return name;
		}
		public String getClassId(){
			return classId;
		}
		public String getClassName(){
			return className;
		}
		public String getId(){
			return id;
		}
	}
	public class ViewInboxTbl {
		private String messageId;
		private String messageContent;
		public ViewInboxTbl(String id, String msg){
			messageId = id;
			messageContent = msg;
		}
		public String getMessageId(){
			return messageId;
		}
		public String getMessageContent(){
			return messageContent;
		}
	}

} 






