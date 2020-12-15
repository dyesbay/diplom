package app.expert.models.statistics;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.constants.Channel;
import app.expert.db.manager.Manager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Data
@RequiredArgsConstructor
public class RsManagerStat {

    private List<ManagerStat> managerStat = new ArrayList<>();
    @JsonIgnore
    private List<String> exclude = Arrays.asList(Channel.CHAT.getValue(),Channel.OTHER.getValue());

    public void add(Manager manager, String sectionName, int count) throws GNotAllowed, GNotFound {
        ManagerStat stat = findByManager(manager);
        if(stat == null){
            ManagerStat newItem = new ManagerStat();
            newItem.setManager(manager);
            newItem.setId(manager.getId());
            newItem.setName(manager.getFullName());
            newItem.setUserName(manager.getUsername());
            newItem.getSections().add(new Section(sectionName, count));
            managerStat.add(newItem);
        } else {
            Section section = stat.findSectionByName(sectionName);
            if(section == null) {
                if(!exclude.contains(sectionName))
                    stat.getSections().add(new Section(sectionName, count));
            } else {
                section.setCount(section.getCount() + count);
            }
        }
    }

    private ManagerStat findByManager(Manager manager){
        return managerStat.stream().filter((s) -> s.getManager().equals(manager)).findFirst().orElse(null);
    }


    @Data
    private static class ManagerStat{

        private String name;
        private Long id;
        private String userName;
        @JsonIgnore
        private Manager manager;
        private List<Section> sections = new ArrayList<>();

        private Section findSectionByName(String name) {
            return sections.stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
        }
    }

    @Data
    private static class Section{

        private String name;
        private int count;

        private Section (String name, int count) {
            this.name = name;
            this.count = count;
        }
    }

}
