package com.example.nutritioncalculator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CustomizationFragment extends Fragment {

    public interface OnCustomizationChangedListener {
        void onCustomizationChanged(CustomizationSettings settings);
        void onCloseCustomizationMenuRequested();
        void onEnterStructureEditModeRequested();
    }

    private CustomizationSettings settings;

    private CheckBox mainCheckLinkFAB;
    private CheckBox mainCheckHelpFAB;
    private CheckBox mainCheckSavedMealsFAB;
    private RadioButton mainRadioShowExpandList;
    private RadioButton mainRadioShowSearchList;
    private RadioButton mainRadioShowBothList;

    private CheckBox mealPlannerCheckCalories;
    private CheckBox mealPlannerCheckProtein;
    private CheckBox mealPlannerCheckCarbs;
    private CheckBox mealPlannerCheckFat;
    private CheckBox mealPlannerCheckUnit;
    private CheckBox mealPlannerCheckDetails;

    private CheckBox helpCheckCalories;
    private CheckBox helpCheckProtein;
    private CheckBox helpCheckCarbs;
    private CheckBox helpCheckFat;
    private RadioButton helpRadioGenderMan;
    private RadioButton helpRadioGenderWoman;
    private RadioButton helpRadioGenderBoth;
    private Button visualFontBtn;
    private Button visualColorThemeBtn;
    private Button structureEditBtn;
    private Button resetSettingsBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customization_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settings = DataHandler.loadCustomizationSettings(requireContext());

        ImageButton returnCustomizationBtn = view.findViewById(R.id.returnCustomizationBtn);
        RadioGroup mainRadioShowLists = view.findViewById(R.id.mainRadioShowLists);
        RadioGroup helpRadioGender = view.findViewById(R.id.helpRadioGender);

        mainCheckLinkFAB = view.findViewById(R.id.mainCheckLinkFAB);
        mainCheckHelpFAB = view.findViewById(R.id.mainCheckHelpFAB);
        mainCheckSavedMealsFAB = view.findViewById(R.id.mainCheckSavedMealsFAB);
        mainRadioShowExpandList = view.findViewById(R.id.mainRadioShowExpandList);
        mainRadioShowSearchList = view.findViewById(R.id.mainRadioShowSearchList);
        mainRadioShowBothList = view.findViewById(R.id.mainRadioShowBothList);

        mealPlannerCheckCalories = view.findViewById(R.id.mealPlannerCheckCalories);
        mealPlannerCheckProtein = view.findViewById(R.id.mealPlannerCheckProtein);
        mealPlannerCheckCarbs = view.findViewById(R.id.mealPlannerCheckCarbs);
        mealPlannerCheckFat = view.findViewById(R.id.mealPlannerCheckFat);
        mealPlannerCheckUnit = view.findViewById(R.id.mealPlannerCheckUnit);
        mealPlannerCheckDetails = view.findViewById(R.id.mealPlannerCheckDetails);

        helpCheckCalories = view.findViewById(R.id.helpCheckCalories);
        helpCheckProtein = view.findViewById(R.id.helpCheckProtein);
        helpCheckCarbs = view.findViewById(R.id.helpCheckCarbs);
        helpCheckFat = view.findViewById(R.id.helpCheckFat);
        helpRadioGenderMan = view.findViewById(R.id.helpRadioGenderMan);
        helpRadioGenderWoman = view.findViewById(R.id.helpRadioGenderWoman);
        helpRadioGenderBoth = view.findViewById(R.id.helpRadioGenderBoth);
        visualFontBtn = view.findViewById(R.id.visualFontBtn);
        visualColorThemeBtn = view.findViewById(R.id.visualColorThemeBtn);
        structureEditBtn = view.findViewById(R.id.structureEditBtn);
        resetSettingsBtn = view.findViewById(R.id.resetSettingsBtn);

        loadCurrentSettings();
        setupChangeListeners(mainRadioShowLists, helpRadioGender);
        setupVisualButtons();
        AppStyler.applyViewStyling(view, settings);

        returnCustomizationBtn.setOnClickListener(v -> {
            if (getActivity() instanceof OnCustomizationChangedListener) {
                ((OnCustomizationChangedListener) getActivity()).onCloseCustomizationMenuRequested();
            }
        });
    }

    private void loadCurrentSettings() {
        mainCheckLinkFAB.setChecked(settings.isShowLinkFab());
        mainCheckHelpFAB.setChecked(settings.isShowHelpFab());
        mainCheckSavedMealsFAB.setChecked(settings.isShowSavedMealsFab());

        if (CustomizationSettings.LIST_MODE_EXPANDABLE.equals(settings.getMainListMode())) {
            mainRadioShowExpandList.setChecked(true);
        } else if (CustomizationSettings.LIST_MODE_SEARCH.equals(settings.getMainListMode())) {
            mainRadioShowSearchList.setChecked(true);
        } else {
            mainRadioShowBothList.setChecked(true);
        }

        mealPlannerCheckCalories.setChecked(settings.isShowMealPlannerCalories());
        mealPlannerCheckProtein.setChecked(settings.isShowMealPlannerProtein());
        mealPlannerCheckCarbs.setChecked(settings.isShowMealPlannerCarbs());
        mealPlannerCheckFat.setChecked(settings.isShowMealPlannerFat());
        mealPlannerCheckUnit.setChecked(settings.isShowMealPlannerUnit());
        mealPlannerCheckDetails.setChecked(settings.isShowMealPlannerDetails());

        helpCheckCalories.setChecked(settings.isShowHelpCalories());
        helpCheckProtein.setChecked(settings.isShowHelpProtein());
        helpCheckCarbs.setChecked(settings.isShowHelpCarbs());
        helpCheckFat.setChecked(settings.isShowHelpFat());

        if (CustomizationSettings.GENDER_MODE_MAN.equals(settings.getHelpGenderMode())) {
            helpRadioGenderMan.setChecked(true);
        } else if (CustomizationSettings.GENDER_MODE_WOMAN.equals(settings.getHelpGenderMode())) {
            helpRadioGenderWoman.setChecked(true);
        } else {
            helpRadioGenderBoth.setChecked(true);
        }
    }

    private void setupChangeListeners(RadioGroup mainRadioShowLists, RadioGroup helpRadioGender) {
        View.OnClickListener checkboxListener = v -> saveCurrentSettings();

        mainCheckLinkFAB.setOnClickListener(checkboxListener);
        mainCheckHelpFAB.setOnClickListener(checkboxListener);
        mainCheckSavedMealsFAB.setOnClickListener(checkboxListener);
        mealPlannerCheckCalories.setOnClickListener(checkboxListener);
        mealPlannerCheckProtein.setOnClickListener(checkboxListener);
        mealPlannerCheckCarbs.setOnClickListener(checkboxListener);
        mealPlannerCheckFat.setOnClickListener(checkboxListener);
        mealPlannerCheckUnit.setOnClickListener(checkboxListener);
        mealPlannerCheckDetails.setOnClickListener(checkboxListener);
        helpCheckCalories.setOnClickListener(checkboxListener);
        helpCheckProtein.setOnClickListener(checkboxListener);
        helpCheckCarbs.setOnClickListener(checkboxListener);
        helpCheckFat.setOnClickListener(checkboxListener);

        mainRadioShowLists.setOnCheckedChangeListener((group, checkedId) -> saveCurrentSettings());
        helpRadioGender.setOnCheckedChangeListener((group, checkedId) -> saveCurrentSettings());
    }

    private void setupVisualButtons() {
        updateVisualButtonLabels();
        visualFontBtn.setOnClickListener(v -> showFontDialog());
        visualColorThemeBtn.setOnClickListener(v -> showColorThemeDialog());
        resetSettingsBtn.setOnClickListener(v -> showResetConfirmationDialog());
        structureEditBtn.setOnClickListener(v -> {
            if (getActivity() instanceof OnCustomizationChangedListener) {
                ((OnCustomizationChangedListener) getActivity()).onEnterStructureEditModeRequested();
            }
        });
    }

    private void updateVisualButtonLabels() {
        visualFontBtn.setText(AppFontOption.fromId(settings.getSelectedFont()).getDisplayName());
        visualColorThemeBtn.setText(AppThemeOption.fromId(settings.getSelectedColorTheme()).getDisplayName());
    }

    private void saveCurrentSettings() {
        settings.setShowLinkFab(mainCheckLinkFAB.isChecked());
        settings.setShowHelpFab(mainCheckHelpFAB.isChecked());
        settings.setShowSavedMealsFab(mainCheckSavedMealsFAB.isChecked());

        if (mainRadioShowExpandList.isChecked()) {
            settings.setMainListMode(CustomizationSettings.LIST_MODE_EXPANDABLE);
        } else if (mainRadioShowSearchList.isChecked()) {
            settings.setMainListMode(CustomizationSettings.LIST_MODE_SEARCH);
        } else {
            settings.setMainListMode(CustomizationSettings.LIST_MODE_BOTH);
        }

        settings.setShowMealPlannerCalories(mealPlannerCheckCalories.isChecked());
        settings.setShowMealPlannerProtein(mealPlannerCheckProtein.isChecked());
        settings.setShowMealPlannerCarbs(mealPlannerCheckCarbs.isChecked());
        settings.setShowMealPlannerFat(mealPlannerCheckFat.isChecked());
        settings.setShowMealPlannerUnit(mealPlannerCheckUnit.isChecked());
        settings.setShowMealPlannerDetails(mealPlannerCheckDetails.isChecked());

        settings.setShowHelpCalories(helpCheckCalories.isChecked());
        settings.setShowHelpProtein(helpCheckProtein.isChecked());
        settings.setShowHelpCarbs(helpCheckCarbs.isChecked());
        settings.setShowHelpFat(helpCheckFat.isChecked());

        if (helpRadioGenderMan.isChecked()) {
            settings.setHelpGenderMode(CustomizationSettings.GENDER_MODE_MAN);
        } else if (helpRadioGenderWoman.isChecked()) {
            settings.setHelpGenderMode(CustomizationSettings.GENDER_MODE_WOMAN);
        } else {
            settings.setHelpGenderMode(CustomizationSettings.GENDER_MODE_BOTH);
        }

        DataHandler.saveCustomizationSettings(requireContext(), settings);
        updateVisualButtonLabels();
        AppStyler.applyViewStyling(requireView(), settings);

        if (getActivity() instanceof OnCustomizationChangedListener) {
            ((OnCustomizationChangedListener) getActivity()).onCustomizationChanged(settings);
        }
    }

    private void showFontDialog() {
        LinearLayout container = createDialogContainer();
        TextView title = createDialogTitle("Välj typsnitt");
        TextView subtitle = createDialogSubtitle("Varje alternativ visar en kort förhandsvisning av texten.");
        LinearLayout optionsContainer = new LinearLayout(requireContext());
        optionsContainer.setOrientation(LinearLayout.VERTICAL);

        final String[] selectedFontId = {settings.getSelectedFont()};
        List<RadioButton> radioButtons = new ArrayList<>();
        List<TextView> previewTexts = new ArrayList<>();

        for (AppFontOption option : AppFontOption.getOptions()) {
            LinearLayout optionLayout = new LinearLayout(requireContext());
            optionLayout.setOrientation(LinearLayout.VERTICAL);
            optionLayout.setPadding(24, 20, 24, 20);

            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(option.getDisplayName());
            radioButton.setTag(option.getId());
            radioButton.setChecked(option.getId().equals(selectedFontId[0]));
            radioButtons.add(radioButton);

            TextView previewText = new TextView(requireContext());
            previewText.setText(option.getPreviewText());
            previewText.setTextSize(16);
            previewText.setPadding(72, 8, 0, 0);
            previewTexts.add(previewText);

            View.OnClickListener optionSelectListener = v -> {
                updateSingleChoiceSelection(radioButtons, radioButton);
                selectedFontId[0] = option.getId();
            };

            optionLayout.setOnClickListener(optionSelectListener);
            radioButton.setOnClickListener(optionSelectListener);

            optionLayout.addView(radioButton);
            optionLayout.addView(previewText);
            optionsContainer.addView(optionLayout);
        }

        container.addView(title);
        container.addView(subtitle);
        container.addView(optionsContainer);
        LinearLayout footer = createDialogFooter();
        Button closeButton = createSecondaryDialogButton("Stäng");
        Button applyButton = createPrimaryDialogButton("Använd typsnitt");
        footer.addView(closeButton);
        footer.addView(applyButton);
        container.addView(footer);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(wrapInScrollView(container))
                .create();

        dialog.show();
        AppStyler.applyDialogStyling(dialog, container, settings);
        reapplyFontPreviewTypefaces(previewTexts);
        closeButton.setOnClickListener(v -> dialog.dismiss());
        applyButton.setOnClickListener(v -> {
            settings.setSelectedFont(selectedFontId[0]);
            saveCurrentSettings();
            dialog.dismiss();
        });
    }

    private void showColorThemeDialog() {
        LinearLayout container = createDialogContainer();
        TextView title = createDialogTitle("Välj färgtema");
        TextView subtitle = createDialogSubtitle("Temat påverkar bakgrund, knappar, listor och dialogrutor.");
        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setOrientation(LinearLayout.VERTICAL);

        final String[] selectedThemeId = {settings.getSelectedColorTheme()};

        for (AppThemeOption option : AppThemeOption.getOptions()) {
            LinearLayout optionLayout = new LinearLayout(requireContext());
            optionLayout.setOrientation(LinearLayout.VERTICAL);
            optionLayout.setPadding(24, 20, 24, 20);

            RadioButton radioButton = new RadioButton(requireContext());
            radioButton.setText(option.getDisplayName());
            radioButton.setTag(option.getId());
            radioButton.setChecked(option.getId().equals(selectedThemeId[0]));

            LinearLayout previewRow = new LinearLayout(requireContext());
            previewRow.setOrientation(LinearLayout.HORIZONTAL);
            previewRow.setGravity(Gravity.CENTER_VERTICAL);
            previewRow.setPadding(72, 12, 0, 0);

            previewRow.addView(createColorSwatch(option.getPrimaryColor()));
            previewRow.addView(createSpacingView(16));
            previewRow.addView(createColorSwatch(option.getAccentColor()));
            previewRow.addView(createSpacingView(16));
            previewRow.addView(createColorSwatch(option.getBackgroundColor()));

            optionLayout.setOnClickListener(v -> {
                radioButton.setChecked(true);
                selectedThemeId[0] = option.getId();
            });

            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedThemeId[0] = option.getId();
                }
            });

            optionLayout.addView(radioButton);
            optionLayout.addView(previewRow);
            radioGroup.addView(optionLayout);
        }

        container.addView(title);
        container.addView(subtitle);
        container.addView(radioGroup);
        LinearLayout footer = createDialogFooter();
        Button closeButton = createSecondaryDialogButton("Stäng");
        Button applyButton = createPrimaryDialogButton("Använd tema");
        footer.addView(closeButton);
        footer.addView(applyButton);
        container.addView(footer);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(wrapInScrollView(container))
                .create();

        dialog.show();
        AppStyler.applyDialogStyling(dialog, container, settings);
        closeButton.setOnClickListener(v -> dialog.dismiss());
        applyButton.setOnClickListener(v -> {
            settings.setSelectedColorTheme(selectedThemeId[0]);
            saveCurrentSettings();
            dialog.dismiss();
        });
    }

    private void showResetConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Återställ inställningar")
                .setMessage("Vill du återställa alla anpassningar? Typsnitt och färgtema sätts till Standard.")
                .setPositiveButton("Ja", (dialogInterface, which) -> resetCustomizationSettings())
                .setNegativeButton("Nej", (dialogInterface, which) -> dialogInterface.dismiss())
                .create();

        dialog.show();
        AppStyler.applyDialogStyling(dialog, null, settings);
    }

    private void resetCustomizationSettings() {
        DataHandler.clearCustomizationSettings(requireContext());
        settings = CustomizationSettings.defaultSettings();
        loadCurrentSettings();
        updateVisualButtonLabels();
        AppStyler.applyViewStyling(requireView(), settings);

        if (getActivity() instanceof OnCustomizationChangedListener) {
            ((OnCustomizationChangedListener) getActivity()).onCustomizationChanged(settings);
        }
    }

    private LinearLayout createDialogContainer() {
        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(36, 36, 36, 24);
        return container;
    }

    private TextView createDialogTitle(String text) {
        TextView title = new TextView(requireContext());
        title.setText(text);
        title.setTextSize(22);
        title.setTypeface(AppStyler.getTypeface(settings), android.graphics.Typeface.BOLD);
        return title;
    }

    private TextView createDialogSubtitle(String text) {
        TextView subtitle = new TextView(requireContext());
        subtitle.setText(text);
        subtitle.setTextSize(15);
        subtitle.setPadding(0, 12, 0, 24);
        return subtitle;
    }

    private View wrapInScrollView(View content) {
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.addView(content);
        return scrollView;
    }

    private View createColorSwatch(int color) {
        View swatch = new View(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(52, 52);
        swatch.setLayoutParams(params);
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(14f);
        drawable.setStroke(2, AppStyler.getTheme(settings).getTextSecondaryColor());
        swatch.setBackground(drawable);
        return swatch;
    }

    private View createSpacingView(int width) {
        View spacing = new View(requireContext());
        spacing.setLayoutParams(new LinearLayout.LayoutParams(width, 1));
        return spacing;
    }

    private LinearLayout createDialogFooter() {
        LinearLayout footer = new LinearLayout(requireContext());
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setGravity(Gravity.END);
        footer.setPadding(0, 28, 0, 0);
        return footer;
    }

    private Button createPrimaryDialogButton(String text) {
        Button button = new Button(requireContext());
        button.setText(text);
        button.setAllCaps(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMarginStart(20);
        button.setLayoutParams(params);
        return button;
    }

    private Button createSecondaryDialogButton(String text) {
        Button button = createPrimaryDialogButton(text);
        AppThemeOption theme = AppStyler.getTheme(settings);
        button.setTextColor(theme.getPrimaryColor());
        button.setBackgroundColor(Color.TRANSPARENT);
        return button;
    }

    private void updateSingleChoiceSelection(List<RadioButton> radioButtons, RadioButton selectedButton) {
        for (RadioButton button : radioButtons) {
            button.setChecked(button == selectedButton);
        }
    }

    private void reapplyFontPreviewTypefaces(List<TextView> previewTexts) {
        List<AppFontOption> fontOptions = AppFontOption.getOptions();
        int count = Math.min(previewTexts.size(), fontOptions.size());
        for (int i = 0; i < count; i++) {
            previewTexts.get(i).setTypeface(fontOptions.get(i).toTypeface());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            AppStyler.applyViewStyling(getView(), settings);
        }
    }
}
