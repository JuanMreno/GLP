package com.aplications.glp.objetos;

import android.graphics.Bitmap;

/**
 * Created by ´ñ´b on 25/11/2015.
 */
public class Registro {

    public static final String TIPO_VEHICULO = "TipoVehiculo";
    public static final String TIPO_PLATAFORMA = "TipoPlataforma";

    String id;
    String ciudad;
    String fecha;
    String hora;
    String vehiBase;
    String nombreCliente;
    String identificacion;
    String direccion;
    String telefono;
    String recargaN;
    String cilindroRecibido;
    String bmpCilRecCod;
    String capCilRec;
    String bmpCilEntCod;
    String capCilEnt;
    String tara;
    String pesoReal;
    String error;
    String estado;
    String valor;
    String fechaRegistro;
    String tipoUsuario;

    public Registro(String id, String ciudad, String fecha, String hora, String vehiBase, String nombreCliente, String identificacion, String direccion, String telefono, String recargaN, String bmpCilRecCod, String capCilRec, String bmpCilEntCod, String capCilEnt, String tara, String pesoReal, String error, String estado, String valor, String fechaRegistro) {
        this.id = id;
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.hora = hora;
        this.vehiBase = vehiBase;
        this.nombreCliente = nombreCliente;
        this.identificacion = identificacion;
        this.direccion = direccion;
        this.telefono = telefono;
        this.recargaN = recargaN;
        this.bmpCilRecCod = bmpCilRecCod;
        this.capCilRec = capCilRec;
        this.bmpCilEntCod = bmpCilEntCod;
        this.capCilEnt = capCilEnt;
        this.tara = tara;
        this.pesoReal = pesoReal;
        this.error = error;
        this.estado = estado;
        this.valor = valor;
        this.fechaRegistro = fechaRegistro;
        this.tipoUsuario = tipoUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getVehiBase() {
        return vehiBase;
    }

    public void setVehiBase(String vehiBase) {
        this.vehiBase = vehiBase;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRecargaN() {
        return recargaN;
    }

    public void setRecargaN(String recargaN) {
        this.recargaN = recargaN;
    }

    public String getCilindroRecibido() {
        return cilindroRecibido;
    }

    public void setCilindroRecibido(String cilindroRecibido) {
        this.cilindroRecibido = cilindroRecibido;
    }

    public String getBmpCilRecCod() {
        return bmpCilRecCod;
    }

    public void setBmpCilRecCod(String bmpCilRecCod) {
        this.bmpCilRecCod = bmpCilRecCod;
    }

    public String getCapCilRec() {
        return capCilRec;
    }

    public void setCapCilRec(String capCilRec) {
        this.capCilRec = capCilRec;
    }

    public String getBmpCilEnCod() {
        return bmpCilEntCod;
    }

    public void setBmpCilEntCod(String bmpCilEntCod) {
        this.bmpCilEntCod = bmpCilEntCod;
    }

    public String getCapCilEnt() {
        return capCilEnt;
    }

    public void setCapCilEnt(String capCilEnt) {
        this.capCilEnt = capCilEnt;
    }

    public String getTara() {
        return tara;
    }

    public void setTara(String tara) {
        this.tara = tara;
    }

    public String getPesoReal() {
        return pesoReal;
    }

    public void setPesoReal(String pesoReal) {
        this.pesoReal = pesoReal;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
