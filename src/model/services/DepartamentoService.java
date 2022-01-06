package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entidades.Departamento;

public class DepartamentoService {
	
	public List<Departamento> encontrarTudo(){
		List<Departamento> lista = new ArrayList<>();
		lista.add(new Departamento(1, "Livro"));
		lista.add(new Departamento(2, "Pc"));
		lista.add(new Departamento(3, "Celular"));
		return lista;

	}
	
	

}
