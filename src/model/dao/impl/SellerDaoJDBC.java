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
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	private Connection conn = null;

	@Override
	public void insert(Seller seller) {
		
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(
					"INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) " + 
					"VALUES	(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getEmail());
			ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());
			
			int affectedRows = ps.executeUpdate();
			
			if(affectedRows > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					seller.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Error during the insert command! No rows effected!");
			}
			
		} catch (SQLException e) {
			throw new DbException("Error during the insert command SQLState=" + e.getSQLState() + "\n Message error: " + e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void update(Seller seller) {

		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(
					"UPDATE seller " +
					"SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " +
					"WHERE Id = ? ");
			
			ps.setString(1, seller.getName());
			ps.setString(2, seller.getEmail());
			ps.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			ps.setDouble(4, seller.getBaseSalary());
			ps.setInt(5, seller.getDepartment().getId());
			ps.setInt(6, seller.getId());
			
			int affectedRows = ps.executeUpdate();
			
			if (affectedRows == 0) {
				throw new DbException("Update not executed! The ID does not exists!");
			}
			
		} catch (SQLException e) {
			throw new DbException("Error during the update command SQLState=" + e.getSQLState() + "\n Message error: " + e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void deleteById(Integer id) {

		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(
					"DELETE FROM seller " +
					"WHERE Id = ? ");
			
			ps.setInt(1, id);
			
			int affectedRows = ps.executeUpdate();
			
			if (affectedRows == 0) {
				throw new DbException("Delete not executed! The ID does not exists!");
			}
		} catch (SQLException e) {
			throw new DbException("Error during the delete command SQLState=" + e.getSQLState() + "\n Message error: " + e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public Seller findById(Integer id) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");

			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Department department = instantiateDepartment(rs);
				Seller seller = intantiateSeller(rs, department);

				return seller;
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findAll() {
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id ORDER BY Id");

			rs = ps.executeQuery();

			// List with seller to return it.
			List<Seller> sellerList = new ArrayList<>();
			// A map to add the departments and avoid duplicated instances
			Map<Integer, Department> mapList = new HashMap<>();

			while (rs.next()) {

				// If the department already exits inside the mapList, the function
				// "mapList.get" is going to returns null
				// because the HashMap does not allows duplicate elements.
				Department dep = mapList.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					mapList.put(rs.getInt("DepartmentId"), dep);
				}

				Seller seller = intantiateSeller(rs, dep);
				sellerList.add(seller);
			}
			return sellerList;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findAllByDepartment(Department department) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name ");

			ps.setInt(1, department.getId());
			rs = ps.executeQuery();

			// List with seller to return it.
			List<Seller> sellerList = new ArrayList<>();
			// A map to add the departments and avoid duplicated instances
			Map<Integer, Department> mapList = new HashMap<>();

			while (rs.next()) {

				// If the department already exits inside the mapList, the function
				// "mapList.get" is going to returns null
				// because the HashMap does not allows duplicate elements.
				Department dep = mapList.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					mapList.put(rs.getInt("DepartmentId"), dep);
				}

				Seller seller = intantiateSeller(rs, dep);
				sellerList.add(seller);
			}
			return sellerList;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}
	
	private Seller intantiateSeller(ResultSet rs, Department department) throws SQLException {
		Seller seller = new Seller(
				rs.getInt("Id"), 
				rs.getString("Name"), 
				rs.getString("Email"),
				rs.getDate("BirthDate"), 
				rs.getDouble("BaseSalary"), 
				department);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department(
				rs.getInt("DepartmentId"), 
				rs.getString("DepName"));
		return dep;
	}
}