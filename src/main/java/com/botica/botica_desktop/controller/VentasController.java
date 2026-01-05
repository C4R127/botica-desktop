package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.entity.DetalleVenta;
import com.botica.botica_desktop.entity.Producto;
import com.botica.botica_desktop.entity.Venta;
import com.botica.botica_desktop.service.ProductoService;
import com.botica.botica_desktop.service.VentaService;
import com.botica.botica_desktop.session.SesionUsuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VentasController {

    // === SERVICIOS ===
    private final ProductoService productoService;
    private final VentaService ventaService;

    // === COMPONENTES FXML ===
    @FXML private Label lblFecha;
    @FXML private Label lblTotal;

    // Buscador
    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tablaBusqueda;
    @FXML private TableColumn<Producto, String> colBusqNombre;
    @FXML private TableColumn<Producto, Integer> colBusqStock;
    @FXML private TableColumn<Producto, Double> colBusqPrecio;

    // Agregar
    @FXML private TextField txtCantidad;

    // Carrito
    @FXML private TableView<DetalleVenta> tablaCarrito;
    @FXML private TableColumn<DetalleVenta, String> colCarrProducto;
    @FXML private TableColumn<DetalleVenta, Integer> colCarrCant;
    @FXML private TableColumn<DetalleVenta, Double> colCarrSubtotal;

    // Resumen
    @FXML private Label lblTotalDia;

    // === VARIABLES DE ESTADO ===
    private ObservableList<Producto> listaProductosInventario;
    private ObservableList<DetalleVenta> listaCarrito;
    private double totalVenta = 0.0;

    // Constructor con Inyección de Dependencias
    public VentasController(ProductoService productoService, VentaService ventaService) {
        this.productoService = productoService;
        this.ventaService = ventaService;
    }

    @FXML
    public void initialize() {
        // 1. Configurar Fecha Actual
        lblFecha.setText("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // 2. Configurar Columnas Tabla BÚSQUEDA
        colBusqNombre.setCellValueFactory(cell -> cell.getValue().nombreProperty());
        colBusqStock.setCellValueFactory(cell -> cell.getValue().stockProperty().asObject());
        colBusqPrecio.setCellValueFactory(cell -> cell.getValue().precioProperty().asObject());

        // 3. Configurar Columnas Tabla CARRITO
        colCarrProducto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProducto().getNombre()));
        // Nota: DetalleVenta no tiene Properties de JavaFX, usamos wrappers simples
        colCarrCant.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getCantidad()).asObject());
        colCarrSubtotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getSubtotal()).asObject());

        // 4. Inicializar Listas
        listaCarrito = FXCollections.observableArrayList();
        tablaCarrito.setItems(listaCarrito);

        cargarInventario();

        actualizarTotalDia();
    }

    private void cargarInventario() {
        // Cargamos todos los productos para filtrar en memoria (más rápido para apps de escritorio)
        List<Producto> productos = productoService.listarTodos();
        // Filtramos solo los activos y con stock > 0
        listaProductosInventario = FXCollections.observableArrayList(
                productos.stream().filter(Producto::isActivo).collect(Collectors.toList())
        );
        tablaBusqueda.setItems(listaProductosInventario);
    }

    @FXML
    public void buscarProducto() {
        String filtro = txtBuscar.getText().toLowerCase();

        if (filtro.isEmpty()) {
            tablaBusqueda.setItems(listaProductosInventario);
        } else {
            // Filtramos la lista original sin volver a consultar la BD
            List<Producto> filtrados = listaProductosInventario.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(filtro))
                    .collect(Collectors.toList());
            tablaBusqueda.setItems(FXCollections.observableArrayList(filtrados));
        }
    }

    @FXML
    public void agregarProducto() {
        Producto productoSeleccionado = tablaBusqueda.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            mostrarAlerta("Atención", "Seleccione un producto de la lista izquierda.", Alert.AlertType.WARNING);
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText());

            if (cantidad <= 0) {
                mostrarAlerta("Error", "La cantidad debe ser mayor a 0.", Alert.AlertType.ERROR);
                return;
            }

            if (cantidad > productoSeleccionado.getStock()) {
                mostrarAlerta("Stock Insuficiente", "Solo quedan " + productoSeleccionado.getStock() + " unidades.", Alert.AlertType.WARNING);
                return;
            }

            // Crear el detalle (item del carrito)
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(productoSeleccionado);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(productoSeleccionado.getPrecio());
            detalle.setSubtotal(cantidad * productoSeleccionado.getPrecio());

            // Agregarlo a la lista visual
            listaCarrito.add(detalle);
            calcularTotal();

            // Limpiar campo cantidad y foco
            txtCantidad.setText("1");
            txtBuscar.requestFocus();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese una cantidad numérica válida.", Alert.AlertType.ERROR);
        }
    }

    // Método para eliminar un item antes de vender
    @FXML
    public void quitarDelCarrito() {
        DetalleVenta seleccionado = tablaCarrito.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Selección requerida", "Seleccione un producto del carrito para quitarlo.", Alert.AlertType.WARNING);
            return;
        }

        // Lo eliminamos de la lista
        listaCarrito.remove(seleccionado);

        // Recalculamos el total
        calcularTotal();

        // (Opcional) Devolvemos el foco al buscador para seguir vendiendo rápido
        txtBuscar.requestFocus();
    }


    private void calcularTotal() {
        totalVenta = 0.0;
        for (DetalleVenta d : listaCarrito) {
            totalVenta += d.getSubtotal();
        }
        lblTotal.setText(String.format("S/. %.2f", totalVenta));
    }

    @FXML
    public void finalizarVenta() {
        if (listaCarrito.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "No hay productos para vender.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // 1. Crear el objeto Venta
            Venta venta = new Venta();
            venta.setUsuario(SesionUsuario.getUsuarioActual()); // Usuario logueado
            venta.setTotal(totalVenta);

            // 2. Asociar los detalles
            // (Es importante crear una nueva lista o pasar los elementos, ya que listaCarrito es observable de JavaFX)
            for (DetalleVenta detalle : listaCarrito) {
                venta.agregarDetalle(detalle);
            }

            // 3. Guardar en Base de Datos (Esto descuenta stock también)
            ventaService.registrarVenta(venta);

            actualizarTotalDia();

            // 4. Éxito
            mostrarAlerta("Venta Exitosa", "La venta se registró correctamente.", Alert.AlertType.INFORMATION);

            // 5. Reiniciar interfaz
            listaCarrito.clear();
            calcularTotal();
            cargarInventario(); // Recargar productos para ver el stock actualizado
            txtBuscar.clear();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "No se pudo procesar la venta: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void actualizarTotalDia() {
        double totalDia = ventaService.obtenerTotalVentasDia();
        lblTotalDia.setText(String.format("Ventas Hoy: S/. %.2f", totalDia));
    }

}