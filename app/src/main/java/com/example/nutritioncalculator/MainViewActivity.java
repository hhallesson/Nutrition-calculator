package com.example.nutritioncalculator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainViewActivity extends AppCompatActivity implements CustomizationFragment.OnCustomizationChangedListener {

    private static final String CUSTOMIZATION_FRAGMENT_TAG = "CustomizationFragment";
    private static final int DEFAULT_FAB_SIZE_DP = 56;
    private static final int MIN_FAB_SIZE_DP = 48;
    private static final int MAX_FAB_SIZE_DP = 88;
    private static final int FAB_EDIT_HIT_SLOP_DP = 36;

    private ExpandableListView expandableListView;
    private ListView searchListView;
    private EditText searchInput;
    private FrameLayout mainContentFrame;
    private FragmentContainerView customizationFragmentContainer;
    private FoodExpandableAdapter expandableAdapter;

    private Map<String, List<FoodItem>> foodData;
    private List<String> categoryList;

    private List<FoodItem> allItems;
    private List<FoodItem> filteredItems;

    private SearchListAdapter searchAdapter;
    private MealPlanner mealPlanner;
    private FloatingActionButton floatingMealBtn;
    private FloatingActionButton floatingSavedMealsBtn;
    private FloatingActionButton floatingHelpBtn;
    private FloatingActionButton floatingFHMBtn;
    private FrameLayout.LayoutParams defaultMealFabLayoutParams;
    private FrameLayout.LayoutParams defaultSavedMealsFabLayoutParams;
    private FrameLayout.LayoutParams defaultHelpFabLayoutParams;
    private FrameLayout.LayoutParams defaultLinkFabLayoutParams;
    private View editModeTouchOverlay;
    private ImageButton settingsBtn;
    private LinearLayout editModeTopBar;
    private Button editModeCancelBtn;
    private Button editModeConfirmBtn;
    private CustomizationHandler customizationHandler;
    private boolean isInEditMode;
    private CustomizationSettings editModeSettings;
    private FloatingActionButton selectedEditFab;
    private int activeFabPointerId = MotionEvent.INVALID_POINTER_ID;
    private int resizePointerId = MotionEvent.INVALID_POINTER_ID;
    private boolean isResizeMode;
    private boolean editGestureLockedUntilRelease;
    private float dragTouchOffsetX;
    private float dragTouchOffsetY;
    private float resizeStartDistance;
    private int resizeStartSizeDp;
    private boolean customizationEnabled;
    private OnBackPressedCallback customizationBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        expandableListView = findViewById(R.id.expandableListView);
        searchListView = findViewById(R.id.searchListView);
        searchInput = findViewById(R.id.searchInput);
        mainContentFrame = findViewById(R.id.mainContentFrame);
        customizationFragmentContainer = findViewById(R.id.customizationFragmentContainer);
        settingsBtn = findViewById(R.id.settingsBtn);
        floatingMealBtn = findViewById(R.id.floatingMealBtn);
        floatingSavedMealsBtn = findViewById(R.id.floatingSavedMealsBtn);
        floatingHelpBtn = findViewById(R.id.floatingHelpBtn);
        floatingFHMBtn = findViewById(R.id.floatingFHMBtn);
        defaultMealFabLayoutParams = copyLayoutParams(floatingMealBtn);
        defaultSavedMealsFabLayoutParams = copyLayoutParams(floatingSavedMealsBtn);
        defaultHelpFabLayoutParams = copyLayoutParams(floatingHelpBtn);
        defaultLinkFabLayoutParams = copyLayoutParams(floatingFHMBtn);
        editModeTouchOverlay = findViewById(R.id.editModeTouchOverlay);
        editModeTopBar = findViewById(R.id.editModeTopBar);
        editModeCancelBtn = findViewById(R.id.editModeCancelBtn);
        editModeConfirmBtn = findViewById(R.id.editModeConfirmBtn);
        customizationEnabled = getIntent().getBooleanExtra(MainActivity.EXTRA_CUSTOMIZATION_ENABLED, true);

        foodData = DataHandler.loadFoodData(this);
        categoryList = new ArrayList<>(foodData.keySet());
        mealPlanner = new MealPlanner();
        customizationHandler = customizationEnabled
                ? CustomizationHandler.fromContext(this)
                : new CustomizationHandler(CustomizationSettings.defaultSettings());

        expandableAdapter = new FoodExpandableAdapter(this, categoryList, foodData, customizationHandler);
        expandableListView.setAdapter(expandableAdapter);

        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (isInEditMode) {
                return true;
            }
            String category = categoryList.get(groupPosition);
            FoodItem clickedItem = foodData.get(category).get(childPosition);
            showFoodDetailDialog(clickedItem);
            return true;
        });

        settingsBtn.setOnClickListener(v -> {
            if (customizationEnabled && !isInEditMode) {
                openCustomizationMenu();
            }
        });

        floatingMealBtn.setOnClickListener(v -> {
            if (!isInEditMode) {
                showMealSummaryDialog();
            }
        });
        floatingSavedMealsBtn.setOnClickListener(v -> {
            if (!isInEditMode) {
                showSavedMealsDialog();
            }
        });
        floatingHelpBtn.setOnClickListener(v -> {
            if (!isInEditMode) {
                showHelpDialog();
            }
        });
        floatingFHMBtn.setOnClickListener(v -> {
            if (isInEditMode) {
                return;
            }
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.folkhalsomyndigheten.se/publikationer-och-material/publikationsarkiv/e/en-hallbar-och-halsosam-livsmedelskonsumtion-aterredovisning-av-regeringsuppdrag/")
            );
            startActivity(browserIntent);
        });

        allItems = flattenFoodData(foodData);
        filteredItems = new ArrayList<>();

        searchAdapter = new SearchListAdapter(this, filteredItems, customizationHandler.getSettings());
        searchListView.setAdapter(searchAdapter);

        searchListView.setOnItemClickListener((parent, view, position, id) -> {
            if (isInEditMode) {
                return;
            }
            FoodItem clickedItem = filteredItems.get(position);
            showFoodDetailDialog(clickedItem);
        });

        expandableListView.setOnTouchListener((v, event) -> isInEditMode);
        searchListView.setOnTouchListener((v, event) -> isInEditMode);
        searchInput.setOnTouchListener((v, event) -> isInEditMode);
        editModeCancelBtn.setOnClickListener(v -> exitEditMode(false));
        editModeConfirmBtn.setOnClickListener(v -> exitEditMode(true));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        customizationBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                closeCustomizationMenu(false, null);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, customizationBackPressedCallback);
        if (isCustomizationMenuVisible()) {
            customizationFragmentContainer.setVisibility(View.VISIBLE);
            customizationBackPressedCallback.setEnabled(true);
        }

        applyCustomizationSettings();
    }

    private List<FoodItem> flattenFoodData(Map<String, List<FoodItem>> data) {
        List<FoodItem> flatList = new ArrayList<>();

        for (Map.Entry<String, List<FoodItem>> entry : data.entrySet()) {
            List<FoodItem> items = entry.getValue();

            if (items != null) {
                flatList.addAll(items);
            }
        }

        return flatList;
    }

    private void updateSearch(String query) {
        filteredItems.clear();

        String trimmedQuery = query == null ? "" : query.trim();

        if (trimmedQuery.isEmpty()) {
            if (customizationHandler.shouldShowSearchList()
                    && !customizationHandler.shouldShowExpandableList()) {
                searchListView.setVisibility(View.GONE);
                expandableListView.setVisibility(View.GONE);
                searchAdapter.notifyDataSetChanged();
                return;
            }

            searchListView.setVisibility(View.GONE);
            expandableListView.setVisibility(customizationHandler.shouldShowExpandableList() ? View.VISIBLE : View.GONE);
            searchAdapter.notifyDataSetChanged();
            return;
        }

        expandableListView.setVisibility(View.GONE);
        searchListView.setVisibility(View.VISIBLE);

        String lowerQuery = trimmedQuery.toLowerCase();

        for (FoodItem item : allItems) {
            if (item.getName().toLowerCase().contains(lowerQuery)) {
                filteredItems.add(item);
            }
        }

        searchAdapter.notifyDataSetChanged();
    }

    private void showFoodDetailDialog(FoodItem item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_food_detail, null);

        TextView detailName = dialogView.findViewById(R.id.detailName);
        TextView detailUnit = dialogView.findViewById(R.id.detailUnit);
        TextView detailCalories = dialogView.findViewById(R.id.detailCalories);
        TextView detailProtein = dialogView.findViewById(R.id.detailProtein);
        TextView detailCarbs = dialogView.findViewById(R.id.detailCarbs);
        TextView detailFat = dialogView.findViewById(R.id.detailFat);
        View detailCaloriesRow = dialogView.findViewById(R.id.detailCaloriesRow);
        View detailProteinRow = dialogView.findViewById(R.id.detailProteinRow);
        View detailCarbsRow = dialogView.findViewById(R.id.detailCarbsRow);
        View detailFatRow = dialogView.findViewById(R.id.detailFatRow);
        EditText inputAmount = dialogView.findViewById(R.id.inputAmount);
        Button addMealBtn = dialogView.findViewById(R.id.addMealBtn);

        if (item.getUnit().equals("st")) {
            detailUnit.setText(getString(R.string.detail_unit, item.getUnit()));
            inputAmount.setHint("Ange antal...");
        } else {
            String formattedUnitText = "100" + item.getUnit();
            detailUnit.setText(getString(R.string.detail_unit, formattedUnitText));
            if (item.getUnit().equals("g")) {
                inputAmount.setHint("Ange antal gram...");
            } else {
                inputAmount.setHint("Ange antal milliliter...");
            }
        }

        detailName.setText(item.getName());
        detailCalories.setText(customizationHandler.formatCalories(this, item.getCalories()));
        detailProtein.setText(customizationHandler.formatMacro(this, item.getProtein()));
        detailCarbs.setText(customizationHandler.formatMacro(this, item.getCarbs()));
        detailFat.setText(customizationHandler.formatMacro(this, item.getFat()));

        detailCaloriesRow.setVisibility(customizationHandler.shouldShowMealPlannerCalories() ? View.VISIBLE : View.GONE);
        detailProteinRow.setVisibility(customizationHandler.shouldShowMealPlannerProtein() ? View.VISIBLE : View.GONE);
        detailCarbsRow.setVisibility(customizationHandler.shouldShowMealPlannerCarbs() ? View.VISIBLE : View.GONE);
        detailFatRow.setVisibility(customizationHandler.shouldShowMealPlannerFat() ? View.VISIBLE : View.GONE);
        detailUnit.setVisibility(customizationHandler.shouldShowMealPlannerUnit() ? View.VISIBLE : View.GONE);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Stäng", null)
                .create();

        addMealBtn.setOnClickListener(v -> {
            String amountText = inputAmount.getText().toString().trim();
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Ange antal först", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountText);
            double calories = item.getCalories();
            double protein = item.getProtein();
            double carbs = item.getCarbs();
            double fat = item.getFat();

            if (!item.getUnit().equals("st")) {
                calories /= 100;
                protein /= 100;
                carbs /= 100;
                fat /= 100;
            }

            double calculatedCalories = calories * amount;
            double calculatedProtein = protein * amount;
            double calculatedCarbs = carbs * amount;
            double calculatedFat = fat * amount;

            mealPlanner.addTotal(calculatedCalories, calculatedProtein, calculatedCarbs, calculatedFat);
            mealPlanner.addMealItem(new MealItem(
                    amount,
                    item.getUnit(),
                    item.getName(),
                    calculatedCalories,
                    calculatedProtein,
                    calculatedCarbs,
                    calculatedFat
            ));

            Toast.makeText(this, getString(R.string.item_added_alert, amount, item.getUnit(), item.getName()), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        dialog.show();
        AppStyler.applyDialogStyling(dialog, dialogView, customizationHandler.getSettings());
    }

    private void showMealSummaryDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_meal_summary, null);

        TextView totalCaloriesText = dialogView.findViewById(R.id.totalCaloriesText);
        TextView totalProteinText = dialogView.findViewById(R.id.totalProteinText);
        TextView totalCarbsText = dialogView.findViewById(R.id.totalCarbsText);
        TextView totalFatText = dialogView.findViewById(R.id.totalFatText);
        TextView mealSummaryTitle = dialogView.findViewById(R.id.mealSummaryTitle);
        ImageButton showMealDetailBtn = dialogView.findViewById(R.id.showMealDetailsBtn);
        View totalCaloriesRow = dialogView.findViewById(R.id.totalCaloriesRow);
        View totalProteinRow = dialogView.findViewById(R.id.totalProteinRow);
        View totalCarbsRow = dialogView.findViewById(R.id.totalCarbsRow);
        View totalFatRow = dialogView.findViewById(R.id.totalFatRow);
        ImageButton returnButton = dialogView.findViewById(R.id.returnBtn);
        Button saveMealBtn = dialogView.findViewById(R.id.saveMealBtn);
        Button clearMealBtn = dialogView.findViewById(R.id.clearMealBtn);

        updateMealSummaryViews(totalCaloriesText, totalProteinText, totalCarbsText, totalFatText, mealSummaryTitle);

        totalCaloriesRow.setVisibility(customizationHandler.shouldShowMealPlannerCalories() ? View.VISIBLE : View.GONE);
        totalProteinRow.setVisibility(customizationHandler.shouldShowMealPlannerProtein() ? View.VISIBLE : View.GONE);
        totalCarbsRow.setVisibility(customizationHandler.shouldShowMealPlannerCarbs() ? View.VISIBLE : View.GONE);
        totalFatRow.setVisibility(customizationHandler.shouldShowMealPlannerFat() ? View.VISIBLE : View.GONE);
        showMealDetailBtn.setVisibility(customizationHandler.shouldShowMealPlannerDetails() ? View.VISIBLE : View.GONE);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        returnButton.setOnClickListener(v -> dialog.dismiss());

        showMealDetailBtn.setOnClickListener(v -> showMealDetailsDialog(() -> updateMealSummaryViews(
                totalCaloriesText,
                totalProteinText,
                totalCarbsText,
                totalFatText,
                mealSummaryTitle
        )));

        saveMealBtn.setOnClickListener(v -> showSaveMealDialog());

        clearMealBtn.setOnClickListener(v -> {
            mealPlanner.resetMealPlanner();
            updateMealSummaryViews(totalCaloriesText, totalProteinText, totalCarbsText, totalFatText, mealSummaryTitle);
            Toast.makeText(this, "Måltiden tömdes.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
        AppStyler.applyDialogStyling(dialog, dialogView, customizationHandler.getSettings());
    }

    private void showMealDetailsDialog(Runnable onMealChanged) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_meal_details, null);

        ListView mealDetailsListView = dialogView.findViewById(R.id.mealDetailsListView);
        ImageButton returnDetailsBtn = dialogView.findViewById(R.id.returnDetailsBtn);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        List<MealItem> mealItems = mealPlanner.getMealItems();
        final MealDetailsAdapter[] adapter = new MealDetailsAdapter[1];

        adapter[0] = new MealDetailsAdapter(this, mealItems, customizationHandler, item -> {
            mealPlanner.removeMealItem(item);
            adapter[0].notifyDataSetChanged();

            if (onMealChanged != null) {
                onMealChanged.run();
            }

            if (mealPlanner.isMealEmpty()) {
                Toast.makeText(this, "Måltiden är nu tom.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, item.getName() + " togs bort.", Toast.LENGTH_SHORT).show();
            }
        });

        mealDetailsListView.setAdapter(adapter[0]);
        returnDetailsBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        AppStyler.applyDialogStyling(dialog, dialogView, customizationHandler.getSettings());
    }

    private void updateMealSummaryViews(TextView totalCaloriesText,
                                        TextView totalProteinText,
                                        TextView totalCarbsText,
                                        TextView totalFatText,
                                        TextView mealSummaryTitle) {
        mealSummaryTitle.setText(mealPlanner.loadedMealName);
        totalCaloriesText.setText(customizationHandler.formatCalories(this, mealPlanner.getTotalCalories()));
        totalProteinText.setText(customizationHandler.formatMacro(this, mealPlanner.getTotalProtein()));
        totalCarbsText.setText(customizationHandler.formatMacro(this, mealPlanner.getTotalCarbs()));
        totalFatText.setText(customizationHandler.formatMacro(this, mealPlanner.getTotalFat()));
    }

    private void showSaveMealDialog() {
        if (mealPlanner.isMealEmpty()) {
            Toast.makeText(this, "Det finns ingen måltid att spara.", Toast.LENGTH_SHORT).show();
            return;
        }

        final EditText input = new EditText(this);
        input.setHint("Ange namn pa måltiden");
        AppStyler.applyViewStyling(input, customizationHandler.getSettings());

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Spara måltid")
                .setView(input)
                .setNegativeButton("Avbryt", null)
                .setPositiveButton("Spara", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button saveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);

            saveButton.setOnClickListener(v -> {
                String mealName = input.getText().toString().trim();

                if (mealName.isEmpty()) {
                    input.setError("Ange ett namn");
                    return;
                }

                if (DataHandler.mealNameExists(this, mealName)) {
                    androidx.appcompat.app.AlertDialog overwriteDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Måltiden finns redan")
                            .setMessage("Det finns redan en måltid med detta namn. Vill du skriva över måltiden?")
                            .setNegativeButton("Nej", null)
                            .setPositiveButton("Ja", (dialogInterface, which) -> {
                                boolean success = DataHandler.overwriteMeal(this, mealName, mealPlanner.getMealItemsCopy());

                                if (success) {
                                    Toast.makeText(this, "Måltiden skrevs över.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(this, "Kunde inte spara måltiden.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create();
                    overwriteDialog.show();
                    AppStyler.applyDialogStyling(overwriteDialog, null, customizationHandler.getSettings());
                } else {
                    boolean success = DataHandler.saveMeal(this, mealName, mealPlanner.getMealItemsCopy());

                    if (success) {
                        Toast.makeText(this, "Måltiden sparades.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Kunde inte spara måltiden.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        dialog.show();
        AppStyler.applyDialogStyling(dialog, input, customizationHandler.getSettings());
    }

    private void showSavedMealsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_saved_meals, null);

        ListView savedMealsListView = dialogView.findViewById(R.id.savedMealsListView);
        ImageButton returnSavedMealsBtn = dialogView.findViewById(R.id.returnSavedMealsBtn);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        List<String> savedMealNames = new ArrayList<>(DataHandler.loadSavedMeals(this).keySet());
        final SavedMealsAdapter[] adapter = new SavedMealsAdapter[1];

        adapter[0] = new SavedMealsAdapter(
                this,
                savedMealNames,
                customizationHandler.getSettings(),
                mealName -> {
                    androidx.appcompat.app.AlertDialog deleteDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Ta bort måltid")
                            .setMessage("Vill du verkligen ta bort den sparade måltiden \"" + mealName + "\"?")
                            .setNegativeButton("Nej", null)
                            .setPositiveButton("Ja", (confirmDialog, which) -> {
                                boolean success = DataHandler.deleteSavedMeal(this, mealName);

                                if (success) {
                                    savedMealNames.remove(mealName);
                                    adapter[0].notifyDataSetChanged();
                                    Toast.makeText(this, "Måltiden togs bort.", Toast.LENGTH_SHORT).show();

                                    if (savedMealNames.isEmpty()) {
                                        dialog.dismiss();
                                    }
                                } else {
                                    Toast.makeText(this, "Kunde inte ta bort måltiden.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create();
                    deleteDialog.show();
                    AppStyler.applyDialogStyling(deleteDialog, null, customizationHandler.getSettings());
                },
                mealName -> {
                    Map<String, List<MealItem>> currentSavedMeals = DataHandler.loadSavedMeals(this);
                    List<MealItem> selectedMealItems = currentSavedMeals.get(mealName);

                    if (selectedMealItems != null) {
                        mealPlanner.loadMeal(selectedMealItems, mealName);
                        Toast.makeText(this, "Måltiden \"" + mealName + "\" laddades.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(this, "Kunde inte ladda måltiden.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        savedMealsListView.setAdapter(adapter[0]);
        returnSavedMealsBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        AppStyler.applyDialogStyling(dialog, dialogView, customizationHandler.getSettings());
    }

    private void showHelpDialog() {
        if (isInEditMode) {
            return;
        }
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_help_view, null);

        ImageButton returnHelpBtn = dialogView.findViewById(R.id.returnHelpBtn);
        TextView helpCaloriesTitle = dialogView.findViewById(R.id.helpCaloriesTitle);
        TableLayout dailyCaloriesWoman = dialogView.findViewById(R.id.dailyCaloriesWoman);
        TableLayout dailyCaloriesMen = dialogView.findViewById(R.id.dailyCaloriesMen);
        TextView helpProteinTitle = dialogView.findViewById(R.id.helpProteinTitle);
        TableLayout dailyProteinWoman = dialogView.findViewById(R.id.dailyProteinWoman);
        TableLayout dailyProteinMen = dialogView.findViewById(R.id.dailyProteinMen);
        TextView helpCarbsTitle = dialogView.findViewById(R.id.helpCarbsTitle);
        TableLayout dailyCarbsWoman = dialogView.findViewById(R.id.dailyCarbsWoman);
        TableLayout dailyCarbsMen = dialogView.findViewById(R.id.dailyCarbsMen);
        TextView helpFatTitle = dialogView.findViewById(R.id.helpFatTitle);
        TableLayout dailyFatWoman = dialogView.findViewById(R.id.dailyFatWoman);
        TableLayout dailyFatMen = dialogView.findViewById(R.id.dailyFatMen);

        helpCaloriesTitle.setVisibility(customizationHandler.shouldShowHelpCalories() ? View.VISIBLE : View.GONE);
        dailyCaloriesWoman.setVisibility(
                customizationHandler.shouldShowHelpCalories() && customizationHandler.shouldShowHelpWomen() ? View.VISIBLE : View.GONE
        );
        dailyCaloriesMen.setVisibility(
                customizationHandler.shouldShowHelpCalories() && customizationHandler.shouldShowHelpMen() ? View.VISIBLE : View.GONE
        );

        helpProteinTitle.setVisibility(customizationHandler.shouldShowHelpProtein() ? View.VISIBLE : View.GONE);
        dailyProteinWoman.setVisibility(
                customizationHandler.shouldShowHelpProtein() && customizationHandler.shouldShowHelpWomen() ? View.VISIBLE : View.GONE
        );
        dailyProteinMen.setVisibility(
                customizationHandler.shouldShowHelpProtein() && customizationHandler.shouldShowHelpMen() ? View.VISIBLE : View.GONE
        );

        helpCarbsTitle.setVisibility(customizationHandler.shouldShowHelpCarbs() ? View.VISIBLE : View.GONE);
        dailyCarbsWoman.setVisibility(
                customizationHandler.shouldShowHelpCarbs() && customizationHandler.shouldShowHelpWomen() ? View.VISIBLE : View.GONE
        );
        dailyCarbsMen.setVisibility(
                customizationHandler.shouldShowHelpCarbs() && customizationHandler.shouldShowHelpMen() ? View.VISIBLE : View.GONE
        );

        helpFatTitle.setVisibility(customizationHandler.shouldShowHelpFat() ? View.VISIBLE : View.GONE);
        dailyFatWoman.setVisibility(
                customizationHandler.shouldShowHelpFat() && customizationHandler.shouldShowHelpWomen() ? View.VISIBLE : View.GONE
        );
        dailyFatMen.setVisibility(
                customizationHandler.shouldShowHelpFat() && customizationHandler.shouldShowHelpMen() ? View.VISIBLE : View.GONE
        );

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        returnHelpBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        AppStyler.applyDialogStyling(dialog, dialogView, customizationHandler.getSettings());
    }

    private void applyCustomizationSettings() {
        AppStyler.applyActivityStyling(this, customizationHandler.getSettings());
        styleEditModePanels();
        settingsBtn.setVisibility(customizationEnabled && !isInEditMode ? View.VISIBLE : View.GONE);
        floatingFHMBtn.setVisibility(customizationHandler.shouldShowLinkFab() ? View.VISIBLE : View.GONE);
        floatingHelpBtn.setVisibility(customizationHandler.shouldShowHelpFab() ? View.VISIBLE : View.GONE);
        floatingSavedMealsBtn.setVisibility(customizationHandler.shouldShowSavedMealsFab() ? View.VISIBLE : View.GONE);
        searchInput.setVisibility(customizationHandler.shouldShowSearchInput() ? View.VISIBLE : View.GONE);
        if (expandableAdapter != null) {
            expandableAdapter.setCustomizationHandler(customizationHandler);
            expandableAdapter.notifyDataSetChanged();
        }
        if (searchAdapter != null) {
            searchAdapter.setCustomizationSettings(customizationHandler.getSettings());
            searchAdapter.notifyDataSetChanged();
        }

        if (!customizationHandler.shouldShowSearchInput()) {
            searchInput.setText("");
        }

        if (customizationHandler.shouldShowSearchList() && !customizationHandler.shouldShowExpandableList()) {
            updateSearch(searchInput.getText().toString());
            return;
        }

        if (searchInput.getText().toString().trim().isEmpty()) {
            expandableListView.setVisibility(customizationHandler.shouldShowExpandableList() ? View.VISIBLE : View.GONE);
            searchListView.setVisibility(View.GONE);
        } else {
            updateSearch(searchInput.getText().toString());
        }

        if (mainContentFrame != null) {
            mainContentFrame.post(() -> applyFabLayouts(customizationHandler.getSettings()));
        }
    }

    @Override
    public void onCustomizationChanged(CustomizationSettings settings) {
        customizationHandler = new CustomizationHandler(settings);
        applyCustomizationSettings();
    }

    @Override
    public void onCloseCustomizationMenuRequested() {
        closeCustomizationMenu(false, null);
    }

    @Override
    public void onEnterStructureEditModeRequested() {
        closeCustomizationMenu(true, this::enterEditMode);
    }

    private void enterEditMode() {
        isInEditMode = true;
        editModeSettings = customizationHandler.getSettings().copy();
        captureCurrentFabLayouts(editModeSettings);
        editModeTopBar.setVisibility(View.VISIBLE);
        floatingSavedMealsBtn.bringToFront();
        floatingMealBtn.bringToFront();
        floatingHelpBtn.bringToFront();
        floatingFHMBtn.bringToFront();
        settingsBtn.setVisibility(View.INVISIBLE);
        searchInput.clearFocus();
        searchInput.setEnabled(false);
        searchInput.setFocusable(false);
        searchInput.setFocusableInTouchMode(false);
        searchInput.setCursorVisible(false);
        resetEditGestureState();
        runAfterMainContentLayoutChange(() -> {
            if (!isInEditMode || editModeSettings == null) {
                return;
            }
            applyFabLayouts(editModeSettings);
            selectEditableFab(findFirstVisibleFab());
        });
    }

    private void exitEditMode(boolean shouldSave) {
        if (!isInEditMode) {
            return;
        }

        CustomizationSettings pendingSettings = shouldSave && editModeSettings != null
                ? editModeSettings.copy()
                : null;

        isInEditMode = false;
        editModeSettings = null;
        selectedEditFab = null;
        searchInput.setEnabled(true);
        searchInput.setFocusable(true);
        searchInput.setFocusableInTouchMode(true);
        searchInput.setCursorVisible(true);
        settingsBtn.setVisibility(customizationEnabled ? View.VISIBLE : View.GONE);
        editModeTopBar.setVisibility(View.GONE);
        resetEditGestureState();
        clearFabSelectionState();

        if (shouldSave) {
            runAfterMainContentLayoutChange(() -> {
                if (pendingSettings == null) {
                    return;
                }
                remapFabLayoutsToCurrentFrame(pendingSettings);
                customizationHandler = new CustomizationHandler(pendingSettings);
                applyCustomizationSettings();
                DataHandler.saveCustomizationSettings(this, pendingSettings);
                openCustomizationMenu();
            });
        } else {
            runAfterMainContentLayoutChange(() -> {
                applyCustomizationSettings();
                openCustomizationMenu();
            });
        }
    }

    private void openCustomizationMenu() {
        if (!customizationEnabled || isCustomizationMenuVisible()) {
            return;
        }

        customizationBackPressedCallback.setEnabled(true);
        customizationFragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        0
                )
                .replace(R.id.customizationFragmentContainer, new CustomizationFragment(), CUSTOMIZATION_FRAGMENT_TAG)
                .commit();
    }

    private void closeCustomizationMenu(boolean allowStateLoss, Runnable onClosed) {
        Fragment customizationFragment = getSupportFragmentManager().findFragmentByTag(CUSTOMIZATION_FRAGMENT_TAG);
        if (customizationFragment == null) {
            customizationFragmentContainer.setVisibility(View.GONE);
            customizationBackPressedCallback.setEnabled(false);
            if (onClosed != null) {
                onClosed.run();
            }
            return;
        }

        customizationBackPressedCallback.setEnabled(false);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        0,
                        R.anim.slide_out_right
                )
                .remove(customizationFragment)
                .runOnCommit(() -> {
                    customizationFragmentContainer.setVisibility(View.GONE);
                    if (onClosed != null) {
                        onClosed.run();
                    }
                });

        if (allowStateLoss) {
            transaction.commitAllowingStateLoss();
        } else {
            transaction.commit();
        }
    }

    private boolean isCustomizationMenuVisible() {
        Fragment customizationFragment = getSupportFragmentManager().findFragmentByTag(CUSTOMIZATION_FRAGMENT_TAG);
        return customizationFragment != null && customizationFragment.isAdded();
    }

    private void moveFabWithinBounds(FloatingActionButton fab, FabLayoutSettings layoutSettings, float x, float y) {
        float clampedX = clamp(x, 0f, mainContentFrame.getWidth() - fab.getWidth());
        float clampedY = clamp(y, 0f, mainContentFrame.getHeight() - fab.getHeight());
        fab.setX(clampedX);
        fab.setY(clampedY);
        layoutSettings.setPositionX(clampedX);
        layoutSettings.setPositionY(clampedY);
        layoutSettings.setBaseLayoutWidth(mainContentFrame.getWidth());
        layoutSettings.setBaseLayoutHeight(mainContentFrame.getHeight());
    }

    private void resizeFabPreservingPosition(FloatingActionButton fab, FabLayoutSettings layoutSettings, int newSizeDp) {
        float centerX = fab.getX() + (fab.getWidth() / 2f);
        float centerY = fab.getY() + (fab.getHeight() / 2f);

        layoutSettings.setSizeDp(newSizeDp);
        applyFabSize(fab, newSizeDp);

        fab.post(() -> {
            if (!isInEditMode || editModeSettings == null) {
                return;
            }

            float newX = centerX - (fab.getWidth() / 2f);
            float newY = centerY - (fab.getHeight() / 2f);
            moveFabWithinBounds(fab, layoutSettings, newX, newY);
        });
    }

    private void startFabDrag(FloatingActionButton fab, MotionEvent event, int pointerIndex) {
        selectedEditFab = fab;
        selectEditableFab(fab);
        activeFabPointerId = event.getPointerId(pointerIndex);
        dragTouchOffsetX = getContentX(event, pointerIndex) - fab.getX();
        dragTouchOffsetY = getContentY(event, pointerIndex) - fab.getY();
        isResizeMode = false;
        editGestureLockedUntilRelease = false;
        resizePointerId = MotionEvent.INVALID_POINTER_ID;
    }

    private void startFabResize(MotionEvent event, int resizePointerIndex) {
        if (selectedEditFab == null || editModeSettings == null) {
            return;
        }

        int activeIndex = event.findPointerIndex(activeFabPointerId);
        if (activeIndex < 0) {
            return;
        }

        resizePointerId = event.getPointerId(resizePointerIndex);
        resizeStartDistance = calculateDistance(
                getContentX(event, activeIndex),
                getContentY(event, activeIndex),
                getContentX(event, resizePointerIndex),
                getContentY(event, resizePointerIndex)
        );
        FabLayoutSettings layoutSettings = getFabLayoutSettings(selectedEditFab, editModeSettings);
        resizeStartSizeDp = layoutSettings.getSizeDp() > 0 ? layoutSettings.getSizeDp() : DEFAULT_FAB_SIZE_DP;
        isResizeMode = true;
        editGestureLockedUntilRelease = true;
    }

    private void updateFabResize(MotionEvent event) {
        if (!isResizeMode || selectedEditFab == null || editModeSettings == null) {
            return;
        }

        int activeIndex = event.findPointerIndex(activeFabPointerId);
        int resizeIndex = event.findPointerIndex(resizePointerId);
        if (activeIndex < 0 || resizeIndex < 0) {
            return;
        }

        float currentDistance = calculateDistance(
                getContentX(event, activeIndex),
                getContentY(event, activeIndex),
                getContentX(event, resizeIndex),
                getContentY(event, resizeIndex)
        );

        if (resizeStartDistance <= 0f) {
            return;
        }

        float scaleFactor = currentDistance / resizeStartDistance;
        int nextSizeDp = Math.round(resizeStartSizeDp * scaleFactor);
        int boundedSizeDp = Math.max(MIN_FAB_SIZE_DP, Math.min(MAX_FAB_SIZE_DP, nextSizeDp));
        FabLayoutSettings layoutSettings = getFabLayoutSettings(selectedEditFab, editModeSettings);

        if (boundedSizeDp != layoutSettings.getSizeDp()) {
            resizeFabPreservingPosition(selectedEditFab, layoutSettings, boundedSizeDp);
        }
    }

    private void updateFabDrag(MotionEvent event) {
        if (selectedEditFab == null || editModeSettings == null || isResizeMode || activeFabPointerId == MotionEvent.INVALID_POINTER_ID) {
            return;
        }

        int activeIndex = event.findPointerIndex(activeFabPointerId);
        if (activeIndex < 0) {
            return;
        }

        FabLayoutSettings layoutSettings = getFabLayoutSettings(selectedEditFab, editModeSettings);
        float nextX = getContentX(event, activeIndex) - dragTouchOffsetX;
        float nextY = getContentY(event, activeIndex) - dragTouchOffsetY;
        moveFabWithinBounds(selectedEditFab, layoutSettings, nextX, nextY);
    }

    private void resetEditGestureState() {
        activeFabPointerId = MotionEvent.INVALID_POINTER_ID;
        resizePointerId = MotionEvent.INVALID_POINTER_ID;
        isResizeMode = false;
        editGestureLockedUntilRelease = false;
        dragTouchOffsetX = 0f;
        dragTouchOffsetY = 0f;
        resizeStartDistance = 0f;
        resizeStartSizeDp = DEFAULT_FAB_SIZE_DP;
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.hypot(dx, dy);
    }

    private float getContentX(MotionEvent event, int pointerIndex) {
        int[] location = new int[2];
        mainContentFrame.getLocationOnScreen(location);
        return event.getRawX(pointerIndex) - location[0];
    }

    private float getContentY(MotionEvent event, int pointerIndex) {
        int[] location = new int[2];
        mainContentFrame.getLocationOnScreen(location);
        return event.getRawY(pointerIndex) - location[1];
    }

    private boolean isEventInsideMainContent(MotionEvent event) {
        int[] location = new int[2];
        mainContentFrame.getLocationOnScreen(location);
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        return rawX >= location[0]
                && rawX <= location[0] + mainContentFrame.getWidth()
                && rawY >= location[1]
                && rawY <= location[1] + mainContentFrame.getHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isInEditMode) {
            return super.dispatchTouchEvent(event);
        }

        if (!isEventInsideMainContent(event)) {
            return super.dispatchTouchEvent(event);
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                FloatingActionButton touchedFab = findEditableFabAt(getContentX(event, 0), getContentY(event, 0));
                if (touchedFab != null && !editGestureLockedUntilRelease) {
                    startFabDrag(touchedFab, event, 0);
                }
                return true;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (selectedEditFab != null && activeFabPointerId != MotionEvent.INVALID_POINTER_ID && resizePointerId == MotionEvent.INVALID_POINTER_ID) {
                    startFabResize(event, event.getActionIndex());
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (isResizeMode) {
                    updateFabResize(event);
                } else if (!editGestureLockedUntilRelease) {
                    updateFabDrag(event);
                }
                return true;
            case MotionEvent.ACTION_POINTER_UP:
                int liftedPointerId = event.getPointerId(event.getActionIndex());
                if (liftedPointerId == resizePointerId && activeFabPointerId != MotionEvent.INVALID_POINTER_ID) {
                    isResizeMode = false;
                    resizePointerId = MotionEvent.INVALID_POINTER_ID;
                    editGestureLockedUntilRelease = false;

                    int activeIndex = event.findPointerIndex(activeFabPointerId);
                    if (activeIndex >= 0 && selectedEditFab != null) {
                        dragTouchOffsetX = getContentX(event, activeIndex) - selectedEditFab.getX();
                        dragTouchOffsetY = getContentY(event, activeIndex) - selectedEditFab.getY();
                    }
                } else if (liftedPointerId == activeFabPointerId) {
                    isResizeMode = false;
                    resizePointerId = MotionEvent.INVALID_POINTER_ID;
                    activeFabPointerId = MotionEvent.INVALID_POINTER_ID;
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetEditGestureState();
                return true;
            default:
                return true;
        }
    }

    private void applyFabLayouts(CustomizationSettings settings) {
        applyFabLayout(floatingMealBtn, settings.getMealFabLayout());
        applyFabLayout(floatingSavedMealsBtn, settings.getSavedMealsFabLayout());
        applyFabLayout(floatingHelpBtn, settings.getHelpFabLayout());
        applyFabLayout(floatingFHMBtn, settings.getLinkFabLayout());
    }

    private void applyFabLayout(FloatingActionButton fab, FabLayoutSettings layoutSettings) {
        int sizeDp = layoutSettings.getSizeDp() > 0 ? layoutSettings.getSizeDp() : DEFAULT_FAB_SIZE_DP;
        applyFabSize(fab, sizeDp);
        int fabSizePx = dpToPx(sizeDp);

        if (layoutSettings.hasCustomPosition()) {
            float targetX = remapFabCoordinate(
                    layoutSettings.getPositionX(),
                    layoutSettings.getBaseLayoutWidth(),
                    mainContentFrame.getWidth(),
                    fabSizePx
            );
            float targetY = remapFabCoordinate(
                    layoutSettings.getPositionY(),
                    layoutSettings.getBaseLayoutHeight(),
                    mainContentFrame.getHeight(),
                    fabSizePx
            );
            applyAbsoluteFabLayout(fab, Math.round(targetX), Math.round(targetY));
        } else {
            restoreDefaultFabLayout(fab);
        }
    }

    private void applyFabSize(FloatingActionButton fab, int sizeDp) {
        int sizePx = dpToPx(sizeDp);
        fab.setCustomSize(sizePx);
        fab.setMaxImageSize((int) (sizePx * 0.52f));
    }

    private void captureCurrentFabLayouts(CustomizationSettings settings) {
        captureCurrentFabLayout(floatingMealBtn, settings.getMealFabLayout());
        captureCurrentFabLayout(floatingSavedMealsBtn, settings.getSavedMealsFabLayout());
        captureCurrentFabLayout(floatingHelpBtn, settings.getHelpFabLayout());
        captureCurrentFabLayout(floatingFHMBtn, settings.getLinkFabLayout());
    }

    private void captureAppliedFabLayoutsForPersistence(CustomizationSettings settings) {
        captureAppliedFabLayoutForPersistence(floatingMealBtn, settings.getMealFabLayout());
        captureAppliedFabLayoutForPersistence(floatingSavedMealsBtn, settings.getSavedMealsFabLayout());
        captureAppliedFabLayoutForPersistence(floatingHelpBtn, settings.getHelpFabLayout());
        captureAppliedFabLayoutForPersistence(floatingFHMBtn, settings.getLinkFabLayout());
    }

    private void remapFabLayoutsToCurrentFrame(CustomizationSettings settings) {
        remapFabLayoutToCurrentFrame(settings.getMealFabLayout());
        remapFabLayoutToCurrentFrame(settings.getSavedMealsFabLayout());
        remapFabLayoutToCurrentFrame(settings.getHelpFabLayout());
        remapFabLayoutToCurrentFrame(settings.getLinkFabLayout());
    }

    private void remapFabLayoutToCurrentFrame(FabLayoutSettings layoutSettings) {
        int sizeDp = layoutSettings.getSizeDp() > 0 ? layoutSettings.getSizeDp() : DEFAULT_FAB_SIZE_DP;
        int fabSizePx = dpToPx(sizeDp);

        if (layoutSettings.hasCustomPosition()) {
            layoutSettings.setPositionX(remapFabCoordinate(
                    layoutSettings.getPositionX(),
                    layoutSettings.getBaseLayoutWidth(),
                    mainContentFrame.getWidth(),
                    fabSizePx
            ));
            layoutSettings.setPositionY(remapFabCoordinate(
                    layoutSettings.getPositionY(),
                    layoutSettings.getBaseLayoutHeight(),
                    mainContentFrame.getHeight(),
                    fabSizePx
            ));
        }

        layoutSettings.setBaseLayoutWidth(mainContentFrame.getWidth());
        layoutSettings.setBaseLayoutHeight(mainContentFrame.getHeight());
    }

    private void captureCurrentFabLayout(FloatingActionButton fab, FabLayoutSettings layoutSettings) {
        if (fab.getVisibility() != View.VISIBLE || fab.getWidth() <= 0 || fab.getHeight() <= 0) {
            return;
        }
        layoutSettings.setPositionX(fab.getX());
        layoutSettings.setPositionY(fab.getY());
        layoutSettings.setSizeDp(pxToDp(fab.getWidth()));
        layoutSettings.setBaseLayoutWidth(mainContentFrame.getWidth());
        layoutSettings.setBaseLayoutHeight(mainContentFrame.getHeight());
    }

    private void captureAppliedFabLayoutForPersistence(FloatingActionButton fab, FabLayoutSettings layoutSettings) {
        if (fab.getVisibility() != View.VISIBLE) {
            return;
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fab.getLayoutParams();
        boolean usesAbsoluteLayout = (params.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.START
                && (params.gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.TOP;

        if (usesAbsoluteLayout && layoutSettings.hasCustomPosition()) {
            layoutSettings.setPositionX(params.leftMargin);
            layoutSettings.setPositionY(params.topMargin);
        } else {
            captureCurrentFabLayout(fab, layoutSettings);
            return;
        }

        int widthPx = fab.getWidth() > 0 ? fab.getWidth() : params.width;
        if (widthPx > 0) {
            layoutSettings.setSizeDp(pxToDp(widthPx));
        }
        layoutSettings.setBaseLayoutWidth(mainContentFrame.getWidth());
        layoutSettings.setBaseLayoutHeight(mainContentFrame.getHeight());
    }

    private FabLayoutSettings getFabLayoutSettings(FloatingActionButton fab, CustomizationSettings settings) {
        if (fab == floatingMealBtn) {
            return settings.getMealFabLayout();
        }
        if (fab == floatingSavedMealsBtn) {
            return settings.getSavedMealsFabLayout();
        }
        if (fab == floatingHelpBtn) {
            return settings.getHelpFabLayout();
        }
        return settings.getLinkFabLayout();
    }

    private void selectEditableFab(FloatingActionButton fab) {
        selectedEditFab = fab;
        clearFabSelectionState();
    }

    private void clearFabSelectionState() {
        resetFabState(floatingMealBtn);
        resetFabState(floatingSavedMealsBtn);
        resetFabState(floatingHelpBtn);
        resetFabState(floatingFHMBtn);
    }

    private void resetFabState(FloatingActionButton fab) {
        fab.setScaleX(1f);
        fab.setScaleY(1f);
        fab.setAlpha(fab.getVisibility() == View.VISIBLE ? 1f : 0f);
    }

    private FloatingActionButton findFirstVisibleFab() {
        if (floatingMealBtn.getVisibility() == View.VISIBLE) {
            return floatingMealBtn;
        }
        if (floatingSavedMealsBtn.getVisibility() == View.VISIBLE) {
            return floatingSavedMealsBtn;
        }
        if (floatingHelpBtn.getVisibility() == View.VISIBLE) {
            return floatingHelpBtn;
        }
        if (floatingFHMBtn.getVisibility() == View.VISIBLE) {
            return floatingFHMBtn;
        }
        return null;
    }

    private FloatingActionButton findEditableFabAt(float touchX, float touchY) {
        int extraHitAreaPx = dpToPx(FAB_EDIT_HIT_SLOP_DP);

        if (selectedEditFab != null && selectedEditFab.getVisibility() == View.VISIBLE
                && isPointWithinFabArea(selectedEditFab, touchX, touchY, extraHitAreaPx)) {
            return selectedEditFab;
        }
        if (floatingMealBtn.getVisibility() == View.VISIBLE && isPointWithinFabArea(floatingMealBtn, touchX, touchY, extraHitAreaPx)) {
            return floatingMealBtn;
        }
        if (floatingSavedMealsBtn.getVisibility() == View.VISIBLE && isPointWithinFabArea(floatingSavedMealsBtn, touchX, touchY, extraHitAreaPx)) {
            return floatingSavedMealsBtn;
        }
        if (floatingHelpBtn.getVisibility() == View.VISIBLE && isPointWithinFabArea(floatingHelpBtn, touchX, touchY, extraHitAreaPx)) {
            return floatingHelpBtn;
        }
        if (floatingFHMBtn.getVisibility() == View.VISIBLE && isPointWithinFabArea(floatingFHMBtn, touchX, touchY, extraHitAreaPx)) {
            return floatingFHMBtn;
        }
        return null;
    }

    private boolean isPointWithinFabArea(FloatingActionButton fab, float touchX, float touchY, int extraHitAreaPx) {
        float left = fab.getX() - extraHitAreaPx;
        float top = fab.getY() - extraHitAreaPx;
        float right = fab.getX() + fab.getWidth() + extraHitAreaPx;
        float bottom = fab.getY() + fab.getHeight() + extraHitAreaPx;
        return touchX >= left && touchX <= right && touchY >= top && touchY <= bottom;
    }

    private float remapFabCoordinate(float position, int savedLayoutSize, int currentLayoutSize, int fabSizePx) {
        float maxCurrentPosition = Math.max(0f, currentLayoutSize - fabSizePx);

        if (savedLayoutSize <= 0) {
            return clamp(position, 0f, maxCurrentPosition);
        }

        float savedMoveSize = Math.max(1f, savedLayoutSize - fabSizePx);
        float currentMoveSize = Math.max(1f, currentLayoutSize - fabSizePx);
        float remappedPosition = (position / savedMoveSize) * currentMoveSize;
        return clamp(remappedPosition, 0f, maxCurrentPosition);
    }

    private void runAfterMainContentLayoutChange(Runnable action) {
        mainContentFrame.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mainContentFrame.removeOnLayoutChangeListener(this);
                action.run();
            }
        });
        mainContentFrame.requestLayout();
    }

    private void applyAbsoluteFabLayout(FloatingActionButton fab, int leftMargin, int topMargin) {
        FrameLayout.LayoutParams params = copyLayoutParams(fab);
        params.gravity = Gravity.TOP | Gravity.START;
        params.leftMargin = leftMargin;
        params.topMargin = topMargin;
        params.rightMargin = 0;
        params.bottomMargin = 0;
        params.setMarginStart(leftMargin);
        params.setMarginEnd(0);
        fab.setLayoutParams(params);
        fab.setTranslationX(0f);
        fab.setTranslationY(0f);
    }

    private void restoreDefaultFabLayout(FloatingActionButton fab) {
        FrameLayout.LayoutParams params = getDefaultFabLayoutParams(fab);
        fab.setLayoutParams(new FrameLayout.LayoutParams(params));
        fab.setTranslationX(0f);
        fab.setTranslationY(0f);
    }

    private FrameLayout.LayoutParams getDefaultFabLayoutParams(FloatingActionButton fab) {
        if (fab == floatingMealBtn) {
            return defaultMealFabLayoutParams;
        }
        if (fab == floatingSavedMealsBtn) {
            return defaultSavedMealsFabLayoutParams;
        }
        if (fab == floatingHelpBtn) {
            return defaultHelpFabLayoutParams;
        }
        return defaultLinkFabLayoutParams;
    }

    private FrameLayout.LayoutParams copyLayoutParams(FloatingActionButton fab) {
        return new FrameLayout.LayoutParams((FrameLayout.LayoutParams) fab.getLayoutParams());
    }

    private void styleEditModePanels() {
        AppThemeOption theme = customizationHandler.getThemeOption();
        editModeTopBar.setBackgroundColor(theme.getSurfaceColor());
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, Math.max(min, max)));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private int pxToDp(int px) {
        return Math.round(px / getResources().getDisplayMetrics().density);
    }
}
