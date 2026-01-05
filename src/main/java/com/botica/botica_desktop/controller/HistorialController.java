package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.entity.DetalleVenta;
import com.botica.botica_desktop.entity.Venta;
import com.botica.botica_desktop.service.VentaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

@Component
public class HistorialController {

    private final VentaService ventaService;

    // === TABLA VENTAS (MAESTRA) ===
    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Long> colId;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colVendedor;
    @FXML private TableColumn<Venta, Double> colTotal;

    // === TABLA DETALLES (DETALLE) ===
    @FXML private Label lblDetalleVenta; // Título dinámico "Detalle Venta #X"
    @FXML private TableView<DetalleVenta> tablaDetalles;
    @FXML private TableColumn<DetalleVenta, String> colProducto;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotal;

    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;

    public HistorialController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @FXML
    public void initialize() {
        configurarTablas();
        cargarDatos();
    }

    private void configurarTablas() {
        // 1. Configuración Tabla VENTAS
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));

        // Formatear fecha bonita (dd/MM/yyyy HH:mm)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colFecha.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFecha().format(formatter)));

        colVendedor.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsuario().getUsername()));
        colTotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTotal()));

        // 2. Configuración Tabla DETALLES
        colProducto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProducto().getNombre()));
        colCantidad.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCantidad()));
        colSubtotal.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getSubtotal()));

        // 3. Listener: Cuando seleccionan una venta, mostramos sus detalles
        tablaVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarDetalles(newVal);
            }
        });
    }

    private void cargarDatos() {
        // Por defecto: Ver ventas de hoy
        dpDesde.setValue(LocalDate.now());
        dpHasta.setValue(LocalDate.now());
        filtrarVentas();
    }


    private void mostrarDetalles(Venta venta) {
        lblDetalleVenta.setText("Contenido de la Venta #" + venta.getId());
        // La lista de detalles ya viene dentro del objeto Venta gracias a JPA
        tablaDetalles.setItems(FXCollections.observableArrayList(venta.getDetalles()));
    }

    @FXML
    public void filtrarVentas() {
        LocalDate inicio = dpDesde.getValue();
        LocalDate fin = dpHasta.getValue();

        if (inicio == null || fin == null) {
            mostrarAlerta("Fechas requeridas", "Seleccione fecha de inicio y fin.");
            return;
        }

        if (inicio.isAfter(fin)) {
            mostrarAlerta("Error de rango", "La fecha 'Desde' no puede ser mayor a 'Hasta'.");
            return;
        }

        // Llamamos al servicio con el rango
        tablaVentas.setItems(FXCollections.observableArrayList(
                ventaService.buscarPorFechas(inicio, fin)
        ));
    }

    @FXML
    public void limpiarFiltros() {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        // Carga ABSOLUTAMENTE TODO (Cuidado si tienes miles de ventas)
        tablaVentas.setItems(FXCollections.observableArrayList(ventaService.listarTodas()));
    }

    // Auxiliar para alertas
    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

}