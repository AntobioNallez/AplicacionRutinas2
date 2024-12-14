package com.example.aplicacionrutinas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionrutinas.Adaptador.RutinaAdaptador;
import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView recyclerView;
    private RutinaAdaptador rutinaAdaptador;
    private FloatingActionButton fab;
    private Toolbar toolbar;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rutinaAdaptador = new RutinaAdaptador(db, this);
        recyclerView.setAdapter(rutinaAdaptador);

        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.miToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis Rutinas");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menuappbar, menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem menuItem) {
                cargarRutinas("");
                return true;
            }
        };

        MenuItem busquedaItem = menu.findItem(R.id.buscar);
        busquedaItem.setOnActionExpandListener(onActionExpandListener);
        SearchView search = (SearchView) busquedaItem.getActionView();
        search.setQueryHint("Introduce la rutina a buscar");

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cargarRutinas(newText);
                return true;
            }
        });

        return true;
    }

    private void cargarRutinas(String query) {
        List<Rutina> rutinasQuery = db.buscarRutinas(query);
        rutinaAdaptador.setRutinas(rutinasQuery);
    }

}