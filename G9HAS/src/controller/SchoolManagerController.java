package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SchoolManagerController implements Initializable{

	@FXML
	private TextArea textAreaViewAllInfo;

	@FXML
	private Hyperlink linkLogout;

	@FXML							     //   1-remove student from course, 2-add student to course, 3-change teacher appointment
	private Label lblUser,lblCurrSemseter,lblEmptyCB;				//	                  request details: "2:classInCourse id:userId"
	@FXML
	private TextArea tfRequestDetails,tfComments;

	@FXML
	Pane blockParentPane,answerRequestsPane,getStatisticReportPane,viewAllInformationPane;

	@FXML
	private ListView<String> lvStudents,lvRequests;//list view to display blocked students in current semester

	@FXML
	private ComboBox<String> cbStudents,cbClasses,cbOptions,cbYear,cbSemester;//combo box for class and students in current semester

	@FXML
	private Button btnReturnAccess,btnBlock;

	@FXML
	private ComboBox<String> cmbOpGSR;


	@FXML
	private ComboBox<ArbitratorComboBox> cmbArb;


	@FXML
	private ComboBox<String> cmbPeriodGSR;

	@FXML
	private Label lblArb;


	@FXML
	private Button btnShowReportGSR;

	@FXML
	private BarChart<String,Number> bcStatistic;

	@FXML
	private NumberAxis axisAvg;

	@FXML
	private CategoryAxis axisSem;

	HashMap<String, ArrayList<String>>	msg ;
	private ObservableList<String> studentsDetails = FXCollections.observableArrayList();//saves student details from db
	private ObservableList<String> classesDetails = FXCollections.observableArrayList();//saves class details from db
	private ObservableList<String> blockedStudents = FXCollections.observableArrayList();//saves blocked students in current semester
	private ObservableList<String> requestsDetails = FXCollections.observableArrayList();//saves requests details from db
	ArrayList<String> arr ;//uses to send parameters to server
	int newYear;
	char newSemester;
	HashMap<Integer, String> classes;//saves all classes in current semester
	HashMap<String, ArrayList<String>> studentsInClass;//saves all students of chosen class in combo box
	HashMap<String, ArrayList<String>> blockedStudentsHM;
	HashMap<String, ArrayList<String>> managerRequests;
	ArrayList<String> semRangGSR = new ArrayList<>();
	ArrayList<ArrayList<String>> teachersGSR = new ArrayList<>();
	ArrayList<ArrayList<String>> classesGSR = new ArrayList<>();

	@FXML
	private Label lblPeriodGSR;

	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	@FXML
	void setBlockParentAccessPaneHandler(ActionEvent event) {
		setPane(blockParentPane);
		initializeBlockParent();
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */


	/**
	 * initialize info for choosing report
	 * @param teachers and classes info that was/is last 4 semesters
	 */
	@FXML
	void setGetStatisticReportPaneHandler(ActionEvent event) {
		setPane(getStatisticReportPane);
		cmbOpGSR.getItems().clear();
		cmbOpGSR.getItems().add("All Classes of a teacher");
		cmbOpGSR.getItems().add("All Teachers of a class");
		cmbOpGSR.getItems().add("All Courses of a class");
		lblArb.setVisible(false);
		cmbArb.setVisible(false);
		lblPeriodGSR.setVisible(false);
		cmbPeriodGSR.setVisible(false);
		btnShowReportGSR.setVisible(false);
		bcStatistic.setVisible(false);
		HashMap<String,ArrayList<String>> msg = new HashMap<>();
		msg.put("getTeachersAndClassesGSR", null);
		LoginController.userClient.sendServer(msg); 
		LoginController.syncWithServer();
		msg.clear();
		//save recieved objects in class attr
		ArrayList<Object> ans = new ArrayList<Object>((ArrayList<Object>)UserClient.ans);
		semRangGSR = new ArrayList<String>((ArrayList<String>)ans.get(0));
		teachersGSR = new ArrayList<>((ArrayList<ArrayList<String>>)ans.get(1));
		classesGSR = new ArrayList<>((ArrayList<ArrayList<String>>)ans.get(2));
		//--
		cmbPeriodGSR.getItems().clear();
		ArrayList <String> tempList = new ArrayList<>(); 
		for(String s: semRangGSR){
			tempList.add("since "+s.substring(0,4)+" semester: "+s.substring(4));
		}
		Collections.sort(tempList);
		cmbPeriodGSR.getItems().addAll(tempList);

	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	@FXML
	void setViewAllInformationPaneHandler(ActionEvent event) {
		initializeViewAllInformation();
		setPane(viewAllInformationPane);
		btnShowReportGSR.setVisible(false);
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	@FXML
	void setAnswerRequestPaneHandler(ActionEvent event) {
		initializeAnswerRequests();
		setPane(answerRequestsPane);
		btnShowReportGSR.setVisible(false);

	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	void setPane(Pane pane){
		blockParentPane.setVisible(false);
		answerRequestsPane.setVisible(false);
		getStatisticReportPane.setVisible(false);
		viewAllInformationPane.setVisible(false);
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
			msg.clear();
			msg.put("returnAccess",arr);
			LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
			LoginController.syncWithServer();
			msg.clear();
			blockedStudents.remove(lvStudents.getSelectionModel().getSelectedItem());
			lvStudents.setItems(blockedStudents);
		}
	}

	/**
	 * executes secretary request and send some comments to secretary INBOX about this approve
	 * @param requrst from requests list view
	 */
	@FXML
	void approveRequesthandler(ActionEvent event){
		String request_id="";
		String course_in_class_id="";
		String student_id="";
		String teacher_id="";
		String query="";
		if(lvRequests.getSelectionModel().getSelectedItem() == null){
			return;
		}else{
			request_id=lvRequests.getSelectionModel().getSelectedItem().split("\\r?\\n")[0];//get first line of selected item in list view
			request_id=request_id.substring(11,request_id.length());//extract request id from end of line
			switch(managerRequests.get(request_id).get(1).split("\\:")[0]){
			case "1":
				course_in_class_id=managerRequests.get(request_id).get(1).split("\\:")[4];//extract course_in_class_id from details field from table manager_request
				student_id=managerRequests.get(request_id).get(1).split("\\:")[3];
				query = "DELETE FROM student_in_course_in_class WHERE course_in_class_id ="+ course_in_class_id +" AND student_id = '"+student_id+"' LIMIT 1";
				break;
			case "2":
				course_in_class_id=managerRequests.get(request_id).get(1).split("\\:")[4];
				student_id=managerRequests.get(request_id).get(1).split("\\:")[3];
				query = "INSERT INTO student_in_course_in_class VALUES("+course_in_class_id+",'"+student_id+"',-1)";
				break;
			case "3":
				course_in_class_id=managerRequests.get(request_id).get(1).split("\\:")[5];
				teacher_id=managerRequests.get(request_id).get(1).split("\\:")[4];
				query = "UPDATE class_in_course SET teacher_id='"+teacher_id+"' WHERE id="+course_in_class_id;
				break;
			}
			arr.clear();
			arr.add(query);
			if(msg!=null) msg.clear(); else msg = new HashMap<String, ArrayList<String>>();
			msg.put("approveRequest",arr);
			LoginController.userClient.sendServer(msg);//send to server user info to verify user details 
			LoginController.syncWithServer();
			msg.clear();
			changeRequestStatus("approved");
		}
	}

	/**
	 * change requests status to "dismissed" and send some comments to secretary INBOX about this dismiss
	 * @param requrst from requests list view
	 */
	@FXML
	void dismissRequesthandler(ActionEvent event){
		changeRequestStatus("dismissed");
	}

	/**
	 * change request status in DB to given status as parameter
	 * @param request status (String)
	 */
	void changeRequestStatus(String status){
		if(lvRequests.getSelectionModel().getSelectedItem()==null) {
			tfComments.clear();
			return;
		}
		arr.clear();
		String request_id=lvRequests.getSelectionModel().getSelectedItem().split("\\r?\\n")[0];
		request_id=request_id.substring(11,request_id.length());
		arr.add(managerRequests.get(request_id).get(0));//secretary id
		//prepare message for secretary includes request details, status(approved/dismissed) and school manager comments
		arr.add(lvRequests.getSelectionModel().getSelectedItem().split("\\r?\\n")[2]+" :"+status+"\nschool manager comments: "+tfComments.getText());//comments
		if(msg!=null) msg.clear(); else msg = new HashMap<String, ArrayList<String>>();
		msg.put("notifySecretary",arr);
		LoginController.userClient.sendServer(msg);//send comments to secretary INBOX
		LoginController.syncWithServer();
		msg.clear();
		arr.clear();
		arr.add(status);
		arr.add(request_id);
		msg.put("changeRequestStatus",arr);
		LoginController.userClient.sendServer(msg);//ask from server to change request status and delete request from list view
		LoginController.syncWithServer();
		msg.clear();
		requestsDetails.remove(lvRequests.getSelectionModel().getSelectedItem());
		lvRequests.setItems(requestsDetails);
		tfComments.clear();
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblUser.setText(UserClient.fullName);
	}


	/**
	 * get and display all necessary data from db for answer requests window
	 * @param 
	 */
	void initializeAnswerRequests(){
		requestsDetails.clear();
		msg = new HashMap<String, ArrayList<String>>();
		arr = new ArrayList<>();
		msg.put("getManagerRequets",null);
		LoginController.userClient.sendServer(msg);//ask from server next semester details
		LoginController.syncWithServer();
		msg.clear();
		if(managerRequests!=null) managerRequests.clear();
		managerRequests=(HashMap<String, ArrayList<String>>) UserClient.ans;
		String request = "";
		String[] requestdetails = new String[7] ;
		if(managerRequests == null) return;
		for(String id: managerRequests.keySet()){
			requestdetails = managerRequests.get(id).get(1).split("\\:");//[request_type,classInCourse,student id]
			//: 2:secretary full name:student full name:student id:classInCourseId:course name
			//3:secretary full name:old teacher full name:new teacher full name:new teacher id:classInCourseId:course name

			switch(requestdetails[0]){
			case "1":
				request= requestdetails[1]+" asks to remove student:"+requestdetails[2]+" from course: "+requestdetails[5];
				break;
			case "2":
				request= requestdetails[1]+" asks to add student:"+requestdetails[2]+" to course: "+requestdetails[5];
				break;
			case "3":
				request= requestdetails[1]+" asks to change teacher appointment from : "+requestdetails[2]+" to : "+requestdetails[3]+" in course "+requestdetails[6];
				break;
			}
			requestsDetails.add("request id:"+id+"\nrequest date:"+managerRequests.get(id).get(2)+"\nrequest:"+request);
		}
		lvRequests.setItems(requestsDetails);
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
			arr.add(String.valueOf(newYear));
			arr.add("1");
		}else{ 
			arr.add(String.valueOf(--newYear));
			arr.add("2");//if next semester is 'A' current is 'B' and current year is new year - 1
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
	/**
	 * gets all chosen report inputs and generating a report
	 * @cmbOpGSR which operation
	 * @cmbArb  get arbitrator for selected operation
	 * @cmbPeriodGSR get period for the report, since chosen one until now
	 * @bcStatistic BarChart to put the data inside
	 * @axisAvg Y axis
	 * @axisSem X axis
	 */
	@FXML
	void GetReportGSR(ActionEvent event) {
		if	(cmbPeriodGSR.getValue()==null ||  cmbOpGSR.getValue()==null) return;
		bcStatistic.getData().clear();
		String op = cmbOpGSR.getSelectionModel().getSelectedItem();
		String arb = cmbArb.getSelectionModel().getSelectedItem().getArbId();
		String period = cmbPeriodGSR.getSelectionModel().getSelectedItem().substring(6, 10)+cmbPeriodGSR.getSelectionModel().getSelectedItem().substring(21,22);
		bcStatistic.setTitle(op+" "+cmbArb.getSelectionModel().getSelectedItem());
		HashMap<String,ArrayList<String>> msg = new HashMap<>();
		ArrayList<String> arrGSR = new ArrayList<>();
		arrGSR.add(op);
		arrGSR.add(arb);
		arrGSR.add(period);
		//Sending server [operation,arbitrator,period]
		//operation values: ""All Classes of a teacher","All Teachers of a class","All Courses of a class"
		///* uncomment after finish SetReportGSR case in EchoServer
		msg.put("GetReportGSR", arrGSR);
		LoginController.userClient.sendServer(msg); 
		LoginController.syncWithServer();
		msg.clear();
		HashMap<String,ArrayList<String>> report  = (HashMap<String,ArrayList<String>>) UserClient.ans;
		
		//ObservableList<String> graphCats = FXCollections.observableArrayList();
		//for(String s: report.keySet())graphCats.add(s);
		//axisSem.setCategories(graphCats);
		
		
		XYChart.Series <String, Number> series;
		switch(op){
		case "All Classes of a teacher":
		
					for(String semId : report.keySet()){
						series = new XYChart.Series<>();
						series.setName(semId);
						for(String semInfo : report.get(semId)){
							String [] semData = semInfo.split(",");
							series.getData().add(new XYChart.Data<>(semData[1], Integer.parseInt(semData[2])));
						}
						bcStatistic.getData().add(series);
					}
					break;
		case "All Teachers of a class":
			for(String tInSem : report.keySet()){
				series = new XYChart.Series<>();
				series.setName(tInSem);
				for(String tSInfo : report.get(tInSem)){
					String[] semData = tSInfo.split(","); //course name,average 
					series.getData().add(new XYChart.Data<>(semData[0], Integer.parseInt(semData[1])));
					
				}
				bcStatistic.getData().add(series);
			}
			break;
		case "All Courses of a class":
			for(String cInSem : report.keySet()){
				series = new XYChart.Series<>();
				series.setName(cInSem);
				for(String tSInfo : report.get(cInSem)){
					String[] semData = tSInfo.split(","); //course name,average 
					series.getData().add(new XYChart.Data<>(semData[0], Integer.parseInt(semData[1])));
				}
				bcStatistic.getData().add(series);
			}
			break;
		}
		bcStatistic.setVisible(true);
	}
	/**
	 * opens period combo box
	 * @cmbArb get artbitrator for making report
	 */
	@FXML
	void arbGSR(ActionEvent event) {

		lblPeriodGSR.setVisible(true);
		cmbPeriodGSR.setVisible(true);
		btnShowReportGSR.setVisible(false);
		bcStatistic.setVisible(false);
	}
	/**
	 * opens the option to execute report 
	 * @cmbPeriodGSR get period for the report
	 */
	@FXML
	void periodHandlerGSR(ActionEvent event) {
		btnShowReportGSR.setVisible(true);
		bcStatistic.setVisible(false);
	}

	/**
	 * showing Arbitrator for chosen operation
	 * @cmbOpGSR get required operation
	 */
	@FXML
	void OperationGSR(ActionEvent event) {
		if(cmbOpGSR.getSelectionModel().getSelectedItem() == null) return;
		lblArb.setVisible(false);
		cmbArb.setVisible(false);
		cmbArb.getItems().clear();
		btnShowReportGSR.setVisible(false);
		bcStatistic.setVisible(false);
		lblPeriodGSR.setVisible(false);
		cmbPeriodGSR.setVisible(false);
	
		switch(cmbOpGSR.getSelectionModel().getSelectedItem()){
		case "All Classes of a teacher":
			lblArb.setText("Choose teacher:");
			lblArb.setVisible(true);
			ArrayList<ArbitratorComboBox> tempList = new ArrayList<>();
			for(ArrayList<String> arr: teachersGSR)
				tempList.add(new ArbitratorComboBox(arr.get(0),arr.get(1)) );

			cmbArb.getItems().addAll(tempList);
			cmbArb.setVisible(true);
			break;
		case "All Teachers of a class":
		case "All Courses of a class":
			lblArb.setText("Choose class:");
			lblArb.setVisible(true);
			for(ArrayList<String> arr: classesGSR)
				cmbArb.getItems().add(new ArbitratorComboBox(arr.get(0),arr.get(1),arr.get(2),arr.get(3)) );
			cmbArb.setVisible(true);
			break;
		}

	}
	/**
	 * showing Arbitrator for chosen operation
	 *@param
	 * chosen info option from comboBox
	 */
	@FXML
	void viewButtonHandler(ActionEvent event){
		String query="";
		lblEmptyCB.setVisible(false);
		if(cbYear.getValue()==null || cbSemester.getValue()==null || cbOptions.getValue()==null){
			lblEmptyCB.setVisible(true);
			return;
		}
		String info="";
		ArrayList<String> params = new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm = null;
		HashMap<String, ArrayList<String>> students,classes,studentsInCourse;
		switch(cbOptions.getValue()){

		case "all classes":
			query ="select * from class where year='"+cbYear.getValue()+"' AND semester='"+cbSemester.getValue()+"'";
			params.add(query);
			hm = (HashMap<String, ArrayList<String>>) queryGenerator(params,"getAllClassesOfSemester");//class id:name

			for(String key : hm.keySet()){
				info+="\nclass "+hm.get(key).get(0)+":\n.................."+"\n";
				info+="students in this class:\n..................................................................................\n";
				params.clear();
				params.add(key);
				queryGenerator(params,"getStudentsInClassVAI");
				students = (HashMap<String,ArrayList<String>>)UserClient.ans;//id:first name,last name,pBlocked
				for(String student : students.keySet()){
					info+="id:"+student+" first name: "+students.get(student).get(0)+", last name: "+students.get(student).get(1)+"\n";
				}
			}
			textAreaViewAllInfo.setText(info);
			break;

		case "all courses":
			info="";
			query ="select * from course where year='"+cbYear.getValue()+"' AND semester='"+cbSemester.getValue()+"'";
			params.add(query);
			hm = (HashMap<String, ArrayList<String>>) queryGenerator(params,"getAllCoursesOfSemester");
	
			for(String key : hm.keySet()){
				if(key!=null)
				{
					info+="*********************************************************\n----------------course "+hm.get(key).get(0)+" ("+hm.get(key).get(1)+""+key+"):----------------\n";//id:name, teaching_unit,weekly_hours,year,semester
					info+="weekly hours:"+hm.get(key).get(2)+"\n";
					info+="classes who learn this course: \n";
					params.clear();
					params.add(cbYear.getValue());
					params.add(cbSemester.getValue());
					params.add(key);
					classes = (HashMap<String, ArrayList<String>>) queryGenerator(params,"getClassesOfCourseSM");
					params.clear();
					for(String cl : classes.keySet()){
						info+="class name:"+classes.get(cl).get(1)+"\n";
						info+="teacher name: "+classes.get(cl).get(2) +"\n";
					}
					info+="*********************************************************\n\n";
				}
			}
			textAreaViewAllInfo.setText(info);
			break;

		case "classes in courses":
			info="";
		if (hm!=null) hm.clear();
			query ="select cic.id,co.name,c.name,u.first_name,u.last_name "
					+ "from users u ,class_in_course cic,class c,course co  "
					+ "where cic.teacher_id=u.user_name AND cic.class_id=c.id AND cic.course_id=co.id AND c.year='"+cbYear.getValue()+"' AND co.year='"+cbYear.getValue()+"'  AND co.semester='"+cbSemester.getValue()+"' AND c.semester='"+cbSemester.getValue()+"'";
			params.add(query);
			hm = (HashMap<String, ArrayList<String>>) queryGenerator(params,"getClassesInCoursesOfSemester");
			for(String key : hm.keySet()){
				if(key!=null)
				{
					info+="*************************************************************************************************\n";
					info+="course name: "+hm.get(key).get(0)+"\n";
					info+="class name: "+hm.get(key).get(1)+"\n";
					info+="teacher name :"+hm.get(key).get(2)+" "+hm.get(key).get(3)+"\n-----------------------------------------------------------------------------------\n";
					params.clear();
					params.add(key);
					studentsInCourse = (HashMap<String, ArrayList<String>>) queryGenerator(params,"getStudentsInCourse");
					for(String id : studentsInCourse.keySet()){
						info+="student name: "+studentsInCourse.get(id).get(0)+"  -   average grade in course: "+studentsInCourse.get(id).get(1)+"\n";	
					}
					info+="***********************************************************************************************\n\n";
				}
			}
			
			textAreaViewAllInfo.setText(info);
			break;

		case "blocked students":
			ArrayList<String> arr;
			info="";
			params.clear();
			query ="SELECT DISTINCT u.first_name,u.last_name,c.name FROM  users u,student s, student_in_class sic, class c WHERE sic.student_id=s.id AND s.pblocked=1 AND s.id=u.user_name  AND sic.class_id=c.id AND c.year="+cbYear.getValue()+" AND c.semester="+cbSemester.getValue();
			params.add(query);
			arr = (ArrayList<String>) queryGenerator(params,"getBlockedStudents");
			info += "*********************************************************\n";
			info += "Blocked students for parent view: \n";
			for(String s : arr)
			{
			info += s +"\n";
					
			}
			info += "*********************************************************";
			textAreaViewAllInfo.setText(info);
			break;
		}
	}
	/**
	 * set lblEmptyCB to visible=false
	 */
	@FXML
	void setLabelFalse(ActionEvent event){
		lblEmptyCB.setVisible(false);
	}
	/**
	 *  display view all information pane and initialize comboBoxes
	 * @param 
	 */
	void initializeViewAllInformation(){
		queryGenerator(null,"getSemesters");
		cbYear.getItems().clear();
		cbSemester.getItems().clear();
		cbOptions.getItems().clear();
		for(String str: (ArrayList<String>)UserClient.ans ){
			if(str.substring(5,6).equals("1")) lblCurrSemseter.setText("current semester : "+str.substring(0,4)+" / "+(str.substring(4,5).equals("1")?"A":"B"));
			if(!cbYear.getItems().contains(str.substring(0,4)))
				cbYear.getItems().add(str.substring(0,4));
			if(!cbSemester.getItems().contains(str.substring(4,5)))
				cbSemester.getItems().add(str.substring(4,5));
		}
		cbOptions.getItems().addAll("all classes","all courses","classes in courses","blocked students");

	}

	public class ArbitratorComboBox {
		private String teacherId;
		private String teacherName;
		private String className;
		private String classId;
		private String classYear;
		private String classSem;
		private String tos;
		private String arbId;
		public ArbitratorComboBox(String id,String name){
			teacherId = id;
			teacherName = name;
			tos = name;
			arbId=id;
		}
		public ArbitratorComboBox(String id,String name,String year,String sem){
			classId = id;
			className = name;
			classYear=year;
			classSem=sem;
			tos = className;
			arbId=name;
		}
		public String getTeacherId() {
			return teacherId;
		}
		public void setTeacherId(String teacherId) {
			this.teacherId = teacherId;
		}
		public String getTeacherName() {
			return teacherName;
		}
		public void setTeacherName(String teacherName) {
			this.teacherName = teacherName;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getClassId() {
			return classId;
		}
		public void setClassId(String classId) {
			this.classId = classId;
		}
		public String getClassYear() {
			return classYear;
		}
		public void setClassYear(String classYear) {
			this.classYear = classYear;
		}
		public String getClassSem() {
			return classSem;
		}
		public String getArbId(){
			return arbId;
		}
		public void setClassSem(String classSem) {
			this.classSem = classSem;
		}
		public String toString(){
			return tos;
		}
	}

	Object queryGenerator(ArrayList<String> params , String serverCase){
		
		if(msg!=null) msg.clear(); else msg = new HashMap<String, ArrayList<String>>();
		msg.put(serverCase,params);
		LoginController.userClient.sendServer(msg);//send ask to server 
		LoginController.syncWithServer();
		return  UserClient.ans;
	}
}


