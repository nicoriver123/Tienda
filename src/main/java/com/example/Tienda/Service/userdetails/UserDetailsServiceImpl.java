/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Service.userdetails;

import com.example.Tienda.Models.Usuario;
import com.example.Tienda.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Implementación del servicio UserDetailsService para la autenticación de usuarios.
 * Este servicio se encarga de cargar los detalles de un usuario desde la base de datos
 * y devolver un objeto UserDetails que Spring Security utilizará para realizar la autenticación.
 *
 * @author USUARIO
 */
@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Se intenta encontrar al usuario en la base de datos mediante el repositorio.
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        // Si se encuentra el usuario, se construye un objeto User de Spring Security con la información del usuario.
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}
