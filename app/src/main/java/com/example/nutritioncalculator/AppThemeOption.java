package com.example.nutritioncalculator;

import android.graphics.Color;

import java.util.Arrays;
import java.util.List;

public class AppThemeOption {

    private static final List<AppThemeOption> OPTIONS = Arrays.asList(
            new AppThemeOption(
                    CustomizationSettings.THEME_STANDARD,
                    "Standard",
                    Color.parseColor("#F4F8FD"),
                    Color.parseColor("#FFFFFFFF"),
                    Color.parseColor("#5B8CCB"),
                    Color.parseColor("#A9C7EA"),
                    Color.parseColor("#1F3654"),
                    Color.parseColor("#617792"),
                    Color.parseColor("#E6F0FA")
            ),
            new AppThemeOption(
                    CustomizationSettings.THEME_SUNRISE,
                    "Sunrise",
                    Color.parseColor("#FFF8F0"),
                    Color.parseColor("#FFFFFFFF"),
                    Color.parseColor("#D97706"),
                    Color.parseColor("#F59E0B"),
                    Color.parseColor("#1F2937"),
                    Color.parseColor("#6B7280"),
                    Color.parseColor("#FFF3E0")
            ),
            new AppThemeOption(
                    CustomizationSettings.THEME_DARK,
                    "Dark Mode",
                    Color.parseColor("#121826"),
                    Color.parseColor("#1E293B"),
                    Color.parseColor("#38BDF8"),
                    Color.parseColor("#0EA5E9"),
                    Color.parseColor("#F8FAFC"),
                    Color.parseColor("#CBD5E1"),
                    Color.parseColor("#0F172A")
            ),
            new AppThemeOption(
                    CustomizationSettings.THEME_SOFT_PINK,
                    "Soft Pink",
                    Color.parseColor("#FFF7FB"),
                    Color.parseColor("#FFFFFFFF"),
                    Color.parseColor("#FC94C8"), //#FC94C8 #EC4899
                    Color.parseColor("#F9A8D4"),
                    Color.parseColor("#4A1D35"),
                    Color.parseColor("#9D5C7A"),
                    Color.parseColor("#FCE7F3")
            ),
            new AppThemeOption(
                    CustomizationSettings.THEME_SOFT_GREEN,
                    "Soft Green",
                    Color.parseColor("#F5FBF5"),
                    Color.parseColor("#FFFFFFFF"),
                    Color.parseColor("#2F855A"),
                    Color.parseColor("#86EFAC"),
                    Color.parseColor("#1F3A2E"),
                    Color.parseColor("#5C7C69"),
                    Color.parseColor("#DCFCE7")
            ),
            new AppThemeOption(
                    CustomizationSettings.THEME_OCEAN,
                    "Ocean Blue",
                    Color.parseColor("#F1F8FF"),
                    Color.parseColor("#FFFFFFFF"),
                    Color.parseColor("#2563EB"),
                    Color.parseColor("#7DD3FC"),
                    Color.parseColor("#172554"),
                    Color.parseColor("#52627A"),
                    Color.parseColor("#DBEAFE")
            ),
            new AppThemeOption(
                    CustomizationSettings.THEME_LAVENDER,
                    "Lavender",
                    Color.parseColor("#FAF7FF"),
                    Color.parseColor("#FFFFFFFF"),
                    Color.parseColor("#7C3AED"),
                    Color.parseColor("#C4B5FD"),
                    Color.parseColor("#312E81"),
                    Color.parseColor("#6D66A8"),
                    Color.parseColor("#EDE9FE")
            )
    );

    private final String id;
    private final String displayName;
    private final int backgroundColor;
    private final int surfaceColor;
    private final int primaryColor;
    private final int accentColor;
    private final int textPrimaryColor;
    private final int textSecondaryColor;
    private final int inputBackgroundColor;

    public AppThemeOption(String id,
                          String displayName,
                          int backgroundColor,
                          int surfaceColor,
                          int primaryColor,
                          int accentColor,
                          int textPrimaryColor,
                          int textSecondaryColor,
                          int inputBackgroundColor) {
        this.id = id;
        this.displayName = displayName;
        this.backgroundColor = backgroundColor;
        this.surfaceColor = surfaceColor;
        this.primaryColor = primaryColor;
        this.accentColor = accentColor;
        this.textPrimaryColor = textPrimaryColor;
        this.textSecondaryColor = textSecondaryColor;
        this.inputBackgroundColor = inputBackgroundColor;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getSurfaceColor() {
        return surfaceColor;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getAccentColor() {
        return accentColor;
    }

    public int getTextPrimaryColor() {
        return textPrimaryColor;
    }

    public int getTextSecondaryColor() {
        return textSecondaryColor;
    }

    public int getInputBackgroundColor() {
        return inputBackgroundColor;
    }

    public static List<AppThemeOption> getOptions() {
        return OPTIONS;
    }

    public static AppThemeOption fromId(String id) {
        for (AppThemeOption option : OPTIONS) {
            if (option.id.equals(id)) {
                return option;
            }
        }
        return OPTIONS.get(0);
    }
}
