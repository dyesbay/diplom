package app.expert.models;

import lombok.Data;

import java.util.List;

@Data
public class RqReassign {

    private List<RqReassignManager> managers;

    private List<Long> requests;

    private Boolean auto;
}
