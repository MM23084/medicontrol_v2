package com.proy.medicontrol.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.proy.medicontrol.entities.Cita;

import java.util.List;

@Dao
public interface CitaDao {

    @Insert
    void insertar(Cita cita);

    @Update
    void actualizar(Cita cita);

    @Delete
    void eliminar(Cita cita);

    @Query("SELECT * FROM citas ORDER BY fecha DESC")
    List<Cita> obtenerTodas();

    @Query("SELECT * FROM citas WHERE idUsuario = :idUsuario ORDER BY fecha DESC")
    List<Cita> obtenerPorUsuario(int idUsuario);

    @Query("SELECT * FROM citas WHERE idMedico = :idMedico ORDER BY fecha DESC")
    List<Cita> obtenerPorMedico(int idMedico);

    @Query("SELECT COUNT(*) FROM citas WHERE estado = 'PENDIENTE' OR estado = 'ACEPTADA'")
    int contarCitas();

    @Query("SELECT COUNT(*) FROM citas WHERE idMedico = :idMedico AND (estado = 'PENDIENTE' OR estado = 'ACEPTADA')")
    int contarCitasPorMedico(int idMedico);

    @Query("SELECT * FROM citas WHERE idUsuario = :idUsuario AND estado = 'FINALIZADA' ORDER BY fecha DESC")
    List<Cita> obtenerFinalizadasPorUsuario(int idUsuario);

    /**
     * Retorna las citas FINALIZADAS de un paciente que fueron atendidas por un médico específico.
     */
    @Query("SELECT * FROM citas WHERE idUsuario = :idUsuario AND idMedico = :idMedico AND estado = 'FINALIZADA' ORDER BY fecha DESC")
    List<Cita> obtenerFinalizadasPorUsuarioYMedico(int idUsuario, int idMedico);

    @Query("SELECT * FROM citas WHERE idMedico = :idMedico AND fecha = :fecha AND hora = :hora AND estado IN ('PENDIENTE', 'ACEPTADA') LIMIT 1")
    Cita verificarDisponibilidad(int idMedico, String fecha, String hora);
}
