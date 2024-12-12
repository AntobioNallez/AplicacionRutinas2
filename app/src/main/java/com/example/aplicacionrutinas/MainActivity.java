package com.example.aplicacionrutinas;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionrutinas.Adaptador.RutinaAdaptador;
import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.example.aplicacionrutinas.Notificaciones.Notificaciones;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView recyclerView;
    private RutinaAdaptador rutinaAdaptador;
    private FloatingActionButton fab, fab2;

    private List<Rutina> rutinas;
    private BaseDeDatosHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new BaseDeDatosHandler(this);
        db.abrirBaseDeDatos();

        rutinas = new ArrayList<>();

        recyclerView = findViewById(R.id.rutinaRecyclerView);
        rutinaAdaptador = new RutinaAdaptador(db,this);
        recyclerView.setAdapter(rutinaAdaptador);

        fab = findViewById(R.id.fab);
        fab2 = findViewById(R.id.fab2);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerTouchHelper(rutinaAdaptador));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        rutinas = db.obtenerRutinas();
        rutinaAdaptador.setRutinas(rutinas);

        fab.setOnClickListener(view -> AddRutina.newInstance().show(getSupportFragmentManager(), AddRutina.TAG));
        fab2.setOnClickListener(view -> Notificaciones.lanzarNotificacion(this));

        solicitarPermisosNotificaciones();
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        rutinas = db.obtenerRutinas();
        rutinaAdaptador.setRutinas(rutinas);
    }

    /**
     * Solicita el permiso de notificaciones
     */
    private void solicitarPermisosNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

}