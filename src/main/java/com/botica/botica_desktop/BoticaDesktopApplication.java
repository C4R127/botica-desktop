package com.botica.botica_desktop;

import com.botica.botica_desktop.entity.Usuario;
import com.botica.botica_desktop.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BoticaDesktopApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoticaDesktopApplication.class, args);
	}

	@Bean
	CommandLineRunner initUsuarios(UsuarioService usuarioService) {
		return args -> {
			if (usuarioService.buscarPorUsername("admin").isEmpty()) {
				Usuario admin = new Usuario();
				admin.setUsername("admin");
				admin.setPassword("admin123"); // luego lo encriptamos
				admin.setRol("ADMIN");

				usuarioService.guardar(admin);
				System.out.println("✔ Usuario admin creado");
			}
		};
	}
}
