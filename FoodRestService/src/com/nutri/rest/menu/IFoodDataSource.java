package com.nutri.rest.menu;

import org.json.JSONObject;

public interface IFoodDataSource {
	
	public JSONObject GetAll();
	public JSONObject GetFoodCategories();
	
	//public JSONObject GetFoodSubcategories();
	
	public JSONObject GetFoodSubcategories(String category);
	
	//public JSONObject GetFoods();
	
	//public JSONObject GetFoods(String subcategory);
	
	public JSONObject GetFoods(String subcategory);
	
}
