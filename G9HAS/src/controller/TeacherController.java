package controller;
import server.EchoServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import controller.SecretaryController.ViewInboxTbl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



public class TeacherController implements Initializable{
	private ArrayList<String> course_id_arr;
	private ArrayList<String> class_id_arr;
	private ArrayList<String> evaluation = new ArrayList<String>();
	ArrayList<String> taskID ;
	private File selectedFile;
	private String studName="";
	private String class_id = "";
	private String class_in_course_id="";
	private String course_id = "";
	private String fname;
	private ObservableList <ViewInboxTbl> teacherInbox;
	@FXML
	private Label lblSelectStudent,lblErrorST;
	@FXML
	private TextField tfCheckUploadPath;

	@FXML
	private Button btnCheckUpload;
	@FXML
	private Button btnChooseFile;

	@FXML
	private TextField tfGrade;

	@FXML
	private ComboBox<String> cbStudent;

	@FXML
	private Button btnDownload;
	@FXML
	private TableView<ViewInboxTbl> tblViewInbox;

	@FXML
	private Label lblClassID;
	@FXML
	private Label lblTaskCreated;
	@FXML
	private Pane defineCoursePane;


	@FXML
	private Label lblTaskGrade;
	@FXML
	private Hyperlink linkLogout;

	@FXML
	private Label lblCreateTask;
	@FXML
	private Label lblDownloadComplete;
	@FXML
	private Label lblSubmissionComments;
	@FXML
	private Button btnCreateTask;

	@FXML
	private TextField tfUploadPath;

	@FXML
	private TextArea taSubmissionComments;
	@FXML
	private Label lblMsg;

	@FXML
	private ComboBox<String> cbCourseID;
	@FXML
	private ComboBox<String> cbClassID;
	@FXML
	private Button btnUpload;

	@FXML
	private Label lblClass;
	@FXML
	private Label lblSelectClass;

	@FXML
	private Label lblTask;

	@FXML
	private Label lblCourseID;
	@FXML
	private Label lblSelectTask;


	@FXML
	private Hyperlink hlCreateTask;

	@FXML
	private Hyperlink hlCheckTask;

	@FXML
	private TableColumn<ViewInboxTbl, String> colMsgVI,colIdVI;


	@FXML
	private TextField tfSubmissionDate;

	@FXML
	private ComboBox<String> cbChooseTask;

	@FXML
	private Label lblUser;

	@FXML
	private Label lblSubmissionDate;

	@FXML
	private ComboBox<String> cbClass;

	@FXML
	private ComboBox<String> cbTeacherCourse;
    @FXML
    private Hyperlink hlViewInbox;
	@FXML
	private Pane checkTaskPane,paneViewInbox,createTaskPane;
	/**
	 * sets visibility of the download task button
	 * @param event
	 */
	@FXML
	void cbStudentHandler(ActionEvent event) {
		if(cbStudent.getValue()== null)
			btnDownload.setVisible(false);
		else
			btnDownload.setVisible(true);
		
		lblMsg.setVisible(false);
		cbClass.setVisible(false);
		lblClass.setVisible(false);
		cbClassID.setVisible(true);
		lblSelectClass.setVisible(true);
		lblSelectTask.setVisible(true);
		lblSelectStudent.setVisible(true);
		cbChooseTask.setVisible(true);
		cbStudent.setVisible(true);
		btnCheckUpload.setVisible(false);
		lblTaskGrade.setVisible(false);

	}  
	void setPane(Pane pane){
		createTaskPane.setVisible(false);
		checkTaskPane.setVisible(false);
		paneViewInbox.setVisible(false);
		pane.setVisible(true);
	}

