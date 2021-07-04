package com.ylzs.service.staticdata;

import com.ylzs.dao.craftstd.WorkTypeDao;
import com.ylzs.entity.craftstd.WorkType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-16 17:57
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkTypeService {

    @Resource
    private WorkTypeDao workTypeDao;

    /**
     * 获取所有的工种，并且每个工种下面的做工类型也取出来
     */
    public List<WorkType> getWorkTypeAndMakeType() {
        return workTypeDao.getWorkTypeAndMakeType();
    }

    public List<WorkType> getSewingWorkType() {
        return workTypeDao.getSewingWorkType();
    }
}
