package com.example.aplicacionrutinas;

import android.app.Activity;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddRutina extends BottomSheetDialogFragment {

    public static final String TAG = "AddRutina";

    private EditText nuevaRutinaText;
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

        db = new BaseDeDatosHandler(getActivity());
        db.abrirBaseDeDatos();

        final boolean isUpdate;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String rutina = bundle.getString("rutina");
            nuevaRutinaText.setText(rutina);
            if (!rutina.isEmpty()) {
                nuevaRutinaText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
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
                    nuevaRutinaText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        botonNuevaRutina.setOnClickListener(view1 -> {
            String rutina = nuevaRutinaText.getText().toString();
            if (isUpdate) {
                db.actualizarRutina(bundle.getInt("id"), rutina, bundle.getString("hora"), bundle.getString("dia"));
            } else {
                Rutina rutina1 = new Rutina();
                rutina1.setRutina(rutina);
                rutina1.setStatus(0);
                db.insertarRutina(rutina1);
            }
            dismiss();
        });
    }

    /**
     * Metodo que se encarga de cerrar el dialogo.
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
