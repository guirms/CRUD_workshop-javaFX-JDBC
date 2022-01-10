package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import aplicacao.Main;
import db.DbException;
import gui.ouvintes.OuvinteDeMudancaDeDados;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidades.Vendedor;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class ListaVendedorController implements Initializable, OuvinteDeMudancaDeDados {

	private VendedorService service;

	@FXML
	private TableView<Vendedor> tableViewVendedor;

	@FXML
	private TableColumn<Vendedor, Integer> colunaTabelaId;

	@FXML
	private TableColumn<Vendedor, String> colunaTabelaNome;
	
	@FXML
	private TableColumn<Vendedor, String> colunaTabelaEmail;
	
	@FXML
	private TableColumn<Vendedor, Double> colunaTabelaSalarioBase;
	
	@FXML
	private TableColumn<Vendedor, Date> colunaTabelaDataNascimento;
	
	@FXML
	private TableColumn<Vendedor, Vendedor> colunaTabelaEDIT;

	@FXML
	private TableColumn<Vendedor, Vendedor> colunaTabelaRemover;

	@FXML
	private Button btNovo;

	@FXML
	private ObservableList<Vendedor> obsList;

	public void setVendedorService(VendedorService ds) {
		this.service = ds;
	}

	@FXML
	public void onBtNovoAction(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		Vendedor dp = new Vendedor();
		criarDialogoForm(dp, "/gui/VendedorForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		iniciarNodes();
	}

	private void iniciarNodes() {
		colunaTabelaId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		colunaTabelaNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));
		colunaTabelaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		colunaTabelaDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatarDataColunaTabela(colunaTabelaDataNascimento, "dd/MM/yyyy");
		colunaTabelaSalarioBase.setCellValueFactory(new PropertyValueFactory<>("salarioBase"));
		Utils.formatarDoubleColunaTabela(colunaTabelaSalarioBase, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}

	public void atualizarTableView() {
		if (service == null) {
			throw new IllegalStateException("Service nulo");
		}
		List<Vendedor> lista = service.encontrarTudo();
		obsList = FXCollections.observableArrayList(lista);
		tableViewVendedor.setItems(obsList);
		initBotoesEdit();
		initBotoesRemocao();
	}

	private void criarDialogoForm(Vendedor dp, String enderecoCompleto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(enderecoCompleto));
			Pane pane = loader.load();

			VendedorFormController controller = loader.getController();
			controller.setVendedor(dp);
			controller.setServices(new VendedorService(), new DepartamentoService());
			controller.carregarObjetosAssociados();
			controller.inscreverOuvinteDados(this);
			controller.atualizarDadosFormulario();

			Stage stageDialogo = new Stage();
			stageDialogo.setTitle("Digite os dados do departamento");
			stageDialogo.setScene(new Scene(pane));
			stageDialogo.setResizable(false);
			stageDialogo.initOwner(parentStage);
			stageDialogo.initModality(Modality.WINDOW_MODAL);
			stageDialogo.showAndWait();

		} catch (IOException io) {
			io.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao carregar a tela", io.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDadoAlterado() {
		atualizarTableView();

	}

	private void initBotoesEdit() {
		colunaTabelaEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaTabelaEDIT.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> criarDialogoForm(obj, "/gui/VendedorForm.fxml", Utils.stageAtual(event)));
			}
		});
	}

	private void initBotoesRemocao() {
		colunaTabelaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaTabelaRemover.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removerEntidade(obj));
			}

		});
	}

	private void removerEntidade(Vendedor obj) {
		Optional<ButtonType> resultado = Alerts.mostrarConfirmacao("Confirmação", "Confirmar remoção");

		if (resultado.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço nulo");
			}
			try {
				service.remover(obj);
				atualizarTableView();
			} catch (DbException de) {
				Alerts.showAlert("Erro removendo objeto", null, de.getMessage(), AlertType.ERROR);
			}
		}
	}

}
