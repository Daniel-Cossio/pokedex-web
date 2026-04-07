# 🔴 Pokédex Pro Wiki - Proyecto Full-Stack

Pokédex Pro es una enciclopedia Pokémon de alto rendimiento y diseño premium (*Dark Mode / Glassmorphism*), diseñada para ofrecer una experiencia de usuario fluida y datos de combate precisos. El proyecto utiliza una arquitectura de **Backend for Frontend (BFF)** para consolidar datos de la PokéAPI y servirlos de forma optimizada y localizada al español.

---

## 🏗️ Arquitectura y Tecnologías

El proyecto está organizado en un monorepo con una separación clara entre lógica de negocio/agregación y presentación.

### 1. ⚙️ Backend (BFF - Backend for Frontend)
- **Localización:** `/backend`
- **Lenguaje:** `Java 17 (LTS)`
- **Framework:** `Spring Boot 4.0.5`
- **Gestor de Construcción:** `Gradle 8.12.1`
- **Lógica de Negocio:** 
  - Agregación multithreaded de PokéAPI.
  - Motor dinámico de efectividad de tipos (Generación VI+).
  - Diccionario de traducción nativo para tipos, habilidades, movimientos y grupos huevo.

### 2. 🎨 Frontend (Diseño Premium)
- **Localización:** `/frontend`
- **Framework:** `Angular 21.2.x` (Arquitectura Zoneless)
- **Gestión de Estado:** `Angular Signals`
- **Estilos:** `Vanilla CSS` con variables CSS para temas dinámicos.
- **Diseño UI/UX:**
  - **Glassmorphism:** Paneles traslúcidos con desenfoque de fondo.
  - **Dark Mode:** Paleta de colores optimizada para legibilidad.
  - **Performance:** Carga perezosa (Lazy loading) de movimientos y optimización de imágenes.

---

## 🌟 Funcionalidades Principales

### 🌍 Localización Total
- **100% en Español:** Nombres, descripciones de habilidades, efectos de movimientos, tipos y categorías de búsqueda.
- **Formato Regional:** Altura en metros (m) y peso en kilogramos (kg).

### ⚔️ Inteligencia de Combate
- **Analizador de Tipos:** Cálculo dinámico de debilidades (x2, x4), resistencias (x0.5, x0.25) e inmunidades (x0).
- **Matriz Global:** Sección dedicada con una matriz interactiva de 18x18 tipos.
- **Calculador Dual:** Herramienta para combinar dos tipos y ver sus debilidades críticas combinadas.

### 📚 Base de Datos Completa
- **Movimientos:** Desglose completo por Nivel, MT/MO, Huevo y Tutor.
- **Evoluciones:** Cadena evolutiva visual y detallada.
- **Generaciones:** Navegación optimizada por regiones (Kanto -> Paldea).

---

## 🛠️ Instalación y Configuración Local

### Prerrequisitos
- **Java 17+**
- **Node.js 25+** (LTS recomendado para npm)
- **Angular CLI (`npm install -g @angular/cli`)**

### Pasos para Ejecutar
1. **Backend:**
   ```powershell
   cd backend
   ./gradlew bootRun
   ```
   El backend estará disponible en `http://localhost:8080/api/pokemon`

2. **Frontend:**
   ```powershell
   cd frontend
   npm install
   npx ng serve --port 4200
   ```
   Accede a la interfaz en `http://localhost:4200`

---

## 🚀 Despliegue (Production Ready)

### Gestión de IPs Dinámicas (Environments)
El proyecto utiliza el sistema de **Environments** de Angular para evitar URLs harcodeadas.
- **Local:** Usa `src/environments/environment.development.ts`.
- **Producción:** Usa `src/environments/environment.ts`.

#### Despliegue del Backend (Render / Railway / Heroku)
1. Conecta tu repo y selecciona el directorio `backend`.
2. Comando de construcción: `./gradlew build`.
3. Comando de inicio: `java -jar build/libs/pokedex-0.0.1-SNAPSHOT.jar`.
4. **IMPORTANTE:** Copia la URL pública generada.

#### Despliegue del Frontend (Vercel / Netlify)
1. Edita `src/environments/environment.ts` y pega la URL de tu backend en `apiUrl`.
2. Conecta tu repo y selecciona el directorio `frontend`.
3. Comando de construcción: `ng build`.
4. Directorio de salida: `dist/pokedex-frontend/browser`.

---

## 📂 Estructura del Proyecto (Referencia)

```text
/
├── backend/
│   ├── src/main/java/com/pokemon/pokedex/
│   │   ├── ControladorPokemon.java  <-- Endpoints REST
│   │   ├── ServicioPokemon.java     <-- Lógica PokéAPI + Traducciones
│   │   ├── TablaEfectividad.java    <-- Motor de tipos
│   │   └── ModeloPokemon.java       <-- DTOs optimizados
├── frontend/
│   ├── src/app/
│   │   ├── components/
│   │   │   ├── home/                <-- Grilla de generaciones
│   │   │   ├── detail/              <-- Enciclopedia individual
│   │   │   └── types-chart/         <-- Matriz y calculadora dual
│   │   └── services/
│   │       └── poke.service.ts      <-- Comunicación con BFF
```

---
*Este proyecto es parte de una iniciativa de enciclopedia Pokémon profesional y es de uso libre para aprendizaje.*
