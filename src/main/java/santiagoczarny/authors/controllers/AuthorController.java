package santiagoczarny.authors.controllers;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import santiagoczarny.authors.classes.Validations;
import santiagoczarny.authors.clients.BookClient;
import santiagoczarny.authors.entities.Author;
import santiagoczarny.authors.services.AuthorService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping ("/author")
public class AuthorController {

    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookClient bookClient;

    @GetMapping("/all")
    public List<Author> findAllAuthors(){
        return  authorService.findAllAuthors();
    }

    @GetMapping("/get/{id}")
    public Author getAuthorById(@PathVariable Long id) {
        Optional<Author> authorOptional = authorService.findAuthorById(id);

        return authorOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found with ID: " + id));
    }

    @Transactional
    @PostMapping("/save")
    public ResponseEntity<?> saveAuthor(@RequestBody @Valid Author request,
                                        BindingResult result) {
        // Handle validation errors
        ResponseEntity<?> validationResponse = Validations.handleValidationErrors(result);
        if (validationResponse != null) {
            return validationResponse;
        }
        try {

            // Check if the id number already exists
            if (authorService.existsByIdNumber(request.getIdNumber())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Id number must be unique.");
            }

            // Build the author from the request
            Author author = authorService.buildAuthor(request);

            // Check the existence of each book and assign their IDs to the author
            List<Long> booksToAssign = authorService.verifyAndAssignBooks(request.getBooksIds());

            // Assign the list of book IDs to the author
            author.setBooksIds(booksToAssign);

            // Save the author
            authorService.createAuthor(author);

            // Assign the author to the books
            bookClient.assignAuthorToBooks(author.getId(), author.getBooksIds());

            // Return a successful response
            return ResponseEntity.status(HttpStatus.CREATED).body("Author saved successfully.");

        } catch (Exception e) {
            String message = "An internal server error occurred: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }


    @Transactional
    @PostMapping("/assign")
    public ResponseEntity<?> assignBookToAuthors(@RequestParam Long bookId, @RequestParam List<Long> authorIds) {
        try {
            // Call the service method to assign the book to the authors
            authorService.assignBookToAuthors(bookId, authorIds);

            // Return a successful response
            return ResponseEntity.ok("The book has been successfully assigned to the authors.");

        } catch (IllegalArgumentException e) {
            // Handle invalid argument errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Handle other internal server errors
            String message = "An internal server error occurred: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @Transactional
    @PostMapping("/assign-books")
    public ResponseEntity<?> assignBooksToAuthor(@RequestParam Long authorId, @RequestBody List<Long> bookIds) {
        try {
            // Verificar que el autor existe
            Optional<Author> optionalAuthor = authorService.findAuthorById(authorId);
            if (!optionalAuthor.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author with ID " + authorId + " not found.");
            }
            Author author = optionalAuthor.get();

            // Check the existence of each book and assign their IDs to the author
            List<Long> booksToAssign = authorService.verifyAndAssignBooks(bookIds);

            // Assign the list of book IDs to the author
            author.setBooksIds(booksToAssign);

            // Edit the author
            authorService.editAuthor(author);

            // Assign the author to the books
            bookClient.assignAuthorToBooks(author.getId(), author.getBooksIds());

            // Retornar una respuesta exitosa
            return ResponseEntity.ok("Books assigned to author successfully.");

        } catch (Exception e) {
            // Manejar excepciones generales
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @Transactional
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editAuthor(@RequestBody @Valid Author request,
                                              @PathVariable Long id,
                                              BindingResult result) {
        try {
            // Handle validation errors
            ResponseEntity<?> validationResponse = Validations.handleValidationErrors(result);
            if (validationResponse != null) {
                return validationResponse;
            }

            // Check if author exists
            Optional<Author> optionalAuthor = authorService.findAuthorById(id);
            if (!optionalAuthor.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Author not found with ID: " + id);
            }

            // Retrieve the existing professional
            Author existingAuthor = optionalAuthor.get();

            // Update the fields
            existingAuthor = authorService.buildAuthor(request);

            // Save the updated professional
            authorService.editAuthor(existingAuthor);

            // Return a success response
            return ResponseEntity.ok("Author updated successfully.");

        } catch (Exception e) {
            // Handle the exception and return an error response
            String message = "An internal server error occurred: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

}
