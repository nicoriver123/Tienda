/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.token;

import com.example.Tienda.Models.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Calendar;
import java.util.Date;
/**
 *
 * @author USUARIO
 */
@Entity
public class VerificationToken {
    // ID del token, se genera automáticamente con la estrategia de autoincremento.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // El token en sí, que se almacenará en la base de datos.
    private String token;
    
    // La fecha de expiración del token, se asegura de que no sea nula.
    @Column(nullable = false)
    private Date expirationTime;
    
    // Tiempo de expiración del token en minutos (valor de 15 minutos por defecto).
    private static final int EXPIRATION_TIME = 15;
    
    // Relación uno a uno con el usuario, que se obtiene de manera 'eager' (siempre se carga cuando se obtiene el token).
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Usuario user;
    
    // Constructor vacío explícito requerido para JPA.
    public VerificationToken() {
    }
   
    // Constructor que recibe un token y un usuario, calcula el tiempo de expiración del token.
    public VerificationToken(String token, Usuario user) {
        this.token = token;
        this.user = user;
        this.expirationTime = calculateExpirationTime();
    }

    // Constructor que recibe solo el token y calcula el tiempo de expiración.
    public VerificationToken(String token) {
        this.token = token;
        this.expirationTime = calculateExpirationTime();
    }

    // Método que calcula el tiempo de expiración del token añadiendo minutos al momento actual.
    private Date calculateExpirationTime() {
        Calendar calendar = Calendar.getInstance();  // Obtiene el calendario actual.
        calendar.setTimeInMillis(new Date().getTime());  // Establece el tiempo actual.
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);  // Añade el tiempo de expiración.
        return calendar.getTime();  // Devuelve la nueva fecha de expiración.
    }

    // Métodos getter y setter para acceder y modificar los atributos de la clase.
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
}
