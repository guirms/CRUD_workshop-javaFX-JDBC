package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartamentoDao;
import model.entidades.Departamento;

public class DepartamentoDaoJDBC implements DepartamentoDao {

	private Connection conn;

	public DepartamentoDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void inserir(Departamento dp) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO department\r\n" + "(Name)\r\n" + "VALUES\r\n" + "(?)",
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, dp.getNome());

			int linhasAlteradas = ps.executeUpdate();

			if (linhasAlteradas > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					dp.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro, nenhuma linha alterada");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public void atualizar(Departamento vendedor) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE department\r\n" + "SET Name= ?\r\n" + "WHERE Id = ?");

			ps.setString(1, vendedor.getNome());
			ps.setInt(2, vendedor.getId());

			int linhasAlteradas = ps.executeUpdate();
			if (linhasAlteradas == 0) {
				throw new DbException("Nenhuma linha alterada");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public void deletarPorId(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("DELETE FROM department\r\n" + "WHERE Id = ?");

			ps.setInt(1, id);
			int linhasAlteradas = ps.executeUpdate();
			if (linhasAlteradas == 0) {
				throw new DbException("Nenhuma linha alterada");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public Departamento encontrarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT * from department WHERE Id = ?");

			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Departamento dep = new Departamento();
				dep.setNome(rs.getString("Name"));
				return dep;
			}

			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public List<Departamento> encontrarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement("SELECT * from department ORDER BY Name");
			
			rs = ps.executeQuery();
			
			List<Departamento> lista = new ArrayList<>();
			
			while(rs.next()) {
				Departamento dep = new Departamento(rs.getInt("Id"), rs.getString("Name"));
				lista.add(dep);
			}
			
			return lista;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

}
