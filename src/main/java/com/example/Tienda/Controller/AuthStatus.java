/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;

/**
 *
 * @author USUARIO
 */
// Define una enumeración (enum) llamada AuthStatus.
// Esta enum representa los diferentes estados posibles relacionados con operaciones de autenticación y registro.
public enum AuthStatus {
    USER_CREATED_SUCCESSFULLY, // Indica que un nuevo usuario fue creado exitosamente.
    USER_NOT_CREATED, // Indica que hubo un problema al intentar crear un usuario.
    LOGIN_SUCCESS, // Indica que el inicio de sesión fue exitoso.
    LOGIN_FAILED               // Indica que el intento de inicio de sesión falló.
}
