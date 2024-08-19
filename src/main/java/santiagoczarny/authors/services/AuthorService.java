package santiagoczarny.authors.services;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import santiagoczarny.authors.classes.BookDto;
import santiagoczarny.authors.clients.BookClient;
import santiagoczarny.authors.entities.Author;
import santiagoczarny.authors.repositories.AuthorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookClient bookClient;

    public Author createAuthor(Author author){
        return authorRepository.save(author);
    }

    public Author editAuthor(Author author){
        return authorRepository.save(author);
    }

    public List<Author> findAllAuthors(){
        return authorRepository.findAll();
    }

    public Optional<Author> findAuthorById(Long id){
        return authorRepository.findById(id);
    }

    public boolean existsByIdNumber(String idNumber) {
        return authorRepository.existsByIdNumber(idNumber);
    }

    public Author buildAuthor(Author author){
        return Author.builder()
                .idNumber(author.getIdNumber())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .phoneNumber(author.getPhoneNumber())
                .birthDate(author.getBirthDate())
                .booksIds(author.getBooksIds())
                .build();
    }

    // Method to assign a book to multiple authors
    public void assignBookToAuthors(Long bookId, List<Long> authorIds) {
        // Iterate over the author IDs to assign the book to them
        for (Long authorId : authorIds) {
            Author author = authorRepository.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("The author with ID " + authorId + " does not exist."));

            // Check if the book is already assigned to the author
            if (!author.getBooksIds().contains(bookId)) {
                author.getBooksIds().add(bookId);
                authorRepository.save(author); // Save the author with the newly assigned book
            }
        }
    }

    public List<Long> verifyAndAssignBooks(List<Long> bookIds) throws Exception {
        List<Long> booksToAssign = new ArrayList<>();
        for (Long id : bookIds) {
            try {
                BookDto book = bookClient.getBookById(id);
                booksToAssign.add(book.getId());
            } catch (FeignException.NotFound e) {
                throw new Exception("Book with ID " + id + " not found.");
            }
        }
        return booksToAssign;
    }



}
