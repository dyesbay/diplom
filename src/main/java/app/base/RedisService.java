package app.base;

import app.base.utils.ObjectUtils;
import app.base.utils.SerializationUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedissonClient client;

    @Value("${info.redis.enabled:true}")
    private Boolean enabled;

    @Value("${info.redis.timeout:3000}")
    private Integer timeout;

    @Value("${info.redis.host:app-redis}")
    private String host;

    @Value("${info.redis.port:6379}")
    private Integer port;


    @PostConstruct
    public void init() {
        if (enabled) {
            Config cfg = new Config();
            cfg.useClusterServers()
                    .addNodeAddress("redis://" + host + ":" + port);
            this.client = Redisson.create(cfg);
        }
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setExpire(String key, Integer seconds) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return;

        try {
            if (seconds != null) client.getKeys().expire(key, seconds, TimeUnit.SECONDS);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }


    /*
        Key value
     */

    public void set(String key, String value, Integer seconds) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return;

        try {
            client.getBucket(key).set(value);
            if (seconds != null) setExpire(key, seconds);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void set(String key, String value) {
        set(key, value, null);
    }

    public void seto(String key, Object value) {
        set(key, SerializationUtils.toJson(value), null);
    }

    public void seto(String key, Object value, Integer seconds) {
        set(key, SerializationUtils.toJson(value), seconds);
    }


    public String getex(String key, Integer expired) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return null;

        if (expired != null) {
            try {
                setExpire(key, expired);
            } catch (Exception ex) {
                // do nothing
            }
        }

        try {
            return client.getBucket(key).toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public String get(String key) {
        return getex(key, null);
    }

    public <T> T get(String key, Class<T> clazz, Integer expired) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return null;

        return SerializationUtils.fromJson(getex(key, expired), clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, null);
    }

    public <T> T getOrDefault(String key, Class<T> clazz, T def) {
        T value = get(key, clazz);
        if (value == null) return def;
        return value;
    }

    public Boolean exists(String key) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return false;

        try {
            return client.getBucket(key).isExists();
        } catch (Exception ex) {
            return false;
        }
    }

    public void del(String key) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return;

        try {
            client.getBucket(key).delete();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void del(String... key) {
        for (String s : key) del(s);
    }


    public Set<String> keys(String pattern) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(pattern)) return new HashSet<>();
        Set<String> result = new HashSet<>();
        try {
            client.getKeys().getKeysByPattern(pattern).forEach(result::add);
            return result;
        } catch (Exception ex) {
            return new HashSet<>();
        }
    }

    public Set<String> keys() {
        return keys("*");
    }

    /*
        Key HashMap
     */

    public void hset(String key, String field, String value, Integer expired) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key) || ObjectUtils.isBlank(field)) return;

        try {
            client.getMap(key).put(field, value);
            if (expired != null) setExpire(key, expired);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    public void hset(String key, String field, String value) {
        hset(key, field, value, null);
    }

    public void hseto(String key, String field, Object value) {
        hset(key, field, SerializationUtils.toJson(value), null);
    }

    public void hseto(String key, String field, Object value, Integer expired) {
        hset(key, field, SerializationUtils.toJson(value), expired);
    }


    public String hgetex(String key, String field, Integer expired) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key) || ObjectUtils.isBlank(field)) return null;

        if (expired != null) {
            try {
                setExpire(key, expired);
            } catch (Exception ex) {
                // do nothing
            }
        }

        try {
            return client.getMap(key).get(field).toString();
        } catch (Exception ex) {
            return null;
        }
    }

    public String hget(String key, String field) {
        return hgetex(key, field, null);
    }

    public <T> T hget(String key, String field, Class<T> clazz, Integer expired) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key) || ObjectUtils.isBlank(field)) return null;

        return SerializationUtils.fromJson(hgetex(key, field, expired), clazz);
    }

    public <T> T hget(String key, String field, Class<T> clazz) {
        return hget(key, field, clazz, null);
    }


    public Map<Object, Object> hgetMap(String key) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return Collections.emptyMap();

        try {
            return client.getMap(key).readAllMap();
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    public <T> Map<String, T> hgetMap(String key, Class<T> clazz) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return Collections.emptyMap();

        Map<Object, Object> map = hgetMap(key);
        Map<String, T> result = new HashMap<>();
        map.keySet().forEach(code -> result.put((String) code, SerializationUtils.fromJson((String) map.get(code), clazz)));
        return result;
    }


//    public List<String> hgetList(String key) {
//        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return Collections.emptyList();
//
//        try {
//            return new ArrayList<>(jedisPoolWrite.hgetAll(key).values());
//        } catch (Exception ex) {
//            return Collections.emptyList();
//        }
//    }
//
//    public <T> List<T> hgetList(String key, Class<T> clazz) {
//        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return Collections.emptyList();
//
//        return hgetList(key).stream().map(value -> SerializationUtils.fromJson(value, clazz)).collect(Collectors.toList());
//    }


    public Boolean hexists(String key, String field) {
        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return false;

        try {
            return client.getMap(key).get(field) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public void hdel(String key, String... fields) {
        if (!Boolean.TRUE.equals(enabled) || Boolean.TRUE.equals(ObjectUtils.isBlank(key)) || fields == null || fields.length == 0)
            return;

        try {
            client.getMap(key).fastRemove(fields);
        } catch (Exception ex) {
            logger.error("{}: {} {}", ex.getMessage(), key, fields);
        }
    }

//    public Set<String> hkeys(String key) {
//        if (!Boolean.TRUE.equals(enabled) || ObjectUtils.isBlank(key)) return new HashSet<>();
//
//        try {
//            return jedisPoolWrite.hkeys(key);
//        } catch (Exception ex) {
//            return new HashSet<>();
//        }
//    }
}
