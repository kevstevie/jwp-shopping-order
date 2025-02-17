package cart.acceptance;

import cart.dao.CartItemDao;
import cart.dto.CartItemAddRequest;
import cart.dto.CartItemUpdateRequest;
import cart.dto.CartItemUpdateResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CartItemsAcceptanceTest extends AcceptanceTest {

    @Autowired
    private CartItemDao cartItemDao;

    @Test
    void 장바구니_아이템_추가() {
        final CartItemAddRequest cartItemAddRequest = new CartItemAddRequest(1L);

        final var response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cartItemAddRequest)
                .auth().preemptive().basic(EMAIL, PASSWORD)
                .when().post("/cart-items")
                .then().log().all()
                .extract();

        final var cartItemUpdateResponse = response.as(CartItemUpdateResponse.class);

        assertAll(
                () -> assertThat(cartItemUpdateResponse.getQuantity()).isEqualTo(1),
                () -> assertThat(cartItemUpdateResponse.isChecked()).isTrue()
        );
    }

    @Test
    void 장바구니_아이템_수량_변경() {
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(350, true);

        final var response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cartItemUpdateRequest)
                .auth().preemptive().basic(EMAIL, PASSWORD)
                .when()
                .patch("/cart-items/1")
                .then()
                .extract();

        final var cartItemUpdateResponse = response.as(CartItemUpdateResponse.class);

        assertAll(
                () -> assertThat(cartItemUpdateResponse.getQuantity()).isEqualTo(350),
                () -> assertThat(cartItemUpdateResponse.isChecked()).isTrue()
        );
    }

    @Test
    void 장바구니_아이템_체크_변경() {
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(2, false);

        final var response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cartItemUpdateRequest)
                .auth().preemptive().basic(EMAIL, PASSWORD)
                .when()
                .patch("/cart-items/1")
                .then()
                .extract();

        final var cartItemUpdateResponse = response.as(CartItemUpdateResponse.class);

        assertAll(
                () -> assertThat(cartItemUpdateResponse.getQuantity()).isEqualTo(2),
                () -> assertThat(cartItemUpdateResponse.isChecked()).isFalse()
        );
    }

    @Test
    void 장바구니_아이템_수량을_0으로_변경하면_삭제() {
        final CartItemUpdateRequest cartItemUpdateRequest = new CartItemUpdateRequest(0, true);

        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cartItemUpdateRequest)
                .auth().preemptive().basic(EMAIL, PASSWORD)
                .when()
                .patch("/cart-items/1")
                .then();

        assertThat(cartItemDao.findById(1L)).isNull();
    }
}
