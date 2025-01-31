/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Service.Auth;

import com.example.Tienda.Models.Producto;
import com.example.Tienda.Repo.ProductoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author USUARIO
 */
@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }
    
    public Optional<Producto> ObtenerPorId(Long id) {
        return productoRepository.findById(id);
    }
    
    public Producto guardarProducto(Producto producto){
        return productoRepository.save(producto);
    }
    
    
    public  void  eliminarProducto(Long id){
        productoRepository.deleteById(id);
    }
}
