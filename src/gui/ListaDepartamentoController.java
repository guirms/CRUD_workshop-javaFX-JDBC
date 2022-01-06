package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entidades.Departamento;
import model.services.DepartamentoService;

public class ListaDepartamentoController implements Initializable {
	
	private DepartamentoService service;
	
	@FXML
	private TableView<Departamento> tableViewDepartamento;
	
	@FXML
	private TableColumn<Departamento, Integer> colunaTabelaId;
	
	@FXML
	private TableColumn<Departamento, String> colunaTabelaNome;
	
	@FXML
	private Button btNovo;
	
	@FXML
	private ObservableList<Departamento> obsList;
	
	public void setDepartamentoService(DepartamentoService ds) {
		this.service = ds;
	}
	
	@FXML
	public void onBtNovoAction() {
		System.out.println("Botão novo");
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
	}
	
}
