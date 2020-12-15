package app.expert.db.manager_address_book;

import app.base.db.GEntity;
import app.expert.models.manager_address_book.RqEditManagerAddress;
import app.expert.models.manager_address_book.RqManagerAddressBook;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "managers_address_book")
public class ManagerAddressBook extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String position;

    private String phone;

    private String email;

    public static ManagerAddressBook getFromRequest(RqManagerAddressBook request) {
        return ManagerAddressBook.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .position(request.getPosition())
                .build();
    }

    public void edit(RqEditManagerAddress request) {
        this.setFirstName(request.getFirstName());
        this.setLastName(request.getLastName());
        this.setMiddleName(request.getMiddleName());
        this.setPhone(request.getPhone());
        this.setEmail(request.getEmail());
        this.setPosition(request.getPosition());
    }
}
