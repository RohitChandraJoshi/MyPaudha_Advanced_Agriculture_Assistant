package com.example.plant_disease_detection;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.plant_disease_detection.ml.PlantDisease;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import android.graphics.Color;
public class MainActivity extends AppCompatActivity {

    TextView result,result2, demoTxt, classified, clickHere, demoTxt2;
    ImageView imageView, arrowImage;
    ImageButton picture;

    int imageSize = 128;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result2 = findViewById(R.id.result2);
        result = findViewById(R.id.result);
        picture = findViewById(R.id.Button);
        imageView = findViewById(R.id.imageview);

        arrowImage = findViewById(R.id.arrow);
        demoTxt = findViewById(R.id.demotext);
        demoTxt2 = findViewById(R.id.demo2text);
        clickHere = findViewById(R.id.click_here);
        classified = findViewById(R.id.classified);


        demoTxt.setVisibility(View.VISIBLE);
        demoTxt2.setVisibility((View.VISIBLE));
        clickHere.setVisibility(View.GONE);
        arrowImage.setVisibility(View.VISIBLE);
        classified.setVisibility(View.GONE);
        result.setVisibility(View.GONE);


        picture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE) ;
                    startActivityForResult(cameraIntent, 1);
                }else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 1 && resultCode == RESULT_OK){
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            demoTxt.setVisibility(View.GONE);
            demoTxt2.setVisibility(View.GONE);
            clickHere.setVisibility(View.VISIBLE);
            arrowImage.setVisibility(View.GONE);
            classified.setVisibility(View.VISIBLE);
            result.setVisibility(View.VISIBLE);
            result2.setVisibility(View.VISIBLE);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize,false);
            classifyImage(image);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void classifyImage(Bitmap image) {
        try {
            PlantDisease model = PlantDisease.newInstance(getApplicationContext());

            //create input for reference
            TensorBuffer inputFeatureO = TensorBuffer.createFixedSize(new int[]{1,128,128,3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());


            //get 1D array of 224*224 pixels in image
            int[] intValue = new int[imageSize * imageSize];
            image.getPixels(intValue, 0, image.getWidth(), 0, 0,image.getWidth(), image.getHeight());

            //iterate over pixels and extract R,G,B  values, add to bytebuffer
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValue[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f/255.f));
                }
            }
            inputFeatureO.loadBuffer(byteBuffer);

            //run model inference and gets result

            PlantDisease.Outputs outputs = model.process(inputFeatureO);
            TensorBuffer outputFeatureO = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeatureO.getFloatArray();

            //find the index...
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidence.length;i++){
                if(confidence[i] > maxConfidence){
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Apple___Apple_scab",
                    "Apple___Black_rot",
                    "Apple___Cedar_apple_rust",
                    "Apple___healthy",
                    "Blueberry___healthy",
                    "Cherry_(including_sour)___Powdery_mildew",
                    "Cherry_(including_sour)___healthy",
                    "Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot",
                    "Corn_(maize)___Common_rust_",
                    "Corn_(maize)___Northern_Leaf_Blight",
                    "Corn_(maize)___healthy",
                    "Grape___Black_rot",
                    "Grape___Esca_(Black_Measles)",
                    "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)",
                    "Grape___healthy",
                    "Orange___Haunglongbing_(Citrus_greening)",
                    "Peach___Bacterial_spot",
                    "Peach___healthy",
                    "Pepper,_bell___Bacterial_spot",
                    "Pepper,_bell___healthy",
                    "Potato___Early_blight",
                    "Potato___Late_blight",
                    "Potato___healthy",
                    "Raspberry___healthy",
                    "Soybean___healthy",
                    "Squash___Powdery_mildew",
                    "Strawberry___Leaf_scorch",
                    "Strawberry___healthy",
                    "Tomato___Bacterial_spot",
                    "Tomato___Early_blight",
                    "Tomato___Late_blight",
                    "Tomato___Leaf_Mold",
                    "Tomato___Septoria_leaf_spot",
                    "Tomato___Spider_mites Two-spotted_spider_mite",
                    "Tomato___Target_Spot",
                    "Tomato___Tomato_Yellow_Leaf_Curl_Virus",
                    "Tomato___Tomato_mosaic_virus",
                    "Tomato___healthy"};

            result2.setText("How to treat : "+classes[maxPos]);
            result2.setTextColor(Color.parseColor("#FFFF00"));
            result.setTextColor(Color.parseColor("#FFFF00"));
            result.setText(classes[maxPos]);
            result2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/search?q="+result2.getText())));
                }
            });

            model.close();




        } catch (IOException e) {

        }

    }
}
