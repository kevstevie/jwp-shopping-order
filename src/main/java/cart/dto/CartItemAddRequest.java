package cart.dto;

public class CartItemAddRequest {
    private Long productId;

    public CartItemAddRequest() {
    }

    public CartItemAddRequest(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
