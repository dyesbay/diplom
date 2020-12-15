package app.expert.db.statics.message_tmpl;

import app.base.db.GRedisCache;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import org.springframework.stereotype.Service;

@Service
public class MessageTmplCache extends GRedisCache<Long, MessegeTmpl, MassageTmplReposiroty> {

    public MessageTmplCache(MassageTmplReposiroty repository) {
        super(repository, MessegeTmpl.class);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.MESSAGE_TEMPLATE_NOT_FOUND;
    }
}
