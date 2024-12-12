package com.example.aplicacionrutinas.Notificaciones;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.aplicacionrutinas.R;
import com.example.aplicacionrutinas.SplashLayout;

public class Notificaciones {

    private static final String NOMBRE_CANAL = "nombreCanal1";
    private static final String ID_CANAL = "idCanal1";

    /**
     * Lanza una nueva notificacion
     * @param context
     */
    public static void lanzarNotificacion(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = crearCanalNotificaciones(context);
            nc.setDescription("Descripción del canal");
            nc.enableVibration(true);
            manager.createNotificationChannel(nc);
        }

        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "idCanal1")
                .setSmallIcon(R.drawable.thumbs)
                .setContentTitle("Título de prueba")
                .setContentText("Contenido de prueba")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent intent = new Intent(context, SplashLayout.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        nb.setContentIntent(pendingIntent);

        Log.d("Notificaciones", "Enviando notificación...");
        manager.notify(537, nb.build());
    }

    /**
     * Metodo que crea la notificacion
     * @param context
     * @return Canal de notificaciones
     */
    private static NotificationChannel crearCanalNotificaciones(Context context) {
        NotificationChannel notificationChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(Notificaciones.NOMBRE_CANAL, Notificaciones.ID_CANAL, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(context.getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500, 500});
        }

        return notificationChannel;
    }
}
