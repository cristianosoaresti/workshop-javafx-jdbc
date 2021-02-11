package model.services;

import java.util.List;

import model.dao.SellerDao;
import model.dao.FactoryDao;
import model.entities.Seller;

public class SellerService {
	
	private SellerDao sellerDao = FactoryDao.createSellerDao();
	
	public List<Seller> findAll(){
		return sellerDao.findAll();
	}
	
	public void saveOrUpdate(Seller seller) {
		if (seller.getId() == null) {
			sellerDao.insert(seller);
		} else {
			sellerDao.update(seller);
		}
	}
	
	public void remove(Seller seller) {
		sellerDao.deleteById(seller.getId());
	}
}