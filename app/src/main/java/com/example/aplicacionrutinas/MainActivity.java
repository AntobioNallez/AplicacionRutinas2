package com.example.aplicacionrutinas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionrutinas.Adaptador.RutinaAdaptador;
import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.example.aplicacionrutinas.Util.MusicManager;
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
    private int opcionFiltrado = 0;
    private String busquedaActual = "";
    private String orden = "hora";

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

        rutinas = db.obtenerRutinas(orden);
        rutinaAdaptador.setRutinas(rutinas);

        fab.setOnClickListener(view -> AddRutina.newInstance().show(getSupportFragmentManager(), AddRutina.TAG));
        MusicManager.empezarMusica(this, R.raw.musica_fondo);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        rutinas = db.obtenerRutinas(orden);
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
                busquedaActual = newText;
                cargarRutinas(newText);
                return true;
            }
        });

        MenuItem filtrarItem = menu.findItem(R.id.filtrar);
        filtrarItem.setOnMenuItemClickListener(item -> {
            showPopUpHoras(findViewById(R.id.filtrar));
            return true;
        });

        MenuItem organizarItem = menu.findItem(R.id.ordenarPor);
        organizarItem.setOnMenuItemClickListener(menuItem -> {
            showPopUpSort(findViewById(R.id.ordenarPor));
            return true;
        });

        return true;
    }

    private void showPopUpHoras(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_filter, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option1) {
                opcionFiltrado = 0;
            } else if (item.getItemId() == R.id.option2) {
                opcionFiltrado = 1;
            } else if (item.getItemId() == R.id.option3) {
                opcionFiltrado = 2;
            } else if (item.getItemId() == R.id.option4) {
                opcionFiltrado = 3;
            } else if (item.getItemId() == R.id.option5) {
                opcionFiltrado = 4;
            }
            cargarRutinas(busquedaActual);
            return true;
        });

        popupMenu.show();
    }

    private void showPopUpSort(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_sort, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.sortHora) {
                orden = "hora";
            } else if (item.getItemId() == R.id.sortRutina) {
                orden = "nombre";
            }
            cargarRutinas(busquedaActual);
            return true;
        });
        popupMenu.show();
    }

    private void cargarRutinas(String query) {
        List<Rutina> rutinasQuery = db.buscarRutinas(query, opcionFiltrado, orden);
        rutinaAdaptador.setRutinas(rutinasQuery);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.detenerMusica();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pausarMusica();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.reaunudarMusica();
    }
}