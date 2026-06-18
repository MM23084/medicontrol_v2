package com.proy.medicontrol.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.proy.medicontrol.entities.Cita;
import com.proy.medicontrol.entities.Expediente;
import com.proy.medicontrol.entities.Usuario;

// Incrementamos la versión a 2 debido al cambio en la entidad Usuario (cédula profesional).
@Database(
        entities = {
                Usuario.class,
                Cita.class,
                Expediente.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();
    public abstract CitaDao citaDao();
    public abstract ExpedienteDao expedienteDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context){

        if (INSTANCE == null){
            synchronized (AppDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "medicontrol_db"
                            )
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
