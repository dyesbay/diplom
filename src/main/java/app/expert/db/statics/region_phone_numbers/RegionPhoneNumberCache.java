package app.expert.db.statics.region_phone_numbers;

import app.base.db.GRedisCache;
import app.base.exceptions.GNotFound;
import app.base.objects.IGEnum;
import app.expert.constants.ExpertErrors;
import app.expert.validation.GPhoneParser;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionPhoneNumberCache extends GRedisCache<Long,
        RegionPhoneNumber, RegionPhoneNumberRepository> {

    public RegionPhoneNumberCache(RegionPhoneNumberRepository repository) {
        super(repository, RegionPhoneNumber.class);
    }

    /**
     * Ищет в базе телефон по коду и фильтрует в зависимости
     * от диапазона значений номера, следующего за кодом
     * Это необходимо потому что один
     * и тот же код может относиться к нескольким регионам одновременно.
     * @return DefPhone - модель для номера мобильного телефона + регион
     */
    public RegionPhoneNumber findByPhone(String phone) throws GNotFound {
        List<RegionPhoneNumber> phones =
                findByCode(Short.parseShort(extractCode(phone)));
        if(phone.startsWith("+79") || phone.startsWith("+78")) {
            int number = Integer.parseInt(extractNumber(phone));
            return phones.stream()
                    .filter((s) -> between(number, s.getDFrom(), s.getDTo()))
                    .findFirst()
                    .orElseThrow(() -> new GNotFound(getNotFoundError()));
        }else if (phone.startsWith("+74") || phone.startsWith("+73")) {
            return phones.stream().findFirst()
                    .orElseThrow(() -> new GNotFound(getNotFoundError()));
        }
        return new RegionPhoneNumber();
    }

    public Page<RegionPhoneNumber> getPaging(Pageable pageable){
        return getRepository().findAll(pageable);
    }

    public Page<RegionPhoneNumber> getPaging(Example<RegionPhoneNumber> probe,
                                             Pageable pageable){
        return getRepository().findAll(probe, pageable);
    }

    private List<RegionPhoneNumber> findByCode(Short code) throws GNotFound {
        return getRepository().findByCode(code)
                .orElseThrow(() -> new GNotFound(getNotFoundError()));
    }


    private String extractNumber(String number) throws GNotFound {
        number = GPhoneParser.parsePhone(number);
        try {
            return number.substring(5);
        } catch (Exception e) {
            throw new GNotFound(getNotFoundError());
        }
    }

    private String extractCode(String number) throws GNotFound {
        number = GPhoneParser.parsePhone(number);
        try{
        return number.substring(2,5);
        } catch (Exception e) {
            throw new GNotFound(getNotFoundError());
        }
    }

    private boolean between(int var , int from, int to) {
        return (var >= from && var <= to);
    }

    @Override
    public IGEnum getNotFoundError() {
        return ExpertErrors.REGION_PHONE_NUMBER_NOT_FOUND;
    }
}
