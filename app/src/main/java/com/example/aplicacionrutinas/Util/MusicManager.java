package com.example.aplicacionrutinas.Util;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.aplicacionrutinas.R;

public class MusicManager {

    private static MediaPlayer mediaPlayer;

    public static void empezarMusica(Context context, int resourceId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resourceId);
            mediaPlayer.setLooping(true); // Reproducir en bucle
            mediaPlayer.setVolume(3f, 3f); // Ajustar volumen
        }
        mediaPlayer.start();
    }

    public static void reproducirBorrarRutina(Context context) {
        if (mediaPlayer != null) detenerMusica();

        mediaPlayer = MediaPlayer.create(context, R.raw.borrar_rutina);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(mp -> {
            detenerMusica();
            MusicManager.empezarMusica(context, R.raw.musica_fondo);
        });
    }

    public static void reaunudarMusica() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void pausarMusica() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void detenerMusica() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
