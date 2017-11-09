package com.labz.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.labz.player.models.Audio;

import java.util.ArrayList;

public class PreferencesHelper {

    private static final String SHARED_PREFERENCES_NAME = "labs";
    private static PreferencesHelper preference;

    private static final String THEME_TYPE = "theme_type";
    private static final String TEXT_TYPE = "text_type";
    private static final String AUDIO_INDEX = "audio_index";
    private static final String AUDIOS_LIST = "audios_list";

    private SharedPreferences sharedPreferences;

    public void clearPrefernces() {
        sharedPreferences.edit().clear().apply();
    }

    public static PreferencesHelper getInstance(Context context) {
        if (preference == null) {
            preference = new PreferencesHelper(context);
        }
        return preference;
    }

    private PreferencesHelper() {
    }

    private PreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getThemeType() {
        return this.sharedPreferences.getString(THEME_TYPE, "Dark theme");
    }

    public void saveThemeType(String theme) {
        this.sharedPreferences.edit().putString(THEME_TYPE, theme).apply();
    }

    public String getTextType() {
        return this.sharedPreferences.getString(TEXT_TYPE, "Regular");
    }

    public void saveTexType(String theme) {
        this.sharedPreferences.edit().putString(TEXT_TYPE, theme).apply();
    }

    public ArrayList<Audio> getCachedAudios() {
        String listJson = this.sharedPreferences.getString(AUDIOS_LIST, null);
        return new Gson().fromJson(listJson,
                new TypeToken<ArrayList<Audio>>() {
                }.getType());
    }

    public String getCachedAudiosStr() {
        return this.sharedPreferences.getString(AUDIOS_LIST, "BAAAM");
    }

    public void saveCachedAudios(ArrayList<Audio> list) {
        String listJson = new Gson().toJson(list);
        this.sharedPreferences.edit().putString(AUDIOS_LIST, listJson).apply();
    }

    public int getPlayingIndex() {
        return this.sharedPreferences.getInt(AUDIO_INDEX, -1);
    }

    public void savePlayingIndex(int index) {
        this.sharedPreferences.edit().putInt(AUDIO_INDEX, index).apply();
    }

}
