package com.ylzs.common.constant;

/**
 * @author weikang
 * @Description
 * @Date 2019/11/14
 */
public class RedisContants {

    //KEYS[1]加锁的key ARGV[1]客户端ID
    public static final String DELSCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    public static final String GETSCRIPT = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";

    public static final Long RELEASE_SUCCESS = 1L;

    public static final String LOCK_SUCCESS = "OK";

    //即当key不存在时，我们进行set操作；若key已经存在，则不做任何操作
    public static final String SET_IF_NOT_EXIST = "NX";

    //要给这个key加一个过期的设置
    public static final String SET_WITH_EXPIRE_TIME = "PX";

    /**
     * 过期时间设置成100秒
     */
    public static final Long EXPIRE_TIME = 100000L;

    /**
     * 过期时间设置成5分钟
     */
    public static final Long FIVE_MIN_EXPIRE_TIME = 5 * 60 * 1000L;

    /**
     * 登录的过期时间设置成24小时
     */
    public static final Long LOGIN_EXPIRE_TIME = 24 * 3600L;

    public static final String REDIS_LOGIN_KEY_PRE = "login:token";
}
