package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
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
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private DepartmentService departmentService;
	private Department departmentEntity;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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

	// Objects that implements DataChangeListener can subscribe to receive the event
	// from this class
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

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
			// when we have the action of save or update
			// we need to notify the listeners from the list
			// about the change
			notifyDataChangeListeners();
			// to close the window after the action
			Utils.currentStage(actionEvent).close();
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErros());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving objetc", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
		Department department = new Department();

		ValidationException exceptionError = new ValidationException("Validation Error");

		department.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exceptionError.addErrors("name", "Field can not be empty");
		}
		department.setName(txtName.getText());
		
		if (exceptionError.getErros().size() > 0) {
			throw exceptionError;
		}

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

	public void setDepartment(Department departmentEntity) {
		this.departmentEntity = departmentEntity;
	}

	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	public void updateFormData() {
		if (departmentEntity == null) {
			throw new IllegalStateException("Department entity is null");
		}
		txtId.setText(String.valueOf(departmentEntity.getId()));
		txtName.setText(departmentEntity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			lbError.setText(errors.get("name"));
		}
	}
}