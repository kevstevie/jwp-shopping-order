package cart.dto;

import cart.domain.CartItem;

public class CartItemResponse {
    private Long id;
    private int quantity;
    private ProductResponse product;
    private boolean checked;

    private CartItemResponse(Long id, int quantity, ProductResponse product, boolean checked) {
        this.id = id;
        this.quantity = quantity;
        this.product = product;
        this.checked = checked;
    }

    public static CartItemResponse of(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getQuantity(),
                ProductResponse.of(cartItem.getProduct()),
                cartItem.isChecked()
        );
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public boolean isChecked() {
        return checked;
    }
}
