package com.proy.medicontrol.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "citas")
public class Cita {

    public static final String ESTADO_PENDIENTE  = "PENDIENTE";
    public static final String ESTADO_ACEPTADA   = "ACEPTADA";
    public static final String ESTADO_RECHAZADA  = "RECHAZADA";
    public static final String ESTADO_CANCELADA  = "CANCELADA";
    public static final String ESTADO_FINALIZADA = "FINALIZADA";

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int idUsuario; // ID del Paciente
    private int idMedico;  // ID del Médico seleccionado
    private String nombreMedico; // Nombre del Médico (para visualización rápida)

    private String paciente;
    private String fecha;
    private String hora;
    private String motivo;
    private String estado;

    @Ignore
    public Cita() {
        this.estado = ESTADO_PENDIENTE;
    }

    public Cita(int idUsuario,
                int idMedico,
                String nombreMedico,
                String paciente,
                String fecha,
                String hora,
                String motivo) {

        this.idUsuario = idUsuario;
        this.idMedico = idMedico;
        this.nombreMedico = nombreMedico;
        this.paciente = paciente;
        this.fecha = fecha;
        this.hora = hora;
        this.motivo = motivo;
        this.estado = ESTADO_PENDIENTE;
    }

    // ── Getters y Setters ────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getNombreMedico() {
        return nombreMedico;
    }

    public void setNombreMedico(String nombreMedico) {
        this.nombreMedico = nombreMedico;
    }

    public String getPaciente() {
        return paciente;
    }

    public void setPaciente(String paciente) {
        this.paciente = paciente;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
