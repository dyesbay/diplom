package app.expert.db.statics.request_comments;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestCommentsCache extends GRedisCache<Long , RequestComment, RequestCommentsRepository> {

    public RequestCommentsCache(RequestCommentsRepository repository) {
        super(repository, RequestComment.class);
    }

    public List<RequestComment> getByRequest(Long id){
        return getRepository().findAllByRequest(id);
    }
}
