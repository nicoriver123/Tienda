/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;

/**
 *
 * @author USUARIO
 */
// Define un registro (record) llamado AuthRequestDto.
// Los records son una característica de Java que permite crear clases inmutables con menos código boilerplate.
// Este registro representa una solicitud de autenticación o registro de usuario.
public record AuthRequestDto(
        String nombre,
        String username,
        String password,
        String email) {

}
