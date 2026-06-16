package com.example.nutritioncalculator;

import java.util.ArrayList;
import java.util.List;

public class MealPlanner {
    double totalCalories, totalProtein, totalCarbs, totalFat;
    private List<MealItem> mealItems;
    String loadedMealName;

    public MealPlanner() {
        totalCalories = 0;
        totalProtein = 0.0;
        totalCarbs = 0.0;
        totalFat = 0.0;
        loadedMealName = "Måltid";
        mealItems = new ArrayList<>();
    }

    public void addTotal(double calories, double protein, double carbs, double fat) {
        totalCalories += calories;
        totalProtein += protein;
        totalCarbs += carbs;
        totalFat += fat;
    }

    public void addMealItem(MealItem item) {
        mealItems.add(item);
    }

    public void removeMealItem(MealItem item) {
        if (mealItems.remove(item)) {
            totalCalories -= item.getCalories();
            totalProtein -= item.getProtein();
            totalCarbs -= item.getCarbs();
            totalFat -= item.getFat();

            if (totalCalories < 0) totalCalories = 0;
            if (totalProtein < 0) totalProtein = 0;
            if (totalCarbs < 0) totalCarbs = 0;
            if (totalFat < 0) totalFat = 0;
        }
    }

    public List<MealItem> getMealItems() {
        return mealItems;
    }

    public List<MealItem> getMealItemsCopy() {
        return new ArrayList<>(mealItems);
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void resetMealPlanner() {
        totalCalories = 0;
        totalProtein = 0;
        totalCarbs = 0;
        totalFat = 0;
        mealItems.clear();
        loadedMealName = "Måltid";
    }

    public boolean isMealEmpty() {
        return totalCalories == 0 && totalProtein == 0 && totalCarbs == 0 && totalFat == 0;
    }

    public void loadMeal(List<MealItem> savedMealItems, String name) {
        resetMealPlanner();

        loadedMealName = name;

        for (MealItem item : savedMealItems) {
            mealItems.add(item);
            totalCalories += item.getCalories();
            totalProtein += item.getProtein();
            totalCarbs += item.getCarbs();
            totalFat += item.getFat();
        }
    }
}