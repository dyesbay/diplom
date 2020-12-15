package app.expert.services;

import app.base.exceptions.GSystemError;
import app.base.services.GContextService;
import app.expert.db.admission.AdmissionSearchEntity;
import app.expert.db.admission.AdmissionSearchEntityRepository;
import app.expert.db.request.RequestSearchEntity;
import app.expert.db.request.RequestSearchEntityRepository;
import app.expert.db.statics.names.Name;
import app.expert.db.statics.names.NamesRepository;
import app.expert.db.statics.surnames.Surname;
import app.expert.db.statics.surnames.SurnamesRepository;
import app.expert.models.*;
import app.expert.models.admission.AdmissionSearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final RequestSearchEntityRepository requestSearchEntityRepository;
    private final AdmissionSearchEntityRepository admissionSearchEntityRepository;
    private final GContextService contexts;
    private final NamesRepository namesRepository;
    private final SurnamesRepository surnamesRepository;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public Page<RsRequestOrAdmission> findRequestsAndAdmissions(RqRequestAdmission rq) throws GSystemError {
        if (rq.getIncludeRequests() && rq.getIncludeAdmissions())
            return getRequestsAndAdmissions(rq);
        else if (rq.getIncludeRequests())
            return getRequests(rq);
        else if (rq.getIncludeAdmissions())
            return getAdmissions(rq);
        return null;
    }

    public Page<RsRequestOrAdmission> getRequests(RqRequestAdmission rq) throws GSystemError {
        RequestFilter filter = rq.getRequestFilter(contexts);
        Page<RequestSearchEntity> page = requestSearchEntityRepository.findAll(
                filter,
                filter.getPageRequest());
        List<RequestSearchEntity> requests = page
                .getContent();
        int rqCount = requestSearchEntityRepository.count(filter);
        return new SearchPage<>(requests
                .stream()
                .map(RsRequestOrAdmission::get)
                .collect(Collectors.toList()),
                new PageRequest(rq.getPage(),
                        rq.getSize()),
                rqCount, 0, rqCount);
    }

    public Page<RsRequestOrAdmission> getAdmissions(RqRequestAdmission rq) throws GSystemError {
        AdmissionSearchFilter filter = rq.getAdmissionFilter(contexts);
        Page<AdmissionSearchEntity> page = admissionSearchEntityRepository.findAll(filter, filter.getPageRequest());
        List<AdmissionSearchEntity> admissions = page
                .getContent();
        int admCount = admissionSearchEntityRepository.count(filter);
        return new SearchPage<>(admissions
                .stream()
                .map(RsRequestOrAdmission::get)
                .collect(Collectors.toList()),
                new PageRequest(rq.getPage(),
                        rq.getSize()),
                admCount, admCount, 0);
    }

    public List<String> findNames(String pattern, Integer size) {
        return namesRepository.findAllByNameStartingWithIgnoreCaseOrderByNameCountDesc(pattern, new PageRequest(0, size))
                .getContent()
                .stream()
                .map(Name::getName)
                .collect(Collectors.toList());
    }

    public List<String> findSurnames(String pattern, Integer size) {
        return surnamesRepository.findAllByNameStartingWithIgnoreCaseOrderByNameCountDesc(pattern, new PageRequest(0, size))
                .getContent()
                .stream()
                .map(Surname::getName)
                .collect(Collectors.toList());
    }

    private Page<RsRequestOrAdmission> getRequestsAndAdmissions(RqRequestAdmission rq) throws GSystemError {

        List<RequestSearchEntity> requests = new ArrayList<>();
        List<AdmissionSearchEntity> admissions = new ArrayList<>();
        List<RsRequestOrAdmission> result = null;

        // Достаем из базы кол-во результатов
        int rqCount = requestSearchEntityRepository.count(rq.getRequestFilter(contexts));
        int adCount= admissionSearchEntityRepository.count(rq.getAdmissionFilter(contexts));

        int size = rq.getSize();
        int page = rq.getPage();

        //Считаем размер страниц
        int adSize = size / 2;
        int rqSize = size - adSize;

        int rqLimit, adLimit, rqOffset, adOffset;
        if (adSize * (page + 1) > adCount) {
            if (adSize * page < adCount) {
                adLimit = adCount - adSize * page;
                rqLimit = size - adLimit;
                adOffset = adSize * page;
                rqOffset = rqSize * page;
            } else {
                adLimit = 0;
                adOffset = 0;
                rqLimit = size;
                rqOffset = size * page - adCount;
            }
        } else if (rqSize * (page + 1) > rqCount) {
            if (rqSize * page < rqCount) {
                rqLimit = rqCount - rqSize * page;
                adLimit = size - rqLimit;
                adOffset = adSize * page;
                rqOffset = rqSize * page;
            } else {
                rqLimit = 0;
                rqOffset = 0;
                adLimit = size;
                adOffset = size * page - rqCount;
            }
        } else {
            adOffset = adSize * page;
            rqOffset = rqSize * page;
            adLimit = adSize;
            rqLimit = rqSize;
        }

        if (rqLimit > 0) {
            Pageable pageRequest = new SearchPageable(rqOffset, rqLimit,
                    new Sort(Sort.Direction.DESC, "created"));
            requests = requestSearchEntityRepository.findAll(rq.getRequestFilter(contexts), pageRequest).getContent();
        }
        if (adLimit > 0) {
            Pageable pageRequest = new SearchPageable(adOffset, adLimit,
                    new Sort(Sort.Direction.DESC, "created"));
            admissions = admissionSearchEntityRepository.findAll(rq.getAdmissionFilter(contexts), pageRequest).getContent();
        }

        result = new ArrayList<>(requests.size() + admissions.size());
        result.addAll(requests.stream().map(RsRequestOrAdmission::get).collect(Collectors.toList()));
        result.addAll(admissions.stream().map(RsRequestOrAdmission::get).collect(Collectors.toList()));


        PagedListHolder<RsRequestOrAdmission> holder = new PagedListHolder<>(result);
        holder.setPage(rq.getPage());
        holder.setPageSize(adSize + rqSize);

        return new SearchPage<>(holder.getPageList(),
                new PageRequest(holder.getPage(),
                        holder.getPageSize()),
                rqCount + adCount, adCount, rqCount);
    }
}
