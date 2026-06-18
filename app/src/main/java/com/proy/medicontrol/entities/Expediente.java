package com.proy.medicontrol.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expedientes")
public class Expediente {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int idUsuario; // ID del Paciente
    private int idCita;    // ID de la Cita
    private int idMedico;  // ID del Médico que generó el expediente

    private String diagnostico;
    private String tratamiento;
    private String medicamentos;
    private String observaciones;
    private String fecha;

    public Expediente(
            int idUsuario,
            int idCita,
            int idMedico,
            String diagnostico,
            String tratamiento,
            String medicamentos,
            String observaciones,
            String fecha) {

        this.idUsuario = idUsuario;
        this.idCita = idCita;
        this.idMedico = idMedico;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.medicamentos = medicamentos;
        this.observaciones = observaciones;
        this.fecha = fecha;
    }

    // ── Getters y Setters ────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public int getIdMedico() { return idMedico; }
    public void setIdMedico(int idMedico) { this.idMedico = idMedico; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public String getMedicamentos() { return medicamentos; }
    public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
