package com.example.aplicacionrutinas;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacionrutinas.Adaptador.RutinaAdaptador;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.example.aplicacionrutinas.Notificaciones.GestorAlarma;

import java.util.List;

/**
 * Clase que se encarga de manejar el deslizamiento de las rutinas en el RecyclerView. Y su funcionalidad.
 */
public class RecyclerTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RutinaAdaptador rutinaAdaptador;

    public RecyclerTouchHelper(RutinaAdaptador rutinaAdaptador) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.rutinaAdaptador = rutinaAdaptador;
    }

    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Evento que se ejecuta cuando se deslizan las rutinas. Se encarga de realizar las acciones correspondientes a la accion realizada
     * borrado o edicion.
     *
     * @param viewHolder El viewholder que se está deslizando
     * @param direction  Direccion en la que ocurre el desplazamiento realizado por el usuario izquierda o derecha
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int posicion = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            AlertDialog.Builder builder = getBuilder(posicion);
            AlertDialog dialog = builder.create();
            dialog.setOnCancelListener(dialogInterface -> rutinaAdaptador.notifyItemChanged(posicion));
            dialog.show();
        } else {
            rutinaAdaptador.editarRutina(posicion);
        }
    }

    private AlertDialog.Builder getBuilder(int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(rutinaAdaptador.getContext());
        builder.setTitle("Eliminar Rutina");
        builder.setMessage("¿Estás seguro de que quieres eliminar esta rutina?");
        builder.setPositiveButton("Sí", (dialogInterface, i) -> {
            List<Rutina> rutinas = rutinaAdaptador.getRutinas();
            GestorAlarma.cancelarAlarma(rutinaAdaptador.getContext(), rutinas.get(posicion).getHora());
            rutinaAdaptador.borrarRutina(posicion);
        });
        builder.setNegativeButton(R.string.cancelOption, (dialogInterface, i) -> rutinaAdaptador.notifyItemChanged(posicion));
        return builder;
    }

    /**
     * Evento que se ejecuta cuando se deslizan las rutinas. Se encarga de realizar las acciones correspondientes a la accion realizada
     *
     * @param c                 El canvas sobre el que se dibuja el fondo
     * @param recyclerView      El RecyclerView al que pertenece el ViewHolder
     * @param viewHolder        El viewholder con el que interacciona el usuario
     * @param dX                Movimiento horizontal
     * @param dY                Movimiento vertical
     * @param actionState       Tipo de accion deslizar o arrastrar
     * @param isCurrentlyActive Si en el momento esta siendo usado o no
     */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView; // Vista del ViewHolder actual

        Drawable icon;
        ColorDrawable fondo;

        if (dX > 0) { // Deslizar hacia la derecha
            icon = ContextCompat.getDrawable(rutinaAdaptador.getContext(), R.drawable.baseline_edit_24); //Icono de papelera
            fondo = new ColorDrawable(ContextCompat.getColor(rutinaAdaptador.getContext(), R.color.colorPrimaryDark));
        } else { // Deslizar hacia la izquierda
            icon = ContextCompat.getDrawable(rutinaAdaptador.getContext(), R.drawable.baseline_restore_from_trash_24); // Icono edicion
            fondo = new ColorDrawable(Color.RED);
        }

        // Asegúrate de que el fondo no exceda los límites de la vista
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Fondo para deslizar a la derecha
            int iconLeft = itemView.getLeft() + iconMargin;
            int iconRight = iconLeft + icon.getIntrinsicWidth();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            fondo.setBounds(itemView.getLeft(), itemView.getTop(), Math.min(itemView.getLeft() + ((int) dX), itemView.getRight()), itemView.getBottom());
        } else if (dX < 0) { // Fondo para deslizar a la izquierda
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            fondo.setBounds(Math.max(itemView.getRight() + ((int) dX), itemView.getLeft()), itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // Estado base
            fondo.setBounds(0, 0, 0, 0);
        }

        fondo.draw(c);
        icon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


}
