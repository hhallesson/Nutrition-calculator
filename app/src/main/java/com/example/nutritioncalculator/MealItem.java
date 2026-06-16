package com.example.nutritioncalculator;

public class MealItem {
    private int amount;
    private String unit;
    private String name;

    private double calories;
    private double protein;
    private double carbs;
    private double fat;

    public MealItem(int amount, String unit, String name,
                    double calories, double protein, double carbs, double fat) {
        this.amount = amount;
        this.unit = unit;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    public int getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }

    public String getDisplayText() {
        return amount + unit + " av " + name;
    }
}