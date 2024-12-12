package com.example.aplicacionrutinas.Notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.aplicacionrutinas.R;
import com.example.aplicacionrutinas.SplashLayout;

public class Notificaciones {

    private static final String NOMBRE_CANAL = "nombreCanal1";
    private static final String ID_CANAL = "idCanal1";

    public static void lanzarNotificacion(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = crearCanalNotificaciones(context, NOMBRE_CANAL, ID_CANAL);
            manager.createNotificationChannel(notificationChannel);
        }

        //Crear notificacion
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, ID_CANAL);
        nb.setDefaults(Notification.DEFAULT_ALL);
        nb.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        nb.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        nb.setSmallIcon(R.drawable.ic_launcher_foreground);
        nb.setContentTitle("BUENOS DIAS");
        nb.setSubText("AVISO DIARIO");
        nb.setContentText("hoasdf");

        Intent intent = new Intent(context, SplashLayout.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        nb.setContentIntent(pendingIntent);
        nb.setAutoCancel(true);
        Notification notification = nb.build();
        manager.notify(537, notification);
    }

    private static NotificationChannel crearCanalNotificaciones(Context context, String nombre, String id) {
        NotificationChannel notificationChannel = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(id, nombre, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(context.getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            notificationChannel.setVibrationPattern(new long[]{500, 500, 500, 500, 500, 500});
        }

        return notificationChannel;
    }
}
