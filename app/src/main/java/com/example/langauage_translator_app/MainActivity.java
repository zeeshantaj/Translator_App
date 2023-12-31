package com.example.langauage_translator_app;

import static com.example.langauage_translator_app.Model.MyLanguage.getLanguageCode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.langauage_translator_app.Adapter.CustomSpinnerAdapter;
import com.example.langauage_translator_app.Model.TranslationTask;
import com.example.langauage_translator_app.PenView.PenView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.mlkit.vision.text.TextRecognition;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private ImageView arrowBtn;
    private TextView translatedTxt;
    private EditText editText;
    private Button translateBtn;
    private ImageView micBtn,cameraBtn,penBtn;
    private Spinner toSpinner, fromSpinner;

    private String[] fromLanguage = {"from", "English", "Urdu", "Hindi", "Arabic", "French", "Japanese", "Chines", "Italian", "German", "Russian"};
    private String[] toLanguage = {"to", "English", "Urdu", "Hindi", "Arabic", "French", "Japanese", "Chines", "Italian", "German", "Russian"};
    private int[] flagResources = {R.drawable.default_flag,R.drawable.us_flag,R.drawable.pk_flag,R.drawable.india_flag,R.drawable.uae_flag,R.drawable.france_flag,R.drawable.japan_flag
                                    ,R.drawable.china_flag,R.drawable.italy_flag,R.drawable.german_flag,R.drawable.russian_flag};

    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int CAMERA_CAPTURE_CODE = 99;
    int languageCode, fromLanguageCode, toLanguageCode = 0;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private boolean isPenEnabled ;
    private PenView penView;
    private RelativeLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrowBtn = findViewById(R.id.arrowBtn);
       arrowBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startAnimation();
           }
       });

        translatedTxt = findViewById(R.id.translatedTxt);
        editText = findViewById(R.id.editText);
        translateBtn = findViewById(R.id.translateBtn);
        toSpinner = findViewById(R.id.toSpinner);
        fromSpinner = findViewById(R.id.fromSpinner);
        cameraBtn = findViewById(R.id.cameraBtn);
        micBtn = findViewById(R.id.micBtn);
        penBtn = findViewById(R.id.pencilBtn);
        container = findViewById(R.id.penEdRelative);
        penView = findViewById(R.id.drawingView);


        translateBtn.setOnClickListener(v -> {
            translatedTxt.setVisibility(View.VISIBLE);
            translatedTxt.setText("");
            if (editText.getText().toString().isEmpty()){
                Toast.makeText(this, "Please Enter text to translate", Toast.LENGTH_SHORT).show();
            }
            else if (fromLanguageCode == 0){
                Toast.makeText(this, "Please Select language", Toast.LENGTH_SHORT).show();
            }
            else if (toLanguageCode == 0){
                Toast.makeText(this, "Please Select language to translate", Toast.LENGTH_SHORT).show();
            }
            else {
                TranslationTask translationTask = new TranslationTask(fromLanguageCode,toLanguageCode,editText.getText().toString(),translatedTxt,this);
                translationTask.execute();
                //translate(fromLanguageCode,toLanguageCode,editText.getText().toString());
            }

        });

        micBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something to translate");
            try {
                startActivityForResult(intent,REQUEST_PERMISSION_CODE);
            }
            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        cameraBtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Request the camera permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted, proceed to open the camera
                openCamera();
            }
        });
        penBtn.setOnClickListener(v -> {
            if (isPenEnabled){
                Toast.makeText(this, "pen enabled you can draw"+isPenEnabled, Toast.LENGTH_SHORT).show();
                editText.setVisibility(View.GONE);
                penView.setVisibility(View.VISIBLE);
            }
            else {
                editText.setVisibility(View.VISIBLE);
                penView.setVisibility(View.GONE);

            }
            isPenEnabled = !isPenEnabled;
        });

        setSpinner();
    }

    private void setSpinner(){
        CustomSpinnerAdapter toAdapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_dropdown_item, toLanguage, flagResources);
        toAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        CustomSpinnerAdapter fromAdapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_dropdown_item, fromLanguage, flagResources);
        fromAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);


        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguage[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle onNothingSelected if needed
            }
        });
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = getLanguageCode(toLanguage[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle onNothingSelected if needed
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //mic data
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (!editText.getText().toString().isEmpty()) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!result.isEmpty()) {
                    editText.setText(result.get(0));
                }
            }
            else {
                Toast.makeText(this, "Mic Off", Toast.LENGTH_SHORT).show();
            }
        }

        // camera data
        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK && data != null){
            Bundle extras = data.getExtras();
            if (extras != null && extras.containsKey("data")){
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getCloudTextRecognizer();
                textRecognizer.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Toast.makeText(MainActivity.this, "Text"+firebaseVisionText.getText(), Toast.LENGTH_SHORT).show();
                        editText.setText(firebaseVisionText.getText());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to Extract"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("MyApp","Failed->"+e.getLocalizedMessage());
                    }
                });
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed to open the camera
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_CAPTURE_CODE);
        } else {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show();
        }
    }
    private void startAnimation() {

        final long duration = 1000; // Animation duration in milliseconds
        final long startTime = System.currentTimeMillis();
        final float startRotation = arrowBtn.getRotation();
        final float endRotation = startRotation + 360f; // 360 degrees

        // Define a custom animation handler
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = System.currentTimeMillis() - startTime;
                float interpolation = elapsed / (float) duration;
                if (interpolation > 1f) {
                    interpolation = 1f;
                }

                float rotation = startRotation + ((endRotation - startRotation) * interpolation);
                arrowBtn.setRotation(rotation);

                if (interpolation < 1f) {
                    // Continue the animation until it reaches the end
                    handler.postDelayed(this, 16); // Update approximately every 16ms for smoother animation (60 FPS)
                }
            }
        });
    }

}