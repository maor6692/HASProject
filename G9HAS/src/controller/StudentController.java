package controller;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;


public class StudentController implements Initializable {
    private ArrayList<String> taskID = new ArrayList<String>();
    HashMap<String,ArrayList<String>> details=new HashMap<String,ArrayList<String>>();
    ArrayList<String> row=new ArrayList<String>();
    private String cid = "";
    private String filename="";
    private File selectedFile;
    private String file_type="";
    @FXML
    private Label lblGradeInTask;
    @FXML
    private Hyperlink hlSubmitTask;
    
    @FXML
    private Label lblErrorST;
    @FXML
    private ComboBox<String> cbChooseCourseST;
    
    @FXML
    private Label lblSubmitTask;
    @FXML
    private Label lblChooseCourse1;
    
	@FXML
	private Hyperlink linkLogout;

	@FXML
	private Label lblCPersonalInfo;
    @FXML
    private Label lblDownloadComplete;
	@FXML
	private Label lblUser;
	@FXML
	private TextArea taTasks;
    @FXML
    private Label lblUpload;
    @FXML
    private Pane SubmitTaskPane;
	@FXML
	private ComboBox<String> cbTask;

	@FXML
	private Label lblShowUsername;
    @FXML
    private Label lblChooseTask;
    @FXML
    private Button btnUploadTask;
    @FXML
    private TextField tfUploadPath;
    @FXML
    private Button btnDownload;

    @FXML
    private Button btnFile;

	@FXML
	private ComboBox<String> cbCourseInClass;
    @FXML
    private ComboBox<String> cbChooseTask;
	@FXML
	private Hyperlink hlPersonalInfo;

	@FXML
	private Label lblCourseAvg;

	@FXML
	private Label lblTaskGrade;

	@FXML
	private Label lblTask;
    @FXML
    private Label lblTeacherNotes;
    @FXML
    private TextArea taTeacherNotes;
	@FXML
	private Label lblAvg;

	@FXML
	private Label lblUsername;

	@FXML
	private Label lblClass;

