package santiagoczarny.authors.classes;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class Validations {

    public static ResponseEntity<?> handleValidationErrors(BindingResult result) {
        if (result.hasErrors()) {
            // If there are validation errors, build a detailed error message
            StringBuilder errorMessage = new StringBuilder("Validation error in the following fields:\n");
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError error : fieldErrors) {
                errorMessage.append("- ").append(error.getField()).append(": ").append(error.getDefaultMessage()).append("\n");
            }
            // Return an error response with the validation details
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }
        return null; // No validation errors
    }

}
