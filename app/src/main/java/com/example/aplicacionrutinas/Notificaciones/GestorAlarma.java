package com.example.aplicacionrutinas.Notificaciones;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class GestorAlarma {

    /**
     * Programa una alarma que se repetirá diariamente a la hora especificada.
     *
     * @param context            Contexto de la aplicación
     * @param horaEnMilisegundos Hora en milisegundos desde el comienzo del día
     */
    public static void programarAlarmaDiaria(Context context, long horaEnMilisegundos, String mensaje) {

        Bundle bundle = new Bundle();
        bundle.putString("titulo", "Recordatorio de tu rutina");
        bundle.putString("mensaje", mensaje);

        // Obtener el calendario actual
        Calendar calendarioAlarma = Calendar.getInstance();

        // Extraer horas y minutos del valor en milisegundos
        int horas = (int) (horaEnMilisegundos / (60 * 60 * 1000)); // Horas en milisegundos
        int minutos = (int) ((horaEnMilisegundos / (60 * 1000)) % 60); // Minutos en milisegundos

        // Establecer la hora y los minutos para la alarma
        calendarioAlarma.set(Calendar.HOUR_OF_DAY, horas);
        calendarioAlarma.set(Calendar.MINUTE, minutos);
        calendarioAlarma.set(Calendar.SECOND, 0);
        calendarioAlarma.set(Calendar.MILLISECOND, 0);

        // Si la hora ya ha pasado hoy, añadir un día para programar para mañana
        Calendar ahora = Calendar.getInstance();
        if (calendarioAlarma.getTimeInMillis() <= ahora.getTimeInMillis()) {
            calendarioAlarma.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Obtener el tiempo en milisegundos hasta la alarma
        long tiempoAlarma = calendarioAlarma.getTimeInMillis();

        // Crear un ID único para la alarma
        int codUnico = generarIdUnico(horas, minutos);

        bundle.putInt("cod", codUnico);

        // Configurar la alarma usando AlarmManager
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmaReceiver.class);
        intent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, codUnico, intent, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (manager != null && !manager.canScheduleExactAlarms()) {
                Log.d("AlarmaReceiver ", "No se puede programar la alarma exacta");
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, tiempoAlarma, AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
        manager.setExact(AlarmManager.RTC_WAKEUP, tiempoAlarma, pendingIntent);
    }


    /**
     * Cancela una alarma diaria.
     *
     * @param context            Contexto de la aplicación
     * @param horaEnMilisegundos Hora en milisegundos desde el comienzo del día
     */
    public static void cancelarAlarma(Context context, long horaEnMilisegundos) {
        // Generar el mismo ID único para identificar la alarma
        int requestCode = generarIdUnico((int) (horaEnMilisegundos / (60 * 60 * 1000)), (int) ((horaEnMilisegundos / (60 * 1000)) % 60));

        // Cancelar la alarma
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmaReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        manager.cancel(pendingIntent);
    }

    /**
     * Crear un hash único a partir de la hora
     *
     * @param horas   Hora en formato de 24 horas
     * @param minutos Minutos
     * @return Código único con el que se identificará la alarma
     */
    private static int generarIdUnico(int horas, int minutos) {
        return horas * 100 + minutos; // Ejemplo simple: usa la hora y los minutos para crear un ID
    }

}