	@FXML
	void viewInboxHandler(ActionEvent event) {


	}
	/**
	 * task handler, shows all the students with the given task.
	 * @param event
	 */
	@FXML
	void selectTaskHandler(ActionEvent event) {
		lblMsg.setVisible(false);
		taSubmissionComments.setVisible(false);
		lblSubmissionComments.setVisible(false);
		lblMsg.setVisible(false);
		btnDownload.setVisible(false);
		cbClass.setVisible(false);
		lblDownloadComplete.setVisible(false);
		tfGrade.setVisible(false);
		btnChooseFile.setVisible(false);
		tfCheckUploadPath.setVisible(false);
		lblClass.setVisible(false);
		cbClassID.setVisible(true);
		lblSelectClass.setVisible(true);
		lblSelectTask.setVisible(true);
		lblSelectStudent.setVisible(true);
		cbChooseTask.setVisible(true);
		cbStudent.getItems().clear();
		cbStudent.setVisible(true);
		btnCheckUpload.setVisible(false);
		lblTaskGrade.setVisible(false);
		if(cbChooseTask.getValue()!= null)
		{
			cbStudent.getItems().clear();
			String class_id_for_select_student="";
			int fTaskID=Integer.parseInt(cbChooseTask.getValue().substring(9));
			String rTaskID=taskID.get(fTaskID-1);
			ArrayList<String> sTask = new ArrayList<String>();
			ArrayList<String> studentsClass=new ArrayList<String>();
			HashMap<String,ArrayList<String>> taskDetails=new HashMap<String,ArrayList<String>>();
			HashMap<String,ArrayList<String>> sclss=new HashMap<String,ArrayList<String>>();
			int i=0;
			while(cbClassID.getValue().charAt(i)!= ' ')
			{
				class_id_for_select_student += cbClassID.getValue().charAt(i);
				i++;
			}
			studentsClass.add(class_id_for_select_student);
			sclss.put("get students in class", studentsClass);
			LoginController.userClient.sendServer(sclss);
			LoginController.syncWithServer();
			studentsClass.clear();
			for(i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
				studentsClass.add(((ArrayList<String>)LoginController.userClient.ans).get(i));

			((ArrayList<String>)LoginController.userClient.ans).clear();
			sTask.add(rTaskID);
			sTask.add(cbTeacherCourse.getValue().substring(0, 5));
			taskDetails.put("get tasks", sTask);
			LoginController.userClient.sendServer(taskDetails);
			LoginController.syncWithServer();
			for(i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
				if(!(cbStudent.getItems().contains(((ArrayList<String>)LoginController.userClient.ans).get(i))))
					cbStudent.getItems().add(((ArrayList<String>)LoginController.userClient.ans).get(i));
		}
	}
	/**
	 * Download Handler - gets the solution file of the students to selected task.
	 * @param event
	 */
	@FXML
	void DownloadHandler(ActionEvent event) {
		ArrayList<String> arr = new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();
		String path = "";
		evaluation.clear();
		fname="";
		int i=0;

		taSubmissionComments.setVisible(true);
		lblSubmissionComments.setVisible(true);
		Stage downloadStage = new Stage();
		downloadStage.setTitle("Task Download");
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose folder");
		File folderpath = dc.showDialog(downloadStage);
		if(folderpath != null)
		{
			path = folderpath.getAbsolutePath();
			if(path!= null)
			{
				lblTaskGrade.setVisible(true);
				lblMsg.setVisible(true);
				tfGrade.setVisible(true);
				btnChooseFile.setVisible(true);
				tfCheckUploadPath.setVisible(true);
				while(i<cbStudent.getValue().length() && cbStudent.getValue().charAt(i)!=' ' )
				{
					studName+=cbStudent.getValue().charAt(i); 
					i++;
				}
				byte[] by;
				arr.add(studName);
				arr.add(taskID.get(cbChooseTask.getItems().indexOf(cbChooseTask.getValue())));
				arr.add(cbTeacherCourse.getValue().substring(2, 5));
				evaluation.add(studName);
				evaluation.add(taskID.get(cbChooseTask.getItems().indexOf(cbChooseTask.getValue())));
				evaluation.add(cbTeacherCourse.getValue().substring(2, 5));
				hm.put("get solution file of student", arr);
				LoginController.userClient.sendServer(hm);
				LoginController.syncWithServer();
				fname = ((ArrayList<String>)LoginController.userClient.ans).get(0);
				arr.clear();
				arr.add(fname);
				hm.remove("get solution file of student");
				hm.put("get file from filename for solution file", arr);
				LoginController.userClient.sendServer(hm);
				LoginController.syncWithServer();
				by = ((byte[])LoginController.userClient.ans);
				try {
					Files.write(Paths.get(path+"\\"+fname), by);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * change pane to check task.
	 * @param event
	 */
	@FXML
	void hlCheckTaskOnClick(ActionEvent event) {
		
		setPane(checkTaskPane);
	}
	/**
	 * change pane to view inbox
	 * @param event
	 */
    @FXML
    void hlViewInboxHandler(ActionEvent event) {
    	setPane(paneViewInbox);
		setPane(paneViewInbox);
		HashMap<String,ArrayList<String>>msg = new HashMap<>();
		tblViewInbox.setPlaceholder(new Label("No Messages to Show"));
		teacherInbox = FXCollections.observableArrayList();
		ArrayList<String> tos = new ArrayList<>();
		tos.add(LoginController.userClient.userName);
		msg.put("getTeacherInbox",tos);
		LoginController.userClient.sendServer(msg);//
		LoginController.syncWithServer();

		HashMap<String,String> msgMap=(HashMap<String,String>) UserClient.ans;

		colMsgVI.setCellValueFactory(new PropertyValueFactory<>("messageContent"));
		colIdVI.setCellValueFactory(new PropertyValueFactory<>("messageId"));
		for(String mid: msgMap.keySet()){
			String msgc = msgMap.get(mid);
			teacherInbox.add(new ViewInboxTbl(mid,msgc));
		}
		tblViewInbox.setItems(teacherInbox);
    }
    
    private boolean isNum(String str)
    {
    	try{
    	Float.parseFloat(str);
    	return true;
    	}
    	catch(Exception e)
    	{
    	 return false;
    	}
    }
    
	/**
	 * handles the comments file upload of the teacher to the given solution file of the selected student.
	 * @param event
	 */
	@FXML
	void CheckUploadHandler(ActionEvent event) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		lblMsg.setVisible(false);
		int i=0;
		class_id="";
		byte[] by = null;
		float sumGrades=0,cntGrades=0;
		HashMap<String,HashMap<String,byte[]>> hm = new HashMap<String,HashMap<String,byte[]>>();
		HashMap<String,byte[]> hmName = new HashMap<String,byte[]>();
		HashMap<String,ArrayList<String>> hms = new HashMap<String,ArrayList<String>>();
		ArrayList<String> msg = new ArrayList<String>();
		File file = new File(tfCheckUploadPath.getText());
		try {
			by = Files.readAllBytes(file.toPath());
			hmName.put(fname, by);
			hm.put("Submission upload", hmName);


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(isNum(tfGrade.getText()) && Float.parseFloat(tfGrade.getText()) >= 0 && Float.parseFloat(tfGrade.getText()) <= 100)
			evaluation.add(tfGrade.getText());
		else
		{
			lblMsg.setText("Ilegal grade");
			lblMsg.setVisible(true);
			return;
		}
///////////////////////////////////////////////////////////
		
		evaluation.add(taSubmissionComments.getText());
		hms.put("insert task grade", evaluation);
		LoginController.userClient.sendServer(hms);
		LoginController.syncWithServer();
		hms.clear();
		hm.clear();
		hmName.clear();


		i=0;
		while(cbClassID.getValue().charAt(i)!= ' ')
		{
			class_id += cbClassID.getValue().charAt(i);
			i++;
		}
		hms.clear();
		msg.clear();
		msg.add(cbTeacherCourse.getValue());
		msg.add(class_id);
		hms.put("Search for class_in_course_id", msg);
		LoginController.userClient.sendServer(hms);
		LoginController.syncWithServer();
		class_in_course_id = ((ArrayList<String>)LoginController.userClient.ans).get(0);
		hms.clear();
		msg.clear();
		msg.add(studName);
		msg.add(cbTeacherCourse.getValue().substring(2, 5));
		hms.put("get grades for student in course",msg);
		LoginController.userClient.sendServer(hms);
		LoginController.syncWithServer();
		sumGrades = 0;
		if(LoginController.userClient.ans != null)
		{
			//System.out.println(((ArrayList<String>)LoginController.userClient.ans));
			for(i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				sumGrades += Float.parseFloat(((ArrayList<String>)LoginController.userClient.ans).get(i));
			}
			cntGrades = ((ArrayList<String>)LoginController.userClient.ans).size();
		}
		hms.clear();
		msg.clear();
		msg.add(studName);
		msg.add(class_in_course_id);
		msg.add(String.valueOf(sumGrades/cntGrades));

		hms.put("update grade of student in class in course",msg);
		LoginController.userClient.sendServer(hms);
		LoginController.syncWithServer();
		hms.clear();
		msg.clear();
		lblMsg.setText("The task updated successfuly");
		lblMsg.setVisible(true);
	}
	/**
	 * handles the change in the class number.
	 * shows all the tasks to the class selected in the given course.
	 * @param event
	 */
	@FXML
	void ClassIDHandler(ActionEvent event) {
		lblMsg.setVisible(false);
		lblSubmissionComments.setVisible(false);
		taSubmissionComments.setVisible(false);
		lblMsg.setVisible(false);
		btnDownload.setVisible(false);
		cbClass.setVisible(false);
		lblClass.setVisible(false);
		cbClassID.setVisible(true);
		lblSelectClass.setVisible(true);
		lblSelectTask.setVisible(true);
		lblSelectStudent.setVisible(false);
		cbChooseTask.getItems().clear();
		cbChooseTask.setVisible(true);
		cbStudent.getItems().clear();
		cbStudent.setVisible(false);
		btnCheckUpload.setVisible(false);
		lblTaskGrade.setVisible(false);

		taskID=new ArrayList<String>();
		ArrayList<String> arr = new ArrayList<String>();
		String classid[];
		HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();
		String cid = "";
		cid += cbTeacherCourse.getValue().substring(2, 5);
		if(cbClassID.getValue()!=null)
		{
			classid = cbClassID.getValue().split(" - ");
			arr.add(cid);
			arr.add(classid[0]);
			hm.put("get class in course id for course id and class id", arr);
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			hm.remove("get class in course id for course id and class id");
			hm.put("get tasks id",((ArrayList<String>)LoginController.userClient.ans));
			LoginController.userClient.sendServer(hm);
			LoginController.syncWithServer();
			for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				if(!taskID.contains(((ArrayList<String>)LoginController.userClient.ans).get(i)))
					taskID.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
				if(!(cbChooseTask.getItems().contains("Task no. "+(i+1))))
					cbChooseTask.getItems().add("Task no. "+(i+1));
			}
			hm.remove("get tasks id");
			arr.clear();
		}

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
	/**
	 * handles change in class for create task window.
	 * @param event
	 */
	@FXML
	void cbClassHandler(ActionEvent event) {
		lblTaskCreated.setVisible(false);
		lblErrorST.setVisible(false);
	}
	/**
	 * changes the window by changing pane to create task.
	 * @param event
	 */
	@FXML
	void hlcreateTaskOnClick(ActionEvent event) {
		setPane(createTaskPane);
	}
	/**
	 * creates new task and upload it.
	 * @param event
	 */
	@FXML
	void createTaskHandler(ActionEvent event) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		lblErrorST.setVisible(false);
		int i=0;
		course_id= "";
		class_id="";
		byte[] by = null;
		HashMap<String,HashMap<String,byte[]>> hm = new HashMap<String,HashMap<String,byte[]>>();
		HashMap<String,byte[]> hmName = new HashMap<String,byte[]>();

		HashMap<String,ArrayList<String>> hms = new HashMap<String,ArrayList<String>>();
		ArrayList<String> msg = new ArrayList<String>();
		File file = new File(tfUploadPath.getText());
		try {
			by = Files.readAllBytes(file.toPath());
			hmName.put(selectedFile.getName(), by);
			hm.put("upload", hmName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		hm.clear();
		hmName.clear();
		//  LoginController.syncWithServer();
		// int i=0;
		// while(cbCourseID.getValue().charAt(i)!= ' ')
		//{
		course_id += cbCourseID.getSelectionModel().getSelectedItem();
		//i++;
		//}
		i=0;
		while(cbClass.getValue().charAt(i)!= ' ')
		{
			class_id += cbClass.getValue().charAt(i);
			i++;
		}
		msg.add(course_id);
		msg.add(class_id);
		hms.put("Search for class_in_course_id",msg);

		LoginController.userClient.sendServer(hms);
		LoginController.syncWithServer();

		if(LoginController.userClient.ans != null)
		{
			class_in_course_id= String.valueOf((((ArrayList<String>)LoginController.userClient.ans)).get(0));
			msg.clear();
			msg.add(String.valueOf((((ArrayList<String>)LoginController.userClient.ans)).get(0)));
			msg.add(selectedFile.getName());
			try
			{
				Date sbd = Date.valueOf(tfSubmissionDate.getText());
				msg.add(tfSubmissionDate.getText());
			}
			catch(Exception e)
			{
				lblErrorST.setText("Ilegal submission date");
				lblErrorST.setVisible(true);
				return;
			}
			msg.add(dtf.format(localDate));
		}
		hms.clear();
		hms.put("Create task",msg);
		LoginController.userClient.sendServer(hms);
		LoginController.syncWithServer();
		hms.clear();
		lblTaskCreated.setVisible(true);
	}

	/**
	 * File chooser handles the choose of file. restrict file extensions to document only.
	 * @param event
	 */
	@FXML
	void fileChooserHandler(ActionEvent event) {
		Stage uploadStage = new Stage();
		uploadStage.setTitle("Task upload");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Document Files", "*.txt", "*.doc","*.docx","*.pdf","*.xls"));
		selectedFile = fileChooser.showOpenDialog(uploadStage);
		if (selectedFile != null) {
			tfUploadPath.setText(selectedFile.getAbsolutePath());

		}

	}

	/**
	 * File chooser handles the choose of file. restrict file extensions to document only.
	 * @param event
	 */
	@FXML
	void CheckfileChooserHandler(ActionEvent event) {
		btnCheckUpload.setVisible(true);
		Stage uploadStage = new Stage();
		uploadStage.setTitle("Evaluation & task grade upload");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Document Files", "*.txt", "*.doc","*.docx","*.pdf","*.xls"));
		selectedFile = fileChooser.showOpenDialog(uploadStage);
		if (selectedFile != null) {
			tfCheckUploadPath.setText(selectedFile.getAbsolutePath());

		}

	}

	/**
	 * Handles course change in check task window.
	 * @param event
	 */
	@FXML
	void TeacherCourseHandler(ActionEvent event) {
		ArrayList<String> params = new ArrayList<String>();
		lblMsg.setVisible(false);
		lblSubmissionComments.setVisible(false);
		taSubmissionComments.setVisible(false);
		lblMsg.setVisible(false);
		btnDownload.setVisible(false);
		cbClass.setVisible(false);
		lblClass.setVisible(false);
		cbClassID.getItems().clear();
		cbClassID.setVisible(true);
		lblSelectClass.setVisible(true);
		lblSelectTask.setVisible(false);
		lblSelectStudent.setVisible(false);
		cbChooseTask.getItems().clear();
		cbChooseTask.setVisible(false);
		cbStudent.getItems().clear();
		cbStudent.setVisible(false);
		btnCheckUpload.setVisible(false);
		lblTaskGrade.setVisible(false);
		btnDownload.setVisible(false);


		String temp="";
		temp += cbTeacherCourse.getValue().substring(2, 5);

		HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();

		params.add(temp);

		params.add(LoginController.userClient.userName);
		hm.put("Get class id for task", params);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				if(!(cbClassID.getItems().contains(((ArrayList<String>)LoginController.userClient.ans).get(i)))){
					cbClassID.getItems().add(i,((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
				}
			}
		}
		cbClassID.setVisible(true);
		lblClassID.setVisible(true);
		hm.clear();
		params.clear();
	}
	/**
	 * handles the course change in create task window.
	 * @param event
	 */
	@FXML
	void cbCourseHandler(ActionEvent event) {

		ArrayList<String> params = new ArrayList<String>();
		lblErrorST.setVisible(false);
		lblTaskCreated.setVisible(false);
		lblMsg.setVisible(false);
		btnDownload.setVisible(false);
		String temp="";
		cbClass.getItems().clear();
		temp += cbCourseID.getValue().substring(2, 5);

		HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();

		///
		params.add(temp);
		params.add(LoginController.userClient.userName);
		hm.put("Get class id for task", params);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				if(!(cbClass.getItems().contains(((ArrayList<String>)LoginController.userClient.ans).get(i)))){
					cbClass.getItems().add(i,((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
				}
			}
		}
		cbClass.setVisible(true);
		lblClass.setVisible(true);
	}
	//
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {	

		lblUser.setText(UserClient.fullName);
		btnUpload.setVisible(true);
		createTaskPane.setVisible(true);
		checkTaskPane.setVisible(false);

		cbClass.setVisible(false);
		lblClass.setVisible(false);
		cbClassID.setVisible(false);
		lblSelectClass.setVisible(false);
		lblSelectTask.setVisible(false);
		lblSelectStudent.setVisible(false);
		cbChooseTask.setVisible(false);
		cbStudent.setVisible(false);
		btnCheckUpload.setVisible(false);
		lblTaskGrade.setVisible(false);
		btnDownload.setVisible(false);
		String userName;
		course_id_arr = new ArrayList<String>();
		HashMap<String,String> hm = new HashMap<String,String>();
		HashMap<String,ArrayList<String>> cid_hm = new HashMap<String,ArrayList<String>>();
		hm.put("Search for teacher courses", UserClient.userName);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
		{
			if(!(course_id_arr.contains(((ArrayList<String>)LoginController.userClient.ans).get(i))))
				course_id_arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
		}
		cid_hm.put("Search for course name", course_id_arr);
		LoginController.userClient.sendServer(cid_hm);
		LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				if(!(cbCourseID.getItems().contains(course_id_arr.get(i) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString())) && !(cbTeacherCourse.getItems().contains(course_id_arr.get(i) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString()))){
					cbCourseID.getItems().add(i,course_id_arr.get(i) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
					cbTeacherCourse.getItems().add(i,course_id_arr.get(i) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString());

				}
			}
		}
		hm.clear();
		cid_hm.clear();
		course_id_arr.clear();
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
