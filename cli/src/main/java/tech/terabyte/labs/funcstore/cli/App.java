package tech.terabyte.labs.funcstore.cli;

import tech.terabyte.labs.funcstore.app.CartService;
import tech.terabyte.labs.funcstore.app.CartServiceImpl;
import tech.terabyte.labs.funcstore.app.PromotionEngine;
import tech.terabyte.labs.funcstore.domain.CartLine;
import tech.terabyte.labs.funcstore.domain.CartResult;
import tech.terabyte.labs.funcstore.infra.DefaultPromotionEngine;
import tech.terabyte.labs.funcstore.infra.InMemoryProductCatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {
    private static final Scanner in = new Scanner(System.in);

    private static final InMemoryProductCatalog productCatalog = new InMemoryProductCatalog();
    private static final PromotionEngine promo = new DefaultPromotionEngine(); // reglas de imágenes
    private static final CartService cartService = new CartServiceImpl(productCatalog, promo);

    private static final List<CartLine> cart = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("""
          ================================
          🛍️  Tienda Funcional (CLI)
          ================================
          """);
        boolean running = true;
        while (running) {
            System.out.print("""
              Menú:
              1) Listar productos
              2) Agregar al carrito (por SKU)
              3) Ver carrito
              4) Pagar (aplica promos)
              5) Vaciar carrito
              0) Salir
              Opción: """);
            switch (in.nextLine().trim()) {
                case "1" -> listarProductos();
                case "2" -> agregarAlCarrito();
                case "3" -> verCarrito();
                case "4" -> pagar();
                case "5" -> {
                    cart.clear();
                    System.out.println("Carrito vacío.\n");
                }
                case "0" -> running = false;
                default -> System.out.println("Opción inválida.\n");
            }
        }
        System.out.println("Bye! ✌️");
    }

    private static void listarProductos() {
        var productos = productCatalog.all();
        System.out.printf("%-11s %-30s %-11s %-8s %-8s %-8s %-8s%n", "SKU", "Nombre", "Categoria", "Marca", "Precio", "Stock", "Rating");
        for (var p : productos) {
            System.out.printf("%-11s %-30s %-11s %-8s %-8s %-8s %-8s%n",
              p.sku(), p.nombre(), p.categoria(), p.marca(), p.precio(), p.stock(), p.rating());
        }
        System.out.println();
    }

    private static void agregarAlCarrito() {
        System.out.print("SKU: ");
        var sku = in.nextLine().trim();
        System.out.print("Cantidad: ");
        int qty;
        try {
            qty = Integer.parseInt(in.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Cantidad inválida.\n");
            return;
        }
        if (qty <= 0) {
            System.out.println("La cantidad debe ser > 0.\n");
            return;
        }

        // validar existencia
        var prod = productCatalog.bySku(sku);
        if (prod.isEmpty()) {
            System.out.println("SKU no encontrado.\n");
            return;
        }

        cart.add(new CartLine(sku, qty));
        System.out.println("Agregado al carrito.\n");
    }

    private static void verCarrito() {
        if (cart.isEmpty()) {
            System.out.println("Carrito vacío.\n");
            return;
        }
        System.out.println("Carrito:");
        var resumen = cart.stream().collect(Collectors.groupingBy(CartLine::sku,
          Collectors.summingInt(CartLine::cantidad)));
        System.out.printf("%-11s %-30s %-8s %-6s%n", "SKU", "Nombre", "Precio", "Cantidad");
        resumen.forEach((sku, qty) -> {
            var p = productCatalog.bySku(sku).orElseThrow();
            System.out.printf("%-11s %-30s %-8s %-6s%n", sku, p.nombre(), p.precio(), qty);
        });
        System.out.println();
    }

    private static void pagar() {
        if (cart.isEmpty()) {
            System.out.println("No hay items.\n");
            return;
        }
        CartResult r = cartService.checkout(cart);

        System.out.println("\nDetalle de pago:");
        for (var it : r.items()) {
            System.out.printf(Locale.ROOT,
              "- %s x%d | S/. %s (desc: S/. %s) => S/. %s%n",
              it.producto().nombre(), it.cantidad(), it.subTotal(), it.descuento(), it.total());
        }
        System.out.printf(Locale.ROOT, "Subtotal: S/. %s%n", r.subtotal());
        System.out.printf(Locale.ROOT, "Descuentos: -S/. %s%n", r.descuentoTotal());
        System.out.printf(Locale.ROOT, "TOTAL: S/. %s%n", r.total());

        if (!r.regalos().isEmpty()) {
            System.out.println("🎁 Regalos:");
            r.regalos().forEach((k, v) -> System.out.printf("- %s: %d%n", k, v));
        }
        System.out.println();
        cart.clear();
    }
}
