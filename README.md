# SocialWork

Red social profesional para trabajadores sociales. Es un proyecto que hice durante el ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM).

## Tecnologías

- Java con Jakarta EE (arquitectura MVC) y Servlets
- Maven para gestionar el proyecto y las dependencias
- MySQL como base de datos
- Docker y Docker Compose para levantar todo el entorno
- HTML, CSS y JavaScript en el frontend

## Arquitectura

El proyecto sigue el patrón MVC:

- **Vista:** páginas HTML con CSS y JavaScript.
- **Controlador:** Servlets de Java que reciben las peticiones del navegador.
- **Modelo / datos:** clases Java y base de datos MySQL.

Cada botón de la interfaz sigue el mismo recorrido: HTML → JavaScript → Servlet → base de datos. Documenté ese recorrido para las 24 acciones interactivas de la aplicación.

## Estructura del repositorio

| Ruta | Contenido |
|---|---|
| `src/main` | Código de la aplicación (backend y frontend) |
| `mysql/init` | Scripts SQL que crean e inicializan la base de datos |
| `Dockerfile` | Imagen de la aplicación |
| `docker-compose.yml` | Aplicación y base de datos juntas |
| `pom.xml` | Configuración de Maven |
| `railway.json` | Configuración de despliegue |

## Cómo ejecutarlo

Requisitos: Docker y Docker Compose instalados.

1. Clona el repositorio:
   ```bash
   git clone https://github.com/sergioroldantorres2016-rgb/socialwork.git
   cd socialwork
   ```
2. Crea tu archivo de variables de entorno a partir de `.env.example` (con tus propias contraseñas) y no lo subas al repositorio.
3. Levanta la aplicación y la base de datos:
   ```bash
   docker compose up --build
   ```
4. Abre el navegador en `http://localhost:8080` (el puerto puede cambiar, está en `docker-compose.yml`).

## Qué aprendí

- Organizar una aplicación web con el patrón MVC en Jakarta EE.
- Conectar un Servlet con una base de datos MySQL.
- Montar un entorno completo con Docker Compose.
- Documentar el flujo de datos de cada función de la aplicación.

## Autor

Sergio Roldán Torres, estudiante de DAM.
[LinkedIn](https://www.linkedin.com/in/sergio-rold%C3%A1n-torres/) · [GitHub](https://github.com/sergioroldantorres2016-rgb)
