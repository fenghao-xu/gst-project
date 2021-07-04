package com.ylzs.service.timeparam;

import com.ylzs.dao.timeparam.MotionCodeConfigDao;
import com.ylzs.entity.timeparam.MotionCodeConfig;
import com.ylzs.vo.timeparam.MotionTypeVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 19:18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MotionCodeConfigService {

    @Resource
    private MotionCodeConfigDao motionCodeConfigDao;

    /**
     * 获取所有的动作定义代码
     */
    public  List<MotionCodeConfig> getAllMotionConfigs(String motionType) {
        return motionCodeConfigDao.getAllMotionConfigs(motionType);
    }
    public List<MotionTypeVo>getMotionType(){
        return motionCodeConfigDao.getMotionType();
    }


    /**
     * 根据编码查询动作
     */
    public List<MotionCodeConfig> getMotionByCode(String motionCode,String motionType){
        return  motionCodeConfigDao.getMotionByCode(motionCode,motionType);
    }
}
