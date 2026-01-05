package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.entity.Producto;
import com.botica.botica_desktop.service.ProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.CheckBox;

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

    @FXML private TextField txtBuscar;
    @FXML private CheckBox chkStockBajo;

    // Variable para manejar el filtro
    private FilteredList<Producto> listaFiltrada;

    private final ProductoService productoService;
    private ObservableList<Producto> listaProductos;

    // 1. Variable para guardar temporalmente el producto que estamos editando
    private Producto productoSeleccionado = null;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @FXML
    public void initialize() {
        // 1. Configurar Columnas (Solo una vez)
        colNombre.setCellValueFactory(data -> data.getValue().nombreProperty());
        colPrecio.setCellValueFactory(data -> data.getValue().precioProperty().asObject());
        colStock.setCellValueFactory(data -> data.getValue().stockProperty().asObject());
        colVencimiento.setCellValueFactory(data -> data.getValue().fechaVencimientoProperty());

        // 2. Configurar Alerta Visual (Filas Rojas)
        tablaProductos.setRowFactory(tv -> new TableRow<Producto>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("fila-stock-bajo"); // Limpiar siempre

                if (item != null && !empty && item.getStock() <= 10) {
                    if (!getStyleClass().contains("fila-stock-bajo")) {
                        getStyleClass().add("fila-stock-bajo");
                    }
                }
            }
        });

        // 3. Cargar Datos Iniciales
        cargarProductos();

        // 4. Listener para Selección (Llenar formulario al hacer clic)
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                productoSeleccionado = newSelection;
                llenarFormulario(newSelection);
            }
        });

        // 5. LISTENERS DE BÚSQUEDA (¡Esto es lo que faltaba!)
        // Detecta cuando escribes en la barra de búsqueda
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());

        // Detecta cuando marcas la casilla de "Solo Stock Bajo"
        chkStockBajo.selectedProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());
    }

    private void cargarProductos() {
        // 1. Traemos la lista maestra de la BD
        listaProductos = FXCollections.observableArrayList(productoService.listarTodos());

        // 2. Envolvemos la lista en una FilteredList (inicialmente muestra todo)
        listaFiltrada = new FilteredList<>(listaProductos, p -> true);

        // 3. Envolvemos en una SortedList para que al ordenar columnas no se rompa el filtro
        SortedList<Producto> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaProductos.comparatorProperty());

        // 4. Asignamos a la tabla
        tablaProductos.setItems(listaOrdenada);
    }

    // NUEVO MÉTODO: Lógica de filtrado
    private void aplicarFiltros() {
        String filtroTexto = txtBuscar.getText().toLowerCase();
        boolean soloStockBajo = chkStockBajo.isSelected();

        listaFiltrada.setPredicate(producto -> {
            // Paso 1: Filtro por Texto (Nombre)
            // Si hay texto escrito y el nombre del producto no lo contiene, lo ocultamos
            if (filtroTexto != null && !filtroTexto.isEmpty()) {
                if (!producto.getNombre().toLowerCase().contains(filtroTexto)) {
                    return false;
                }
            }

            // Paso 2: Filtro por Stock Bajo
            // Si el checkbox está marcado y el producto tiene más de 10, lo ocultamos
            if (soloStockBajo) {
                if (producto.getStock() > 10) {
                    return false;
                }
            }

            // Si pasa ambas pruebas, se muestra
            return true;
        });
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