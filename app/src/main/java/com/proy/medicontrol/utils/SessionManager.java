package com.proy.medicontrol.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "sesion";

    private static final String KEY_LOGUEADO = "logueado";
    private static final String KEY_ID       = "idUsuario";
    private static final String KEY_USUARIO  = "usuario";
    private static final String KEY_ROL      = "rol";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {

        prefs = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        editor = prefs.edit();
    }

    public void guardarSesion(
            int idUsuario,
            String usuario,
            String rol
    ) {
        editor.putBoolean(KEY_LOGUEADO, true);
        editor.putInt(KEY_ID, idUsuario);
        editor.putString(KEY_USUARIO, usuario);
        editor.putString(KEY_ROL, rol);
        editor.apply();
    }


    public boolean estaLogueado() {
        return prefs.getBoolean(KEY_LOGUEADO, false);
    }


    public String obtenerUsuario() {
        return prefs.getString(KEY_USUARIO, "Invitado");
    }

    public int obtenerIdUsuario() {
        return prefs.getInt(KEY_ID, -1);
    }

    public String obtenerRol() {
        return prefs.getString(KEY_ROL, "");
    }

    public String obtenerNombreCompleto() {
        return prefs.getString(KEY_USUARIO, "Invitado");
    }


    public boolean esMedico() {
        return "MEDICO".equalsIgnoreCase(obtenerRol());
    }

    public boolean esPaciente() {
        return "PACIENTE".equalsIgnoreCase(obtenerRol());
    }

    public void cerrarSesion() {
        editor.clear();
        editor.apply();
    }
}
