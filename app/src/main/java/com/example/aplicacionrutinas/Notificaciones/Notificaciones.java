package com.example.aplicacionrutinas.Notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.aplicacionrutinas.R;
import com.example.aplicacionrutinas.SplashLayout;

public class Notificaciones {

    private static final String NOMBRE_CANAL = "Recordatorio Rutina";
    private static final String ID_CANAL = "idCanal1";
    private static Uri SONIDO_URI;

    /**
     * Lanza una nueva notificacion
     *
     * @param context
     */
    public static void lanzarNotificacion(Context context, String titulo, String texto) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //manager.deleteNotificationChannel(NOMBRE_CANAL); Esta linea esta para borrar el canal duplicado ya que antes estaba el id y nombre cambiados(ya no hay duplicado)
            manager.deleteNotificationChannel(ID_CANAL);
            NotificationChannel nc = crearCanalNotificaciones(context);
            nc.setDescription("Descripción del canal");
            nc.enableVibration(true);
            nc.setImportance(NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(nc);
        }

        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, ID_CANAL)
                .setSmallIcon(R.drawable.thumbs)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(SONIDO_URI);

        Intent intent = new Intent(context, SplashLayout.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        nb.setContentIntent(pendingIntent);

        Log.d("Notificaciones", "Enviando notificación...");
        manager.notify(537, nb.build());
    }

    /**
     * Metodo que crea el canal de notificaciones
     *
     * @param context
     * @return Canal de notificaciones
     */
    private static NotificationChannel crearCanalNotificaciones(Context context) {
        NotificationChannel notificationChannel = null;
        SONIDO_URI = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.borrar_rutina);
        Log.d("Notificaciones", "URI de sonido: " + SONIDO_URI.toString());
        AudioAttributes audio = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(Notificaciones.ID_CANAL, Notificaciones.NOMBRE_CANAL, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setSound(SONIDO_URI, audio);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500, 500});
        }

        return notificationChannel;
    }
}
