package com.socialwork.model;

public class Conexion {
    private int id;
    private int seguidorId;
    private int seguidoId;
    private String createdAt;
    private String seguidoNombre;
    private String seguidoAvatar;
    private String seguidoRol;

    public Conexion() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSeguidorId() { return seguidorId; }
    public void setSeguidorId(int seguidorId) { this.seguidorId = seguidorId; }
    public int getSeguidoId() { return seguidoId; }
    public void setSeguidoId(int seguidoId) { this.seguidoId = seguidoId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getSeguidoNombre() { return seguidoNombre; }
    public void setSeguidoNombre(String seguidoNombre) { this.seguidoNombre = seguidoNombre; }
    public String getSeguidoAvatar() { return seguidoAvatar; }
    public void setSeguidoAvatar(String seguidoAvatar) { this.seguidoAvatar = seguidoAvatar; }
    public String getSeguidoRol() { return seguidoRol; }
    public void setSeguidoRol(String seguidoRol) { this.seguidoRol = seguidoRol; }
}
