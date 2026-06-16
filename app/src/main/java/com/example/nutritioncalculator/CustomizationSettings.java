package com.example.nutritioncalculator;

public class CustomizationSettings {

    public static final String LIST_MODE_EXPANDABLE = "expandable";
    public static final String LIST_MODE_SEARCH = "search";
    public static final String LIST_MODE_BOTH = "both";

    public static final String GENDER_MODE_MAN = "man";
    public static final String GENDER_MODE_WOMAN = "woman";
    public static final String GENDER_MODE_BOTH = "both";

    public static final String FONT_DEFAULT = "default";
    public static final String FONT_SERIF = "serif";
    public static final String FONT_MONOSPACE = "monospace";
    public static final String FONT_CONDENSED = "condensed";

    public static final String THEME_SUNRISE = "sunrise";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SOFT_PINK = "soft_pink";
    public static final String THEME_SOFT_GREEN = "soft_green";
    public static final String THEME_OCEAN = "ocean";
    public static final String THEME_LAVENDER = "lavender";
    public static final String THEME_STANDARD = "standard";

    private boolean showLinkFab = true;
    private boolean showHelpFab = true;
    private boolean showSavedMealsFab = true;
    private String mainListMode = LIST_MODE_BOTH;

    private boolean showMealPlannerCalories = true;
    private boolean showMealPlannerProtein = true;
    private boolean showMealPlannerCarbs = true;
    private boolean showMealPlannerFat = true;
    private boolean showMealPlannerUnit = true;
    private boolean showMealPlannerDetails = true;

    private boolean showHelpCalories = true;
    private boolean showHelpProtein = true;
    private boolean showHelpCarbs = true;
    private boolean showHelpFat = true;
    private String helpGenderMode = GENDER_MODE_BOTH;
    private String selectedFont = FONT_DEFAULT;
    private String selectedColorTheme = THEME_STANDARD;
    private FabLayoutSettings mealFabLayout = new FabLayoutSettings();
    private FabLayoutSettings savedMealsFabLayout = new FabLayoutSettings();
    private FabLayoutSettings helpFabLayout = new FabLayoutSettings();
    private FabLayoutSettings linkFabLayout = new FabLayoutSettings();

    public static CustomizationSettings defaultSettings() {
        return new CustomizationSettings();
    }

    public boolean isShowLinkFab() {
        return showLinkFab;
    }

    public void setShowLinkFab(boolean showLinkFab) {
        this.showLinkFab = showLinkFab;
    }

    public boolean isShowHelpFab() {
        return showHelpFab;
    }

    public void setShowHelpFab(boolean showHelpFab) {
        this.showHelpFab = showHelpFab;
    }

    public boolean isShowSavedMealsFab() {
        return showSavedMealsFab;
    }

    public void setShowSavedMealsFab(boolean showSavedMealsFab) {
        this.showSavedMealsFab = showSavedMealsFab;
    }

    public String getMainListMode() {
        return mainListMode;
    }

    public void setMainListMode(String mainListMode) {
        this.mainListMode = mainListMode;
    }

    public boolean isShowMealPlannerCalories() {
        return showMealPlannerCalories;
    }

    public void setShowMealPlannerCalories(boolean showMealPlannerCalories) {
        this.showMealPlannerCalories = showMealPlannerCalories;
    }

    public boolean isShowMealPlannerProtein() {
        return showMealPlannerProtein;
    }

    public void setShowMealPlannerProtein(boolean showMealPlannerProtein) {
        this.showMealPlannerProtein = showMealPlannerProtein;
    }

    public boolean isShowMealPlannerCarbs() {
        return showMealPlannerCarbs;
    }

    public void setShowMealPlannerCarbs(boolean showMealPlannerCarbs) {
        this.showMealPlannerCarbs = showMealPlannerCarbs;
    }

    public boolean isShowMealPlannerFat() {
        return showMealPlannerFat;
    }

    public void setShowMealPlannerFat(boolean showMealPlannerFat) {
        this.showMealPlannerFat = showMealPlannerFat;
    }

    public boolean isShowMealPlannerUnit() {
        return showMealPlannerUnit;
    }

    public void setShowMealPlannerUnit(boolean showMealPlannerUnit) {
        this.showMealPlannerUnit = showMealPlannerUnit;
    }

