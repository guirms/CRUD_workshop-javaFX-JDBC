package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
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

public class MainTelaPrincipalController implements Initializable {

	@FXML
	private MenuItem menuItemVendedor;
	@FXML
	private MenuItem menuItemDepartamento;
	@FXML
	private MenuItem menuItemSobre;

	@FXML
	public void onMenuItemVendedorAction() {
		System.out.println("onMenuItemVendedorAction()");
	}

	@FXML
	public void onMenuItemDepartamentoAction() {
		carregarTela("/gui/ListaDepartamento.fxml");
	}

	@FXML
	public void onMenuItemSobreAction() {
		carregarTela("/gui/Sobre.fxml");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	public synchronized void carregarTela(String enderecoCompleto) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(enderecoCompleto));
			VBox vBox = loader.load();
			 
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox)((ScrollPane)mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(vBox);
			
		} catch (IOException e) {
			Alerts.showAlert("IoException", "Erro ao carregar a página", e.getMessage(), AlertType.ERROR);
		}
	}

}
