/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Service.userdetails;

import com.example.Tienda.Models.Usuario;
import com.example.Tienda.Repo.UserRepo;
import com.example.Tienda.Service.Auth.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author USUARIO
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public Usuario obtenerUsuarioPorUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado") );
    }
    
    
}
