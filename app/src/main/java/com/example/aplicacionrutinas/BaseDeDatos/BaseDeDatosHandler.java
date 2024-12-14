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

    /**
     * Actualiza la base de datos y la version de esta
     *
     * @param sqLiteDatabase Objeto que contiene la base de datos
     * @param oldVersion     Version antigua de la base de datos
     * @param newVersion     Nueva version de la base de datos
     */
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
     * Inserta una nueva rutina en la base de datos
     *
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
     * Devuelve una rutina dado un id
     *
     * @param id Id de la rutina
     * @return Objeto rutina con los datos de esta
     */
    @SuppressLint("Range")
    public Rutina obtenerRutina(int id) {
        List<Rutina> rutinas = obtenerRutinaConQuery("SELECT * FROM rutina WHERE id = ?", new String[]{String.valueOf(id)});
        return rutinas.get(0);
    }

    /**
     * Busca rutinas dado un texto "Ej. Busqueda='Co' -> Resultado=Compra, Cocinar, etc."
     *
     * @param texto Texto a buscar
     * @return List que contiene las rutinas encontradas
     */
    @SuppressLint("Range")
    public List<Rutina> buscarRutinas(String texto) {
        String query = "SELECT * FROM rutina WHERE nombre LIKE ?";
        return obtenerRutinaConQuery(query, new String[]{texto + "%"});
    }

    /**
     * Devuelve todas las rutinas
     *
     * @return List que contiene todas las rutinas
     */
    @SuppressLint("Range")
    public List<Rutina> obtenerRutinas() {
        return obtenerRutinaConQuery("SELECT * FROM rutina", null);
    }

    /**
     * Actualiza el status de una rutina dado un id
     *
     * @param id     Id de la rutina a modificar
     * @param status Nuevo status de la rutina
     */
    public void actualizarStatus(int id, int status) {
        ContentValues values = new ContentValues();
        values.put(RUTINA_STATUS, status);
        actualizarRutinaPorId(id, values);
    }

    /**
     * Actualiza una rutina dado un id
     *
     * @param id     Id de la rutina a modificar
     * @param rutina Nombre de la rutina
     * @param hora   Hora de la rutina
     * @param dia    Dia de la rutina
     */
    public void actualizarRutina(int id, String rutina, String hora, String dia) {
        ContentValues values = new ContentValues();
        values.put(RUTINA_NOMBRE, rutina);
        values.put(RUTINA_HORA, hora);
        values.put(RUTINA_DIA, dia);
        actualizarRutinaPorId(id, values);
    }

    /**
     * Elimina una rutina dado un id
     *
     * @param id Id de la rutina a eliminar
     */
    public void eliminarRutina(int id) {
        db.delete(RUTINAS_TABLE, RUTINA_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Ejecuta una query y devuelve una lista de rutinas
     *
     * @param query      Query a ejecutar
     * @param argumentos Argumentos de la query
     * @return List que contiene las rutinas encontradas
     */
    private List<Rutina> obtenerRutinaConQuery(String query, String[] argumentos) {
        List<Rutina> rutinas = new ArrayList<>();
        try (Cursor cursor = db.rawQuery(query, argumentos)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    rutinas.add(construirRutina(cursor));
                } while (cursor.moveToNext());
            }
        }

        return rutinas;
    }

    /**
     * Construye un objeto rutina dado un cursor
     *
     * @param cursor Cursor que contiene los datos de la rutina
     * @return Objeto rutina con los datos de esta
     */
    @SuppressLint("Range")
    private Rutina construirRutina(Cursor cursor) {
        Rutina rutina = new Rutina();
        rutina.setId(cursor.getInt(cursor.getColumnIndex(RUTINA_ID)));
        rutina.setRutina(cursor.getString(cursor.getColumnIndex(RUTINA_NOMBRE)));
        rutina.setStatus(cursor.getInt(cursor.getColumnIndex(RUTINA_STATUS)));
        rutina.setHora(cursor.getString(cursor.getColumnIndex(RUTINA_HORA)));
        rutina.setDia(cursor.getString(cursor.getColumnIndex(RUTINA_DIA)));
        return rutina;
    }

    /**
     * Actualiza una rutina dado un id y un objeto content values
     *
     * @param id     Id de la rutina a modificar
     * @param values Objeto content values con los datos a modificar
     */
    private void actualizarRutinaPorId(int id, ContentValues values) {
        db.update(RUTINAS_TABLE, values, RUTINA_ID + " = ?", new String[]{String.valueOf(id)});
    }

}
