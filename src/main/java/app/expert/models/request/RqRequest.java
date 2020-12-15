package app.expert.models.request;

import app.expert.constants.Channel;
import app.expert.db.request.ApplicantContacts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * subject/subSubject - id темы/подтемы
 * data - массив MField (информация по subject) {@link app.expert.models.request.MField}
 * clientContact - контакты клиента-инициатора обращения
 * clientId - id клиента
 * channel - канал связи (по умолчанию PHONE)
 * status - статус обращения (по умолчанию PENDING_PROCESSING)
 * link - ссылка на уже существующий запрос
 */

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class RqRequest {

    @NotNull
    private Long subject;

    private Long subSubject;

    private List<MField> data;

    @Valid
    private ApplicantContacts clientContacts;

    private String clientPhone;

    private Channel channel;

    private String status;

    private String text;

    private Long link;

    private String callIdentifier;

    public Channel getChannel() {
        return channel == null ? channel = Channel.PHONE : channel;
    }

    public String getStatus() {
        return status == null ? status = "101" : status;
    }
}
