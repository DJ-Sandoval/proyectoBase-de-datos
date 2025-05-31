package model;
import java.util.Date;
public class Multa {
    private int idMulta;
    private int idPrestamo;
    private double monto;
    private Date fechaEmision;
    private String estado;

    public Multa() {
    }

    public Multa(int idMulta, int idPrestamo, double monto, Date fechaEmision, String estado) {
        this.idMulta = idMulta;
        this.idPrestamo = idPrestamo;
        this.monto = monto;
        this.fechaEmision = fechaEmision;
        this.estado = estado;
    }

    public int getIdMulta() {
        return idMulta;
    }

    public void setIdMulta(int idMulta) {
        this.idMulta = idMulta;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }



}