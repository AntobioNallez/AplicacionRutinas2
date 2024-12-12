package com.example.aplicacionrutinas.BaseDeDatos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aplicacionrutinas.Modelo.Rutina;

import java.util.ArrayList;
import java.util.List;

public class BaseDeDatosHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rutinas.db"; // Nombre correcto de la base de datos
    private static final int DATABASE_VERSION = 1;
    private static final String RUTINAS_TABLE = "rutina";
    private static final String RUTINA_ID = "id";
    private static final String RUTINA_NOMBRE = "nombre";
    private static final String RUTINA_STATUS = "status";
    private static final String RUTINA_HORA = "hora";
    private static final String RUTINA_DIA = "dia";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + RUTINAS_TABLE + "(" + RUTINA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RUTINA_NOMBRE + " TEXT, " + RUTINA_STATUS + " INTEGER, " + RUTINA_HORA + " TEXT, " + RUTINA_DIA + " TEXT)";

    private SQLiteDatabase db;

    public BaseDeDatosHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // Nombre correcto de la base de datos
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY); // Usa el objeto pasado como argumento
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RUTINAS_TABLE);
        onCreate(sqLiteDatabase); // Usa el objeto pasado como argumento
    }

    /**
     * Inicializa la base de datos para poder escribir
     */
    public void abrirBaseDeDatos() {
        db = this.getWritableDatabase(); // Inicializa la base de datos
    }

    /**
     * Inserta una nueva rutina
     * @param rutina Rutina nueva a ingresar
     */
    public void insertarRutina(Rutina rutina) {
        ContentValues values = new ContentValues();
        values.put(RUTINA_NOMBRE, rutina.getRutina());
        values.put(RUTINA_STATUS, rutina.getStatus());
        values.put(RUTINA_HORA, rutina.getHora());
        values.put(RUTINA_DIA, rutina.getDia());
        db.insert(RUTINAS_TABLE, null, values);
    }

    /**
     * Devuelve todas las rutinas
     * @return List que contiene todas las rutinas
     */
    @SuppressLint("Range")
    public List<Rutina> obtenerRutinas() {
        List<Rutina> rutinas = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query(RUTINAS_TABLE, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Rutina rutina = new Rutina();
                        rutina.setId(cursor.getInt(cursor.getColumnIndex(RUTINA_ID)));
                        rutina.setRutina(cursor.getString(cursor.getColumnIndex(RUTINA_NOMBRE)));
                        rutina.setStatus(cursor.getInt(cursor.getColumnIndex(RUTINA_STATUS)));
                        rutinas.add(rutina);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return rutinas;
    }

    /**
     * Actualiza el status de una rutina dado un id
     * @param id Id de la rutina a modificar
     * @param status Nuevo status de la rutina
     */
    public void actualizarStatus(int id, int status) {
        ContentValues values = new ContentValues();
        values.put(RUTINA_STATUS, status);
        db.update(RUTINAS_TABLE, values, RUTINA_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Actualiza una rutina dado un id
     * @param id Id de la rutina a modificar
     * @param rutina Nombre de la rutina
     * @param hora Hora de la rutina
     * @param dia Dia de la rutina
     */
    public void actualizarRutina(int id, String rutina, String hora, String dia) {
        ContentValues values = new ContentValues();
        values.put(RUTINA_NOMBRE, rutina);
        values.put(RUTINA_HORA, hora);
        values.put(RUTINA_DIA, dia);
        db.update(RUTINAS_TABLE, values, RUTINA_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void eliminarRutina(int id) {
        db.delete(RUTINAS_TABLE, RUTINA_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
