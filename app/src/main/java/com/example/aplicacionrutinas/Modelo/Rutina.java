package com.example.aplicacionrutinas.Modelo;

public class Rutina {

    private int id, status;
    private String rutina;
    private long hora;
    private String dia;

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRutina() {
        return rutina;
    }

    public void setRutina(String rutina) {
        this.rutina = rutina;
    }

    public static long convertirHoraAMilisegundos(String hora) {
        String[] partes = hora.split(":");
        int horas = Integer.parseInt(partes[0]);
        int minutos = Integer.parseInt(partes[1]);
        return (horas * 3600 + minutos * 60) * 1000;
    }

    // Convertir de milisegundos a HH:mm
    public static String convertirMilisegundosAHora(long milisegundos) {
        int horas = (int) (milisegundos / (1000 * 3600));
        int minutos = (int) ((milisegundos % (1000 * 3600)) / (1000 * 60));
        return String.format("%02d:%02d", horas, minutos);
    }
}
