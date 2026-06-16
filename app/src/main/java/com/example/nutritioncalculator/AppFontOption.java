package com.example.nutritioncalculator;

import android.graphics.Typeface;

import java.util.Arrays;
import java.util.List;

public class AppFontOption {

    private static final List<AppFontOption> OPTIONS = Arrays.asList(
            new AppFontOption(CustomizationSettings.FONT_DEFAULT, "Standard", "sans-serif", "Snabb preview: Havregryn och bär"),
            new AppFontOption(CustomizationSettings.FONT_SERIF, "Serif", "serif", "Snabb preview: Havregryn och bär"),
            new AppFontOption(CustomizationSettings.FONT_MONOSPACE, "Monospace", "monospace", "Snabb preview: Havregryn och bär"),
            new AppFontOption(CustomizationSettings.FONT_CONDENSED, "Condensed", "sans-serif-condensed", "Snabb preview: Havregryn och bär")
    );

    private final String id;
    private final String displayName;
    private final String fontFamilyName;
    private final String previewText;

    public AppFontOption(String id, String displayName, String fontFamilyName, String previewText) {
        this.id = id;
        this.displayName = displayName;
        this.fontFamilyName = fontFamilyName;
        this.previewText = previewText;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPreviewText() {
        return previewText;
    }

    public Typeface toTypeface() {
        return Typeface.create(fontFamilyName, Typeface.NORMAL);
    }

    public static List<AppFontOption> getOptions() {
        return OPTIONS;
    }

    public static AppFontOption fromId(String id) {
        for (AppFontOption option : OPTIONS) {
            if (option.id.equals(id)) {
                return option;
            }
        }
        return OPTIONS.get(0);
    }
}
