package com.example.nutritioncalculator;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class CustomizationHandler {

    private final CustomizationSettings settings;

    public CustomizationHandler(CustomizationSettings settings) {
        this.settings = settings != null ? settings : CustomizationSettings.defaultSettings();
    }

    public static CustomizationHandler fromContext(Context context) {
        return new CustomizationHandler(DataHandler.loadCustomizationSettings(context));
    }

    public CustomizationSettings getSettings() {
        return settings;
    }

    public AppThemeOption getThemeOption() {
        return AppStyler.getTheme(settings);
    }

    public boolean shouldShowLinkFab() {
        return settings.isShowLinkFab();
    }

    public boolean shouldShowHelpFab() {
        return settings.isShowHelpFab();
    }

    public boolean shouldShowSavedMealsFab() {
        return settings.isShowSavedMealsFab();
    }

    public boolean shouldShowExpandableList() {
        return CustomizationSettings.LIST_MODE_EXPANDABLE.equals(settings.getMainListMode())
                || CustomizationSettings.LIST_MODE_BOTH.equals(settings.getMainListMode());
    }

    public boolean shouldShowSearchList() {
        return CustomizationSettings.LIST_MODE_SEARCH.equals(settings.getMainListMode())
                || CustomizationSettings.LIST_MODE_BOTH.equals(settings.getMainListMode());
    }

    public boolean shouldShowSearchInput() {
        return shouldShowSearchList();
    }

    public boolean shouldShowMealPlannerCalories() {
        return settings.isShowMealPlannerCalories();
    }

    public boolean shouldShowMealPlannerProtein() {
        return settings.isShowMealPlannerProtein();
    }

    public boolean shouldShowMealPlannerCarbs() {
        return settings.isShowMealPlannerCarbs();
    }

    public boolean shouldShowMealPlannerFat() {
        return settings.isShowMealPlannerFat();
    }

    public boolean shouldShowMealPlannerUnit() {
        return settings.isShowMealPlannerUnit();
    }

    public boolean shouldShowMealPlannerDetails() {
        return settings.isShowMealPlannerDetails();
    }

    public boolean shouldShowHelpCalories() {
        return settings.isShowHelpCalories();
    }

    public boolean shouldShowHelpProtein() {
        return settings.isShowHelpProtein();
    }

    public boolean shouldShowHelpCarbs() {
        return settings.isShowHelpCarbs();
    }

    public boolean shouldShowHelpFat() {
        return settings.isShowHelpFat();
    }

    public boolean shouldShowHelpMen() {
        return CustomizationSettings.GENDER_MODE_MAN.equals(settings.getHelpGenderMode())
                || CustomizationSettings.GENDER_MODE_BOTH.equals(settings.getHelpGenderMode());
    }

    public boolean shouldShowHelpWomen() {
        return CustomizationSettings.GENDER_MODE_WOMAN.equals(settings.getHelpGenderMode())
                || CustomizationSettings.GENDER_MODE_BOTH.equals(settings.getHelpGenderMode());
    }

    public String formatCalories(Context context, double value) {
        int roundedValue = (int) Math.round(value);
        if (shouldShowMealPlannerUnit()) {
            return context.getString(R.string.detail_calories, roundedValue);
        }
        return context.getString(R.string.detail_calories_no_unit, roundedValue);
    }

    public String formatMacro(Context context, double value) {
        if (shouldShowMealPlannerUnit()) {
            return context.getString(R.string.detail_protein, value);
        }
        return context.getString(R.string.detail_macro_no_unit, value);
    }

    public String formatMealItemText(MealItem item) {
        if (shouldShowMealPlannerUnit()) {
            return item.getAmount() + item.getUnit() + " av " + item.getName();
        }
        return item.getAmount() + " av " + item.getName();
    }

    public String formatFoodListDetails(FoodItem item) {
        List<String> parts = new ArrayList<>();

        if (shouldShowMealPlannerCalories()) {
            parts.add("Kalorier: " + item.getCalories());
        }
        if (shouldShowMealPlannerCarbs()) {
            parts.add("Kolhydrater: " + item.getCarbs() + "g");
        }
        if (shouldShowMealPlannerProtein()) {
            parts.add("Protein: " + item.getProtein() + "g");
        }
        if (shouldShowMealPlannerFat()) {
            parts.add("Fett: " + item.getFat() + "g");
        }

        return String.join(" | ", parts);
    }
}
