package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private SellerService sellerService;
	private DepartmentService departmentService;
	private Seller sellerEntity;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private Label lbError;

	@FXML
	private Label lbErrorEmail;

	@FXML
	private Label lbErrorBithDate;

	@FXML
	private Label lbErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	// Objects that implements DataChangeListener can subscribe to receive the event
	// from this class
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent actionEvent) {
		if (sellerEntity == null) {
			throw new IllegalStateException("Seller Entity is null");
		}
		if (sellerService == null) {
			throw new IllegalStateException("Seller Service is null");
		}

		try {
			sellerEntity = getFormData();
			sellerService.saveOrUpdate(sellerEntity);
			// when we have the action of save or update
			// we need to notify the listeners from the list
			// about the change
			notifyDataChangeListeners();
			// to close the window after the action
			Utils.currentStage(actionEvent).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErros());
		} catch (DbException e) {
			Alerts.showAlert("Error saving objetc", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller seller = new Seller();

		ValidationException exceptionError = new ValidationException("Validation Error");

		seller.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exceptionError.addErrors("name", "Field can not be empty");
		}
		seller.setName(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exceptionError.addErrors("email", "Field can not be empty");
		}
		seller.setEmail(txtEmail.getText());
		
		if (dpBirthDate.getValue() == null){
			exceptionError.addErrors("birthDate", "Field can not be empty");
		} else {
			// get the date that was choose by the user and convert to the type of instant, 
			// this type is independent of location  
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			seller.setBirthDate(Date.from(instant));
		}
		
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exceptionError.addErrors("baseSalary", "Field can not be empty");
		}
		seller.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));

		seller.setDepartment(comboBoxDepartment.getValue());
		
		if (exceptionError.getErros().size() > 0) {
			throw exceptionError;
		}

		return seller;
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
		Constraints.setTextFieldMaxLength(txtName, 50);
		Constraints.setTextFieldMaxLength(txtEmail, 70);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(txtBaseSalary);
		
		initializeComboBoxDepartment();
	}

	public void setSeller(Seller sellerEntity) {
		this.sellerEntity = sellerEntity;
	}

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	public void updateFormData() {
		if (sellerEntity == null) {
			throw new IllegalStateException("Seller entity is null");
		}
		txtId.setText(String.valueOf(sellerEntity.getId()));
		txtName.setText(sellerEntity.getName());
		txtEmail.setText(sellerEntity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", sellerEntity.getBaseSalary()));
		if (sellerEntity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(sellerEntity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		
		if (sellerEntity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(sellerEntity.getDepartment());
		}
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Department service is null");
		}
		List<Department> departmentList = departmentService.findAll();
		obsList = FXCollections.observableArrayList(departmentList);
		comboBoxDepartment.setItems(obsList);
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		lbError.setText((fields.contains("name") ? errors.get("name") : ""));
		lbErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		lbErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
		lbErrorBithDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
	}
}