package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
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
import model.entidades.Departamento;
import model.services.DepartamentoService;

public class ListaDepartamentoController implements Initializable, OuvinteDeMudancaDeDados {

	private DepartamentoService service;

	@FXML
	private TableView<Departamento> tableViewDepartamento;

	@FXML
	private TableColumn<Departamento, Integer> colunaTabelaId;

	@FXML
	private TableColumn<Departamento, String> colunaTabelaNome;

	@FXML
	TableColumn<Departamento, Departamento> colunaTabelaEDIT;

	@FXML
	TableColumn<Departamento, Departamento> colunaTabelaRemover;

	@FXML
	private Button btNovo;

	@FXML
	private ObservableList<Departamento> obsList;

	public void setDepartamentoService(DepartamentoService ds) {
		this.service = ds;
	}

	@FXML
	public void onBtNovoAction(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		Departamento dp = new Departamento();
		criarDialogoForm(dp, "/gui/DepartamentoForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		iniciarNodes();
	}

	private void iniciarNodes() {
		colunaTabelaId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		colunaTabelaNome.setCellValueFactory(new PropertyValueFactory<>("Nome"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void atualizarTableView() {
		if (service == null) {
			throw new IllegalStateException("Service nulo");
		}
		List<Departamento> lista = service.encontrarTudo();
		obsList = FXCollections.observableArrayList(lista);
		tableViewDepartamento.setItems(obsList);
		initBotoesEdit();
		initBotoesRemocao();
	}

	private void criarDialogoForm(Departamento dp, String enderecoCompleto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(enderecoCompleto));
			Pane pane = loader.load();

			DepartamentoFormController controller = loader.getController();
			controller.setDepartamento(dp);
			controller.setDpService(new DepartamentoService());
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
			Alerts.showAlert("IOException", "Erro ao carregar a tela", io.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDadoAlterado() {
		atualizarTableView();

	}

	private void initBotoesEdit() {
		colunaTabelaEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaTabelaEDIT.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> criarDialogoForm(obj, "/gui/DepartamentoForm.fxml", Utils.stageAtual(event)));
			}
		});
	}

	private void initBotoesRemocao() {
		colunaTabelaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaTabelaRemover.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
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

	private void removerEntidade(Departamento obj) {
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
