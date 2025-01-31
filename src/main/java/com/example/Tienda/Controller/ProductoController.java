/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.Tienda.Controller;

import com.example.Tienda.Models.Producto;
import com.example.Tienda.Repo.ProductoRepository;
import com.example.Tienda.Service.Auth.ProductoService;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author USUARIO
 */
@RestController // Indica que esta clase es un controlador en una aplicación REST y que los métodos devuelven directamente los datos al cliente (en formato JSON, por ejemplo).
@RequestMapping("/api/productos")
@CrossOrigin
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Producto> ObtenerPorId(@PathVariable Long id) {
        return productoService.ObtenerPorId(id);
    }

    @PostMapping("/crear")
    public ResponseEntity<Producto> crearProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("imagen") MultipartFile imagen) {

        try {
            String imageUrl = uploadImage(imagen);

            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setStock(stock);
            producto.setImagenUrl(imageUrl);

            return ResponseEntity.ok(productoRepository.save(producto));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String uploadImage(MultipartFile file) throws IOException {

        Path uploadPath = Paths.get("uploads/");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);

        }

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString();

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        } else {
            extension = ".jpg";
        }
        fileName += extension;

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/api/uploads/" + fileName;
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {
        Optional<Producto> productoExistente = productoService.ObtenerPorId(id);

        if (productoExistente.isPresent()) {
            Producto producto = productoExistente.get();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setStock(stock);

            if (imagen != null && !imagen.isEmpty()) {
                try {
                    String imageUrl = uploadImage(imagen);
                    producto.setImagenUrl(imageUrl);
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }

            Producto productoActualizado = productoService.guardarProducto(producto);
            return ResponseEntity.ok(productoActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
    }

}
