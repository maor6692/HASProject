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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TeacherController implements Initializable{
	private ArrayList<String> cid_arr;
	private int class_id;
	private int course_id;
	@FXML
	private Hyperlink linkLogout;
    @FXML
    private TextField tfUploadPath;
	@FXML
	private Label lblUser;
    @FXML
    private Button btnUpload;
    @FXML
    private Button btnCreateTask;
    @FXML
    private ComboBox<String> cbCourseID;
    @FXML
    private TextField tfSubmissionDate;

    @FXML
    private Label lblSubmissionDate;
    @FXML
    private Label lblClass;

    @FXML
    private ComboBox<String> cbClass;

    @FXML
    void uploadHandler(ActionEvent event) {
    	byte[] by = null;
    	HashMap<String,byte[]> hm = new HashMap<String,byte[]>();
    	
        File file = new File("c:\\tc\\mesi.docx");
        try {
			by = Files.readAllBytes(file.toPath());
			hm.put("upload", by);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        LoginController.userClient.sendServer(hm);
 
    }
    
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

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@FXML
	void createTaskHandler(ActionEvent event) {
		//int course_id, class_id;
		//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		//LocalDate localDate = LocalDate.now();
		//dtf.format(localDate);
		
		
	}

    @FXML
    void fileChooserHandler(ActionEvent event) {
		Stage uploadStage = new Stage();
		uploadStage.setTitle("Task upload");
    	 FileChooser fileChooser = new FileChooser();
    	 fileChooser.setTitle("Open Resource File");
    	 fileChooser.getExtensionFilters().addAll(
    	         new ExtensionFilter("Text Files", "*.txt"),
    	         new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
    	         new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
    	         new ExtensionFilter("All Files", "*.*"));
    	 File selectedFile = fileChooser.showOpenDialog(uploadStage);
    	 if (selectedFile != null) {
    		 tfUploadPath.setText(selectedFile.getAbsolutePath());
    	 }
         
    }
    @FXML
    void cbCourseHandler(ActionEvent event) {
    	ArrayList<String> params = new ArrayList<String>();
    	String temp="";
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
		cbClass.setVisible(false);
		lblClass.setVisible(false);
		String userName;
		cid_arr = new ArrayList<String>();
        HashMap<String,String> hm = new HashMap<String,String>();
        HashMap<String,ArrayList<String>> cid_hm = new HashMap<String,ArrayList<String>>();
        hm.put("Search for teacher courses", UserClient.userName);
        LoginController.userClient.sendServer(hm);
        LoginController.syncWithServer();
        for(int i=0;i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
        {
        	cid_arr.add(((ArrayList<String>)LoginController.userClient.ans).get(i));
        }
        cid_hm.put("Search for course name", (ArrayList<String>)LoginController.userClient.ans);
        LoginController.userClient.sendServer(cid_hm);
        LoginController.syncWithServer();
		if(LoginController.userClient.ans != null)
		{
			for(int i=0; i<((ArrayList<String>)LoginController.userClient.ans).size();i++)
			{
				if(!(cbCourseID.getItems().contains(cid_arr.get(0) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString())))
				cbCourseID.getItems().add(i,cid_arr.get(0) + " - " +((ArrayList<String>)LoginController.userClient.ans).get(i).toString());
			}
		}
	}//
//		Stage primaryStage1 = new Stage();
//		primaryStage1.setTitle("File Chooser Sample");
//		final FileChooser fileChooser = new FileChooser(); 
//		configureFileChooser(fileChooser);
//	    File file = fileChooser.showOpenDialog(primaryStage1);
//	    String filePath = file.getAbsolutePath();
//
//
//
//String url = "jdbc:mysql://localhost/hasproject";
//    String user = "root";
//    String password = "123456";
//    try {
//    	DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
//        Connection conn = DriverManager.getConnection(url, user, password);
//        String sql = "INSERT INTO assignment (file) values (?)";
//        PreparedStatement statement = conn.prepareStatement(sql);
//
//        InputStream inputStream = new FileInputStream(new File(filePath));
//
//        statement.setBinaryStream(1, inputStream,(int)file.length());
//
//        int row = statement.executeUpdate();
//        if (row > 0) {
//        	Stage dialogStage = new Stage();
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//
//            VBox vbox = new VBox(new Text("An assignment was inserted with file"));
//            vbox.setAlignment(Pos.CENTER);
//            vbox.setPadding(new Insets(15));
//
//            dialogStage.setScene(new Scene(vbox));
//            dialogStage.show();
//        }
//        conn.close();
//    } catch (SQLException ex) {
//        ex.printStackTrace();
//    } catch (IOException ex) {
//        ex.printStackTrace();
//    }
//
//
//
//	 }
//	private static void configureFileChooser(
//	        final FileChooser fileChooser) {      
//	            fileChooser.setTitle("Choose File");
//	            fileChooser.setInitialDirectory(
//	                new File(System.getProperty("user.home"))
//	            );                 
//	            fileChooser.getExtensionFilters().addAll(
//	                new FileChooser.ExtensionFilter("DOC", "*.docx"),
//	                new FileChooser.ExtensionFilter("PDF", "*.pdf")
//	            );
//	}
}