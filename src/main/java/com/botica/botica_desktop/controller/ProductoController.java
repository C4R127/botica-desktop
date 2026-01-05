package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.entity.Producto;
import com.botica.botica_desktop.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProductoController {

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, LocalDate> colVencimiento;

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private DatePicker dpVencimiento;

    private final ProductoService productoService;
    private ObservableList<Producto> listaProductos;

    // 1. Variable para guardar temporalmente el producto que estamos editando
    private Producto productoSeleccionado = null;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(data -> data.getValue().nombreProperty());
        colPrecio.setCellValueFactory(data -> data.getValue().precioProperty().asObject());
        colStock.setCellValueFactory(data -> data.getValue().stockProperty().asObject());
        colVencimiento.setCellValueFactory(data -> data.getValue().fechaVencimientoProperty());

        cargarProductos();

        // 2. AGREGAR LISTENER: Detectar clic en la tabla para llenar el formulario
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection; // Guardamos la referencia
                llenarFormulario(newSelection);
            }
        });
    }

    private void cargarProductos() {
        listaProductos = FXCollections.observableArrayList(
                productoService.listarTodos()
        );
        tablaProductos.setItems(listaProductos);
    }

    // 3. Método auxiliar para llenar los campos de texto
    private void llenarFormulario(Producto p) {
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        dpVencimiento.setValue(p.getFechaVencimiento());
    }

    @FXML
    public void guardar() {
        Producto p;

        if (productoSeleccionado != null) {
            p = productoSeleccionado;
        } else {
            p = new Producto();
        }

        try {
            // Seteamos los valores en las propiedades de JavaFX
            p.setNombre(txtNombre.getText());
            p.setPrecio(Double.parseDouble(txtPrecio.getText()));
            p.setStock(Integer.parseInt(txtStock.getText()));
            p.setFechaVencimiento(dpVencimiento.getValue());
            p.setActivo(true);

            // === LÍNEA NUEVA Y CRUCIAL ===
            // Pasamos los datos de JavaFX a los campos que entiende la Base de Datos
            p.prepararGuardado();
            // ==============================

            productoService.guardar(p);

            cargarProductos();
            limpiarFormulario();

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese números válidos");
        }
    }

    @FXML
    public void eliminar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            productoService.eliminar(seleccionado.getId());
            cargarProductos();
            limpiarFormulario(); // Limpiamos por si acaso estaba seleccionado en el formulario
        }
    }

    // 5. Método modificado para reiniciar todo
    @FXML // (Asegúrate de vincular este método a un botón "Limpiar" o llamarlo al guardar)
    private void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        dpVencimiento.setValue(null);

        // MUY IMPORTANTE: Desmarcamos la selección para volver a modo "Crear Nuevo"
        productoSeleccionado = null;
        tablaProductos.getSelectionModel().clearSelection();
    }
}