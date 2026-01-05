package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.config.SpringContext;
import com.botica.botica_desktop.session.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class DashboardController {

    @FXML
    private Label lblBienvenida;

    @FXML
    public void initialize() {
        if (SesionUsuario.getUsuarioActual() != null) {
            lblBienvenida.setText(
                    "Bienvenido, " + SesionUsuario.getUsuarioActual().getUsername()
            );
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            // 1. Limpiar sesión
            SesionUsuario.cerrarSesion();

            // 2. Cerrar dashboard actual
            Stage stageActual = (Stage) lblBienvenida.getScene().getWindow();
            stageActual.close();

            // 3. Volver a abrir login
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/javafx/login.fxml")
            );
            loader.setControllerFactory(SpringContext.getContext()::getBean);

            Parent root = loader.load();
            Stage stageLogin = new Stage();
            stageLogin.setTitle("Login - Botica");
            stageLogin.setScene(new Scene(root));
            stageLogin.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // === MÉTODOS DE NAVEGACIÓN ===

    // Este era el que faltaba y causaba el error
    @FXML
    public void abrirProductos() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/javafx/productos.fxml")
        );
        loader.setControllerFactory(SpringContext.getContext()::getBean);

        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Gestión de Productos");
        stage.show();
    }

    // Este es el nuevo para ventas
    @FXML
    public void abrirVentas() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/javafx/ventas.fxml")
        );
        loader.setControllerFactory(SpringContext.getContext()::getBean);

        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Punto de Venta");
        stage.show();
    }

    @FXML
    public void abrirHistorial() throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/javafx/historial.fxml")
        );
        loader.setControllerFactory(SpringContext.getContext()::getBean);

        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Historial de Ventas");
        stage.show();
    }

}