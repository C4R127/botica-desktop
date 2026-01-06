package com.botica.botica_desktop;

import com.botica.botica_desktop.config.SpringContext;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BoticaDesktopApplication extends Application {

	private static ConfigurableApplicationContext context;

	@Override
	public void init() {
		context = SpringApplication.run(BoticaDesktopApplication.class);

		// Guardamos el contexto de Spring para usarlo en los controladores
		SpringContext.setContext(context);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/javafx/login.fxml")
		);

		// JavaFX usa los beans de Spring
		loader.setControllerFactory(context::getBean);

		// --- CAMBIOS REALIZADOS AQUÍ ---
		// 1. Establecemos el tamaño fijo de 600x450
		Scene scene = new Scene(loader.load(), 600, 450);

		stage.setTitle("Botica - Login");
		stage.setScene(scene);

		// 2. Bloqueamos el redimensionamiento
		stage.setResizable(false);

		// 3. (Opcional) Centramos la ventana al iniciar
		stage.centerOnScreen();

		stage.show();
		// -------------------------------
	}

	@Override
	public void stop() {
		context.close();
	}

	public static void main(String[] args) {
		launch(args);
	}
}