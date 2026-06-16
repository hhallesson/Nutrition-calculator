package com.example.nutritioncalculator;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AppStyler {

    private AppStyler() {
    }

    public static void applyActivityStyling(AppCompatActivity activity, CustomizationSettings settings) {
        View content = activity.findViewById(android.R.id.content);
        if (content != null) {
            applyRootBackground(content, settings);
            applyViewStyling(content, settings);
        }

        Window window = activity.getWindow();
        if (window != null) {
            AppThemeOption theme = getTheme(settings);
            window.setStatusBarColor(theme.getPrimaryColor());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.setNavigationBarColor(theme.getBackgroundColor());
            }
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            boolean useDarkIcons = isColorLight(theme.getPrimaryColor());
            controller.setAppearanceLightStatusBars(useDarkIcons);
            controller.setAppearanceLightNavigationBars(isColorLight(theme.getBackgroundColor()));
        }
    }

    public static void applyDialogStyling(AlertDialog dialog, @Nullable View dialogView, CustomizationSettings settings) {
        if (dialogView != null) {
            applyRootBackground(dialogView, settings);
            applyViewStyling(dialogView, settings);
        }

        Window window = dialog.getWindow();
        if (window != null) {
            GradientDrawable background = new GradientDrawable();
            background.setColor(getTheme(settings).getSurfaceColor());
            background.setCornerRadius(36f);
            window.setBackgroundDrawable(background);
        }

        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        styleDialogButton(positive, settings, true);
        styleDialogButton(negative, settings, false);
        styleDialogButton(neutral, settings, false);
    }

    public static void applyViewStyling(View view, CustomizationSettings settings) {
        AppThemeOption theme = getTheme(settings);
        Typeface typeface = getTypeface(settings);

        if (view instanceof Toolbar) {
            Toolbar toolbar = (Toolbar) view;
            int onPrimary = getReadableTextColor(theme.getPrimaryColor());
            toolbar.setBackgroundColor(theme.getPrimaryColor());
            toolbar.setTitleTextColor(onPrimary);
            styleChildViews(toolbar, settings);
            return;
        }

        if (view instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) view;
            int onPrimary = getReadableTextColor(theme.getPrimaryColor());
            fab.setBackgroundTintList(ColorStateList.valueOf(theme.getPrimaryColor()));
            fab.setImageTintList(ColorStateList.valueOf(onPrimary));
            return;
        }

        if (view instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) view;
            checkBox.setTypeface(typeface);
            checkBox.setTextColor(theme.getTextPrimaryColor());
            checkBox.setButtonTintList(ColorStateList.valueOf(theme.getPrimaryColor()));
            return;
        }

        if (view instanceof RadioButton) {
            RadioButton radioButton = (RadioButton) view;
            radioButton.setTypeface(typeface);
            radioButton.setTextColor(theme.getTextPrimaryColor());
            radioButton.setButtonTintList(ColorStateList.valueOf(theme.getPrimaryColor()));
            return;
        }

        if (view instanceof Button) {
            styleActionButton((Button) view, settings);
            return;
        }

        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setTypeface(typeface);
            editText.setTextColor(theme.getTextPrimaryColor());
            editText.setHintTextColor(theme.getTextSecondaryColor());
            editText.setBackground(createRoundedDrawable(theme.getInputBackgroundColor(), theme.getAccentColor(), 28f));
            editText.setPadding(36, 24, 36, 24);
            return;
        }

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTypeface(typeface);
            textView.setTextColor(theme.getTextPrimaryColor());
            return;
        }

        if (view instanceof ImageButton) {
            ImageButton imageButton = (ImageButton) view;
            int iconColor = isInsideToolbar(imageButton)
                    ? getReadableTextColor(theme.getPrimaryColor())
                    : theme.getPrimaryColor();
            imageButton.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            return;
        }

        if (view instanceof ListView || view instanceof TableLayout || view instanceof ScrollView) {
            view.setBackgroundColor(theme.getBackgroundColor());
        }

        if (view instanceof ViewGroup) {
            styleChildViews((ViewGroup) view, settings);
        }
    }

    public static void styleListItemView(View view, CustomizationSettings settings) {
        AppThemeOption theme = getTheme(settings);
        view.setBackground(createRoundedDrawable(theme.getSurfaceColor(), theme.getAccentColor(), 26f));
        applyViewStyling(view, settings);
    }

    public static AppThemeOption getTheme(CustomizationSettings settings) {
        return AppThemeOption.fromId(settings != null ? settings.getSelectedColorTheme() : null);
    }

    public static Typeface getTypeface(CustomizationSettings settings) {
        return AppFontOption.fromId(settings != null ? settings.getSelectedFont() : null).toTypeface();
    }

    public static void applyRootBackground(View view, CustomizationSettings settings) {
        view.setBackgroundColor(getTheme(settings).getBackgroundColor());
    }

    private static void styleActionButton(Button button, CustomizationSettings settings) {
        AppThemeOption theme = getTheme(settings);
        button.setTypeface(getTypeface(settings));
        button.setTextColor(getReadableTextColor(theme.getPrimaryColor()));
        button.setBackground(createRoundedDrawable(theme.getPrimaryColor(), theme.getPrimaryColor(), 30f));
        ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(theme.getPrimaryColor()));
    }

    private static void styleDialogButton(@Nullable Button button, CustomizationSettings settings, boolean emphasized) {
        if (button == null) {
            return;
        }

        AppThemeOption theme = getTheme(settings);
        button.setTypeface(getTypeface(settings));

        if (emphasized) {
            button.setTextColor(getReadableTextColor(theme.getPrimaryColor()));
            button.setBackground(createRoundedDrawable(theme.getPrimaryColor(), theme.getPrimaryColor(), 24f));
            ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(theme.getPrimaryColor()));
        } else {
            button.setTextColor(theme.getPrimaryColor());
            ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(Color.TRANSPARENT));
        }
    }

    private static void styleChildViews(ViewGroup group, CustomizationSettings settings) {
        for (int i = 0; i < group.getChildCount(); i++) {
            applyViewStyling(group.getChildAt(i), settings);
        }
    }

    private static GradientDrawable createRoundedDrawable(int fillColor, int strokeColor, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(radius);
        drawable.setStroke(2, strokeColor);
        return drawable;
    }

    private static int getReadableTextColor(int backgroundColor) {
        return isColorLight(backgroundColor) ? Color.BLACK : Color.WHITE;
    }

    private static boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.35;
    }

    private static boolean isInsideToolbar(View view) {
        ViewParent parent = view.getParent();

        while (parent instanceof View) {
            if (parent instanceof Toolbar) {
                return true;
            }
            parent = parent.getParent();
        }

        return false;
    }
}
