# 🛍️ Tienda Funcional (Java 17+)

Sistema modular que simula la venta de auriculares con **descuentos por cantidad** y **promociones de adaptadores Bluetooth** según la marca, soportando múltiples interfaces de usuario:

- **CLI** (actual)
- **REST API** (futuro) sin modificar el core

---

## 📦 Características

- **Java 17+**, sin legacy code.
- **Arquitectura limpia**:
  - `core`: Dominio, reglas de negocio y servicios de aplicación.
  - `cli`: Adaptador de línea de comandos.
  - `rest` (opcional futuro): Adaptador HTTP usando el mismo core.
- **Reglas de descuento**:

  | Cantidad de auriculares | Descuento |
  |-------------------------|-----------|
  | `< 2`                   | 0%        |
  | `2 a 3`                 | 5%        |
  | `5 a 8`                 | 8.5%      |
  | `>= 10`                 | 12%       |

- **Reglas de obsequios**:
 
  | Marca     | Adaptadores Bluetooth                     |
  |-----------|-------------------------------------------|
  | Sony      | 1 por cada 2 auriculares                  |
  | JBL       | 2 por cada 3 auriculares                  |
  | Logitech  | 1 por cada auricular                      |
  | Redragon  | 2 por cada auricular                      |
 
- **Precios por marca**:

  | Marca     | Precio (S/.) |
  |-----------|--------------|
  | Sony      | 120.00       |
  | JBL       | 135.50       |
  | Logitech  | 98.75        |
  | Redragon  | 110.00       |

---

## 🗂 Estructura de carpetas

```

tienda-funcional/
├─ pom.xml                # POM padre (módulos core + cli)
├─ core/                  # Dominio + aplicación + infraestructura
│  └─ src/main/java/com/terabyte/funcstore/
│     ├─ domain/          # Entidades, interfaces (puertos)
│     ├─ app/             # Servicios de aplicación
│     └─ infra/           # Adaptadores de datos
└─ cli/                   # Adaptador CLI
└─ src/main/java/com/terabyte/funcstore/cli/

````

---

## 🚀 Ejecución

### 1. Compilar y empaquetar

Desde la raíz del proyecto:

```bash
mvn -q -DskipTests -pl cli -am package
````

Esto compila `core` y `cli` y genera un JAR ejecutable con dependencias en `cli/target`.

---

### 2. Ejecutar CLI

```bash
java -jar cli/target/cli-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 💻 Uso en CLI

Menú principal:

```
===============================
🛍️  Tienda Funcional (CLI)
===============================
Menú:
1) Comprar auriculares (con promo)
0) Salir
```

Ejemplo:

```
Marcas: [SONY, JBL, LOGITECH, REDRAGON]
Marca: sony
Cantidad: 5

📦 Resumen de la compra
--------------------------------
Marca: SONY
Cantidad: 5
Precio unitario: S/. 120.00
Importe de compra: S/. 600.00
Descuento aplicado: 8.5%
Importe del descuento: S/. 51.00
➡️  Total a pagar: S/. 549.00
🎁 Adaptadores obsequiados: 2
```

---

## 🌐 Preparado para REST API

El módulo `core` es totalmente independiente de la interfaz.
Para exponerlo vía HTTP, basta con crear un módulo `rest` y un controlador:

```java
@RestController
@RequestMapping("/api/compras")
public class CompraController {
    private final CompraService service;
    public CompraController(CompraService service) { this.service = service; }

    @PostMapping("/auriculares")
    public CompraResult comprar(@RequestBody CompraRequest req) {
        return service.calcular(req);
    }
}
```

---

## 🧪 Pruebas

Puedes crear pruebas unitarias sobre `CompraServiceImpl` inyectando un `PriceCatalog` fake para validar descuentos y obsequios sin depender de la CLI ni REST.

---

## 📄 Licencia

MIT