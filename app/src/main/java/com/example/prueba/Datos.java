package com.example.prueba;

public class Datos {
    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String estadoCuenta;  // Nuevo campo

    public Datos(int id, String nombre, String correo, String contrasena, String estadoCuenta) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.estadoCuenta = estadoCuenta;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }
}