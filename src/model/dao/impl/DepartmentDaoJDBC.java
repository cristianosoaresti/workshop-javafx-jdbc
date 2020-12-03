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
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	private Connection conn = null;
	
	@Override
	public void insert(Department department) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					  "INSERT INTO department "
					+ "(Name) "
					+ "VALUES "
					+ "(?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, department.getName());
			int result = ps.executeUpdate();
			
			if (result > 0) {
				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					department.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Error during the inserted.");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void update(Department department) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					  "UPDATE department "
					+ "SET Name = ?"
					+ "WHERE Id = ?");
			
			ps.setString(1, department.getName());
			ps.setInt(2, department.getId());
			
			int result = ps.executeUpdate();
			
			if (result > 0) {
				System.out.println("Department updated! ");
			} else {
				throw new DbException("Error during the inserted.");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(
					  "DELETE FROM department "
					+ "WHERE Id = ?");
			
			ps.setInt(1, id);
			
			int result = ps.executeUpdate();
			
			if (result > 0) {
				System.out.println("Department deleted! ");
			} else {
				throw new DbException("Error during the inserted.");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(
					"SELECT * FROM department WHERE Id = ?");
			
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				Department department = instantiateDepartment(rs);
				return department;
			}
			
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement("SELECT * FROM department");
			rs = ps.executeQuery();
			List<Department> departmentList = new ArrayList<Department>();
			
			while (rs.next()) {
				departmentList.add(instantiateDepartment(rs));
			}
			
			return departmentList;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department(
				rs.getInt("Id"), 
				rs.getString("Name"));
		return dep;
	}
}