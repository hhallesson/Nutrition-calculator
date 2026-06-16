package com.example.nutritioncalculator;

public class FoodItem {
    String name, unit;
    int calories;
    double protein, carbs, fat;
    public FoodItem (String name, String unit, int calories, double protein, double carbs, double fat){
        this.name = name;
        this.unit = unit;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    public String getName(){
        return name;
    }
    public String getUnit(){
        return unit;
    }
    public int getCalories(){
        return calories;
    }
    public double getProtein(){
        return protein;
    }
    public double getCarbs(){
        return carbs;
    }
    public double getFat(){
        return fat;
    }
}
