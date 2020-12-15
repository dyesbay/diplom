package app.expert.db.statics.message_signature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "message_signatures")
public class MessageSignature {
    
    @Id
    private Long id;
    private String body;
}
