package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.ouvintes.OuvinteDeMudancaDeDados;
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
import model.entidades.Departamento;
import model.entidades.Vendedor;
import model.exceptions.ValidacaoException;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {

	private Vendedor vd;

	private VendedorService vdService;

	private DepartamentoService dpService;

	private List<OuvinteDeMudancaDeDados> listaOuvintesDados = new ArrayList<>();

	public void setVendedor(Vendedor vd) {
		this.vd = vd;
	}

	public void setServices(VendedorService vs, DepartamentoService dp) {
		this.vdService = vs;
		this.dpService = dp;
	}

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpDataNascimento;

	@FXML
	private TextField txtSalarioBase;

	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;

	@FXML
	private Label labelErroNome;

	@FXML
	private Label labelErroEmail;

	@FXML
	private Label labelErroDataNascimento;

	@FXML
	private Label labelErroSalarioBase;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	@FXML
	private ObservableList<Departamento> obsLista;

	@FXML
	public void onBtSalvarAction(ActionEvent evento) {
		if (vd == null) {
			throw new IllegalStateException("Vendedor nulo");
		}
		if (vdService == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		try {
			vd = getFormData();
			vdService.atualizarOuSalvar(vd);
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
		Constraints.setTextFieldMaxLength(txtNome, 70);
		Constraints.setTextFieldDouble(txtSalarioBase);
		Constraints.setTextFieldMaxLength(txtEmail, 30);
		Utils.formatarDatePicker(dpDataNascimento, "dd/MM/yyyy");
		iniciarComboBoxDepartamento();
	}

	public void atualizarDadosFormulario() {
		if (vd == null) {
			throw new IllegalStateException("Vendedor nulo");
		}
		txtId.setText(String.valueOf(vd.getId()));
		txtNome.setText(vd.getNome());
		txtEmail.setText(vd.getEmail());
		Locale.setDefault(Locale.US);
		txtSalarioBase.setText(String.format("%.2f", vd.getSalarioBase()));
		if (vd.getDataNascimento() != null) {
			dpDataNascimento.setValue(LocalDate.ofInstant(vd.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
		if (vd.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartamento.setValue(vd.getDepartamento());
		}
	}

	public void carregarObjetosAssociados() {
		if (dpService == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		List<Departamento> lista = dpService.encontrarTudo();
		obsLista = FXCollections.observableArrayList(lista);
		comboBoxDepartamento.setItems(obsLista);
	}

	private void setMensagensDeErros(Map<String, String> erros) {
		Set<String> campos = erros.keySet();

		if (campos.contains("nome")) {
			labelErroNome.setText(erros.get("nome"));
		}

	}

	private void iniciarComboBoxDepartamento() {
		Callback<ListView<Departamento>, ListCell<Departamento>> fabrica = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean vazio) {
				super.updateItem(item, vazio);
				setText(vazio ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(fabrica);
		comboBoxDepartamento.setButtonCell(fabrica.call(null));
	}

}
