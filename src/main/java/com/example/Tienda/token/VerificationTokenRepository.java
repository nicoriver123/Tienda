/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.token;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author USUARIO
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
   // Método personalizado para encontrar un VerificationToken por su valor de token.
    VerificationToken findByToken(String token);
}
