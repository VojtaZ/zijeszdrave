package com.nutri.rest.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.json.impl.JSONHelper;

 
public class ExcelDataSource implements IFoodDataSource {

	//final static String DATA_SOURCE_FILE = "C:\\Users\\zbranek\\Workspace\\trunk_new\\cst\\Trainings\\ClientsAndServices\\FoodRestService\\resources\\Kalorické tabulky a GI.xlsx";
	//final static String DATA_SOURCE_FILE  = "resources/kt.xlsx";
	final static String DATA_SOURCE_FILE  = "https://dl.dropboxusercontent.com/u/52348831/kt.xlsx";
	final static String FOOD_NAME = "food";
	final static String ENERGY = "energy";
	final static String PROTEIN = "protein";
	final static String FAT = "fat";
	final static String SACHARIDS = "sacharids";
	final static String G_INDEX = "gi";
	final static String CHOLESTEROL = "cholesterol";
	final static String FIBER = "fiber";
	final static String SUBCATEGORY = "subcategory";
	final static String CATEGORY = "category";
	
	
	final static String CATEGORY_PREFIX = "Kategorie:";
		
	//static HashMap<String, HashMap<String, JSONObject>> subcategoriesAndFoods = new HashMap<String, HashMap<String, JSONObject>>();
	
	static HashMap<String, List<String>> categoriesToSubcategories = new HashMap<String, List<String>>();
	static HashMap<String, List<String>> subcategoriesToFoods = new HashMap<String, List<String>>();
	static HashMap<String, JSONObject> foodNameToJson = new HashMap<String, JSONObject>();
	
	
	// TODO relative path
	
