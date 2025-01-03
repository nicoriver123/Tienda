/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author USUARIO
 */
@RestController
@RequestMapping("/api/secret")
public class SecretController {

       /**
     * Maneja una solicitud GET en el endpoint "/api/secret/".
     * Este método genera un identificador único (UUID) y lo devuelve en la respuesta.
     * 
     * @return ResponseEntity<String> Un objeto ResponseEntity que contiene el UUID generado como una cadena
     *         y un estado HTTP 200 OK.
     */
    @GetMapping("/")
    public ResponseEntity<String> getSecret() {
           // Genera un UUID único y lo convierte a una cadena de texto.
        return ResponseEntity
                .status(HttpStatus.OK)  // Establece el estado HTTP de la respuesta a 200 OK.
                .body(UUID.randomUUID().toString());  // Devuelve el UUID generado como cuerpo de la respuesta.
    }
}
