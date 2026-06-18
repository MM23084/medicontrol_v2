package com.proy.medicontrol.utils;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    // Nombre del archivo de preferencias donde se guarda la config.
    private static final String PREF      = "config";
    // Clave bajo la que se almacena el modo de tema elegido.
    private static final String KEY_THEME = "theme_mode";

    public static void guardarTema(Context context, int mode) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        // Persiste la preferencia para que se recuerde al reabrir la app.
        prefs.edit()
                .putInt(KEY_THEME, mode)
                .apply();

        // Aplica el cambio de tema de forma inmediata sin reiniciar la app.
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public static void aplicarTema(Context context) {

        AppCompatDelegate.setDefaultNightMode(
                obtenerTema(context)
        );
    }


    public static int obtenerTema(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        return prefs.getInt(
                KEY_THEME,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        );
    }
}
