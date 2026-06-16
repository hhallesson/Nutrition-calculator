package com.example.nutritioncalculator;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataHandler {

    private static final String SAVED_MEALS_FILE = "SavedMeals.json";
    private static final String CUSTOMIZATION_SETTINGS_FILE = "CustomizationSettings.json";

    public static Map<String, List<FoodItem>> loadFoodData(Context context) {
        try {
            InputStream is = context.getAssets().open("FoodData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<FoodItem>>>() {}.getType();

            Map<String, List<FoodItem>> data = gson.fromJson(json, type);
            return data != null ? data : Collections.emptyMap();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public static Map<String, List<MealItem>> loadSavedMeals(Context context) {
        try {
            FileInputStream fis = context.openFileInput(SAVED_MEALS_FILE);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<MealItem>>>() {}.getType();

            Map<String, List<MealItem>> savedMeals = gson.fromJson(json, type);
            return savedMeals != null ? savedMeals : new LinkedHashMap<>();

        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    public static boolean saveMeal(Context context, String mealName, List<MealItem> mealItems) {
        try {
            Map<String, List<MealItem>> savedMeals = loadSavedMeals(context);

            savedMeals.put(mealName, new ArrayList<>(mealItems));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(savedMeals);

            FileOutputStream fos = context.openFileOutput(SAVED_MEALS_FILE, Context.MODE_PRIVATE);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean mealNameExists(Context context, String mealName) {
        Map<String, List<MealItem>> savedMeals = loadSavedMeals(context);
        return savedMeals.containsKey(mealName);
    }

    public static boolean overwriteMeal(Context context, String mealName, List<MealItem> mealItems) {
        try {
            Map<String, List<MealItem>> savedMeals = loadSavedMeals(context);

            savedMeals.put(mealName, new ArrayList<>(mealItems));

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(savedMeals);

            FileOutputStream fos = context.openFileOutput(SAVED_MEALS_FILE, Context.MODE_PRIVATE);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean clearSavedMeals(Context context) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String emptyJson = gson.toJson(new LinkedHashMap<String, List<MealItem>>());

            FileOutputStream fos = context.openFileOutput(SAVED_MEALS_FILE, Context.MODE_PRIVATE);
            fos.write(emptyJson.getBytes(StandardCharsets.UTF_8));
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteSavedMeal(Context context, String mealName) {
        try {
            Map<String, List<MealItem>> savedMeals = loadSavedMeals(context);

            if (!savedMeals.containsKey(mealName)) {
                return false;
            }

            savedMeals.remove(mealName);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(savedMeals);

            FileOutputStream fos = context.openFileOutput(SAVED_MEALS_FILE, Context.MODE_PRIVATE);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static CustomizationSettings loadCustomizationSettings(Context context) {
        try {
            FileInputStream fis = context.openFileInput(CUSTOMIZATION_SETTINGS_FILE);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            CustomizationSettings settings = gson.fromJson(json, CustomizationSettings.class);
            return settings != null ? settings : CustomizationSettings.defaultSettings();

        } catch (Exception e) {
            return CustomizationSettings.defaultSettings();
        }
    }

    public static boolean saveCustomizationSettings(Context context, CustomizationSettings settings) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(settings != null ? settings : CustomizationSettings.defaultSettings());

            FileOutputStream fos = context.openFileOutput(CUSTOMIZATION_SETTINGS_FILE, Context.MODE_PRIVATE);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean clearCustomizationSettings(Context context) {
        if (!context.getFileStreamPath(CUSTOMIZATION_SETTINGS_FILE).exists()) {
            return true;
        }

        if (context.deleteFile(CUSTOMIZATION_SETTINGS_FILE)) {
            return true;
        }

        return saveCustomizationSettings(context, CustomizationSettings.defaultSettings());
    }
}
