/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author jose
 */
public class TipoUsuario {
    private int idTipoUsuario;
    private String descripcion;

    public TipoUsuario() {}
    
    public TipoUsuario(int idTipoUsuario, String descripcion) {
        this.idTipoUsuario = idTipoUsuario;
        this.descripcion = descripcion;
    }

    public int getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(int idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
}
