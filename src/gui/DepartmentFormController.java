package gui;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private DepartmentService departmentService;
	private Department departmentEntity;
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label lbError;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	public void onBtSaveAction(ActionEvent actionEvent) {
		if (departmentEntity == null) {
			throw new IllegalStateException("Department Entity is null");
		}
		if (departmentService == null) {
			throw new IllegalStateException("Department Service is null");
		}
		
		try {
			departmentEntity = getFormData();
			departmentService.saveOrUpdate(departmentEntity);
			// to close the window after the action
			Utils.currentStage(actionEvent).close();
		} catch (DbException e) {
			Alerts.showAlert("Error saving objetc", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private Department getFormData() {
		Department department = new Department();
		
		department.setId(Utils.tryParseToInt(txtId.getText()));
		department.setName(txtName.getText());
		
		return department;
	}

	@FXML
	public void onBtCancelAction(ActionEvent actionEvent) {
		Utils.currentStage(actionEvent).close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeFields();
	}

	private void initializeFields() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void setDepartment (Department departmentEntity) {
		this.departmentEntity = departmentEntity;
	}
	
	public void setDepartmentService (DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	
	public void updateFormData() {
		if(departmentEntity == null) {
			throw new IllegalStateException("Department entity is null");
		}
		txtId.setText(String.valueOf(departmentEntity.getId()));
		txtName.setText(departmentEntity.getName());
	}
}
