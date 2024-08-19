package santiagoczarny.authors.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 8, max = 8, message = "The ID number must be exactly 8 digits long")
    @Pattern(regexp = "\\d{8}", message = "The ID number must contain only numbers")
    @Column(unique = true, nullable = false, length = 8)
    private String idNumber;

    @Column(name = "first_name")
    @Pattern(regexp = "^[A-Za-zñÑáéíóúÁÉÍÓÚ\\s]+$", message = "The first name can only contain letters and spaces")
    private String firstName;

    @Column(name = "last_name")
    @Pattern(regexp = "^[A-Za-zñÑáéíóúÁÉÍÓÚ\\s]+$", message = "The last name can only contain letters and spaces")
    private String lastName;

    @Column(name = "phone_number")
    @Pattern(regexp = "\\d{9,12}", message = "The phone number must be between 9 and 12 digits long")
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ElementCollection
    private List<Long> booksIds;
}

