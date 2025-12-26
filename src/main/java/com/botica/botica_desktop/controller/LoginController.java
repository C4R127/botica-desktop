package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.entity.Usuario;
import com.botica.botica_desktop.service.UsuarioService;
import com.botica.botica_desktop.session.SesionUsuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMensaje;

    private final UsuarioService usuarioService;
    private final ApplicationContext context;

    public LoginController(UsuarioService usuarioService, ApplicationContext context) {
        this.usuarioService = usuarioService;
        this.context = context;
    }

    @FXML
    public void login() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        Optional<Usuario> usuarioOpt = usuarioService.login(user, pass);

        if (usuarioOpt.isPresent()) {
            SesionUsuario.setUsuarioActual(usuarioOpt.get());
            abrirDashboard();
            cerrarVentanaLogin();
        } else {
            alerta("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
        }
    }


    private void abrirDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/javafx/dashboard.fxml")
            );

            // ✅ USAR EL CONTEXTO INYECTADO
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sistema de Botica");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void cerrarVentanaLogin() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }

    private void alerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
