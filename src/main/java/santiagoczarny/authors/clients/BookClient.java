package santiagoczarny.authors.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import santiagoczarny.authors.classes.BookDto;

import java.util.List;

@FeignClient(name = "books", url = "http://localhost:8082/book")
public interface BookClient {

    @GetMapping("/get/{id}")
    BookDto getBookById(@PathVariable Long id);

    @PostMapping("/assign")
    void assignAuthorToBooks(@RequestParam Long authorId, @RequestParam List<Long> booksIds);


}
