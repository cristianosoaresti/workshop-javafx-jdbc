package model.services;

import java.util.List;

import model.dao.DepartmentDao;
import model.dao.FactoryDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao deptDao = FactoryDao.createDepartmentDao();
	
	public List<Department> findAll(){
		return deptDao.findAll();
	}
}
