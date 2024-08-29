package com.example.prueba;

import java.io.Serializable;

public class elemento implements Serializable {
    private String id;
    private String nombre_registro;
    private String apellido_registro;
    private String correo_registro;
    private String contrasena_registro;
    private String genero_registro;
    private String estadoCuenta;  // Nuevo campo

    // Constructor principal
    public elemento(String id, String nombre_registro, String apellido_registro, String correo_registro, String contrasena_registro, String genero_registro, String estadoCuenta) {
        this.id = id;
        this.nombre_registro = nombre_registro;
        this.apellido_registro = apellido_registro;
        this.correo_registro = correo_registro;
        this.contrasena_registro = contrasena_registro;
        this.genero_registro = genero_registro;
        this.estadoCuenta = estadoCuenta;
    }

    // Constructor sin el campo estadoCuenta
    public elemento(String id, String nombre_registro, String apellido_registro, String correo_registro, String contrasena_registro, String genero_registro) {
        this(id, nombre_registro, apellido_registro, correo_registro, contrasena_registro, genero_registro, "desactivado");
    }

    // Métodos getter y setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre_registro() {
        return nombre_registro;
    }

    public void setNombre_registro(String nombre_registro) {
        this.nombre_registro = nombre_registro;
    }

    public String getApellido_registro() {
        return apellido_registro;
    }

    public void setApellido_registro(String apellido_registro) {
        this.apellido_registro = apellido_registro;
    }

    public String getCorreo_registro() {
        return correo_registro;
    }

    public void setCorreo_registro(String correo_registro) {
        this.correo_registro = correo_registro;
    }

    public String getContrasena_registro() {
        return contrasena_registro;
    }

    public void setContrasena_registro(String contrasena_registro) {
        this.contrasena_registro = contrasena_registro;
    }

    public String getGenero_registro() {
        return genero_registro;
    }

    public void setGenero_registro(String genero_registro) {
        this.genero_registro = genero_registro;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    @Override
    public String toString() {
        return "elemento{" +
                "id='" + id + '\'' +
                ", nombre_registro='" + nombre_registro + '\'' +
                ", apellido_registro='" + apellido_registro + '\'' +
                ", correo_registro='" + correo_registro + '\'' +
                ", genero_registro='" + genero_registro + '\'' +
                ", estadoCuenta='" + estadoCuenta + '\'' +
                '}';
    }
}
