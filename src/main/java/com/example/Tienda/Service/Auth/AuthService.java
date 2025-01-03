/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Service.Auth;

/**
 *
 * @author USUARIO
 */
public interface AuthService {

    String login(String username, String password);

    String signUp(String nombre, String username, String password, String email);

    String verifyToken(String token);
}
