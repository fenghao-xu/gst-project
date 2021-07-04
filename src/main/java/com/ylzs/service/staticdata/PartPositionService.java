package com.ylzs.service.staticdata;

import com.ylzs.dao.staticdata.PartPositionDao;
import com.ylzs.entity.craftstd.PartPosition;
import com.ylzs.service.IOriginService;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.vo.PartPositionVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-04 15:30
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PartPositionService extends OriginServiceImpl<PartPositionDao, PartPosition> implements IOriginService<PartPosition> {

    @Resource
    private PartPositionDao partPositionDao;


    public List<PartPosition> getAllPartPositions() {
        return partPositionDao.getAllPartPositions();
    }

    /**
     * @param clothingCategoryCode--服装品类编码
     * @return
     */
    public List<PartPosition> getAllPartPositionByCategory(String clothingCategoryCode) {
        return partPositionDao.getAllPartPositionByCategory(clothingCategoryCode);
    }
    public List<PartPosition> getPartPositionByType( String partType){
       return  partPositionDao.getPartPositionByType(partType);
    }

    /**
     * 根据指定类型查询部件位置数据
     *
     * @param map
     * @return
     */
    public List<PartPositionVo> getPartPositonDataList(HashMap map) {
        return partPositionDao.getPartPositonDataList(map);
    }
    /**
     * 获取所有的缝边位置,或者根据服装品类获取缝边位置
     */
    public List<PartPosition> getSewingPartPositions(String param) {
        return partPositionDao.getSewingPartPositions(param);
    }

    public List<PartPosition> getAll(){
        return partPositionDao.getAllPartPositions();
    }

    public void add(List<PartPosition> partPositionList){

        partPositionDao.addPartPosition(partPositionList);

    }

    public void update(List<PartPosition> partPositionList){

        partPositionDao.updatePartPosition(partPositionList);

    }

    public void addOrUpdatePartPosition(List<PartPosition> allPartPositionList){
        partPositionDao.addOrUpdatePartPosition(allPartPositionList);
    }
}
