package model.dao;

import java.util.List;

import model.entidades.Departamento;
import model.entidades.Vendedor;

public interface VendedorDao {
	
	void inserir(Vendedor vendedor);
	void atualizar(Vendedor vendedor);
	void deletarPorId(Integer id);
	Vendedor encontrarPorId(Integer id);
	List<Vendedor> encontrarTudo();
	List<Vendedor> encontrarPorDepartamento(Departamento dep);
	
}
