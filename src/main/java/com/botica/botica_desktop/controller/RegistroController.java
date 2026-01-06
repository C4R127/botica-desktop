package com.botica.botica_desktop.controller;

import com.botica.botica_desktop.entity.Usuario;
import com.botica.botica_desktop.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class RegistroController {

    private final UsuarioService usuarioService;

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private PasswordField txtPassConfirm;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @FXML
    public void registrarse() {
        String user = txtUser.getText();
        String pass = txtPass.getText();
        String confirm = txtPassConfirm.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Error", "Por favor completa todos los campos.");
            return;
        }

        if (!pass.equals(confirm)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return;
        }

        try {
            Usuario nuevo = new Usuario();
            nuevo.setUsername(user);
            nuevo.setPassword(pass);
            nuevo.setRol("VENDEDOR"); // Rol por defecto (no ADMIN)
            nuevo.setActivo(true);

            usuarioService.guardar(nuevo);

            mostrarAlerta("Éxito", "Cuenta creada correctamente. ¡Ahora inicia sesión!");
            cerrarVentana();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo registrar: " + e.getMessage());
        }
    }

    @FXML
    public void cerrarVentana() {
        // Cierra solo la ventanita de registro
        Stage stage = (Stage) txtUser.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}