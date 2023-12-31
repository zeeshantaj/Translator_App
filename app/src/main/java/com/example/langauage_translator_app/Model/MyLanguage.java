package com.example.langauage_translator_app.Model;

import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;

public class MyLanguage {

    public static int getLanguageCode(String language) {
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
}
