package com.botica.botica_desktop.entity;

import jakarta.persistence.*;
import javafx.beans.property.*;
import java.time.LocalDate;

@Entity
@Table(name = "productos")
public class Producto {

    /* =======================
       CAMPOS JPA (BASE DATOS)
       ======================= */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORRECCIÓN: Agregamos (name = "nombre") para apuntar a la columna correcta
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombreBD;

    @Column(name = "precio", nullable = false)
    private double precioBD;

    @Column(name = "stock", nullable = false)
    private int stockBD;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimientoBD;

    @Column(name = "activo", nullable = false)
    private boolean activoBD = true;

    /* =======================
       CAMPOS JAVAFX (UI)
       ======================= */

    @Transient
    private StringProperty nombre = new SimpleStringProperty();

    @Transient
    private DoubleProperty precio = new SimpleDoubleProperty();

    @Transient
    private IntegerProperty stock = new SimpleIntegerProperty();

    @Transient
    private ObjectProperty<LocalDate> fechaVencimiento =
            new SimpleObjectProperty<>();

    @Transient
    private BooleanProperty activo =
            new SimpleBooleanProperty(true);

    /* =======================
       SINCRONIZACIÓN JPA ↔ FX
       ======================= */

    // 1. Quitamos @PreUpdate para evitar conflictos y lo hacemos PÚBLICO
    @PrePersist
    public void prepararGuardado() {
        if (nombre.get() != null) this.nombreBD = nombre.get();
        this.precioBD = precio.get();
        this.stockBD = stock.get();
        this.fechaVencimientoBD = fechaVencimiento.get();
        this.activoBD = activo.get();
    }

    @PostLoad
    private void cargarDespuesDeLeer() {
        this.nombre.set(nombreBD);
        this.precio.set(precioBD);
        this.stock.set(stockBD);
        this.fechaVencimiento.set(fechaVencimientoBD);
        this.activo.set(activoBD);
    }

    /* =======================
       GETTERS / SETTERS JPA
       ======================= */

    public Long getId() {
        return id;
    }

    /* =======================
       GETTERS / SETTERS FX
       ======================= */

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public double getPrecio() {
        return precio.get();
    }

    public void setPrecio(double precio) {
        this.precio.set(precio);
    }

    public int getStock() {
        return stock.get();
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento.get();
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento.set(fechaVencimiento);
    }

    public boolean isActivo() {
        return activo.get();
    }

    public void setActivo(boolean activo) {
        this.activo.set(activo);
    }

    /* =======================
       PROPERTIES (TableView)
       ======================= */

    public StringProperty nombreProperty() {
        return nombre;
    }

    public DoubleProperty precioProperty() {
        return precio;
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public ObjectProperty<LocalDate> fechaVencimientoProperty() {
        return fechaVencimiento;
    }

    public BooleanProperty activoProperty() {
        return activo;
    }
}
