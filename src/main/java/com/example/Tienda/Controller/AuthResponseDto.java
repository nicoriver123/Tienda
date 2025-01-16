/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;


/**
 *
 * @author USUARIO
 */
// Define un registro (record) llamado AuthResponseDto.
// Este record representa la respuesta que se envía al cliente después de un intento de autenticación o registro.
public record AuthResponseDto(
    String token,        // Contiene el token JWT generado si la autenticación o el registro fueron exitosos.
    AuthStatus authStatus ,// Representa el estado de la operación de autenticación o registro.
        String message
) {
   
}