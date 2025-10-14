# PokeAPI SOAP Gateway

**Versión Actual:** 1.2.3
**Estado:** MVP Completado - Primera Etapa Finalizada (6/6 Hitos)

Para instrucciones detalladas de pruebas end-to-end, consultar:
[Guía de Pruebas SOAP](./Documentacion/guia-pruebas-soap.md)

## Resumen del Proyecto

Este proyecto implementa un **Gateway de Servicios en Spring Boot**, actuando como un puente entre clientes que utilizan el estándar **SOAP (XML)** y el proveedor de datos **PokeAPI (REST/JSON)**.

**Objetivo de la Arquitectura Final:** Escalar a **N=12 Endpoints SOAP Granulares** con resiliencia (**Circuit Breaker**) y caching (**Caffeine**), siguiendo los principios de la **Arquitectura Hexagonal**.

---

## 1. Requisitos del Sistema

- **Java:** JDK 21 o superior.
- **Gestor de Dependencias:** Maven (v3.8+).
- **Base de Datos (Embedida):** H2 (incluida en la configuración del `pom.xml`).

---

## 2. Configuración e Inicio

### 2.1. Instalación y Generación de Clases JAXB

Antes de ejecutar, es obligatorio generar las clases Java a partir de los esquemas XSD/WSDL.

```bash
# Ejecutar la fase de 'generate-sources' de Maven
$ mvn clean install
```

### 2.2. Ejecución de la Aplicación

El servicio se ejecuta en el puerto por defecto de Spring Boot: **8080**.

```bash
# Ejecutar la aplicación con Spring Boot
$ mvn spring-boot:run
```

---

## 3. Endpoints SOAP Disponibles (Hito 1)

En esta etapa de Mínimo Viable, se expone un único endpoint que consolida los 6 métodos requeridos.

| Servicio | WSDL URL | Descripción |
| :--- | :--- | :--- |
| `PokeCoreService` | `http://localhost:8080/ws/pokemon-core.wsdl` | Expone los 6 métodos obligatorios (ID, Name, Abilities, etc.). |

### Ejemplo de Validación (Método: `getBaseExperience`)

Para probar la funcionalidad mínima, se puede enviar una solicitud `SOAPAction` al endpoint.

```bash
# Comando cURL para obtener la experiencia base de un Pokémon (ej. Bulbasaur)
curl --location 'http://localhost:8080/ws/pokemon-core' \
--header 'Content-Type: text/xml' \
--data '
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:gs="http://pokegateway.com/soap/gen">
   <soapenv:Header/>
   <soapenv:Body>
      <gs:getBaseExperienceRequest>
         <gs:name>bulbasaur</gs:name>
      </gs:getBaseExperienceRequest>
   </soapenv:Body>
</soapenv:Envelope>
'
```

**Respuesta Esperada:** Se recibirá una respuesta XML con la experiencia base de Bulbasaur (`142`).

---

## 4. Verificación del Estado Actual (v0.1.1)

### 4.1. Puntos de Verificación Implementados

- [x] Servicio SOAP base funcionando
- [x] Base de datos H2 configurada
- [x] AOP para logging implementado
- [x] Arquitectura Hexagonal base implementada
- [x] Captura de IP en logs
- [x] Medición de tiempos de respuesta

### 4.2. Acceso a Herramientas de Monitoreo

- **Consola H2:** `http://localhost:8080/h2-console`
  - **JDBC URL:** `jdbc:h2:mem:testdb`
  - **Usuario:** `sa`
  - **Contraseña:** (dejar en blanco)
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Actuator:** `http://localhost:8080/actuator`

### 4.3. Pruebas Disponibles

1. **Tests Unitarios:** 
   ```bash
   mvn test
   ```

2. **Tests de Integración con Cucumber:**
   ```bash
   mvn verify
   ```

3. **Verificación Manual del Endpoint SOAP:**
   - Tiempo de arranque esperado: ~30 segundos
   - Primera llamada puede tardar hasta 5 segundos (calentamiento)
   - Llamadas subsecuentes deben responder en <1 segundo

### 4.4. Métricas y Logs

- Verificar logs de requests en tabla `REQUEST_LOG`
- Revisar métricas de tiempo de respuesta en Actuator
- Consultar trazas de IP y métodos invocados

---

## 🚧 Próximos Pasos (Transición a Etapa 2)

1. **Migración a OpenFeign** *(En Progreso)*
   - Reemplazo de RestTemplate
   - Configuración de timeouts
   - Manejo de errores mejorado

2. **Resiliencia y Performance**
   - Implementar Circuit Breaker
   - Configurar Caffeine Cache
   - Optimizar tiempos de arranque

3. **Mejoras de Calidad**
   - Coverage con SonarQube
   - Ampliación de scenarios Cucumber
   - Documentación OpenAPI completa