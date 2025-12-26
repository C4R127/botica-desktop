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

		// 🔥 ESTA LÍNEA ES LA CLAVE
		SpringContext.setContext(context);
	}

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("/javafx/login.fxml")
		);

		// 🔥 JavaFX usa Spring
		loader.setControllerFactory(context::getBean);

		Scene scene = new Scene(loader.load(), 300, 250);
		stage.setTitle("Botica - Login");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop() {
		context.close();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
