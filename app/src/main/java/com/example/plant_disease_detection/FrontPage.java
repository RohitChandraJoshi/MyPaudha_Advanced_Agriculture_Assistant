package com.example.plant_disease_detection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FrontPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
    }

    public void btn_disease_module(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
    public void btn_soil_module(View view){
        startActivity(new Intent(getApplicationContext(),Soil_Detection_Activity.class));
    }
    public void btn_pest_module(View view){
        startActivity(new Intent(getApplicationContext(),Pest_Detection_Activity.class));
    }
    public void btn_rice_module(View view){
        startActivity(new Intent(getApplicationContext(),nutrient_deficiency_Activity.class));
    }
}