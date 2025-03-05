/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Service.Auth;

import com.example.Tienda.Models.Usuario;

/**
 *
 * @author USUARIO
 */
public interface UsuarioService {
    
    Usuario obtenerUsuarioPorUsername(String username);
    
    Usuario actualizarUsuario(Usuario usuario);
    
}
