package app.expert.db.statics.message_tmpl;

import app.base.db.GEntity;
import app.expert.db.statics.message_signature.MessageSignature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "message_templates")
public class MessegeTmpl extends GEntity<Long>{
    
    @Id
    private Long id;
    private String subject;
    private String body;
    @OneToOne
    @JoinColumn(name = "id")
    private MessageSignature signature;
}
