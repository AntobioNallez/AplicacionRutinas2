package com.example.aplicacionrutinas.Notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Notificaciones.lanzarNotificacion(context, "Titulo", "Mensaje");
    }
}