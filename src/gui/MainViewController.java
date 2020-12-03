package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction");
	}
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}
	
	public void loadView(String absoluteName) {
		try {
			
			// Loading a new view
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			// Getting the main scene to use and keep some parts of it, like the menu
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)(((ScrollPane) mainScene.getRoot()).getContent());
			
			// Getting the menu from the main view to keep it on the new view
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			
			// Adding the elements on the new VBox
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
		} catch (IOException e){
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
		
	}
}
