package com.example.langauage_translator_app.Model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.concurrent.ExecutionException;

public class TranslationTask extends AsyncTask<Void,Void,String> {
    private int fromLanguageCode;
    private int toLanguageCode;
    private String source;
    private TextView translatedTxt;
    private Context context;

    public TranslationTask(int fromLanguageCode, int toLanguageCode, String source, TextView translatedTxt, Context context) {
        this.fromLanguageCode = fromLanguageCode;
        this.toLanguageCode = toLanguageCode;
        this.source = source;
        this.translatedTxt = translatedTxt;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        translatedTxt.setText("Downloading model, Please wait...");
    }

    @Override
    protected String doInBackground(Void... voids) {
        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();

        // Download the model if needed and translate afterward
        try {
            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Start the translation after model download
                    translateAndSetText(translator);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to download model! Please Check your internet connection!"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void translateAndSetText(FirebaseTranslator translator) {
        translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                // Update the UI with the translated text
                translatedTxt.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to translate! Try again: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // This method can be used to handle any post-execution actions if needed
    }
}
