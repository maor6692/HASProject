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
	private Hyperlink linkLogout;

	@FXML							     //   1-remove student from course, 2-add student to course, 3-change teacher appointment
	private Label lblUser,lblChooseRequest;				//	                  request details: "2:classInCourse id:userId"
	@FXML
	private TextArea tfRequestDetails,tfComments;

	@FXML
	Pane blockParentPane,answerRequestsPane,getStatisticReportPane;

	@FXML
	private ListView<String> lvStudents,lvRequests;//list view to display blocked students in current semester

	@FXML
	private ComboBox<String> cbStudents,cbClasses;//combo box for class and students in current semester

	@FXML
	private Button btnReturnAccess,btnBlock;
	
    @FXML
    private ComboBox<String> cmbOpGSR;

	
	@FXML
    private ComboBox<String> cmbArb;
	
	
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
		cmbOpGSR.getItems().add("All Grades of a teacher");
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
		setPane(blockParentPane);
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	@FXML
	void setAnswerRequestPaneHandler(ActionEvent event) {
		initializeAnswerRequests();
		setPane(answerRequestsPane);
	}
	/**
	 * change visible user window to appropriate window
	 * @param pane
	 */
	void setPane(Pane pane){
		blockParentPane.setVisible(false);
		answerRequestsPane.setVisible(false);
		getStatisticReportPane.setVisible(false);
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
	 * executes secretary request and send some comments to secretary INBOX about this approve
	 * @param requrst from requests list view
	 */
	@FXML
	void approveRequesthandler(ActionEvent event){
		lblChooseRequest.setVisible(false);
		String request_id="";
		String course_in_class_id="";
		String student_id="";
		String teacher_id="";
		String query="";
		if(lvRequests.getSelectionModel().getSelectedItem() == null){
			lblChooseRequest.setVisible(true);
			return;
		}else{
			request_id=lvRequests.getSelectionModel().getSelectedItem().split("\\r?\\n")[0];//get first line of selected item in list view
			request_id=request_id.substring(11,request_id.length());//extract request id from end of line
			switch(managerRequests.get(request_id).get(1).split("\\:")[0]){
			case "1":
				course_in_class_id=managerRequests.get(request_id).get(1).split("\\:")[4];//extract course_in_class_id from details field from table manager_request
				student_id=managerRequests.get(request_id).get(3);
				query = "DELETE FROM student_in_course_in_class WHERE course_in_class_id ="+ course_in_class_id +"AND student_id = '"+student_id+"' LIMIT 1";
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
		lblChooseRequest.setVisible(false);
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
		arr.add(managerRequests.get(request_id).get(1).split("\\:")[0]);//secretary id
		//prepare message for secretary includes request details, status(approved/dismissed) and school manager comments
		arr.add(lvRequests.getSelectionModel().getSelectedItem().split("\\r?\\n")[2]+" :"+status+"\nschool manager comments: "+tfComments.getText());//comments
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
	 * get and display all necessary data from db for answer requests window
	 * @param 
	 */
	void initializeAnswerRequests(){

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
				request= requestdetails[1]+" asks to change teacher's appointment from : "+requestdetails[2]+" to : "+requestdetails[3]+" in course "+requestdetails[6];
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
			   String op = cmbOpGSR.getSelectionModel().getSelectedItem();
			   String arb = cmbArb.getSelectionModel().getSelectedItem();
			   String period = cmbPeriodGSR.getSelectionModel().getSelectedItem();
			   bcStatistic.setTitle(op+" "+arb);
			   HashMap<String,ArrayList<String>> msg = new HashMap<>();
			   ArrayList<String> arrGSR = new ArrayList<>();
			   arrGSR.add(op);
			   arrGSR.add(arb);
			   arrGSR.add(period);
			   //Sending server [operation,arbitrator,period]
			   //operation values: ""All Grades of a teacher","All Teachers of a class","All Courses of a class"
			   /* uncomment after finish SetReportGSR case in EchoServer
			   msg.put("GetReportGSR", arrGSR);
			   LoginController.userClient.sendServer(msg); 
			   LoginController.syncWithServer();
			   msg.clear();
				*/
		
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
				lblArb.setVisible(false);
				cmbArb.setVisible(false);
				cmbArb.getItems().clear();
				btnShowReportGSR.setVisible(false);
				bcStatistic.setVisible(false);
				lblPeriodGSR.setVisible(false);
				cmbPeriodGSR.setVisible(false);
				
				switch(cmbOpGSR.getSelectionModel().getSelectedItem()){
				case "All Grades of a teacher":
					lblArb.setText("Choose teacher:");
					lblArb.setVisible(true);
					ArrayList<String> tempList = new ArrayList<>();
					for(ArrayList<String> arr: teachersGSR)
						tempList.add(arr.get(1));
					Collections.sort(tempList);
					cmbArb.getItems().addAll(tempList);
					cmbArb.setVisible(true);
					break;
				case "All Teachers of a class":
				case "All Courses of a class":
					lblArb.setText("Choose class:");
					lblArb.setVisible(true);
					for(ArrayList<String> arr: classesGSR)
						cmbArb.getItems().add(arr.get(1)+"  ["+arr.get(2)+","+arr.get(3)+"]");
					cmbArb.setVisible(true);
					break;
				}
				
		}
}


