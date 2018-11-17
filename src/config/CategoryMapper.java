package config;

import config.model.CategoryNameType;
import config.model.DomainType;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class CategoryMapper implements ConfigChangeListener {

	private static Logger logger = Logger.getLogger(CategoryMapper.class.toString());
	private static CategoryMapper instance;

	private static Object LOCK = new Object();
	public static CategoryMapper getInstance() {
		synchronized (LOCK) {
			if (instance == null) {
				instance = new CategoryMapper();
			}
		}
		return instance;
	}

	private HashMap<String, CategoryNameType> edumallCategoryMap;
	private HashMap<String, CategoryNameType> unicaCategoryMap;
	private HashMap<String, CategoryNameType> kholCategoryMap;

	private CategoryMapper() {
		edumallCategoryMap = new HashMap<>();
		unicaCategoryMap = new HashMap<>();
		kholCategoryMap = new HashMap<>();

	}


	public CategoryNameType mapEdumall(String edumallCategoryName) {

		if (edumallCategoryMap.containsKey(edumallCategoryName)) {
			return edumallCategoryMap.get(edumallCategoryName);
		}
		return CategoryNameType.OTHER;
	}

	public CategoryNameType mapUnica(String unicaCategoryName) {
		if (unicaCategoryMap.containsKey(unicaCategoryName)) {
			return unicaCategoryMap.get(unicaCategoryName);
		}
		return CategoryNameType.OTHER;
	}


	public CategoryNameType mapKhoaHocOnline(String khoaHocOnlineCategoryName) {
		if (kholCategoryMap.containsKey(khoaHocOnlineCategoryName)) {
			return kholCategoryMap.get(khoaHocOnlineCategoryName);
		}
		return CategoryNameType.OTHER;
	}





	@Override
	public void onConfigChange() {

		//update edumall
		edumallCategoryMap.clear();
		List<DomainType.CategoryMappingList.CategoryMapping> edumallMappingList = ConfigManager.getInstance().getConfigModel().getEdumall().getCategoryMappingList().getCategoryMapping();
		for (DomainType.CategoryMappingList.CategoryMapping categoryMapping : edumallMappingList) {
			edumallCategoryMap.put(categoryMapping.getSource(), categoryMapping.getTo());
		}

		//update unica
		unicaCategoryMap.clear();
		List<DomainType.CategoryMappingList.CategoryMapping> unicalMappingList = ConfigManager.getInstance().getConfigModel().getUnica().getCategoryMappingList().getCategoryMapping();
		for (DomainType.CategoryMappingList.CategoryMapping categoryMapping : unicalMappingList) {
			unicaCategoryMap.put(categoryMapping.getSource(), categoryMapping.getTo());
		}

		//update khol
		kholCategoryMap.clear();
		List<DomainType.CategoryMappingList.CategoryMapping> khoaHocOnlineMappingList = ConfigManager.getInstance().getConfigModel().getKhoaHocOnline().getCategoryMappingList().getCategoryMapping();
		for (DomainType.CategoryMappingList.CategoryMapping categoryMapping : khoaHocOnlineMappingList) {
			kholCategoryMap.put(categoryMapping.getSource(), categoryMapping.getTo());
		}

	}
}
