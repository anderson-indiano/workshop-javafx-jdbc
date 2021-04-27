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
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	private SellerService sellerService;

	private Seller sellerEntity;

	public void setSellerEntity(Seller sellerEntity) {
		this.sellerEntity = sellerEntity;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent actionEvent) {
		if (sellerEntity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (sellerService == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			sellerEntity = getFormData();
			sellerService.saveOrUpdate(sellerEntity);
			notifyDataChangeListeners();
			Utils.currentStage(actionEvent).close();
		} 
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} 
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller seller = new Seller();
		ValidationException exception = new ValidationException("Validation error");
		
		seller.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", " Field can't be empty");
		}		
		
		seller.setName(txtName.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return seller;
	}

	@FXML
	public void onBtCancelAction(ActionEvent actionEvent) {
		Utils.currentStage(actionEvent).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormData() {
		if (sellerEntity == null) {
			throw new IllegalStateException("Seller entity was null");
		}
		txtId.setText(String.valueOf(sellerEntity.getId()));
		txtName.setText(sellerEntity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}












