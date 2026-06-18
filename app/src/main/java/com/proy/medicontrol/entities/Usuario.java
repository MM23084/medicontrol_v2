package com.proy.medicontrol.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nombre;
    private String correo;
    private String password;
    private String rol;
    private String fotoPerfil;
    
    // Campo para la cédula profesional (solo para médicos)
    private String cedulaProfesional;

    public Usuario(String nombre,
                   String correo,
                   String password,
                   String rol,
                   String fotoPerfil,
                   String cedulaProfesional) {

        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.fotoPerfil = fotoPerfil;
        this.cedulaProfesional = cedulaProfesional;
    }

    // ── Getters y Setters ────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getCedulaProfesional() {
        return cedulaProfesional;
    }

    public void setCedulaProfesional(String cedulaProfesional) {
        this.cedulaProfesional = cedulaProfesional;
    }
}
