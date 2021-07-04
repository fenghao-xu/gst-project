package com.ylzs.service.craftstd.impl;

import com.ylzs.common.util.BaseException;
import com.ylzs.common.util.CreateSerialUtil;
import com.ylzs.common.util.RedisUtil;
import com.ylzs.dao.craftstd.MaxCodeDao;
import com.ylzs.entity.craftstd.MaxCode;
import com.ylzs.service.craftstd.IMaxCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Transactional(rollbackFor = Exception.class)
public class MaxCodeService implements IMaxCodeService {
    @Resource
    private MaxCodeDao maxCodeDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param moduleCode 模块代码
     * @param preStr 编码前缀
     * @return 当前最大流水号
     */
    @Override
    public Integer updateMaxId(String moduleCode, String preStr) {
        MaxCode maxCode = new MaxCode();
        maxCode.setModuleCode(moduleCode);
        maxCode.setPreStr(preStr);

        Integer ret = maxCodeDao.updateMaxId(maxCode);
        if(ret == null || ret == 0) {
            maxCode.setMaxId(1);
            maxCode.setCreateTime(new Date());
            ret = maxCodeDao.addMaxCode(maxCode);
            if(ret == null || ret == 0) {
                throw new BaseException("创建流水号失败");
            }
            ret = maxCodeDao.updateMaxId(maxCode);
            if(ret == null || ret == 0) {
                throw new BaseException("更新流水号失败");
            }
        }
        return maxCode.getMaxId();
    }

    @Override
    public String getNextSerialNo(String moduleCode, String preStr, int len, boolean hasHistory) {
        Integer lastSerialNo = null;
        if(hasHistory) {
            lastSerialNo = maxCodeDao.selectMaxId(moduleCode, preStr);
        }
        String newSerialNo =  CreateSerialUtil.createSerial(redisUtil, redisTemplate, moduleCode, preStr, len,lastSerialNo);
        if(null == newSerialNo || newSerialNo.isEmpty()) {
            throw new BaseException("产生流水号失败");
        }
        return preStr+newSerialNo;
    }


}
