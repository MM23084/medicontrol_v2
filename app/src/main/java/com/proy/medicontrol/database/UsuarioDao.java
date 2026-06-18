package com.proy.medicontrol.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.proy.medicontrol.entities.Usuario;

import java.util.List;

// @Dao marca esta interfaz como DAO de Room. Room genera
// automáticamente la implementación en tiempo de compilación.
@Dao
public interface UsuarioDao {

    // ── Operaciones CRUD básicas ──────────────────────────────
    // Room genera el SQL de INSERT, UPDATE y DELETE
    // automáticamente a partir de estas anotaciones.

    @Insert
    void insertar(Usuario usuario);

    @Update
    void actualizar(Usuario usuario);

    @Delete
    void eliminar(Usuario usuario);

    // ── Consultas de autenticación ────────────────────────────

    /**
     * Busca un usuario solo por correo. Se usa para verificar
     * si el correo ya está registrado antes de crear una cuenta,
     * y también para dar un mensaje de error diferenciado en
     * el login (correo inexistente vs. contraseña incorrecta).
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    Usuario buscarCorreo(String correo);

    /**
     * Consulta de login: retorna el usuario solo si correo Y
     * contraseña coinciden. Si retorna null, el login falló.
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :password LIMIT 1")
    Usuario login(String correo, String password);

    // ── Consultas de búsqueda ─────────────────────────────────

    /** Retorna un usuario por su ID único. */
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    Usuario obtenerPorId(int id);

    /**
     * Cuenta solo los pacientes registrados (rol = "PACIENTE").
     * Se usa en el Dashboard del médico para mostrar el total.
     */
    @Query("SELECT COUNT(*) FROM usuarios WHERE rol = 'PACIENTE'")
    int contarUsuarios();

    /**
     * Retorna la lista de nombres de todos los pacientes,
     * ordenada alfabéticamente. Se usa para poblar los
     * spinners de filtro en Citas y Expedientes.
     */
    @Query("SELECT nombre FROM usuarios WHERE rol = 'PACIENTE' ORDER BY nombre")
    List<String> obtenerNombresPacientes();

    /**
     * Retorna todos los usuarios con un rol específico
     * ("MEDICO" o "PACIENTE"), ordenados por nombre.
     * Se usa en PacientesFragment para listar pacientes.
     */
    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY nombre")
    List<Usuario> obtenerPorRol(String rol);

    /** Retorna solo el nombre de un usuario por su ID. */
    @Query("SELECT nombre FROM usuarios WHERE id = :id LIMIT 1")
    String obtenerNombrePorId(int id);

    /**
     * Busca un paciente por nombre exacto. Se usa al crear
     * un expediente para obtener el objeto Usuario completo
     * a partir del nombre seleccionado en el spinner.
     */
    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    Usuario obtenerPacientePorNombre(String nombre);
}
