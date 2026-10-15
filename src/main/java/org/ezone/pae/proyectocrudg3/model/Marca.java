package org.ezone.pae.proyectocrudg3.model;

public class Marca {

    private int idMarca;
    private String nombre;
    private String paisOrigen;
    private int estado;

    public Marca() {
    }

    public Marca(int idMarca, String nombre, String paisOrigen, int estado) {
        this.idMarca = idMarca;
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.estado = estado;
    }

    public Marca(int idMarca, String nombre) {
        this.idMarca = idMarca;
        this.nombre = nombre;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
}