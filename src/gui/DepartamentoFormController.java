package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entidades.Departamento;

public class DepartamentoFormController implements Initializable {

	private Departamento dp;

	public void setDepartamento(Departamento dp) {
		this.dp = dp;
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
	public void onBtSalvarAction() {
		System.out.println("Botão salvar");
	}

	public void onBtCancelarAction() {
		System.out.println("Botão cancelar");
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
