# PokeAPI SOAP Gateway

**Estado Actual:** Hito 1/4: Configuración Base (Mínimo Viable en Progreso)

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

## 4. Persistencia y Auditoría Mínima

En esta etapa, se ha configurado la base de datos H2 para guardar los logs de auditoría obligatorios.

- **Logs Persistidos:** IP de origen, Fecha/Hora y Método SOAP invocado.
- **Consola H2:** `http://localhost:8080/h2-console`

---

## 🚧 Próximos Pasos (Transición a Etapa 2)

La próxima etapa se centrará en:

1.  **Migrar `RestTemplate` a OpenFeign**.
2.  **Implementar Auditoría Completa usando AOP**.
3.  **Implementar la suite de pruebas Cucumber y SonarQube**.