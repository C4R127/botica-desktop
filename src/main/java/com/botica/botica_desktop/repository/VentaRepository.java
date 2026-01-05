package com.botica.botica_desktop.repository;

import com.botica.botica_desktop.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // IMPORTANTE
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // ESTA ES LA SOLUCIÓN MÁGICA:
    // "Trae la Venta (v), únete y trae sus detalles (d),
    // y de esos detalles trae sus productos (p), y también trae al usuario (u)"
    @Query("SELECT DISTINCT v FROM Venta v " +
            "LEFT JOIN FETCH v.detalles d " +
            "LEFT JOIN FETCH d.producto p " +
            "JOIN FETCH v.usuario u")
    List<Venta> findAllWithDetalles();

    // Suma el campo 'total' de las ventas en un rango de fechas
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin")
    Double sumarVentasDelDia(LocalDateTime inicio, LocalDateTime fin);

    // Busca ventas en un rango de fechas y carga sus detalles (JOIN FETCH)
    @Query("SELECT DISTINCT v FROM Venta v " +
            "LEFT JOIN FETCH v.detalles d " +
            "LEFT JOIN FETCH d.producto p " +
            "JOIN FETCH v.usuario u " +
            "WHERE v.fecha BETWEEN :inicio AND :fin")
    List<Venta> buscarPorRangoFecha(LocalDateTime inicio, LocalDateTime fin);

}