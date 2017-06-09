package controller;


import java.awt.List;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SecretaryController implements Initializable{
	ComboBoxTableCell cb = new ComboBoxTableCell();
	
	private final ObservableList<Row> rows =
			FXCollections.observableArrayList(
					new Row("A", new ComboBoxTableCell("gsdfg","jfdssfk")),
					new Row("B", new ComboBoxTableCell("gsdfg","jfdssfk")),
					new Row("C", new ComboBoxTableCell("gdg","jdsfsdk")),
					new Row("D", new ComboBoxTableCell("gfg","jhgftdk")),
					new Row("E", new ComboBoxTableCell("gdsfg","sdf"))
					);


	@FXML
	private Pane paneRemoveStudent,paneChangeAppointment,paneCreateSemester,paneDefineClass,paneAddStudent;

	@FXML
	private Label lblUser;

	@FXML
	private ComboBox<?> cmbChooseCourse;

	private ComboBox<String> cmbTeacher;

	@FXML
	private ComboBox<?> cmbYear;

	@FXML
	private ListView<String> lvClasses;

	@FXML
	private TableView<Row> tblClassTeacher;

	@FXML
	private TableView<String> tblExceptions;

	@FXML
	private Button btnAssign;



	@FXML
	private TableColumn<ComboBoxTableCell,String> teachers;

	@FXML
	private TableColumn<String,String> classes;

	@FXML
	void listViewEditChoise(ActionEvent event) {

	}

	@FXML
	void classClickHandler(ActionEvent event) {

	}


	@FXML
	void chooseCourseHandler(ActionEvent event) {

	}

	@FXML
	void chooseYearHandler(ActionEvent event) {

	}

	@FXML
	void chooseSemesterHandler(ActionEvent event) {

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
		initializeCreateSemester();

	}

	void initializeCreateSemester(){
		lblUser.setText(UserClient.fullName);
		lvClasses.getItems().addAll("Algebra","Hedva","computers","real time");
		lvClasses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		cmbTeacher = new ComboBox<>();
		classes.setCellValueFactory(new PropertyValueFactory<>("classi"));
		//classes.setCellValueFactory(new PropertyValueFactory<>("teacher"));
		teachers.setCellFactory(ComboBoxTableCell.forTableColumn("avi sofer ben zona","Malki","Ilya"));
		tblClassTeacher.setItems(rows);
	
	}
	public static class Row {

		private final SimpleStringProperty classi;
		private final ComboBoxTableCell teacher;

		private Row(String classi, ComboBoxTableCell teacher) {
			this.classi = new SimpleStringProperty(classi);
			this.teacher = new ComboBoxTableCell(teacher);
		}

		public String getClassi() {
			return classi.get();
		}

		public void setClass(String class1) {
			classi.set(class1);
		}

		public ComboBoxTableCell getTeacher() {
			return teacher;
		}

		public void setTeacher(ComboBoxTableCell teacher1) {
			
		}

	}

} 



