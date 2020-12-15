package app.expert.services;

import app.base.exceptions.GException;
import app.expert.db.statics.admission_event_type.AdmissionEventType;
import app.expert.db.statics.admission_event_type.AdmissionEventTypeCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdmissionEventTypeService {
        
    private final AdmissionEventTypeCache cache;
    
    public AdmissionEventType add(AdmissionEventType type) {
        return cache.save(type);
    }
    
    public AdmissionEventType get(String code) {
        try {
            return cache.find(code);
        } catch (GException e) {
            return null;
        }
    }
    
    public void delete(String code) throws GException {
        cache.delete(code);
    }
}
