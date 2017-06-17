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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class TeacherController implements Initializable{
	private ArrayList<String> course_id_arr;
	private ArrayList<String> class_id_arr;
	private File selectedFile;
	private String class_id = "";
	private int class_in_course_id;
	private String course_id = "";



    @FXML
    private Pane defineCoursePane;

    @FXML
    private Pane createTaskPane;

    @FXML
    private Hyperlink linkLogout;

    @FXML
    private Label lblCreateTask;

    @FXML
    private Button btnCreateTask;

    @FXML
    private TextField tfUploadPath;

    @FXML
    private ComboBox<String> cbCourseID;

    @FXML
    private Button btnUpload;

    @FXML
    private Label lblClass;

    @FXML
    private Label lblTask;

    @FXML
    private Label lblCourseID;

    @FXML
    private Hyperlink hlCreateTask;

    @FXML
    private Hyperlink hlCheckTask;

    @FXML
    private TextField tfSubmissionDate;

    @FXML
    private Label lblUser;

    @FXML
    private Label lblSubmissionDate;

    @FXML
    private ComboBox<String> cbClass;

    @FXML
    private Pane checkTaskPane;


    @FXML
    void hlCheckTaskOnClick(ActionEvent event) {
    	
    	createTaskPane.setVisible(false);
    	checkTaskPane.setVisible(true);
    }
//    @FXML
//    void uploadHandler(ActionEvent event) {
//    	byte[] by = null;
//    	HashMap<String,byte[]> hm = new HashMap<String,byte[]>();
//    	
//        File file = new File("c:\\tc\\mesi.docx");
//        try {
//			by = Files.readAllBytes(file.toPath());
//			hm.put("upload", by);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        LoginController.userClient.sendServer(hm);
// 
//    }
    
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
    @FXML
    void hlcreateTaskOnClick(ActionEvent event) {
    	createTaskPane.setVisible(true);
    	checkTaskPane.setVisible(false);
    }
	@FXML
	void createTaskHandler(ActionEvent event) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		LocalDate localDate = LocalDate.now();
		course_id= "";
		class_id="";
    	byte[] by = null;
    	HashMap<String,HashMap<String,byte[]>> hm = new HashMap<String,HashMap<String,byte[]>>();
    	HashMap<String,byte[]> hmName = new HashMap<String,byte[]>();
    	
    	//HashMap<String,byte[]> hm = new HashMap<String,byte[]>();
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
      //  i=0;
       // while(cbClass.getValue().charAt(i)!= ' ')
        //{
        	class_id += cbClass.getSelectionModel().getSelectedItem();
        	//i++;
        //}
        msg.add(course_id);
        msg.add(class_id);
        hms.put("Search for class_in_course_id",msg);
        
        LoginController.userClient.sendServer(hms);
        LoginController.syncWithServer();
        
		if(LoginController.userClient.ans != null)
		{
		msg.clear();
		msg.add(String.valueOf((((ArrayList<String>)LoginController.userClient.ans)).get(0)));
		msg.add(selectedFile.getName());
		msg.add(tfSubmissionDate.getText());
		msg.add(dtf.format(localDate));
		}
		System.out.println(msg.toString());
		hms.clear();
		hms.put("Create task",msg);
        LoginController.userClient.sendServer(hms);
        LoginController.syncWithServer();
        hms.clear();
        //LoginController.syncWithServer();
		
	}

	
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
    @FXML
    void cbCourseHandler(ActionEvent event) {
    	ArrayList<String> params = new ArrayList<String>();
    	String temp="";
    	cbClass.getItems().clear();
    	temp += cbCourseID.getValue().substring(0, 3);
    	
    	HashMap<String,ArrayList<String>> hm = new HashMap<String,ArrayList<String>>();
    	params.add(temp);
    	System.out.println(temp);
    	params.add(LoginController.userClient.userName);
    	System.out.println(LoginController.userClient.userName);
    	hm.put("Get class id for task", params);
        LoginController.userClient.sendServer(hm);
        LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				if(!(cbClass.getItems().contains(((ArrayList<String>)LoginController.userClient.ans).get(i))))
				cbClass.getItems().add(i,((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
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
		String userName;
		course_id_arr = new ArrayList<String>();
        HashMap<String,String> hm = new HashMap<String,String>();
        HashMap<String,ArrayList<String>> cid_hm = new HashMap<String,ArrayList<String>>();
        hm.put("Search for teacher courses", UserClient.userName);
        LoginController.userClient.sendServer(hm);
        LoginController.syncWithServer();
        for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
        {
        	course_id_arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
        }
        cid_hm.put("Search for course name", (ArrayList<String>)LoginController.userClient.ans);
        LoginController.userClient.sendServer(cid_hm);
        LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				if(!(cbCourseID.getItems().contains(course_id_arr.get(i) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString())))
				cbCourseID.getItems().add(i,course_id_arr.get(i) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
			}
		}
	}
}