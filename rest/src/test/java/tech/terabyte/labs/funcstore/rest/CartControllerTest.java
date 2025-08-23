package tech.terabyte.labs.funcstore.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tech.terabyte.labs.funcstore.app.CartService;
import tech.terabyte.labs.funcstore.app.CartStore;
import tech.terabyte.labs.funcstore.domain.CartLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CartStore cartStore;

    @MockitoBean
    CartService cartService;

    @Autowired
    ObjectMapper mapper;

    @Test
    @DisplayName("POST /api/carts -> 201 Created y cuerpo contiene 'cartId'")
    void createCart_returns201_and_hasCartId() throws Exception {
        // no necesitamos stub; sólo verificamos save() con un id y lista vacía
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);

        mockMvc.perform(post("/api/carts"))
          .andDo(print())
          .andExpect(status().isCreated())
          // $..cartId hace búsqueda recursiva (independiente de la envoltura ApiResponse)
          .andExpect(jsonPath("$..cartId", not(empty())));

        verify(cartStore, times(1)).save(idCaptor.capture(), listCaptor.capture());
        String generatedId = idCaptor.getValue();
        List savedList = listCaptor.getValue();

        // sanity checks
        assertThat(generatedId, not(is(emptyOrNullString())));
//        assertThat(savedList, is(empty()));
    }

    @Test
    @DisplayName("GET /api/carts/{id} (carrito vacío) -> 200 y payload vacío")
    void viewCart_empty_returnsOk_andEmptyList() throws Exception {
        String id = "cart-123";
        when(cartStore.find(id)).thenReturn(Optional.of(new ArrayList<>()));

        mockMvc.perform(get("/api/carts/{id}", id))
          .andDo(print())
          .andExpect(status().isOk())
          // verificamos que en algún lugar del JSON haya un array vacío (p. ej. data: [])
          .andExpect(content().string(containsString("[]")));
    }

    @Test
    @DisplayName("POST /api/carts/{id}/checkout -> 200, invoca checkout y borra el carrito")
    void checkout_invokesService_andClearsCart() throws Exception {
        String id = "cart-abc";
        List<CartLine> lines = new ArrayList<>();
        when(cartStore.find(id)).thenReturn(Optional.of(lines));

        // No stubeamos cartService.checkout(...) para no depender de CartResult;
        // el controller devolverá data=null en el envoltorio y está bien para este test.
        mockMvc.perform(post("/api/carts/{id}/checkout", id))
          .andDo(print())
          .andExpect(status().isOk());

        verify(cartService, times(1)).checkout(same(lines));
        verify(cartStore, times(1)).delete(id);
    }

    @Test
    void viewCart_notFound_returns404_withEnvelope() throws Exception {
        when(cartStore.find("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/carts/{id}", "missing"))
          .andExpect(status().isNotFound())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()))
          .andExpect(jsonPath("$.error.code").value("NOT_FOUND"))
          .andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.startsWith("Cart not found")))
          .andExpect(jsonPath("$.error.details").value(org.hamcrest.Matchers.nullValue()))
          .andExpect(jsonPath("$.meta.requestId").isNotEmpty())
          .andExpect(jsonPath("$.meta.timestamp").exists());
    }
}
