package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TeacherController implements Initializable{

	@FXML
	private Hyperlink linkLogout;

	@FXML
	private Label lblUser;

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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {	
		lblUser.setText(UserClient.fullName);

		Stage primaryStage1 = new Stage();
		primaryStage1.setTitle("File Chooser Sample");
		final FileChooser fileChooser = new FileChooser(); 
		configureFileChooser(fileChooser);
	    File file = fileChooser.showOpenDialog(primaryStage1);
	    String filePath = file.getAbsolutePath();



String url = "jdbc:mysql://localhost/hasproject";
    String user = "root";
    String password = "123456";
    try {
    	DriverManager.registerDriver(new com.mysql.jdbc.Driver ());
        Connection conn = DriverManager.getConnection(url, user, password);
        String sql = "INSERT INTO assignment (file) values (?)";
        PreparedStatement statement = conn.prepareStatement(sql);

        InputStream inputStream = new FileInputStream(new File(filePath));

        statement.setBinaryStream(1, inputStream,(int)file.length());

        int row = statement.executeUpdate();
        if (row > 0) {
        	Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);

            VBox vbox = new VBox(new Text("An assignment was inserted with file"));
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(15));

            dialogStage.setScene(new Scene(vbox));
            dialogStage.show();
        }
        conn.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    }



	 }
	private static void configureFileChooser(
	        final FileChooser fileChooser) {      
	            fileChooser.setTitle("Choose File");
	            fileChooser.setInitialDirectory(
	                new File(System.getProperty("user.home"))
	            );                 
	            fileChooser.getExtensionFilters().addAll(
	                new FileChooser.ExtensionFilter("DOC", "*.docx"),
	                new FileChooser.ExtensionFilter("PDF", "*.pdf")
	            );
	}
}
