package com.example.aplicacionrutinas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionrutinas.Adaptador.RutinaAdaptador;
import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView recyclerView;
    private RutinaAdaptador rutinaAdaptador;
    private FloatingActionButton fab;

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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        rutinaAdaptador = new RutinaAdaptador(db,this);
        recyclerView.setAdapter(rutinaAdaptador);

        fab = findViewById(R.id.fab);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerTouchHelper(rutinaAdaptador));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        rutinas = db.obtenerRutinas();
        rutinaAdaptador.setRutinas(rutinas);

        fab.setOnClickListener(view -> AddRutina.newInstance().show(getSupportFragmentManager(), AddRutina.TAG));

    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        rutinas = db.obtenerRutinas();
        rutinaAdaptador.setRutinas(rutinas);
    }

}