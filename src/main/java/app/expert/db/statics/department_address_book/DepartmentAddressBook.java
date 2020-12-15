package app.expert.db.statics.department_address_book;

import app.base.db.GEntity;
import app.expert.models.department_address_book.RqDepartmentAddressBook;
import app.expert.models.department_address_book.RqEditDepartmentAddress;
import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "department_address_book")
public class DepartmentAddressBook extends GEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String city;

    private String region;

    private String address;

    private String phone;

    private String email;

    private String contact;

    public static DepartmentAddressBook getFromRequest(RqDepartmentAddressBook request) {
        return DepartmentAddressBook.builder()
                .name(request.getName())
                .city(request.getCity())
                .region(request.getRegion())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .contact(request.getContact())
                .build();
    }

    public void edit(RqEditDepartmentAddress request) {
        this.setName(request.getName());
        this.setCity(request.getCity());
        this.setRegion(request.getRegion());
        this.setAddress(request.getAddress());
        this.setPhone(request.getPhone());
        this.setEmail(request.getEmail());
        this.setContact(request.getContact());
    }
}
