package app.expert.db.uploaded_files;

import app.base.db.GEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "uploaded_files")
public class UploadedFile extends GEntity<String>{

    @Id
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "uploaded_on")
    private Date uploaded;

    private String size;

    private String content;
}