    public boolean isShowMealPlannerDetails() {
        return showMealPlannerDetails;
    }

    public void setShowMealPlannerDetails(boolean showMealPlannerDetails) {
        this.showMealPlannerDetails = showMealPlannerDetails;
    }

    public boolean isShowHelpCalories() {
        return showHelpCalories;
    }

    public void setShowHelpCalories(boolean showHelpCalories) {
        this.showHelpCalories = showHelpCalories;
    }

    public boolean isShowHelpProtein() {
        return showHelpProtein;
    }

    public void setShowHelpProtein(boolean showHelpProtein) {
        this.showHelpProtein = showHelpProtein;
    }

    public boolean isShowHelpCarbs() {
        return showHelpCarbs;
    }

    public void setShowHelpCarbs(boolean showHelpCarbs) {
        this.showHelpCarbs = showHelpCarbs;
    }

    public boolean isShowHelpFat() {
        return showHelpFat;
    }

    public void setShowHelpFat(boolean showHelpFat) {
        this.showHelpFat = showHelpFat;
    }

    public String getHelpGenderMode() {
        return helpGenderMode;
    }

    public void setHelpGenderMode(String helpGenderMode) {
        this.helpGenderMode = helpGenderMode;
    }

    public String getSelectedFont() {
        return selectedFont;
    }

    public void setSelectedFont(String selectedFont) {
        this.selectedFont = selectedFont;
    }

    public String getSelectedColorTheme() {
        return selectedColorTheme;
    }

    public void setSelectedColorTheme(String selectedColorTheme) {
        this.selectedColorTheme = selectedColorTheme;
    }

    public FabLayoutSettings getMealFabLayout() {
        if (mealFabLayout == null) {
            mealFabLayout = new FabLayoutSettings();
        }
        return mealFabLayout;
    }

    public void setMealFabLayout(FabLayoutSettings mealFabLayout) {
        this.mealFabLayout = mealFabLayout;
    }

    public FabLayoutSettings getSavedMealsFabLayout() {
        if (savedMealsFabLayout == null) {
            savedMealsFabLayout = new FabLayoutSettings();
        }
        return savedMealsFabLayout;
    }

    public void setSavedMealsFabLayout(FabLayoutSettings savedMealsFabLayout) {
        this.savedMealsFabLayout = savedMealsFabLayout;
    }

    public FabLayoutSettings getHelpFabLayout() {
        if (helpFabLayout == null) {
            helpFabLayout = new FabLayoutSettings();
        }
        return helpFabLayout;
    }

    public void setHelpFabLayout(FabLayoutSettings helpFabLayout) {
        this.helpFabLayout = helpFabLayout;
    }

    public FabLayoutSettings getLinkFabLayout() {
        if (linkFabLayout == null) {
            linkFabLayout = new FabLayoutSettings();
        }
        return linkFabLayout;
    }

    public void setLinkFabLayout(FabLayoutSettings linkFabLayout) {
        this.linkFabLayout = linkFabLayout;
    }

    public CustomizationSettings copy() {
        CustomizationSettings copy = new CustomizationSettings();
        copy.showLinkFab = showLinkFab;
        copy.showHelpFab = showHelpFab;
        copy.showSavedMealsFab = showSavedMealsFab;
        copy.mainListMode = mainListMode;
        copy.showMealPlannerCalories = showMealPlannerCalories;
        copy.showMealPlannerProtein = showMealPlannerProtein;
        copy.showMealPlannerCarbs = showMealPlannerCarbs;
        copy.showMealPlannerFat = showMealPlannerFat;
        copy.showMealPlannerUnit = showMealPlannerUnit;
        copy.showMealPlannerDetails = showMealPlannerDetails;
        copy.showHelpCalories = showHelpCalories;
        copy.showHelpProtein = showHelpProtein;
        copy.showHelpCarbs = showHelpCarbs;
        copy.showHelpFat = showHelpFat;
        copy.helpGenderMode = helpGenderMode;
        copy.selectedFont = selectedFont;
        copy.selectedColorTheme = selectedColorTheme;
        copy.mealFabLayout = getMealFabLayout().copy();
        copy.savedMealsFabLayout = getSavedMealsFabLayout().copy();
        copy.helpFabLayout = getHelpFabLayout().copy();
        copy.linkFabLayout = getLinkFabLayout().copy();
        return copy;
    }
}
