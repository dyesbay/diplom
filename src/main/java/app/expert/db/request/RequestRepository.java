package app.expert.db.request;

import app.expert.constants.Channel;
import app.expert.constants.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByAssigneeIsNullOrderByCreatedAsc();

    List<Request> findByAssigneeIsNullAndPlatformAndChannelOrderByCreatedAsc(Platform platform, Channel channel);
    List<Request> findByAssigneeIsNullAndPlatformAndChannelAndSubjectOrderByCreatedAsc(Platform platform, Channel channel, Long subject);
    List<Request> findByAssigneeAndPlatformAndChannelOrderByCreatedAsc(Long assignee, Platform platform, Channel channel);

    Page<Request> findAll(Specification<Request> spec, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM requests rq " +
            "LEFT JOIN status st " +
            "ON st.code=rq.status" +
            " WHERE state= :state AND platform = :plat AND rq.created_on > :date", nativeQuery = true)
    int countByStatusStateAndPlatform(@Param("state")String statusState,
                                      @Param("plat") String platform,
                                      @Param("date") Date afterThisTimePoint);

    @Query(value = "SELECT COUNT(*) FROM requests rq " +
            "LEFT JOIN status st " +
            "ON st.code=rq.status" +
            " WHERE state= :state AND rq.platform = :plat AND rq.route_type = :route AND rq.created_on > :date", nativeQuery = true)
    int countByStatusAndRouteTypeAndPlatform(@Param("state") String state,
                                             @Param("route") String route,
                                             @Param("plat") String platform,
                                             @Param("date") Date afterThisTimePoint);

    @Query(value = "SELECT (SELECT SUM(EXTRACT(epoch FROM (closed_on - created_on))/3600/24)" +
            "            from (SELECT r.created_on, r.closed_on" +
            "            from requests r" +
            "            left join status s " +
            "            on r.status=s.code where r.created_on > :date and s.state='CLOSED')as foo)" +
            "            /(SELECT COUNT(*) from requests r WHERE (r.id, r.created_on) IN (" +
            "            SELECT r.id, r.created_on" +
            "            FROM requests r" +
            "            LEFT JOIN status s" +
            "            ON r.status = s.code" +
            "            WHERE s.state='CLOSED' AND r.created_on > :date));", nativeQuery = true)
    double computeAverageProcessTime(@Param("date") Date afterThisTimePoint);

    int countByStatusInAndPlatformInAndCreatedAfter(List<String> statuses, List<Platform> platforms, Date afterThisTimePoint);

    int countByStatusAndPlatform(String code, Platform platform);

    int countByChannelAndPlatformAndCreatedAfter(Channel channel, Platform platform, Date afterThisTimePoint);

    long countByChannelAndPlatform(Channel channel, Platform platform);

    long countByChannel(Channel channel);


    @Query(
            value = "select rq.platform, st.state, rq.channel " +
                    "from status st join requests rq on rq.status=st.code " +
                    "where rq.created_on between :from and :to ;",
            nativeQuery = true)
    List<Object[]> findCommonRequestStat(@Param("from") Date from,
                                         @Param("to") Date to);
}
