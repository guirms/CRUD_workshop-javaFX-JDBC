package gui;

import java.net.URL;
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
import model.entidades.Departamento;
import model.services.DepartamentoService;

public class DepartamentoFormController implements Initializable {

	private Departamento dp;

	private DepartamentoService dpService;
	
	private ListaDepartamentoController dpListaController;

	public void setDepartamento(Departamento dp) {
		this.dp = dp;
	}

	public void setDpService(DepartamentoService ds) {
		this.dpService = ds;
	}

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private Label labelErro;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (dp == null) {
			throw new IllegalStateException("Departamento nulo");
		}
		if (dpService == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		try {
			dp = getFormData();
			dpService.atualizarOuSalvar(dp);
			Utils.stageAtual(evento).close();
		} catch(DbException db) {
			Alerts.showAlert("Erro ao salvar", null, db.getMessage(), AlertType.ERROR);
		}
		
	}

	private Departamento getFormData() {
		Departamento dp = new Departamento();

		dp.setId(Utils.TryParseToInt(txtId.getText()));
		dp.setNome(txtNome.getText());

		return dp;
	}

	public void onBtCancelarAction(ActionEvent evento) {
		Utils.stageAtual(evento).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
	}

	public void atualizarDadosFormulario() {
		if (dp == null) {
			throw new IllegalStateException("Departamento nulo");
		}
		txtId.setText(String.valueOf(dp.getId()));
		txtNome.setText(dp.getNome());

	}

}
