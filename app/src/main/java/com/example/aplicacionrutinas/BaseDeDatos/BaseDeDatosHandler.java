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

    private static final String DATABASE_NAME = "rutinas.db";
    private static final int DATABASE_VERSION = 1;
    private static final String RUTINAS_TABLE = "rutina";
    private static final String RUTINA_ID = "id";
    private static final String RUTINA_NOMBRE = "nombre";
    private static final String RUTINA_STATUS = "status";
    private static final String RUTINA_HORA = "hora";
    private static final String RUTINA_DIA = "dia";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + RUTINAS_TABLE + "(" + RUTINA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RUTINA_NOMBRE + " TEXT, " + RUTINA_STATUS + " INTEGER, " + RUTINA_HORA + " INTEGER, " + RUTINA_DIA + " TEXT)";

    private SQLiteDatabase db;
    private final long[][] rangosHoras = {
            {0, 86340000}, // 0:00 a 23:59
            {0, 21540000}, // 0:00 a 5:59
            {21600000, 43140000}, // 6:00 a 11:59
            {43200000, 64740000}, // 12:00 a 17:59
            {64800000, 86340000}  // 18:00 a 23:59
    };

    public BaseDeDatosHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY);
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
        onCreate(sqLiteDatabase);
    }

    /**
     * Inicializa la base de datos para poder escribir
     */
    public void abrirBaseDeDatos() {
        db = this.getWritableDatabase();
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
        List<Rutina> rutinas = obtenerRutinaConQuery("SELECT * FROM rutina WHERE id = ?", new String[]{String.valueOf(id)}, "hora");
        return rutinas.get(0);
    }

    /**
     * Busca rutinas dado un texto "Ej. Busqueda='Co' -> Resultado=Compra, Cocinar, etc."
     *
     * @param texto Texto a buscar
     * @return List que contiene las rutinas encontradas
     */
    @SuppressLint("Range")
    public List<Rutina> buscarRutinas(String texto, int rangoHora, String orden) {
        String query = "SELECT * FROM rutina WHERE nombre LIKE ? AND hora >= ? AND hora <= ?";
        long inicio = rangosHoras[rangoHora][0];
        long fin = rangosHoras[rangoHora][1];

        return obtenerRutinaConQuery(query, new String[]{texto + "%", String.valueOf(inicio), String.valueOf(fin)}, orden);
    }

    /**
     * Devuelve todas las rutinas
     *
     * @return List que contiene todas las rutinas
     */
    @SuppressLint("Range")
    public List<Rutina> obtenerRutinas(String orden) {
        return obtenerRutinaConQuery("SELECT * FROM rutina", null, orden);
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
    private List<Rutina> obtenerRutinaConQuery(String query, String[] argumentos, String orden) {
        List<Rutina> rutinas = new ArrayList<>();
        query += " ORDER BY " + orden + " ASC"; //Orden por parametro que se le pase
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
        rutina.setHora(cursor.getLong(cursor.getColumnIndex(RUTINA_HORA)));
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
