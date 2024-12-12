package com.example.aplicacionrutinas.Notificaciones;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class GestorAlarma {

    public static void programarAlarma(Context context) {
        Calendar calendar = Calendar.getInstance();
        long tiempo = calendar.getTimeInMillis() + 10000;

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmaReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 69, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.RTC_WAKEUP, tiempo, pendingIntent);
    }
}
