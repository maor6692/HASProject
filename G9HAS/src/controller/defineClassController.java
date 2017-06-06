package controller;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import common.*;




	public class defineClassController {

	    @FXML
	    private TextField txtFieldClassId;
	    @FXML
	    private TextField txtFieldYear;
	    @FXML
	    private TextField txtFieldSemester;
	    @FXML
	    private Hyperlink linkLogout;
	    @FXML
	    private Label lblYear;
	    @FXML
	    private Button btnAdd;
	    @FXML
	    private Label lblUser;
	    @FXML
	    private Label lblClassName;
	    @FXML
	    private Label lblErr;
	    @FXML
	    private Label lblSemester;
	    @FXML
	    private Label lblClassId;
	    @FXML
	    private TextField txtFieldClassName;
	    @FXML

		void logoutHandler(ActionEvent event) {
			Parent user_parent;
			try {
				user_parent = FXMLLoader.load(getClass().getResource("../gui/loginWindow.fxml"));
				Scene user_scene = new Scene(user_parent);
				Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
				stage.setScene(user_scene);
				stage.show();  

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    @FXML
	    void addClassHandler(ActionEvent event){
	    	lblErr.setText("");
	    	if(txtFieldClassName.getText().equals(null)){
	    		lblErr.setText("fill class name!");
	    		return;
	    	}
	    	if(txtFieldClassId.getText().equals(null)){
	    		lblErr.setText("fill class id!");
	    		return;
	    	}
	    	if(txtFieldYear.getText().equals(null)){
	    		lblErr.setText("fill class year!");
	    		return;
	    	}
	    	if(txtFieldSemester.getText().equals(null)){
	    		lblErr.setText("fill class semester!");
	    		return;
	    	}
	    	int id = Integer.parseInt(txtFieldClassId.getText());
	    	String name = txtFieldClassName.getText();
	    	int year = Integer.parseInt(txtFieldYear.getText());
	    	int semester = Integer.parseInt(txtFieldSemester.getText());
	    	Class c1 = new Class(id,name,year,semester);
	    }
	    
    

	}


