package app.expert.db.client;

import app.base.db.GRedisCache;
import app.base.exceptions.GNotFound;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import app.expert.validation.GPhoneParser;
import org.springframework.stereotype.Service;

@Service
public class ClientCache extends GRedisCache<Long, Client, ClientRepository>{

    public ClientCache(ClientRepository repository) {
        super(repository, Client.class);
    }
    
    @Override
    public IGEnum getAlreadyExistsError() {
        return ExpertErrors.CLIENT_ALREADY_EXISTS;
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.CLIENT_NOT_FOUND;
    }

    public Client getByPhone(String phone) {
        return getRepository().getByPhone(GPhoneParser.parsePhone(phone)).orElse(null);
    }

    public Client findByPhone(String phone) throws GNotFound {
        return getRepository().getByPhone(phone).orElseThrow(() -> new GNotFound(getNotFoundError()));
    }
}