	@FXML
	private Label lblShowClass;
    @FXML
    private Pane personalInfoPane;
    /**
     * Changes the task details to task selected
     * @param event
     */
    @FXML
    void chooseTaskHandler(ActionEvent event) {
    	lblUpload.setVisible(true);
    	btnUploadTask.setVisible(false);
    	btnFile.setVisible(true);
    	tfUploadPath.setVisible(true);
    	lblErrorST.setVisible(false);
    	lblGradeInTask.setVisible(false);
    	lblTeacherNotes.setVisible(false);
    	taTeacherNotes.setVisible(false);
    	String tempstr[],stemp;
    	filename="";
    	ArrayList<String> arr = new ArrayList<String>();
    	int i;
    	HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();
    	filename=cbChooseCourseST.getValue().substring(0, 5)+"_";
    	filename+=LoginController.userClient.userName+"-";
    	if(cbChooseTask.getValue()!=null){
    		btnDownload.setVisible(true);
		arr.add(taskID.get(cbChooseTask.getItems().indexOf(cbChooseTask.getValue())));
		hm.put("get file name for task id", arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		row.clear();
		row.add(LoginController.userClient.userName);
		row.add(taskID.get(cbChooseTask.getItems().indexOf(cbChooseTask.getValue())));
		row.add(cbChooseCourseST.getValue().substring(2, 5));
//		for(i=0;i<((ArrayList<String>)LoginController.userClient.ans).get(0).length();i++)
//		{
//			if(((ArrayList<String>)LoginController.userClient.ans).get(0).charAt(i)=='.' && (i==((ArrayList<String>)LoginController.userClient.ans).get(0).length()-5 || i== ((ArrayList<String>)LoginController.userClient.ans).get(0).length()-4))
//				break;
//			
//		}

		tempstr = ((ArrayList<String>)LoginController.userClient.ans).get(0).split("\\.");
		stemp = tempstr[tempstr.length-1];
		filename += ((ArrayList<String>)LoginController.userClient.ans).get(0).substring(0, ((ArrayList<String>)LoginController.userClient.ans).get(0).length()-stemp.length()-1)+".";

		
		//filename += ((ArrayList<String>)LoginController.userClient.ans).get(0).substring(0, i);
		//System.out.println(file_type);
		//filename += file_type;
		
		hm.clear();
		hm.put("get status for task of student",row);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(((ArrayList<String>)LoginController.userClient.ans).size()>0 && Float.parseFloat(((ArrayList<String>)LoginController.userClient.ans).get(0))!=0)
		{
			lblGradeInTask.setText("Your task grade is: "+((ArrayList<String>)LoginController.userClient.ans).get(0));
			lblGradeInTask.setVisible(true);
			lblUpload.setVisible(false);
			tfUploadPath.setVisible(false);
			btnFile.setVisible(false);
			btnUploadTask.setVisible(false);
			lblTeacherNotes.setVisible(true);
			taTeacherNotes.setVisible(true);
			taTeacherNotes.setText(((ArrayList<String>)LoginController.userClient.ans).get(1));
		}
		hm.clear();
		}
    }
    /**
     * Responsible for download the selected task
     * @param event
     */
    @FXML
    void downloadHandler(ActionEvent event) {
    	ArrayList<String> arr = new ArrayList<String>();
    	HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();
    	String path = "",fname="";
		Stage downloadStage = new Stage();
		downloadStage.setTitle("Task Download");
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Choose folder");
		 File folderpath = dc.showDialog(downloadStage);
		 if(folderpath != null)
			 path = folderpath.getAbsolutePath();

			 byte[] by;
			 arr.add(taskID.get(cbChooseTask.getItems().indexOf(cbChooseTask.getValue())));
			 hm.put("get file name for task id", arr);
			 LoginController.userClient.sendServer(hm);
			 LoginController.syncWithServer();
			 fname = ((ArrayList<String>)LoginController.userClient.ans).get(0);
			 arr.clear();
			 arr.add(fname);
			 hm.remove("get file name for task id");
				 hm.put("get file from filename", arr);
				 LoginController.userClient.sendServer(hm);
				 LoginController.syncWithServer();
				 by = ((byte[])LoginController.userClient.ans);
				 try {
					 Files.write(Paths.get(path+"\\"+fname), by);
					 lblDownloadComplete.setVisible(true);
				 } catch (IOException e1) {
					 // TODO Auto-generated catch block
					 //e1.printStackTrace();
				 }
			 catch(Exception e)
			 {
				 
			 }	 
		 
    }
    /**
     * responsible for the path of the file we want to upload
     * @param event
     */
    @FXML
    void fileUploadHandler(ActionEvent event) {
    	
		Stage uploadStage = new Stage();
		uploadStage.setTitle("Submission upload");
    	 FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Document Files", "*.txt", "*.doc","*.docx","*.pdf","*.xls"));
    	 selectedFile = fileChooser.showOpenDialog(uploadStage);
    	 if (selectedFile != null) {
    		 btnUploadTask.setVisible(true);
    		tfUploadPath.setText(selectedFile.getAbsolutePath());
//    		if(selectedFile.getName().substring(selectedFile.getName().length()-5, selectedFile.getName().length()-2).charAt(0)== '.')
//    		file_type += selectedFile.getName().substring(selectedFile.getName().length()-5);
//    		else
//    			file_type += selectedFile.getName().substring(selectedFile.getName().length()-4);
    	 }
    }
    /**
     * upload the chosen file as a solution to selected task.
     * @param event
     */
    @FXML
    void uploadTaskHandler(ActionEvent event) {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.now();
		String tempstr[],stemp;
		tempstr = selectedFile.getName().split("\\.");
		stemp = tempstr[tempstr.length-1];
		filename += stemp;
		row.add(filename);
		row.add(String.valueOf(localDate));
		details.clear();
		details.put("add task to student",row);
		LoginController.userClient.sendServer(details);
        LoginController.syncWithServer();
        if((LoginController.userClient.ans)!= null)
        if(((String)(LoginController.userClient.ans)).equals("exist"))
        {
        	lblErrorST.setText("There is already submission for this task");
        	lblErrorST.setVisible(true);
        }
		//
    	byte[] by = null;
    	HashMap<String,HashMap<String,byte[]>> hm = new HashMap<String,HashMap<String,byte[]>>();
    	HashMap<String,byte[]> hmName = new HashMap<String,byte[]>();
    	
    	//HashMap<String,byte[]> hm = new HashMap<String,byte[]>();
    	HashMap<String,ArrayList<String>> hms = new HashMap<String,ArrayList<String>>();
    	ArrayList<String> msg = new ArrayList<String>();
        File file = new File(tfUploadPath.getText());
        try {
			by = Files.readAllBytes(file.toPath());
			hmName.put(filename, by);
			hm.put("Submission upload", hmName);
			 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LoginController.userClient.sendServer(hm);
        LoginController.syncWithServer();
        hm.clear();
        hmName.clear();
        lblErrorST.setText("Task uploaded successfuly");
        lblErrorST.setVisible(true);
    }
    /**
     * chooses the course which we need to upload solution to task.
     * @param event
     */
    @FXML
    void ChooseCourseSTHandler(ActionEvent event) {

    	cbChooseTask.getItems().clear();
    	ArrayList<String> arr = new ArrayList<String>();
    	HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();
    	cid = "";
    	cid += cbChooseCourseST.getValue().substring(2, 5);
    	arr.add(cid);
    	hm.put("get class in course id for course id", arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		
//		if(LoginController.userClient.ans != null){
//			for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
//				arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
//		}
		hm.remove("get class in course id for course id");
		hm.put("get tasks id",((ArrayList<String>)LoginController.userClient.ans));
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
		if(!((ArrayList<String>)LoginController.userClient.ans).isEmpty()){
		for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
		{
		if(!taskID.contains(((ArrayList<String>)LoginController.userClient.ans).get(i)))
		taskID.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
		if(!(cbChooseTask.getItems().contains("Task no. "+(i+1))))
		cbChooseTask.getItems().add(i, "Task no. "+(i+1));
		}
    }
		hm.remove("get tasks id");
		arr.clear();

		
    	
    }
    /**
     * changes pane to submit task.
     * @param event
     */
    @FXML
    void hlSubmitTaskOnClick(ActionEvent event) {
    	personalInfoPane.setVisible(false);
    	SubmitTaskPane.setVisible(true);
		ArrayList<String> arr=new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm= new HashMap<String,ArrayList<String>>();
		arr.add(UserClient.userName);

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
			for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++){
				arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
			}			
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
				if(!(cbChooseCourseST.getItems().contains(arr.get(i)+" - "+((ArrayList<String>)LoginController.userClient.ans).get(i).toString())))
				cbChooseCourseST.getItems().add(i,arr.get(i)+" - "+((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
			}
		}

		arr.clear();

		
    }
    /**
     * logout handler, logout the user if X is pressed and change the window to login window.
     * @param event
     */
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
    /**
     * Responsible for displaying personal information of the student to selected course.
     * @param event
     */
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
		for(int i=2;i<5;i++)
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
    /**
     * change pane to personal info
     * @param event
     */
    @FXML
    void PersonalInfoHandler(ActionEvent event) {
    	personalInfoPane.setVisible(true);
    	SubmitTaskPane.setVisible(false);
    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		btnDownload.setVisible(false);
		lblUpload.setVisible(false);
		btnUploadTask.setVisible(false);
		btnFile.setVisible(false);
		tfUploadPath.setVisible(false);
		ArrayList<String> arr=new ArrayList<String>();
		HashMap<String,ArrayList<String>> hm= new HashMap<String,ArrayList<String>>();
    	personalInfoPane.setVisible(true);
    	SubmitTaskPane.setVisible(false);
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
		hm.remove("get course id");
		hm.put("Search for course name",arr);
		LoginController.userClient.sendServer(hm);
		LoginController.syncWithServer();
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
		lblAvg.setText(String.valueOf((Float)(sumgrades/((ArrayList<String>)LoginController.userClient.ans).size())));
        
	}

}

