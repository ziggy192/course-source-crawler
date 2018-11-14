package dao;

import entity.DomainEntity;
import util.DBUtils;

import javax.persistence.EntityManager;
import java.util.List;

public class DomainDAO extends BaseDAO<DomainEntity, Integer> {
	private static DomainDAO instance;
	private static Object LOCK = new Object();
	public DomainDAO() {

	}

	public static DomainDAO getInstance() {
		synchronized (LOCK) {
			if (instance == null) {
				instance = new DomainDAO();
			}
		}
		return instance;
	}
	public DomainEntity getDomainByName(String domainName) {
		EntityManager entityManager = DBUtils.getEntityManager();
		List<DomainEntity> resultList = entityManager.createNamedQuery("Domain.getDomainByDomainName")
				.setParameter("domainName", domainName)
				.getResultList();
		if (resultList.isEmpty()) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

	public List<DomainEntity> getAllDomain() {
		EntityManager entityManager = DBUtils.getEntityManager();
		List resultList = entityManager.createNamedQuery("Domain.getAllDomain").getResultList();
		return resultList;g
	}



}
