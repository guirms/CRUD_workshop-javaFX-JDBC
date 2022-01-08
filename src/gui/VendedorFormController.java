package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.ouvintes.OuvinteDeMudancaDeDados;
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
import model.entidades.Vendedor;
import model.exceptions.ValidacaoException;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {

	private Vendedor dp;

	private VendedorService dpService;

	private List<OuvinteDeMudancaDeDados> listaOuvintesDados = new ArrayList<>();

	public void setVendedor(Vendedor dp) {
		this.dp = dp;
	}

	public void setDpService(VendedorService ds) {
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
			throw new IllegalStateException("Vendedor nulo");
		}
		if (dpService == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		try {
			dp = getFormData();
			dpService.atualizarOuSalvar(dp);
			notificarListaOuvintesDados();
			Utils.stageAtual(evento).close();
		} catch (DbException db) {
			Alerts.showAlert("Erro ao salvar", null, db.getMessage(), AlertType.ERROR);
		} catch (ValidacaoException ve) {
			setMensagensDeErros(ve.getErros());
		}

	}

	private void notificarListaOuvintesDados() {
		for (OuvinteDeMudancaDeDados ouvinte : listaOuvintesDados) {
			ouvinte.onDadoAlterado();
		}

	}

	public void inscreverOuvinteDados(OuvinteDeMudancaDeDados ouvinte) {
		listaOuvintesDados.add(ouvinte);
	}

	private Vendedor getFormData() {
		Vendedor dp = new Vendedor();

		ValidacaoException validException = new ValidacaoException("Erro de validação");

		dp.setId(Utils.TryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim() == "") {
			validException.adicionarErro("nome", "O campo não pode ser vazio");
		}
		dp.setNome(txtNome.getText());

		if (validException.getErros().size() > 0) {
			throw validException;
		}

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
			throw new IllegalStateException("Vendedor nulo");
		}
		txtId.setText(String.valueOf(dp.getId()));
		txtNome.setText(dp.getNome());

	}

	private void setMensagensDeErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();

		if (campos.contains("nome")) {
			labelErro.setText(erros.get("nome"));
		}

	}

}
