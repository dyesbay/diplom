package app.expert.models.statistics;

import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.constants.State;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RsRequestStat {

    private int total;
    private Map<String, RqPlatform> platforms = new HashMap<>();

    public RsRequestStat () {
        Arrays.stream(Platform.values()).forEach((p) -> platforms.put(p.getKey().toLowerCase(), new RqPlatform()));
    }

    public void add(List<Object[]> commonRequestStat) {
        for (Object[] row : commonRequestStat) {
            String platform = ((String)row[0]).toLowerCase();
            String state = ((String)row[1]).toLowerCase();
            String channel = ((String)row[2]).toLowerCase();
            platforms.get(platform).add(state);
            platforms.get(platform).getStates().get(state).add(channel);
            this.total += 1;
        }
    }

    @Data
    private class RqPlatform {

        private int total;
        private Map<String, RqState> states = new HashMap<>();

        private RqPlatform(){
            Arrays.stream(State.values()).forEach((s) -> states.put(s.getKey().toLowerCase(), new RqState()));
        }

        private void add(String state){
            states.get(state);
            this.total +=1;
        }
    }

    @Data
    private class RqState {

        private int total;

        Map<String, Integer> channels = new HashMap<>();

        private RqState() {
            Arrays.stream(Channel.values()).forEach((st) -> channels.put(st.getKey().toLowerCase(), 0));
        }

        private void add(String channel){
            channels.put(channel, (channels.get(channel) + 1));
            this.total += 1;
        }
    }
}
