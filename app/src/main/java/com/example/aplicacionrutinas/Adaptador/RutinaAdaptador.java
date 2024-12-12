package com.example.aplicacionrutinas.Adaptador;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionrutinas.AddRutina;
import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.MainActivity;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.example.aplicacionrutinas.R;

import java.util.List;

public class RutinaAdaptador extends RecyclerView.Adapter<RutinaAdaptador.ViewHolder> {

    private List<Rutina> rutinas;
    private MainActivity mainActivity;
    private BaseDeDatosHandler db;

    public RutinaAdaptador(BaseDeDatosHandler db, MainActivity mainActivity) {
        this.db = db;
        this.mainActivity = mainActivity;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_layout, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        db.abrirBaseDeDatos();
        final Rutina rutina = rutinas.get(position);
        holder.rutina.setText(rutina.getRutina());
        holder.rutina.setChecked(rutina.getStatus() == 1);
        holder.rutina.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                db.actualizarStatus(rutina.getId(), 1);
            } else {
                db.actualizarStatus(rutina.getId(), 0);
            }
        });
    }

    public Activity getContext() {
        return mainActivity;
    }

    /**
     * Metodo que obtiene el numero de rutinas existentes
     * @return Numero total de rutinas
     */
    @Override
    public int getItemCount() {
        return rutinas.size();
    }

    /**
     * Metodo que se encarga de actualizar la lista de rutinas.
     * @param rutinaList Lista donde se almacenan las rutinas
     */
    public void setRutinas(List<Rutina> rutinaList) {
        this.rutinas = rutinaList;
        notifyDataSetChanged();
    }

    /**
     * Metodo que se encarga de borrar una rutina dado una posicion en la lista.
     * @param posicion Posicion en la lista
     */
    public void borrarRutina(int posicion) {
        Rutina rutina = rutinas.get(posicion);
        db.eliminarRutina(rutina.getId());
        rutinas.remove(posicion);
        notifyItemRemoved(posicion);
    }

    /**
     * Metodo que se encarga de editar una rutina dado una posicion en la lista.
     * @param posicion Posicion en la lista
     */
    public void editarRutina(int posicion) {
        Rutina rutina = rutinas.get(posicion);
        Bundle bundle = new Bundle();
        bundle.putInt("id", rutina.getId());
        bundle.putString("rutina", rutina.getRutina());
        AddRutina addRutina = new AddRutina();
        addRutina.setArguments(bundle);
        addRutina.show(mainActivity.getSupportFragmentManager(), AddRutina.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox rutina;

        ViewHolder(View view) {
            super(view);
            rutina = view.findViewById(R.id.rutinaCheckBox);
        }
    }
}
