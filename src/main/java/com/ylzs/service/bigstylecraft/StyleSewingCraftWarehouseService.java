package com.ylzs.service.bigstylecraft;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftActionDao;
import com.ylzs.dao.bigstylecraft.StyleSewingCraftWarehouseDao;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.vo.bigstylereport.CraftVO;
import com.ylzs.vo.sewing.VSewingCraftVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-04-05 16:10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StyleSewingCraftWarehouseService {

    @Resource
    private StyleSewingCraftWarehouseDao styleSewingCraftWarehouseDao;

    @Resource
    private StyleSewingCraftActionDao styleSewingCraftActionDao;

    public List<SewingCraftWarehouse> getDataByCraftCodeList(List<String> codeList, String bigStyleAnalyseCode) {
        return styleSewingCraftWarehouseDao.getDataByCraftCodeList(codeList, bigStyleAnalyseCode);
    }

    public List<SewingCraftWarehouse> getDataByBigStyleAnalyseCodeList(List<String> codeList) {
        return styleSewingCraftWarehouseDao.getDataByBigStyleAnalyseCodeList(codeList);
    }

    public List<SewingCraftWarehouse> getDataByPartDetailIds(List<Long> idList) {
        return styleSewingCraftWarehouseDao.getDataByPartDetailIds(idList);
    }

    public List<CraftVO> getDataForExcelReport(Long randomCode) {
        return styleSewingCraftWarehouseDao.getDataForExcelReport(randomCode);
    }

    public List<CraftVO> getDataForExcelReportOrderByWorkOrder(Long randomCode) {
        return styleSewingCraftWarehouseDao.getDataForExcelReportOrderByWorkOrder(randomCode);
    }


    public List<VSewingCraftVo> getCraftCodeDataAll(Map<String, String> styleMap, int materialGrade, String mainFrameCode, List<String> bigStyleAnalyseCodes) {
        List<VSewingCraftVo> list = new ArrayList<>();
        try {
            List<VSewingCraftVo> tempList = styleSewingCraftWarehouseDao.getCraftCodeDataAll(materialGrade, mainFrameCode, bigStyleAnalyseCodes);
            if (null != tempList && tempList.size() > 0) {
                for (VSewingCraftVo vo : tempList) {
                    String key = vo.getBigStyleAnalyseCode() + vo.getCraftCode();
                    if (styleMap.containsKey(key)) {
                        list.add(vo);
                    }
                }
            }
            list.parallelStream().forEach(house -> {
                house.setMotionsList(styleSewingCraftActionDao.getDataBySewingCraftRandomCodeAndCraftCode(house.getRandomCode(), house.getCraftCode()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
