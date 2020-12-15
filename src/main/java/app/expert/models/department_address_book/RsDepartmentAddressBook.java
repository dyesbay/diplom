package app.expert.models.department_address_book;

import app.expert.db.statics.department_address_book.DepartmentAddressBook;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsDepartmentAddressBook {

    private Long id;

    private String name;

    private String city;

    private String region;

    private String address;

    private String phone;

    private String email;

    private String contact;

    public static RsDepartmentAddressBook getFromEntity(DepartmentAddressBook entity) {
        return RsDepartmentAddressBook.builder()
                .id(entity.getId())
                .name(entity.getName())
                .city(entity.getCity())
                .region(entity.getRegion())
                .address(entity.getAddress())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .contact(entity.getContact())
                .build();
    }
}
