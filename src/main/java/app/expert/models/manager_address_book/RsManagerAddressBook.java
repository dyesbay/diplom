package app.expert.models.manager_address_book;

import app.expert.db.manager_address_book.ManagerAddressBook;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsManagerAddressBook {

    private Long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String fullName;

    private String position;

    private String phone;

    private String email;

    private static String getFullName(ManagerAddressBook entity) {
        StringBuilder sb = new StringBuilder(entity.getLastName().length() + 4);
        sb.append(entity.getLastName()).append(" ").append(entity.getFirstName().charAt(0)).append('.');
        if (entity.getMiddleName() != null) {
            sb.append(' ').append(entity.getMiddleName().charAt(0)).append('.');
        }
        return sb.toString();
    }

    public static RsManagerAddressBook getFromEntity(ManagerAddressBook entity) {
        return RsManagerAddressBook.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .middleName(entity.getMiddleName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .position(entity.getPosition())
                .fullName(getFullName(entity))
                .build();
    }
}
