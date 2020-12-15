package app.expert.models.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@AllArgsConstructor
@Data
public class RsStat {

    private int average;
    private int managers;
    private int notifications;
    private List<Section> sections;

    public RsStat (){
        sections = new ArrayList<>();
    }

    public void add(String sectionName, String itemName, int amount) {
        Section section = find(sectionName);
        if(section == null) {
            sections.add(new Section(sectionName));
        }
        section = find(sectionName);
        section.updateItem(itemName, amount);
        section.setTotal(section.getTotal() + amount);
    }

    public void addManagers(int managers){
        this.managers = managers;
    }

    public void addAverage(int average){
        this.average = average;
    }

    public void addNotifications(int notifications){
        this.notifications = notifications;
    }

    private Section find(String sectionName) {
        return sections.stream().filter(s-> s.getName().equals(sectionName)).findFirst().orElse(null);
    }

        @EqualsAndHashCode
        @Data
        private static class Section {

            private String name;
            private int total;
            private List<Item> items = new ArrayList<>();

            Section (String name){
                this.name = name;
                this.total = 0;
            }

            void updateItem(String itemName, int amount){
                Item item = findItem(itemName);
                if(item == null) {
                    items.add(new Item(itemName));
                }
                item = findItem(itemName);
                item.setCount(item.getCount() + amount);
            }

            private Item findItem(String itemName) {
                return items.stream().filter(i-> i.getName().equals(itemName)).findFirst().orElse(null);
            }
        }

        @EqualsAndHashCode
        @Data
        private static class Item {

            private String name;
            private int count;

            Item(String name){
                this.name = name;
                this.count = 0;
            }
        }
}
