package com.nutri.rest.menu;

import org.json.JSONObject;

public class Food {
	public Food(){}
	
	public Food(String food, int energy, double protein, double fats,
			double sugars, String gi, int cholesterol, int fiber) {
		super();
		this.food = food;
		this.energy = energy;
		this.protein = protein;
		this.fats = fats;
		this.sugars = sugars;
		this.gi = gi;
		this.cholesterol = cholesterol;
		this.fiber = fiber;
	}

	//@Column (name="Potravina - 100 g")
	  private String food;

	  //@Column (name="Energie")
	  private int energy;

	  //@Column (name="Bílk.")
	  private double protein; 

	  //@Column (name="Tuky")
	  private double fats; 
	  
	  //@Column (name="Sach.")
	  private double sugars; 
	  
	  // Glykemicky index
	  //@Column (name="GI")
	  private String gi; 
	  
	  //@Column (name="Cholest.")
	  private int cholesterol; 
	  
	  //@Column (name="Vláknina")
	  private int fiber; 
	  
	  public JSONObject toJSON() {
		  return new JSONObject( this );  
	  }
}
