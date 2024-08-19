package santiagoczarny.authors.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import santiagoczarny.authors.entities.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByIdNumber(String idNumber);
}
