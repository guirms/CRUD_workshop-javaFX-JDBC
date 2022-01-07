package model.services;

import java.util.List;

import model.dao.DaoFabrica;
import model.dao.VendedorDao;
import model.entidades.Vendedor;

public class VendedorService {

	VendedorDao dp = DaoFabrica.criarVendedorDao();

	public List<Vendedor> encontrarTudo() {
		return dp.encontrarTudo();
	}

	public void atualizarOuSalvar(Vendedor obj) {
		if (obj.getId() == null) {
			dp.inserir(obj);
		} else {
			dp.atualizar(obj);
		}
	}
	
	public void remover(Vendedor obj) {
		dp.deletarPorId(obj.getId());
	}

}
