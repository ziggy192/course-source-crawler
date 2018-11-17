package dao;

import entity.CategoryEntity;
import config.model.CategoryNameType;
import util.DBUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class CategoryDAO extends BaseDAO<CategoryEntity, Integer> {

	private static CategoryDAO instance;
	private static Object LOCK = new Object();
	public CategoryDAO() {

	}


	public  int getCategoryId(CategoryNameType categoryNameType) {

		String categoryName = categoryNameType.getValue();
		CategoryEntity category = getCategoryByName(categoryName);
		return category.getId();

	}

	public void insertCategoryByNameIfNotExist(String name) {
		CategoryEntity entity = getCategoryByName(name);
		if (entity == null) {
			//insert
			entity = new CategoryEntity();
			entity.setName(name);
			persist(entity);
		}
	}

	public CategoryEntity getCategoryByName(String name) {

		List<CategoryEntity> resultList = DBUtils.getEntityManager().createNamedQuery("CategoryEntity.getCategoryByName")
				.setParameter("categoryName", name)
				.getResultList();
		if (resultList.isEmpty()) {
			return null;
		} else {
			return resultList.get(0);
		}

	}


	public List<CategoryEntity> getCategoryList() {
		EntityManager entityManager = DBUtils.getEntityManager();
		Query namedQuery = entityManager.createNamedQuery("CategoryEntity.getCategoryList");

		return namedQuery.getResultList();


	}

	public static CategoryDAO getInstance() {
		synchronized (LOCK) {
			if (instance == null) {
				instance = new CategoryDAO();
			}
		}
		return instance;
	}

}
