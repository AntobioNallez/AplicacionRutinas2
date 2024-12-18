package com.example.aplicacionrutinas;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.example.aplicacionrutinas.Notificaciones.GestorAlarma;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddRutina extends BottomSheetDialogFragment {

    public static final String TAG = "AddRutina";

    private EditText nuevaRutinaText, horaEditText;
    private Button botonNuevaRutina;
    private BaseDeDatosHandler db;

    public static AddRutina newInstance() {
        return new AddRutina();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.nueva_rutina, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nuevaRutinaText = getView().findViewById(R.id.nuevoTextoRutina);
        botonNuevaRutina = getView().findViewById(R.id.botonNuevaRutina);
        horaEditText = getView().findViewById(R.id.horaEditText);

        db = new BaseDeDatosHandler(getActivity());
        db.abrirBaseDeDatos();

        final boolean isUpdate;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String rutina = bundle.getString("rutina");
            nuevaRutinaText.setText(rutina);
            assert rutina != null;
            if (!rutina.isEmpty())
                nuevaRutinaText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
        } else {
            isUpdate = false;
        }

        nuevaRutinaText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.equals("")) {
                    nuevaRutinaText.setEnabled(false);
                    nuevaRutinaText.setTextColor(Color.GRAY);
                } else {
                    nuevaRutinaText.setEnabled(true);
                    nuevaRutinaText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        botonNuevaRutina.setOnClickListener(view1 -> {
            String rutina = nuevaRutinaText.getText().toString();
            String stringHora = horaEditText.getText().toString();
            stringHora = stringHora.trim();

            if (rutina.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (stringHora.isEmpty()) {
                stringHora = "12:00";
                Toast.makeText(requireContext(), "Se ha introducido un valor por defecto, las 12AM debido a la falta de hora.", Toast.LENGTH_SHORT).show();
            } else if (!stringHora.contains(":")) {
                if (stringHora.length() == 4) { //En el caso de haber omitido los : pero haber introducido la hora correctamente
                    stringHora = stringHora.substring(0, 2) + ":" + stringHora.substring(2, 4);
                } else {
                    Toast.makeText(requireContext(), "Por favor introduce una hora válida.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String[] tiempo = stringHora.split(":");
            long hora = Long.parseLong(tiempo[0]) * 3600000 + Long.parseLong(tiempo[1]) * 60000;
            long minutos = Long.parseLong(tiempo[1]);

            if (minutos < 0 || minutos > 59) {
                Toast.makeText(requireContext(), "Por favor introduce una hora válida. (Minutos deben estar entre 00 y 59)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (hora > 86340000) {
                Toast.makeText(requireContext(), "Por favor introduce una hora válida. (Entre 00:00 y 23:59)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isUpdate) { //Comprueba si es la actualizacion de una rutina ya existente o si es nueva
                int idRutina = bundle.getInt("id");
                Rutina rutinaAntigua = db.obtenerRutina(idRutina);
                GestorAlarma.cancelarAlarma(requireContext(), rutinaAntigua.getHora());
                db.actualizarRutina(idRutina, rutina, String.valueOf(hora), "Lunes");
            } else {
                Rutina rutina1 = new Rutina();
                rutina1.setStatus(1);
                rutina1.setRutina(rutina);
                rutina1.setHora(hora);
                db.insertarRutina(rutina1);
            }

            GestorAlarma.programarAlarmaDiaria(requireContext(), hora, rutina); //Programa una nueva alarma
            dismiss(); //Cierre del dialogo
        });
    }

    /**
     * Metodo que se encarga de cerrar el dialogo.
     *
     * @param dialog
     */
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }
}
