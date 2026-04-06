# Pokédex Web - Proyecto Full-Stack

Este proyecto es una aplicación web interactiva para explorar Pokémon, consumiendo los datos de la PokéAPI pública. Está construido bajo una arquitectura moderna dividida en Backend y Frontend.

## 🏗️ Arquitectura y Tecnologías

Este proyecto reside en un Monorepo que incluye los dos componentes principales:

### 1. Backend (Java / Spring Boot)
- **Ruta del componente:** `/backend`
- **Lenguaje:** Java 25 (LTS próximo/actual)
- **Framework Principal:** Spring Boot 3.3.x
- **Gestor de Dependencias:** Maven o Gradle (Por definir)
- **Dependencias y Librerías Principales:**
  - `spring-boot-starter-web`: Exposición de la API REST para que el frontend pueda solicitar datos.
  - `spring-boot-starter-data-jpa`: Capa de persistencia, facilitando el mapeo objeto-relacional (ORM) con Hibernate.
  - `h2`: Base de datos en memoria sumamente ligera. Ideal para la fase de desarrollo, permitiendo guardar favoritos o caché sin necesidad de instalar un motor externo.
  - `spring-boot-starter-webflux`: Cliente reactivo (`WebClient`) necesario para consumir eficientemente la PokéAPI externa.
  - `lombok`: Herramienta de productividad para reducir el "código repetitivo" (getters, setters, constructores) mediante anotaciones.

### 2. Frontend (Angular)
- **Ruta del componente:** `/frontend`
- **Lenguaje:** TypeScript
- **Framework:** Angular 18+
- **Estilos:** Vanilla CSS / SCSS (Enfocado a diseño Premium: Glassmorphism, micro-animaciones, colores vibrantes de tipo HSL, tipografía moderna como 'Inter' o 'Outfit').
- **Módulos Activos:**
  - `HttpClientModule`: Para comunicarse con nuestro Backend en Java.
  - `RouterModule`: Navegación fluida tipo SPA (Single Page Application) entre el listado general y el detalle individual de cada Pokémon.

---

## 🛠️ Entorno de Trabajo Requerido (Setup Inicial)

Para que puedas correr, compilar y modificar este proyecto en tu entorno local (Windows), necesitas:

1. **Java JDK:** (Instalado - detectada versión 25).
2. **Node.js (LTS):** Necesario para ejecutar Angular CLI y compilar el frontend. Descárgalo en [nodejs.org](https://nodejs.org/).
3. **IDE / Editor:** Se recomienda fuertemente **Visual Studio Code** (con extensiones de Angular y Java) o **IntelliJ IDEA** para la parte del servidor.
4. **Angular CLI:** Una vez instalado Node.js, debes instalar Angular globalmente abriendo una terminal y ejecutando:
   ```bash
   npm install -g @angular/cli
   ```

## 🚀 Instrucciones para Ejecutar el Proyecto

*(Esta sección se irá expandiendo a medida que generemos el código base)*

### Levantar el Backend
1. Abre una terminal en la carpeta `/backend`.
2. Ejecuta el comando `./mvnw spring-boot:run` (si usamos Maven) o su equivalente en Gradle.
3. El servidor se expondrá por defecto en `http://localhost:8080`.

### Levantar el Frontend
1. Abre una terminal en la carpeta `/frontend`.
2. Estando seguro de haber corrido previamente `npm install` para instalar dependencias, ejecuta:
   ```bash
   ng serve
   ```
3. La aplicación visual estará disponible en el navegador accediendo a `http://localhost:4200`.

---
*Este documento se mantendrá vivo y se actualizará a medida que integramos nuevos servicios, bases de datos externas o componentes avanzados.*
