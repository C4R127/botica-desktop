# 💊 Sistema de Gestión Farmacéutica (Botica Desktop)

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green) ![JavaFX](https://img.shields.io/badge/Frontend-JavaFX-blue) ![MySQL](https://img.shields.io/badge/Database-MySQL-lightgrey)

Aplicación de escritorio integral para la gestión administrativa y operativa de farmacias. Desarrollada con arquitectura **Full Stack** utilizando **Spring Boot** para la lógica de negocio y **JavaFX** para una interfaz moderna y responsiva.

Este proyecto soluciona problemas reales de control de stock, gestión de ventas y análisis de datos mediante reportes gráficos.

---

## 📸 Galería

| Dashboard de Ventas | Punto de Venta (POS) |
|:-------------------:|:--------------------:|
| ![Dashboard](screenshots/dashboard.png) | ![POS](screenshots/pos.png) |
| *Análisis de KPIs y gráficos en tiempo real* | *Carrito de compras y cálculo automático* |

| Gestión de Inventario | Historial de Transacciones |
|:---------------------:|:--------------------------:|
| ![Inventario](screenshots/inventario.png) | ![Historial](screenshots/historial.png) |
| *Alertas visuales de stock bajo* | *Filtros por fecha y usuario* |

---

## 🚀 Características Clave

* **🛒 Punto de Venta (POS):** Interfaz ágil para procesar ventas, cálculo automático de totales y actualización inmediata del inventario.
* **📦 Gestión de Inventario Inteligente:**
    * CRUD completo de productos.
    * **Alertas Visuales:** Las filas se tiñen de rojo automáticamente cuando el stock es crítico (<= 10 unidades).
    * **Soft Delete:** Implementación de borrado lógico para mantener la integridad histórica de las ventas.
* **📊 Business Intelligence:** Dashboard interactivo con gráficos de barras para visualizar el flujo de ventas semanal y los productos "Top Seller".
* **🔒 Seguridad y Usuarios:** Sistema de Login con validación de roles y credenciales en base de datos.
* **💾 Persistencia Robusta:** Manejo de transacciones ACID para asegurar que el stock no se descuadre ante ventas simultáneas.

---

## 🛠️ Tecnologías Utilizadas

### Backend (Lógica y Datos)
* **Lenguaje:** Java 21 (JDK 21).
* **Framework:** Spring Boot 3 (Inyección de dependencias, Spring Data JPA).
* **ORM:** Hibernate (Mapeo Objeto-Relacional).
* **Base de Datos:** MySQL (Relacional).

### Frontend (Interfaz de Usuario)
* **Framework UI:** JavaFX.
* **Estilos:** CSS3 (Diseño personalizado, paleta de colores corporativa y tipografía Segoe UI).
* **Librerías:** Lombok (Reducción de código repetitivo).

---

## ⚙️ Instalación y Ejecución

### Prerrequisitos
* Java JDK 21 instalado.
* MySQL Server (XAMPP o Workbench).
* Maven.

### Pasos
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/sistema-botica.git](https://github.com/tu-usuario/sistema-botica.git)
    ```
2.  **Configurar Base de Datos:**
    * Crear una base de datos en MySQL llamada `botica_db`.
    * El sistema generará las tablas automáticamente al iniciar (Hibernate `update`).
3.  **Configurar Credenciales:**
    * Editar `src/main/resources/application.properties` con tu usuario/pass de MySQL.
4.  **Ejecutar:**
    ```bash
    mvn spring-boot:run
    ```

---

*Estudiante de Ingeniería de Software | Full Stack Developer*

* 💼 (www.linkedin.com/in/carlos-eduardo-barra-cconcho)
* 📧 (carloseduardobc27@gmail.com)

---
*Este proyecto fue desarrollado con fines educativos para demostrar competencias en arquitectura de software y desarrollo Full Stack.*
