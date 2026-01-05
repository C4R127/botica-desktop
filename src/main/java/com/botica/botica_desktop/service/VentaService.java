package com.botica.botica_desktop.service;

import com.botica.botica_desktop.entity.DetalleVenta;
import com.botica.botica_desktop.entity.Producto;
import com.botica.botica_desktop.entity.Venta;
import com.botica.botica_desktop.repository.ProductoRepository;
import com.botica.botica_desktop.repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Venta registrarVenta(Venta venta) {
        double totalCalculado = 0;

        for (DetalleVenta detalle : venta.getDetalles()) {

            // 1. Buscar producto real en BD
            Producto productoBD = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // 2. Validar Stock
            if (productoBD.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + productoBD.getNombre());
            }

            // 3. Descontar Stock
            int nuevoStock = productoBD.getStock() - detalle.getCantidad();
            productoBD.setStock(nuevoStock);

            // === CORRECCIÓN IMPORTANTE ===
            // Sincronizamos el cambio de JavaFX hacia JPA antes de guardar
            productoBD.prepararGuardado();
            // =============================

            productoRepository.save(productoBD);

            // 4. Datos del detalle
            detalle.setPrecioUnitario(productoBD.getPrecio());
            detalle.setSubtotal(productoBD.getPrecio() * detalle.getCantidad());

            totalCalculado += detalle.getSubtotal();
        }

        venta.setTotal(totalCalculado);
        return ventaRepository.save(venta);
    }

    // Busca este método y CAMBIA su contenido:
    public List<Venta> listarTodas() {
        // ANTES: return ventaRepository.findAll();

        // AHORA: Usamos la consulta optimizada que trae los detalles
        return ventaRepository.findAllWithDetalles();
    }

    public double obtenerTotalVentasDia() {
        LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN); // Hoy a las 00:00
        LocalDateTime finDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);    // Hoy a las 23:59

        Double suma = ventaRepository.sumarVentasDelDia(inicioDia, finDia);

        // Si no hay ventas, devuelve null, así que lo convertimos a 0.0
        return (suma != null) ? suma : 0.0;
    }

    public List<Venta> buscarPorFechas(LocalDate inicio, LocalDate fin) {
        // Convertimos LocalDate (solo fecha) a LocalDateTime (fecha y hora exacta)
        LocalDateTime fechaInicio = inicio.atStartOfDay(); // 00:00:00
        LocalDateTime fechaFin = fin.atTime(LocalTime.MAX); // 23:59:59.999

        return ventaRepository.buscarPorRangoFecha(fechaInicio, fechaFin);
    }

}