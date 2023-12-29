package com.example.langauage_translator_app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private ImageView arrowBtn;
    private TextView translatedTxt;
    private EditText editText;
    private Button translateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrowBtn = findViewById(R.id.arrowBtn);
        translatedTxt = findViewById(R.id.translatedTxt);
        editText = findViewById(R.id.editText);
        translateBtn = findViewById(R.id.translateBtn);
        arrowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startAnimation();
            }
        });
        translateBtn.setOnClickListener(v -> {
            String textToTranslate = editText.getText().toString().trim();
            translateText(textToTranslate);
        });


    }
    private void startAnimation() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(arrowBtn,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();

    }
    private String translateText(String textToTranslate) {
        String translatedText = "";

        // Translate the text from English to Urdu
//        Translate translate = TranslateOptions.getDefaultInstance().getService();
//        Translation translation = translate.translate(textToTranslate,
//                Translate.TranslateOption.targetLanguage("ur"));
//
//        translatedText = translation.getTranslatedText();
        return translatedText;
    }


}