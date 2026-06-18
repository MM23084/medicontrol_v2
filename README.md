# MediControl v2

Un sistema de control médico moderno desarrollado en **Java** con herramientas modernas de construcción.

## 📋 Descripción

MediControl v2 es una aplicación Java diseñada para gestionar y controlar procesos médicos. El proyecto utiliza un enfoque modular con Gradle como sistema de construcción, permitiendo escalabilidad y mantenimiento eficiente.

## 🛠️ Tecnologías

- **Lenguaje**: Java
- **Build Tool**: Gradle (Kotlin DSL)
- **Estructura**: Proyecto modular con Gradle

## 📁 Estructura del Proyecto

```
medicontrol_v2/
├── app/                    # Módulo principal de la aplicación
├── gradle/                 # Configuración de Gradle
├── build.gradle.kts        # Build script del proyecto
├── settings.gradle.kts     # Configuración de módulos
├── gradle.properties       # Propiedades del proyecto
├── gradlew                 # Gradle Wrapper (Linux/Mac)
├── gradlew.bat            # Gradle Wrapper (Windows)
└── .gitignore             # Archivos ignorados por Git
```

## 🚀 Requisitos Previos

- **Java Development Kit (JDK)**: 11 o superior
- **Gradle**: La versión requerida se descargará automáticamente mediante Gradle Wrapper

## 📦 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/MM23084/medicontrol_v2.git
cd medicontrol_v2
```

### 2. Construir el proyecto

#### En Linux/Mac:
```bash
./gradlew build
```

#### En Windows:
```bash
gradlew.bat build
```

## 🎯 Uso

### Compilar el proyecto
```bash
./gradlew build
```

### Ejecutar pruebas
```bash
./gradlew test
```

### Limpiar archivos generados
```bash
./gradlew clean
```

### Ejecutar la aplicación
```bash
./gradlew run
```

## 🔧 Configuración

Las propiedades del proyecto se pueden configurar en el archivo `gradle.properties`:

```properties
# Ejemplo de propiedades personalizadas
org.gradle.java.home=/path/to/jdk
```

## 📚 Estructura de Módulos

El proyecto utiliza una estructura modular definida en `settings.gradle.kts`. Cada módulo representa un componente específico de la aplicación.

## 🤝 Contribuir

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto actualmente no tiene una licencia especificada. Consulta con el propietario del repositorio para más información.

## 📧 Contacto

Para más información o preguntas, contacta a través de GitHub: [@MM23084](https://github.com/MM23084)

## 📝 Notas Adicionales

- El proyecto utiliza **Gradle Wrapper** para garantizar que todos los desarrolladores usen la misma versión de Gradle
- Se recomienda usar un IDE como **IntelliJ IDEA** o **Eclipse** para el desarrollo
- Revisa los cambios más recientes en la rama `master`

---

**Última actualización**: 2026-06-18
