package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.common.utils.CommonUtils;
import com.controller.Keys;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.model.bean.Categories;
import com.model.bean.ProductInfo;

public class ProductRanking {

	private static ProductRanking ranking;
	private ProductIndexComparer comparer = new ProductIndexComparer();

	private ProductRanking() {
	}

	public static ProductRanking getInstance() {
		if (ranking == null) {
			synchronized (ProductRanking.class) {
				if (ranking == null) {
					ranking = new ProductRanking();
					FileChangeListener.getInstance();
				}
			}
		}
		return ranking;
	}

	private static Map<String, List<ProductInfo>> categoryToProductMap = new HashMap<String, List<ProductInfo>>();

	public List<ProductInfo> getList(String key) {
		List<ProductInfo> pr = categoryToProductMap.get(key);
		return pr;
	}
	
	public List<String> getAllCategories() {
		List<String> cats = new ArrayList<String>(categoryToProductMap.keySet());
		return cats;
	}

	private static ProductInfo getProductgetProduct(String skuId) {
		String url = String.format(Keys.PRODUCT_URL, skuId);
		String output = CommonUtils.fetchJsonData(url);
		Gson gson = new Gson();
		ProductInfo info = gson.fromJson(output, ProductInfo.class);
		return info;
	}

	public void loadAllProducts() {
		ArrayList<String> categories = FileChangeListener.getInstance().getCategories();
		String parentCategory;
		for (String category : categories) {
			String url = String.format(Keys.CATEGORY_URL, category);
			String data;
			data = CommonUtils.fetchJsonData(url);
			System.out.println(data);
			System.out.println(url);
			Gson gson = new Gson();
			Categories catogries = gson.fromJson(data, Categories.class);
			parentCategory = catogries.getName();
			ArrayList<ProductInfo> productList = new ArrayList<ProductInfo>();
			for (Object cat : catogries.getCategories()) {
				String cat_string = (String) ((LinkedTreeMap) cat).get("type");
				String id = (String) ((LinkedTreeMap) cat).get("id");
				if (cat_string.equals("Category")) {
					url = String.format(Keys.CATEGORY_URL, id);
					data = CommonUtils.fetchJsonData(url);
					try {
						JsonObject json = new JsonObject();
						catogries = gson.fromJson(data, Categories.class);
						ArrayList prods = (ArrayList) ((catogries.getProducts()));
						Double sku_d = (Double) ((LinkedTreeMap) prods.get(0)).get("sku");
						String sku = sku_d.toString().replace(".0", "");
						ProductInfo product = getProductgetProduct(sku);
						product.loadOtherData();
						productList.add(product);
						categoryToProductMap.put(parentCategory, productList);
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
			java.util.Collections.sort(productList, comparer);
			System.out.println(categoryToProductMap);
		}
	}
}