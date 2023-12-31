package com.example.langauage_translator_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

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

    private static final int REQUEST_PERMISSION_CODE = 1;
    int languageCode, fromLanguageCode, toLanguageCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrowBtn = findViewById(R.id.arrowBtn);
        translatedTxt = findViewById(R.id.translatedTxt);
        editText = findViewById(R.id.editText);
        translateBtn = findViewById(R.id.translateBtn);
        toSpinner = findViewById(R.id.toSpinner);
        fromSpinner = findViewById(R.id.fromSpinner);
        cameraBtn = findViewById(R.id.cameraBtn);
        micBtn = findViewById(R.id.micBtn);
        penBtn = findViewById(R.id.pencilBtn);


        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
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
                translate(fromLanguageCode,toLanguageCode,editText.getText().toString());
            }

        });
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode = getLanguageCode(toLanguage[position]);
                Toast.makeText(MainActivity.this, "to clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter toAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode = getLanguageCode(fromLanguage[position]);
                Toast.makeText(MainActivity.this, "from clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter fromAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

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

    }

    private void translate(int fromLanguageCode,int toLanguageCode,String source) {
        translatedTxt.setText("Downloading model, Please wait...");
        FirebaseTranslatorOptions   options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedTxt.setText("Translation...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTxt.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to translate! try again"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to download model! Please Check your internet connection!"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_CODE){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            editText.setText(result.get(0));
        }
    }

    //"English","Urdu","Hindi","Arabic","French","Japanese","Chines","Italian","German","Russian"
    private int getLanguageCode(String language) {
        int languageCode = 0;
        switch (language) {
            case "English":
                languageCode = FirebaseTranslateLanguage.EN;
                break;
            case "Urdu":
                languageCode = FirebaseTranslateLanguage.UR;
                break;
            case "Hindi":
                languageCode = FirebaseTranslateLanguage.HI;
                break;
            case "Arabic":
                languageCode = FirebaseTranslateLanguage.AR;
                break;
            case "French":
                languageCode = FirebaseTranslateLanguage.FR;
                break;
            case "Japanese":
                languageCode = FirebaseTranslateLanguage.JA;
                break;
            case "Chines":
                languageCode = FirebaseTranslateLanguage.CY;
                break;
            case "Italian":
                languageCode = FirebaseTranslateLanguage.IT;
                break;
            case "German":
                languageCode = FirebaseTranslateLanguage.GA;
                break;
            case "Russian":
                languageCode = FirebaseTranslateLanguage.RU;
                break;
            default:
                languageCode = 0;

        }
        return languageCode;

    }

    private void startAnimation() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(arrowBtn, "rotation", 0f, 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }

}