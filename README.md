# 💊 Sistema de Gestión Farmacéutica - Versión Portable (Standalone)

![Release](https://img.shields.io/badge/Release-Portable-blue) ![DB](https://img.shields.io/badge/Database-H2_Embedded-orange) ![JavaFX](https://img.shields.io/badge/Frontend-JavaFX-green)

Esta rama (`version-portable`) contiene la configuración necesaria para generar la versión distribuible del software. A diferencia de la versión de desarrollo (rama `main`), esta versión **no requiere la instalación de servidores externos (MySQL/XAMPP)**, ya que utiliza una base de datos embebida.

> 🔗 **Nota:** Si buscas el código fuente para desarrollo con MySQL, dirígete a la rama [main](../../tree/main).

---

## 🚀 Características de esta Versión

* **Base de Datos Embebida (H2):** El sistema crea y gestiona su propia base de datos en un archivo local (`botica_db.mv.db`) que viaja con el ejecutable.
* **Modo Servidor Automático:** Configurado con `AUTO_SERVER=TRUE` para evitar bloqueos de archivos y permitir reconexiones seguras.
* **Empaquetado EXE:** Preparado para ser convertido en ejecutable de Windows utilizando **Launch4j**.
* **Portabilidad Total:** La carpeta del programa puede moverse de una PC a otra (o ejecutarse desde un USB) manteniendo todos los datos.

---

## 🛠️ Guía de Construcción (Build)

Si deseas generar el instalador desde este código fuente, sigue estos pasos:

### 1. Generar el JAR
Ejecuta el ciclo de vida de Maven para limpiar y empaquetar el proyecto con las dependencias de H2:

```bash
mvn clean package
Esto generará el archivo en /target/botica-desktop-0.0.1-SNAPSHOT.jar.

2. Convertir a EXE (Launch4j)
Utiliza la herramienta Launch4j con la siguiente configuración recomendada:

Output: Sistema Botica.exe

Jar: El archivo generado en el paso 1.

Icon: Archivo .ico de 256x256 píxeles.

JRE Min Version: 21

Header Type: GUI.

📦 Instalación y Uso para el Cliente final
Para entregar este software a un cliente, solo necesitas proporcionar la carpeta con el ejecutable generado.

Requisitos Previos
Sistema Operativo: Windows 10/11.

Java Runtime (JRE/JDK) 21 instalado.

Credenciales por Defecto (Primer Uso)
El sistema generará automáticamente un usuario administrador si la base de datos está vacía:

Rol	Usuario	Contraseña
Administrador	admin	admin123

Exportar a Hojas de cálculo

📂 Estructura de Archivos en Producción
Una vez instalado/ejecutado, la carpeta del usuario se verá así:

Plaintext

/Carpeta_Del_Programa
│
├── Sistema Botica.exe    <-- Ejecutable principal
├── botica_db.mv.db       <-- Base de datos (NO BORRAR)
└── botica_db.trace.db    <-- Log de transacciones (temporal)
🔧 Diferencias Técnicas con la Rama Main
Característica	Rama main (Dev)	Rama version-portable
Base de Datos	MySQL (Puerto 3306)	H2 File (Local Storage)
Driver	mysql-connector-j	com.h2database:h2
Persistencia	Servidor XAMPP requerido	Archivo .mv.db autónomo
Propósito	Desarrollo y Tests	Producción y Distribución

Exportar a Hojas de cálculo

👤 Autor
[Carlos Eduardo Barra Cconcho] Full Stack Developer


---
