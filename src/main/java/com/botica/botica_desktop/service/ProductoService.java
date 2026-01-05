package com.botica.botica_desktop.service;

import com.botica.botica_desktop.entity.Producto;
import com.botica.botica_desktop.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // 🔹 Guardar o actualizar
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    // 🔹 Listar todos
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // 🔹 Buscar por ID
    public Optional<Producto> buscarPorId(Long id) {
        return productoRepository.findById(id);
    }

    // 🔹 Eliminar
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
