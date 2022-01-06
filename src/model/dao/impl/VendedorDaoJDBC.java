package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.VendedorDao;
import model.entidades.Departamento;
import model.entidades.Vendedor;

public class VendedorDaoJDBC implements VendedorDao {

	private Connection conn;

	public VendedorDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void inserir(Vendedor vendedor) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("INSERT INTO seller\r\n"
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId)\r\n"
					+ "VALUES\r\n"
					+ "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, vendedor.getNome());
			ps.setString(2, vendedor.getEmail());
			ps.setDate(3, new java.sql.Date(vendedor.getDataNascimento().getTime()));
			ps.setDouble(4, vendedor.getSalarioBase());
			ps.setInt(5, vendedor.getDepartamento().getId());
			
			int linhasAlteradas = ps.executeUpdate();
			
			if (linhasAlteradas > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					vendedor.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Erro, nenhuma linha alterada");
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
			
	}

	@Override
	public void atualizar(Vendedor vendedor) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("UPDATE seller\r\n"
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?\r\n"
					+ "WHERE Id = ?");
			
			ps.setString(1, vendedor.getNome());
			ps.setString(2, vendedor.getEmail());
			ps.setDate(3, new java.sql.Date(vendedor.getDataNascimento().getTime()));
			ps.setDouble(4, vendedor.getSalarioBase());
			ps.setInt(5, vendedor.getDepartamento().getId());
			ps.setInt(6, vendedor.getId());
			
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
			ps = conn.prepareStatement("DELETE FROM seller\r\n"
					+ "WHERE Id = ?");
			
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
	public Vendedor encontrarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName\r\n" + "FROM seller INNER JOIN department\r\n"
							+ "ON seller.DepartmentId = department.Id\r\n" + "WHERE seller.Id = ?");

			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Departamento dep = InstanciaDepartamento(rs);
				Vendedor vendedor = InstanciaVendedor(rs, dep);
				return vendedor;
			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}

	}

	private Vendedor InstanciaVendedor(ResultSet rs, Departamento dep) throws SQLException {
		Vendedor vendedor = new Vendedor();
		vendedor.setId(rs.getInt("Id"));
		vendedor.setNome(rs.getString("Name"));
		vendedor.setEmail(rs.getString("Email"));
		vendedor.setSalarioBase(rs.getDouble("BaseSalary"));
		vendedor.setDataNascimento(rs.getDate("BirthDate"));
		vendedor.setDepartamento(dep);
		return vendedor;
	}

	private Departamento InstanciaDepartamento(ResultSet rs) throws SQLException {
		Departamento dep = new Departamento();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setNome(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Vendedor> encontrarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName\r\n"
					+ "FROM seller INNER JOIN department\r\n" + "ON seller.DepartmentId = department.Id\r\n"
					+ "ORDER BY Name");
			
			rs = ps.executeQuery();
			
			List<Vendedor> lista = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				Departamento depp = map.get(rs.getInt("DepartmentId"));
				if (depp == null) {
					depp = InstanciaDepartamento(rs);
					map.put(rs.getInt("DepartmentId"), depp);
				}
				Vendedor vendedor = InstanciaVendedor(rs, depp);
				lista.add(vendedor);
			}
			
			return lista;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}

	}

	@Override
	public List<Vendedor> encontrarPorDepartamento(Departamento dep) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("SELECT seller.*,department.Name as DepName\r\n"
					+ "FROM seller INNER JOIN department\r\n" + "ON seller.DepartmentId = department.Id\r\n"
					+ "WHERE DepartmentId = ?\r\n" + "ORDER BY Name");
			
			ps.setInt(1, dep.getId());
			rs = ps.executeQuery();
			
			List<Vendedor> lista = new ArrayList<>();
			Map<Integer, Departamento> map = new HashMap<>();
			
			while (rs.next()) {
				Departamento depp = map.get(rs.getInt("DepartmentId"));
				if (depp == null) {
					depp = InstanciaDepartamento(rs);
					map.put(rs.getInt("DepartmentId"), depp);
				}
				Vendedor vendedor = InstanciaVendedor(rs, depp);
				lista.add(vendedor);
			}
			
			return lista;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}

	}

}
