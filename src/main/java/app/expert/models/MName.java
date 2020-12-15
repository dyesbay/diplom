package app.expert.models;

import app.expert.db.statics.names.Name;
import app.expert.db.statics.surnames.Surname;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class MName {
    @SerializedName("ID")
    private Long id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Surname")
    private String surname;

    @SerializedName("PeoplesCount")
    private Long count;

    public Name convertToName(){
        return Name.builder()
                .id(id)
                .name(name)
                .nameCount(count)
                .build();
    }

    public Surname convertToSurname(){
        return Surname.builder()
                .id(id)
                .name(surname)
                .nameCount(count)
                .build();
    }
}
