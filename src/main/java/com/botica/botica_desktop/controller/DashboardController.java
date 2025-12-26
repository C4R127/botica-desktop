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
            // 1️⃣ Limpiar sesión
            SesionUsuario.cerrarSesion();

            // 2️⃣ Cerrar dashboard
            Stage stageActual = (Stage) lblBienvenida.getScene().getWindow();
            stageActual.close();

            // 3️⃣ Volver a abrir login
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/javafx/login.fxml")
            );
            loader.setControllerFactory(
                    SpringContext.getContext()::getBean
            );

            Parent root = loader.load();

            Stage stageLogin = new Stage();
            stageLogin.setTitle("Login - Botica");
            stageLogin.setScene(new Scene(root));
            stageLogin.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
