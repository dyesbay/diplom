package app.expert.models.statistics;

import app.expert.constants.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RqStat {

    private List<Platform> platforms;
}
