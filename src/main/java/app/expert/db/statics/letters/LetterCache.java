package app.expert.db.statics.letters;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LetterCache extends GRedisCache<Long, Letter, LetterRepository> {

    public LetterCache(LetterRepository repository) {
        super(repository, Letter.class);
    }

    public List<Letter> getByRequest (Long id) {
        return getRepository().findByRequest(id);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.REQUEST_EMAIL_NOT_FOUND;
    }
}
