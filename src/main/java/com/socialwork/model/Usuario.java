package com.socialwork.model;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String passwordHash;
    private String rol;
    private String avatar;
    private String createdAt;

    public Usuario() {}

    public Usuario(int id, String nombre, String email, String rol, String avatar) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.avatar = avatar;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
