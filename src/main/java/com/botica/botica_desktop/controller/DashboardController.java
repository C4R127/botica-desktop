package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.config.SpringContext;
import com.botica.botica_desktop.entity.Producto;
import com.botica.botica_desktop.service.ProductoService;
import com.botica.botica_desktop.service.VentaService;
import com.botica.botica_desktop.session.SesionUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DashboardController {

    private final VentaService ventaService;
    private final ProductoService productoService;

    // === ETIQUETAS BARRA DE ESTADO ===
    @FXML private Label lblBienvenida;
    @FXML private Label lblTotalVentasHoy;
    @FXML private Label lblProductosBajos;
    @FXML private Label lblTotalInventario;

    // === GRÁFICOS ===
    @FXML private BarChart<String, Number> graficoBarras;
    @FXML private LineChart<String, Number> graficoLineas;

    // Inyección de dependencias
    public DashboardController(VentaService ventaService, ProductoService productoService) {
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    @FXML
    public void initialize() {
        // 1. Mensaje de Bienvenida
        if (SesionUsuario.getUsuarioActual() != null) {
            lblBienvenida.setText("👋 Hola, " + SesionUsuario.getUsuarioActual().getUsername());
        }

        cargarKPIs();
        cargarGraficoBarras();
        cargarGraficoLineas();
    }

    private void cargarKPIs() {
        // KPI 1: Ventas Hoy
        double ventasHoy = ventaService.obtenerTotalVentasDia();
        lblTotalVentasHoy.setText(String.format("S/. %.2f", ventasHoy));

        // KPI 2: Stock Bajo
        long countBajos = productoService.listarTodos().stream().filter(p -> p.getStock() <= 10).count();
        lblProductosBajos.setText(String.valueOf(countBajos));

        // KPI 3: Total Productos
        long totalProds = productoService.listarTodos().size();
        lblTotalInventario.setText(String.valueOf(totalProds));
    }

    private void cargarGraficoBarras() {
        graficoBarras.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unidades Vendidas");

        List<Object[]> topProductos = ventaService.obtenerTopProductos();

        for (Object[] fila : topProductos) {
            String producto = (String) fila[0];
            Number cantidad = (Number) fila[1];
            series.getData().add(new XYChart.Data<>(producto, cantidad));
        }

        graficoBarras.getData().add(series);
    }

    private void cargarGraficoLineas() {
        graficoLineas.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos (S/.)");

        Map<String, Double> tendencia = ventaService.obtenerVentasUltimaSemana();

        for (Map.Entry<String, Double> entrada : tendencia.entrySet()) {
            // Convertir "2026-01-05" a "05/01" para que ocupe menos espacio
            String fechaCorta = entrada.getKey().substring(8, 10) + "/" + entrada.getKey().substring(5, 7);
            series.getData().add(new XYChart.Data<>(fechaCorta, entrada.getValue()));
        }

        graficoLineas.getData().add(series);
    }

    // === MÉTODOS DE NAVEGACIÓN (Se mantienen igual) ===
    @FXML
    private void cerrarSesion() {
        try {
            // 1. Limpiamos la sesión
            SesionUsuario.cerrarSesion();

            // 2. Cerramos la ventana GRANDE actual
            Stage stageActual = (Stage) lblBienvenida.getScene().getWindow();
            stageActual.close();

            // 3. Preparamos la ventana de LOGIN
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/login.fxml"));
            loader.setControllerFactory(SpringContext.getContext()::getBean);
            Parent root = loader.load();

            Stage stageLogin = new Stage();
            stageLogin.setTitle("Login - Botica");

            // --- CORRECCIÓN DE TAMAÑO ---
            // Forzamos el tamaño exacto de 600x450 y prohibimos cambiarlo
            Scene scene = new Scene(root, 600, 450);
            stageLogin.setScene(scene);
            stageLogin.setResizable(false); // ESTO BLOQUEA QUE SE AGRANDE
            stageLogin.centerOnScreen();    // LA CENTRA EN TU MONITOR
            // -----------------------------

            stageLogin.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirProductos() throws Exception {
        // 1. Abrimos la ventana y guardamos la referencia
        Stage stage = abrirVentana("/javafx/productos.fxml", "Gestión de Productos");

        // 2. DETECTOR: Cuando cierres la ventana de productos...
        stage.setOnHidden(event -> {
            System.out.println("Recalculando inventario en Dashboard...");
            cargarKPIs();           // Actualiza el contador de "Stock Crítico"
            cargarGraficoBarras();  // Actualiza nombres si editaste alguno
            // cargarGraficoLineas(); // (Opcional) No suele cambiar al editar productos
        });
    }

    @FXML
    public void abrirVentas() throws Exception {
        // 1. Abrimos la ventana y guardamos la referencia en 'stage'
        Stage stage = abrirVentana("/javafx/ventas.fxml", "Punto de Venta");

        // 2. MAGIA: Cuando esta ventana se cierre (Hidden), actualizamos el Dashboard
        stage.setOnHidden(event -> {
            System.out.println("Actualizando Dashboard...");
            cargarKPIs();
            cargarGraficoBarras();
            cargarGraficoLineas();
        });
    }

    @FXML
    public void abrirHistorial() throws Exception {
        abrirVentana("/javafx/historial.fxml", "Historial de Ventas");
    }

    private Stage abrirVentana(String fxml, String titulo) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setControllerFactory(SpringContext.getContext()::getBean);
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle(titulo);
        stage.show();
        return stage;
    }
}