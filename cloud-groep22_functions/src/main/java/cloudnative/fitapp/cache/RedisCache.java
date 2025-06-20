package cloudnative.fitapp.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.logging.Logger;

public class RedisCache {

    private static final Logger logger = Logger.getLogger(RedisCache.class.getName());
    private static RedisCache instance;
    private final JedisPool jedisPool;
    private final ObjectMapper objectMapper;

    // Cache duration constants (in seconds)
    public static final int WORKOUT_CACHE_DURATION = 900; // 15 minutes
    public static final int USER_CACHE_DURATION = 1800; // 30 minutes
    public static final int EXERCISE_CACHE_DURATION = 600; // 10 minutes

    private RedisCache(JedisPool jedisPool) {
        if (jedisPool == null) {
            throw new IllegalArgumentException("JedisPool is required");
        }
        this.jedisPool = jedisPool;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private static JedisPool createJedisPool() {
        String hostName = System.getenv("REDIS_HOST_NAME");
        String accessKey = System.getenv("REDIS_ACCESS_KEY");
        String portStr = System.getenv("REDIS_PORT");

        if (hostName == null || hostName.isEmpty()) {
            throw new IllegalStateException("REDIS_HOST_NAME environment variable is not set");
        }
        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalStateException("REDIS_ACCESS_KEY environment variable is not set");
        }

        int port = portStr != null ? Integer.parseInt(portStr) : 6380;

        logger.info("Connecting to Redis: " + hostName + ":" + port);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setBlockWhenExhausted(true);

        return new JedisPool(poolConfig, hostName, port, 2000, accessKey, true);
    }

    public static synchronized RedisCache getInstance() {
        if (instance == null) {
            try {
                JedisPool pool = createJedisPool();
                instance = new RedisCache(pool);
                logger.info("Redis cache instance created successfully");
            } catch (Exception e) {
                logger.severe("Failed to create Redis cache instance: " + e.getMessage());
                throw new RuntimeException("Failed to initialize Redis cache", e);
            }
        }
        return instance;
    }


    // Cache user workouts by user ID
    public void cacheUserWorkouts(String userId, Object workouts) {
        String key = "workouts:user:" + userId;
        try (Jedis jedis = jedisPool.getResource()) {
            String value = objectMapper.writeValueAsString(workouts);
            jedis.setex(key, WORKOUT_CACHE_DURATION, value);
            logger.info("Cached workouts for user: " + userId);
        } catch (JsonProcessingException | JedisException e) {
            logger.warning("Failed to cache user workouts: " + e.getMessage());
        }
    }


    // Get cached user workouts
    public List<Object> getCachedUserWorkouts(String userId) {
        String key = "workouts:user:" + userId;
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            if (value != null) {
                logger.info("Data retrieved from cache for user workouts: " + userId);
                return objectMapper.readValue(value, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
            } else {
                logger.info("Data not found in cache for user workouts: " + userId);
                return null;
            }
        } catch (Exception e) {
            logger.warning("Failed to get cached user workouts: " + e.getMessage());
            return null;
        }
    }


    // Cache user data
    public void cacheUser(String userEmail, Object user) {
        String key = "user:email:" + userEmail;
        try (Jedis jedis = jedisPool.getResource()) {
            String value = objectMapper.writeValueAsString(user);
            jedis.setex(key, USER_CACHE_DURATION, value);
            logger.info("Cached user: " + userEmail);
        } catch (JsonProcessingException | JedisException e) {
            logger.warning("Failed to cache user: " + e.getMessage());
        }
    }


    // Get cached user data
    public Object getCachedUser(String userEmail) {
        String key = "user:email:" + userEmail;
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            if (value != null) {
                logger.info("Data retrieved from cache for user: " + userEmail);
                return objectMapper.readValue(value, Object.class);
            } else {
                logger.info("Data not found in cache for user: " + userEmail);
                return null;
            }
        } catch (Exception e) {
            logger.warning("Failed to get cached user: " + e.getMessage());
            return null;
        }
    }


    // Cache exercises by workout ID
    public void cacheWorkoutExercises(String workoutId, Object exercises) {
        String key = "exercises:workout:" + workoutId;
        try (Jedis jedis = jedisPool.getResource()) {
            String value = objectMapper.writeValueAsString(exercises);
            jedis.setex(key, EXERCISE_CACHE_DURATION, value);
            logger.info("Cached exercises for workout: " + workoutId);
        } catch (JsonProcessingException | JedisException e) {
            logger.warning("Failed to cache workout exercises: " + e.getMessage());
        }
    }


    // Get cached exercises by workout ID
    public List<Object> getCachedWorkoutExercises(String workoutId) {
        String key = "exercises:workout:" + workoutId;
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get(key);
            if (value != null) {
                logger.info("Data retrieved from cache for workout exercises: " + workoutId);
                return objectMapper.readValue(value, objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
            } else {
                logger.info("Data not found in cache for workout exercises: " + workoutId);
                return null;
            }
        } catch (Exception e) {
            logger.warning("Failed to get cached workout exercises: " + e.getMessage());
            return null;
        }
    }


    // Invalidate user cache when data changes
    public void invalidateUserCache(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("workouts:user:" + userId);
            jedis.del("user:id:" + userId);
            logger.info("Invalidated cache for user: " + userId);
        } catch (JedisException e) {
            logger.warning("Failed to invalidate user cache: " + e.getMessage());
        }
    }


    // Invalidate workout cache
    public void invalidateWorkoutCache(String workoutId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("exercises:workout:" + workoutId);
            logger.info("Invalidated cache for workout: " + workoutId);
        } catch (JedisException e) {
            logger.warning("Failed to invalidate workout cache: " + e.getMessage());
        }
    }


    // Check cache connection status
    public boolean isConnected() {
        try (Jedis jedis = jedisPool.getResource()) {
            return "PONG".equals(jedis.ping());
        } catch (Exception e) {
            logger.warning("Redis connection check failed: " + e.getMessage());
            return false;
        }
    }


    // Close the Redis connection pool
    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            logger.info("Redis connection pool closed");
        }
    }
}
