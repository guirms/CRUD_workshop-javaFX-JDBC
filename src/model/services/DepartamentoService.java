package model.services;

import java.util.List;

import model.dao.DaoFabrica;
import model.dao.DepartamentoDao;
import model.entidades.Departamento;

public class DepartamentoService {
	
	DepartamentoDao dp = DaoFabrica.criarDepartamentoDao();
	
	public List<Departamento> encontrarTudo(){
		return dp.encontrarTudo();
	}
	
	

}
