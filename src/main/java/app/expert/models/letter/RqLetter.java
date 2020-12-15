package app.expert.models.letter;

import app.base.utils.DateUtils;
import app.expert.db.manager.Manager;
import app.expert.db.statics.letters.Letter;
import app.expert.db.statics.letters.LetterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RqLetter {

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

    @Email
    private String email;

    @NotNull
    private Long request;

    private String signature;

    private LetterType type;

    private MultipartFile[] attachments;

    public Letter getLetter(Manager manager){
        return Letter.builder()
                .subject(subject)
                .body(body)
                .sender(manager.getId())
                .sent(DateUtils.convert(new Date(), DateUtils.HUMAN_DATE_TIME))
                .request(getRequest())
                .build();
    }
}
