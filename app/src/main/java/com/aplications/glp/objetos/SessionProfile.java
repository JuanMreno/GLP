package com.aplications.glp.objetos;

/**
 * Created by ´ñ´b on 15/11/2015.
 */
public class SessionProfile {

    public static final String TIPO_VEHICULO = "TipoVehiculo";
    public static final String TIPO_PLATAFORMA = "TipoPlataforma";

    String tipoUsuario;
    String tipoNombre;
    String nombre;
    String identificacion;
    String telefono;

    public SessionProfile(String tipoUsuario, String tipoNombre, String nombre, String identificacion, String telefono) {
        this.tipoUsuario = tipoUsuario;
        this.tipoNombre = tipoNombre;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.telefono = telefono;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipousuario) {
        this.tipoUsuario = tipousuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }
}
