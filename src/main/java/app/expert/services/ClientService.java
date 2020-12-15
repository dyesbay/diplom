package app.expert.services;

import app.base.exceptions.*;
import app.base.models.GFilter;
import app.expert.constants.ExpertErrors;
import app.expert.db.admission.CommunicationMethod;
import app.expert.db.client.Client;
import app.expert.db.client.ClientCache;
import app.expert.db.client.ClientRepository;
import app.expert.db.request.ApplicantContacts;
import app.expert.db.statics.client_type.ClientTypeCache;
import app.expert.db.statics.region.RegionsCache;
import app.expert.models.client.RqClient;
import app.expert.models.client.RsClient;
import app.expert.validation.GPhoneParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientCache cache;
    private final ClientRepository repo;
    private final ClientTypeCache clientTypeCache;
    private final RegionsCache regionsCache;
    private final ClientTypeCache typeCache;

    public RsClient getByPhoneOrId(Long id, String phone) throws GNotFound, GNotAllowed, GBadRequest {
        if(id != null)
            return RsClient.get(cache.find(id), regionsCache, typeCache);
        else if(phone != null)
            return RsClient.get(cache.findByPhone(GPhoneParser.parsePhone(phone)), regionsCache, typeCache);
        else
            throw new GBadRequest();
    }

    /**
     * в данном случае номер телефона -
     * уникальный идентификатор. если телефон есть в базе клиентов то обновляем запись
     * если нет то создаем новую
     * @param request - информация по клиенту
     * @return RsClient - response model
     */
    public RsClient createOrUpdate(RqClient request) throws GException {

        //  валидируем данные из запроса:
        // проверяем что такой регион суще
        regionsCache.find(request.getRegion());

        // проверяем что такой тип клиента существует
        typeCache.find(request.getClientType());

        Client client = cache.getByPhone(request.getPhone());
        if (client == null) {
            client = Client.createClientFromRequest(request);
        } else {
            client = client.updateClient(request);
        }
        return RsClient.get(cache.save(client), regionsCache, typeCache);
    }
    
    public Page<RsClient> getFilteredPage(GFilter<Long, Client> rq) {
        return cache.getRepository().findAll(rq, new PageRequest(rq.getPage(), rq.getSize()))
                .map(source -> {
                    try {
                        return RsClient.get(source, regionsCache, typeCache);
                    } catch (GException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void delete(Long id, String phone) throws GNotFound, GNotAllowed, GBadRequest {
        Client client;
        if (id != null) {
            client = cache.find(id);
            client.setDisabledOn(new Date());
        } else if(phone != null) {
            client = cache.findByPhone(GPhoneParser.parsePhone(phone));
            client.setDisabledOn(new Date());
        } else {
            throw new GBadRequest();
        }
        cache.save(client);
    }

    private void checkEmail(String email) throws GAlreadyExists {
        //Проверяем есть ли клиент с таким e-mail в базе
        if(!repo.findByEmail(email).isEmpty()) throw new GAlreadyExists(ExpertErrors.DUPLICATE_CLIENT_EMAIL);
    }

    public Client editClient(ApplicantContacts applicantContacts) throws GNotAllowed, GNotFound {

        Client client;

        if ((client = cache.getByPhone(applicantContacts.getPhone())) == null) {
            client = Client.builder().build();
        }

        // обновим данные клиента
        client.setLastName(applicantContacts.getLastName());
        client.setFirstName(applicantContacts.getFirstName());
        client.setMiddleName(applicantContacts.getMiddleName());
        client.setEmail(applicantContacts.getEmail());
        client.setPhone(applicantContacts.getPhone());
        if (applicantContacts.getClientType() != null)
            client.setClientType(clientTypeCache.find(applicantContacts.getClientType()).getCode());

        if (applicantContacts.getCommType() == null)
            client.setCommunicationType(CommunicationMethod.PHONE);
        else
            client.setCommunicationType(applicantContacts.getCommType());

        return cache.save(client);
    }
}
