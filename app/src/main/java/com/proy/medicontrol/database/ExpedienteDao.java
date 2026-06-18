package com.proy.medicontrol.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.proy.medicontrol.entities.Expediente;

import java.util.List;

@Dao
public interface ExpedienteDao {

    @Insert
    void insertar(Expediente expediente);

    @Update
    void actualizar(Expediente expediente);

    @Delete
    void eliminar(Expediente expediente);

    @Query("SELECT * FROM expedientes ORDER BY id DESC")
    List<Expediente> obtenerTodos();

    @Query("SELECT * FROM expedientes WHERE idUsuario = :idUsuario ORDER BY id DESC")
    List<Expediente> obtenerPorPaciente(int idUsuario);

    /**
     * Retorna los expedientes generados por un médico específico.
     */
    @Query("SELECT * FROM expedientes WHERE idMedico = :idMedico ORDER BY id DESC")
    List<Expediente> obtenerPorMedico(int idMedico);

    @Query("SELECT * FROM expedientes WHERE idCita = :idCita LIMIT 1")
    Expediente obtenerPorCita(int idCita);

    @Query("SELECT COUNT(*) FROM expedientes")
    int contarExpedientes();

    /**
     * Cuenta los expedientes creados por un médico específico.
     */
    @Query("SELECT COUNT(*) FROM expedientes WHERE idMedico = :idMedico")
    int contarExpedientesPorMedico(int idMedico);

    @Query("SELECT COUNT(*) FROM expedientes WHERE idCita = :idCita")
    int existeExpedienteParaCita(int idCita);
}
