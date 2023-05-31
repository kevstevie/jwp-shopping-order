package cart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Void> handlerAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(CartItemException.IllegalMember.class)
    public ResponseEntity<Void> handleException(CartItemException.IllegalMember e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Void> handleProductNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({
            InvalidOrderCheckedException.class,
            InvalidOrderQuantityException.class,
            InvalidOrderProductException.class,
            IllegalPointUsageException.class
    })
    public ResponseEntity<String> handleCustomApiException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(exception.getClass().getSimpleName());
    }
}
