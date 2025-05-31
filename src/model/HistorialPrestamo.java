/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author jose
 */
public class HistorialPrestamo {
   private int idHistorial;
    private Prestamo idPrestamo;
    private Date fechaRegistro;
    private String accion;

    public HistorialPrestamo() {}
    
    public HistorialPrestamo(int idHistorial, Prestamo idPrestamo, 
                           Date fechaRegistro, String accion) {
        this.idHistorial = idHistorial;
        this.idPrestamo = idPrestamo;
        this.fechaRegistro = fechaRegistro;
        this.accion = accion;
    } 

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
    }

    public Prestamo getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Prestamo idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
    
    
}
