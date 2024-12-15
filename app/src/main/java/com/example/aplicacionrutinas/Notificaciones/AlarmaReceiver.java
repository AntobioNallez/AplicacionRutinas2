package com.example.aplicacionrutinas.Notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.aplicacionrutinas.BaseDeDatos.BaseDeDatosHandler;
import com.example.aplicacionrutinas.Modelo.Rutina;

import java.util.List;

public class AlarmaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra("bundle");
        Notificaciones.lanzarNotificacion(context, bundle.getString("titulo"), bundle.getString("mensaje"));
        Log.d("AlarmaReceiver", "Alarma recibida con código " + bundle.getInt("cod"));
    }
}