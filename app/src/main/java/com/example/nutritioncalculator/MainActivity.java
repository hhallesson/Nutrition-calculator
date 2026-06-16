package com.example.nutritioncalculator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_CUSTOMIZATION_ENABLED = "com.example.uppsatsapp.EXTRA_CUSTOMIZATION_ENABLED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomizationSettings settings = DataHandler.loadCustomizationSettings(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_view);
        AppStyler.applyActivityStyling(this, settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button loginButton = findViewById(R.id.loginButton);
        RadioGroup versionRadioGroup = findViewById(R.id.versionRadioGroup);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainViewActivity.class);
            boolean customizationEnabled = versionRadioGroup.getCheckedRadioButtonId() != R.id.versionARadio;
            intent.putExtra(EXTRA_CUSTOMIZATION_ENABLED, customizationEnabled);
            startActivity(intent);
        });
    }

}