	boolean updated;
	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		//testCestiny();
		//testJson();
		//read();
		initializeDataFromExcel();
		System.out.println();
		System.out.println("subcategoriesToFoods:" + subcategoriesToFoods);
		for (String subcategory : subcategoriesToFoods.keySet()) {
			System.out.println( "Subcategory:" + subcategory + ":" + subcategoriesToFoods.get(subcategory));
		}
	}	

	/*
	public static void read() {
		try
        {
            FileInputStream file = new FileInputStream(new File(ExcelFileSource));
            
            //Create Workbook instance holding reference to .xlsx file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
 
            //Get first/desired sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();
                short lastCellNum = row.getLastCellNum();
                System.out.println("Last cell num: " + lastCellNum + "|" + row.getPhysicalNumberOfCells() );
                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();
                    //Check the cell type and format accordingly
                    switch (cell.getCellType())
                    {
                        case Cell.CELL_TYPE_NUMERIC:
                        	System.out.print(cell.getNumericCellValue() + "|");
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	System.out.print(cell.getStringCellValue() + "|");
                            break;
                    }
                }
                System.out.println();
            }
            file.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	*/
	
	public static void initializeDataFromExcel() throws InvalidFormatException, IOException {
		System.out.println("[" + new Date() + "] - REALLY READING DATA SOURCE!");
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		//InputStream inp = new FileInputStream(DATA_SOURCE_FILE);
		InputStream inp = new URL(DATA_SOURCE_FILE).openStream();
		
		
	    Workbook wb = WorkbookFactory.create(inp);
	    Sheet sheet = wb.getSheetAt(0);
	    int foodIndex = 0;
	    String category = null;
	    String subcategory = null;
	    
	    //Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
        
            Row row = rowIterator.next();
            short lastCellNum = row.getLastCellNum();
            //System.out.println("cell count: " + lastCellNum);
            
            if (lastCellNum == 8) {
            	Cell ndColumn = row.getCell(1);
            	String ndColumnStringValue = ndColumn.toString();
            	if (ndColumnStringValue == null || ndColumnStringValue.isEmpty()) {
            		String firstColumn = row.getCell(0).getStringCellValue();
            		if (firstColumn.startsWith(CATEGORY_PREFIX)) {
            			category = firstColumn.substring(CATEGORY_PREFIX.length()+1);
            			//System.out.println("Category: " + category);
            		} else {
            			subcategory = firstColumn;
            			//System.out.println("Subcategory: " + subcategory);
            			List<String> subcategories = categoriesToSubcategories.get(category);
            			if (subcategories == null) {
            				subcategories = new ArrayList<String>();
            				categoriesToSubcategories.put(category, subcategories);
            			}
            			subcategories.add(subcategory);
            		}
            	} else {
            		switch (ndColumn.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                        	List<String> foods = subcategoriesToFoods.get(subcategory);
                        	if (foods == null) {
                        		foods = new ArrayList<String>();
                        		subcategoriesToFoods.put(subcategory, foods);
                        	}
                        	JSONObject foodJson = readFoodJSONFromRow(row, subcategory, category);
                        	String foodName = foodJson.get(FOOD_NAME).toString();
                        	foods.add(foodName);
                        	foodNameToJson.put(foodName, foodJson);
                        	// System.out.println(foodJson);
                        	foodIndex++;
                            break;
                        case Cell.CELL_TYPE_STRING:
                        	// not correct food 
                            break;
                    }
            	}
            }
        }
        
        System.out.println("Categories size: " + categoriesToSubcategories.size());
        System.out.println("Subcategories size: " + subcategoriesToFoods.size());
        System.out.println("Food size: " + foodNameToJson.size());
	}

	private static JSONObject readFoodJSONFromRow(Row row, String subcategory, String category) {
		JSONObject jo = new JSONObject();
		jo.put(FOOD_NAME, row.getCell(0));
		jo.put(ENERGY, row.getCell(1));
		jo.put(PROTEIN, row.getCell(2));
		jo.put(FAT, row.getCell(3));
		jo.put(SACHARIDS, row.getCell(4));
		jo.put(G_INDEX, row.getCell(5));
		jo.put(CHOLESTEROL, row.getCell(6));
		jo.put(FIBER, row.getCell(7));
		jo.put(SUBCATEGORY, subcategory);
		jo.put(CATEGORY, category);
		return jo;
	}
	
	private void checkIfUpdated() {
		if (!updated) {
			System.out.println("NOT UPDATED, RELOADING DATA SOURCE!");
			try {
				initializeDataFromExcel();
				updated = true;
				System.out.println("UPDATED");
			} catch (InvalidFormatException | IOException e) {
				// TODO
				JSONObject jsonErrorMessage = new JSONObject();
				jsonErrorMessage.put("errorMessage", e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@Override
	public JSONObject GetFoodCategories() {
		checkIfUpdated();
		JSONObject categoriesJson = new JSONObject();
		JSONArray jsonArray = new JSONArray(categoriesToSubcategories.keySet().toArray());
		categoriesJson.put("categories", jsonArray);
		return categoriesJson;
	}

	@Override
	public JSONObject GetFoodSubcategories(String category) {
		checkIfUpdated();
		JSONObject subcategoriesJson = new JSONObject();
		if (category == null) {
			JSONArray jsonArray = new JSONArray(subcategoriesToFoods.keySet().toArray());
			subcategoriesJson.put("subcategories", jsonArray);
		} else {
			JSONArray jsonArray = new JSONArray(categoriesToSubcategories.get(category).toArray());
			subcategoriesJson.put("subcategories", jsonArray);
		}
		return subcategoriesJson;
	}

	@Override
	public JSONObject GetFoods(String subcategory) {
		checkIfUpdated();
		
		JSONObject root = new JSONObject();
		
		/*if (subcategory == null) {
			List<JSONObject> newArrayList = new ArrayList<JSONObject>();
			for (List<JSONObject> value : subcategoriesAndFoods.values()) {
				newArrayList.addAll(value);
			}
			JSONArray jsonArray = new JSONArray(newArrayList.toArray());
			foodsJson.put("foods", jsonArray);
		} else {
			JSONArray jsonArray = new JSONArray(subcategoriesAndFoods.get(subcategory).toArray());
			foodsJson.put("foods", jsonArray);
		}*/
		
		if (subcategory == null) {
			// TEST
			root.put("foods", foodNameToJson);
		} else {
			//TODO
		}
		
		
		return root;
	}

	/*
	@Override
	public JSONObject GetFoods(String subcategory) {
		checkIfUpdated();
		JSONObject foodsJson = new JSONObject();
		if (subcategory == null) {
			for (List<JSONObject> subcategoryFoods : subcategoriesAndFoods.values()) {
				for (int i = 0; i < subcategoryFoods.size(); i++) {
					JSONObject foodJson = subcategoryFoods.get(i);
					System.out.println( "food: " + foodJson);
					System.out.println( "food JSON: " + foodJson.get("food"));
					foodsJson.put(foodJson.get("food").toString(), foodJson);
				}
			}
			foodsJson.put("foods", foodsJson);
		} else {
			JSONArray jsonArray = new JSONArray(subcategoriesAndFoods.get(subcategory).toArray());
			foodsJson.put("foods", jsonArray);
		}
		return foodsJson;
	}
	*/
	
	@Override
	public JSONObject GetAll() {
		checkIfUpdated();
		JSONObject allJson = GetFoods(null);
		allJson.put("subcategories", subcategoriesToFoods);
		allJson.put("categories", categoriesToSubcategories);
		return allJson;
	}
	
/*	@Override
	public JSONObject GetAll() {
		checkIfUpdated();

		JSONObject allJson = GetFoods(null);
		JSONArray jsonArray = new JSONArray(subcategoriesAndFoods.keySet().toArray());
		allJson.put("subcategories", jsonArray);
		jsonArray = new JSONArray(categoriesAndSubcategories.keySet().toArray());
		allJson.put("categories", jsonArray);
		return allJson;
	}*/
}
