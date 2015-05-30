package com.nutri.rest.menu;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;

@Path("/menu")
public class MenuRestService {

	static IFoodDataSource FoodDataSource = new ExcelDataSource();
	
	@Path("/all")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response GetAll() {
		System.out.println("REST API: GetAll()" + FoodDataSource.GetAll().toString());
		return Response.status(200).entity(FoodDataSource.GetAll().toString()).build();
	}
	
	//menu/categories
	@Path("/categories")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response GetFoodCategories() throws JSONException {
		System.out.println("REST API: GetFoodCategories(): " + FoodDataSource.GetFoodCategories().toString());
		return Response.status(200).entity(FoodDataSource.GetFoodCategories().toString()).build();
	}
	
	@Path("/subcategories")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response GetFoodSubcategories() {
		System.out.println("REST API: GetFoodSubcategories(): " + FoodDataSource.GetFoodSubcategories(null).toString());
		return Response.status(200).entity("GetFoodSubcategories: " + FoodDataSource.GetFoodSubcategories(null).toString()).build();	
	}
	
	@Path("/foods")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response GetFoods() {
		System.out.println("GetFoods:" + FoodDataSource.GetFoods(null).toString());
		return Response.status(200).entity(FoodDataSource.GetFoods(null).toString()).build();
	}
}
