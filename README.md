# controlalmacenservidor
Parte Servidor del proyecto de control de almacén.
# Proyecto - Aplicación de Almacén

## Índice

- [Introducción](#introducción)
- [Descripción General](#descripción-general)
- [Aspectos Técnicos](#aspectos-técnicos)
  - [App Móvil](#aspectos-técnicos---app-móvil)
  - [Servidor](#aspectos-técnicos---servidor)
- [Consideraciones Adicionales](#consideraciones-adicionales)

## Introducción

El proyecto "Control de Almacén" tiene como objetivo crear una aplicación para la gestión de inventarios en el almacén de una pequeña empresa. Este sistema permitirá a los empleados gestionar de manera eficiente las existencias, registrar altas y bajas de productos, y generar informes relacionados con las compras y el inventario. Aunque está pensado principalmente para bares, puede ser adaptado a otras pequeñas empresas.

El sistema consta de dos componentes principales:
- **App Móvil**: Interfaz con el usuario, accesible a través de dispositivos como tablets.
- **Servidor**: Almacena la información en una base de datos y gestiona las peticiones de la aplicación.

## Descripción General

La aplicación reemplaza el proceso manual de gestión de inventarios, permitiendo a los empleados conocer en tiempo real la cantidad de productos disponibles en el almacén. La aplicación ofrece funcionalidades simples como:
- **Diario**: Registro de altas y bajas de productos.
- **Perfiles**: Gestión de usuarios con diferentes permisos (empleados y administradores).
- **Albaranes**: Consulta y generación de informes de entradas de productos.
- **Inventario**: Consulta y generación de informes sobre productos en el almacén.

### Perfiles de usuario:
- **Usuarios**: Acceso solo al Diario.
- **Administradores**: Acceso completo a todas las funcionalidades.

### Características adicionales:
- Interfaz sencilla, fácil de usar, con interacción mínima.
- Acceso a la aplicación solo mediante identificación de usuario.
- Control de inactividad para garantizar la eficiencia.

### App Móvil

La aplicación móvil está desarrollada en **Kotlin** y **Java**. La interfaz debe ser sencilla, permitiendo realizar las acciones de manera eficiente con la menor cantidad de interacciones posibles.

#### Pantallas principales:

- **Pantalla de Inicio**: Logo animado.
- **Pantalla Principal - Diario**: Selección de perfil e interacción con productos (alta y baja de productos).
- **Añadir Producto**: Funcionalidad para registrar nuevos productos.
- **Menú Lateral**: Opciones avanzadas (Perfiles, Albaranes, Inventario), accesibles solo por administradores.

### Servidor

El servidor está desarrollado en **Spring Boot** y usa **MySQL** como base de datos. Se utiliza **JPA/Hibernate** para la persistencia y **Spring Security** para la autenticación y autorización de usuarios.

#### Comunicación:
- API RESTful con JWT para autenticación.
- Uso de Retrofit para consumir la API desde la aplicación móvil.

#### Base de datos:
- **MySQL**: Diseño relacional con tablas para usuarios, productos, albaranes, inventarios, etc.
- **Seguridad**: Control de accesos mediante roles (usuario y administrador).

## Consideraciones Adicionales

- **Ampliaciones futuras**: La aplicación se desarrollará pensando en su escalabilidad y mantenimiento a largo plazo.
- **Requerimientos adicionales**:
  - **Manual de usuario**.
  - **Trello** para la gestión del proyecto.
  - **GitHub** para el control de versiones.
- **Posibles ampliaciones**: Integración de reconocimiento de productos mediante cámara (código de barras o texto).


- **Lenguaje de programación**: Kotlin, Java.
- **Base de datos**: MySQL.
- **Gestor de versiones**: GitHub.
- **Comunicación**: REST, Spring Boot, JWT, Retrofit.
- **Notificaciones**: Integración con SMTP o API de correos.
