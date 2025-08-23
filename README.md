# TiendaFuncional

> **Java 17 · Maven (multimódulo) · Clean/Hexa style (core/cli/rest) · Enfoque funcional (inmutabilidad + reglas puras)**

Proyecto de **tienda de auriculares** con dominio funcional y capas desacopladas. El **core** concentra las reglas; **cli** y **rest** solo orquestan IO.

## 🧱 Módulos

```
TiendaFuncional/
├─ core/   # Dominio y reglas (funciones puras, VOs, servicios de cálculo)
├─ cli/    # Interfaz de consola (usa core)
└─ rest/   # Spring Boot 3 (API HTTP) que delega en core
```

## 🔧 Requisitos

* **Java 17+**
* **Maven 3.9+**

## 📦 Parent POM (resumen)

* **groupId**: `tech.terabyte.labs`
* **artifactId**: `tienda-funcional`
* **version**: `1.0-SNAPSHOT`
* **packaging**: `pom`
* **modules**: `core`, `cli`, `rest`
* **BOMs/Plugins**:

    * JUnit BOM **5.11.0**
    * Spring Boot deps **3.5.4**
    * `maven-compiler-plugin` **3.14.0** (`release=17`)
    * `maven-assembly-plugin` **3.6.0** (solo en **cli**)
    * `spring-boot-maven-plugin` **3.5.4** (solo en **rest**)

## 🏗️ Build

Compilar todo (saltando tests):

```bash
mvn -q -DskipTests clean package
```

Solo **CLI** (construye core como dependencia):

```bash
mvn -q -DskipTests -pl cli -am package
```

Solo **REST** (construye core como dependencia):

```bash
mvn -q -DskipTests -pl rest -am package
```

## ▶️ Ejecutar

**CLI** (jar “fat” creado por assembly):

```bash
java -jar cli/target/cli-*-jar-with-dependencies.jar
```

**REST** (Spring Boot):

```bash
# Opción A: desde Maven
mvn -q -DskipTests -pl rest -am spring-boot:run

# Opción B: jar reempaquetado
java -jar rest/target/rest-*.jar
```

> Por defecto: `http://localhost:8080/`

## 🌐 API REST (implementación actual)

> Todas las respuestas se devuelven envueltas en `ApiResponse<T>`. En los ejemplos de abajo mostramos solo el **payload** principal para brevedad.

### 1) Crear carrito

**POST** `/api/carts`

**Response** `201 Created`

```json
{
  "cartId": "e2f4c6a4-9b7e-4e2a-9e0a-6d0d3b8f3c1a"
}
```

---

### 2) Agregar/Reemplazar un ítem en el carrito

**PUT** `/api/carts/{id}/items`

Reemplaza la línea con el mismo `sku` (si existe) y devuelve todas las líneas actualizadas del carrito.

**Request body** = `CartLine` en JSON (usa el modelo real del proyecto). Ejemplo ilustrativo:

```json
{
  "sku": "SONY-1000XM5",
  "quantity": 2
}
```

**Response** `200 OK`

```json
[
  { "sku": "SONY-1000XM5", "quantity": 2 },
  { "sku": "JBL-660NC",   "quantity": 1 }
]
```

**cURL**

```bash
curl -s -X PUT http://localhost:8080/api/carts/{cartId}/items \
  -H 'Content-Type: application/json' \
  -d '{"sku":"SONY-1000XM5","quantity":2}'
```

---

### 3) Ver carrito

**GET** `/api/carts/{id}`

**Response** `200 OK`

```json
[
  { "sku": "SONY-1000XM5", "quantity": 2 },
  { "sku": "JBL-660NC",   "quantity": 1 }
]
```

**cURL**

```bash
curl -s http://localhost:8080/api/carts/{cartId}
```

---

### 4) Checkout (calcular totales y limpiar carrito)

**POST** `/api/carts/{id}/checkout`

Ejecuta el cálculo en `CartService.checkout(lines)` y **borra** el carrito.

**Response** `200 OK` (payload = `CartResult` del dominio; ejemplo ilustrativo):

```json
{
  "subtotal": 12999.00,
  "discounts": [
    { "label": "Promo Sony 2x", "amount": 1000.00 },
    { "label": "Adaptador JBL", "amount": 199.00 }
  ],
  "total": 11799.00,
  "lines": [
    { "sku": "SONY-1000XM5", "quantity": 2 },
    { "sku": "JBL-660NC",   "quantity": 1 }
  ]
}
```

**cURL**

```bash
curl -s -X POST http://localhost:8080/api/carts/{cartId}/checkout
```

---

### Errores comunes

* `NoSuchElementException` (carrito inexistente) → mapealo a **404 Not Found** con un `@ControllerAdvice`. Sin ese handler global, Spring podría responder **500**.
* Validación de payload: añade `@Valid` y constraints en los DTOs para respuestas **400** claras.

## 🔐 Seguridad (opcional)

El **rest** puede exponerse público o protegido. Dos opciones típicas:

**1) Basic Auth (demo rápida)**

* Usuarios en memoria y reglas por rol.
* Handlers JSON para 401/403.

**2) OAuth2 Resource Server (JWT)**

* Valida `Authorization: Bearer <jwt>` contra tu IdP (Keycloak/Auth0/Azure AD).
* `JwtAuthenticationConverter` para mapear claims → `ROLE_*` si usas `hasRole`.

> La API y las reglas de negocio del **core** no cambian; solo cambia el mecanismo de autenticación/autorización en **rest**.

## 🧠 Dominio y reglas (core)

* Entidades/VOs inmutables (Producto, Marca, ItemCarrito, Carrito, Descuento, Promocion).
* Reglas de negocio como **funciones puras**/estrategias componibles (Open/Closed).
* `CompraService` orquesta: `subtotal → aplicar reglas → total`, retornando un **detalle** auditable.

## 🧪 Pruebas

* **core**: tests de unidad puros (sin Spring/IO).
* **rest**: tests de integración (mapeos, validaciones, 200/401/403).

Ejecutar tests:

```bash
mvn test
```

## 🛣️ Roadmap

* [ ] Documentación OpenAPI/Swagger para **rest**.
* [ ] Matriz de reglas configurable (YAML/DB) para activar/desactivar promos.
* [ ] Métricas (Prometheus) y logging estructurado (ELK) en **rest**.

## 🤝 Contribuciones

1. Branch por feature (`feature/nueva-regla-descuento`).
2. Añade tests y doc.
3. Pull Request con contexto de negocio y casos cubiertos.

## 📝 Licencia

MIT (o la licencia del repo).

---

**Changelog**

* **2025-08-22**: README actualizado: **REST** integrado al flujo de build/run, BOMs/Plugins alineados al parent POM, ejemplos de API y seguridad opcional.
