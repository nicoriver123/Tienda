/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Repo;

import com.example.Tienda.Models.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Interfaz de repositorio para gestionar las operaciones CRUD relacionadas con los usuarios.
 * Hereda de JpaRepository, lo que permite acceder a métodos predefinidos para interactuar con la base de datos.
 * Incluye métodos personalizados para verificar la existencia de usuarios por nombre de usuario y correo electrónico,
 * así como para buscar usuarios por nombre de usuario.
 * @author USUARIO
 */
@Repository // Anotación que indica que esta interfaz es un componente de acceso a datos en Spring.
public interface UserRepo extends JpaRepository<Usuario, Long> {
     /**
     * Verifica si un nombre de usuario ya existe en la base de datos.
     * @param username El nombre de usuario que se desea verificar.
     * @return true si el nombre de usuario ya existe; false si no existe.
     */
  boolean existsByUsername(String username);

  
   /**
     * Busca un usuario en la base de datos utilizando su nombre de usuario.
     * 
     * @param username El nombre de usuario del usuario que se desea buscar.
     * @return Un objeto Optional que contiene el usuario si se encuentra, o está vacío si no se encuentra.
     */
  Optional<Usuario> findByUsername(String username);
  /**
     * Verifica si un correo electrónico ya está registrado en la base de datos.
     * 
     * @param email El correo electrónico que se desea verificar.
     * @return true si el correo electrónico ya existe; false si no existe.
     */
  boolean existsByEmail(String email);
}
