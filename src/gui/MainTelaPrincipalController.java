package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import aplicacao.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class MainTelaPrincipalController implements Initializable {

	@FXML
	private MenuItem menuItemVendedor;
	@FXML
	private MenuItem menuItemDepartamento;
	@FXML
	private MenuItem menuItemSobre;

	@FXML
	public void onMenuItemVendedorAction() {
		carregarTela("/gui/ListaVendedor.fxml", (ListaVendedorController controller) -> {
			controller.setVendedorService(new VendedorService());
			controller.atualizarTableView();
		});
	}

	@FXML
	public void onMenuItemDepartamentoAction() {
		carregarTela("/gui/ListaDepartamento.fxml", (ListaDepartamentoController controller) -> {
			controller.setDepartamentoService(new DepartamentoService());
			controller.atualizarTableView();
		});
	}

	@FXML
	public void onMenuItemSobreAction() {
		carregarTela("/gui/Sobre.fxml", x -> {});
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	public synchronized <T> void carregarTela(String enderecoCompleto, Consumer<T> inicializacao) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(enderecoCompleto));
			VBox vBox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(vBox);
			
			T controlador = loader.getController();
			inicializacao.accept(controlador);

		} catch (IOException e) {
			Alerts.showAlert("IoException", "Erro ao carregar a página", e.getMessage(), AlertType.ERROR);
		}
	}

}
