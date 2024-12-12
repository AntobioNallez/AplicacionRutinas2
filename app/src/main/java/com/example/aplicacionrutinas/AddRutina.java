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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AddRutina extends BottomSheetDialogFragment {

    public static final String TAG = "AddRutina";
    private final String[] dias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};

    private EditText nuevaRutinaText, horaEditText;
    private Button botonNuevaRutina;
    private BaseDeDatosHandler db;
    private Spinner diaSpinner;

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
        diaSpinner = getView().findViewById(R.id.diaSpinner);
        horaEditText = getView().findViewById(R.id.horaEditText);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dias); //Crea el adapter con los dias de la semana
        diaSpinner.setAdapter(adapter); //Añade los dias de la semana al spinner

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
            String dia = diaSpinner.getSelectedItem().toString();
            String hora = calendarioMiliSegundos(horaEditText.getText().toString());

            if (isUpdate) {
                db.actualizarRutina(bundle.getInt("id"), rutina, hora, dia);
            } else {
                Rutina rutina1 = new Rutina();
                rutina1.setStatus(0);
                rutina1.setRutina(rutina);
                rutina1.setHora(hora);
                rutina1.setDia(dia);
                db.insertarRutina(rutina1);
            }
            dismiss();
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

    public String calendarioMiliSegundos(String hora) {
        String[] horaSeparacion = hora.split(":");
        int horaInt = Integer.parseInt(horaSeparacion[0]);
        int minutosInt = Integer.parseInt(horaSeparacion[1]);

        if (horaInt >= 0 && horaInt <= 23 && minutosInt >= 0 && minutosInt <= 59) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, horaInt);
            calendar.set(Calendar.MINUTE, minutosInt);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long horaLong = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);

            hora = String.valueOf(horaLong - calendar.getTimeInMillis());
        } else {
            hora = "28800000";
        }

        return hora;
    }
}
