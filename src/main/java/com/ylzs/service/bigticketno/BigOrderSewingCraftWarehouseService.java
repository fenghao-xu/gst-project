package com.ylzs.service.bigticketno;

import com.ylzs.dao.bigstylecraft.StyleSewingCraftWarehouseDao;
import com.ylzs.dao.bigticketno.BigOrderSewingCraftWarehouseDao;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.vo.bigstylereport.CraftVO;
import com.ylzs.vo.bigstylereport.WorkTypeReport;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-04-05 16:10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigOrderSewingCraftWarehouseService {

    @Resource
    private BigOrderSewingCraftWarehouseDao bigOrderSewingCraftWarehouseDao;

    public List<SewingCraftWarehouse> getDataByCraftCodeList(List<String> codeList) {
        return bigOrderSewingCraftWarehouseDao.getDataByCraftCodeList(codeList);
    }
    public List<SewingCraftWarehouse> getDataByBigStyleAnalyseCodeList(List<String> codeList) {
        return bigOrderSewingCraftWarehouseDao.getDataByBigStyleAnalyseCodeList(codeList);
    }
    public List<SewingCraftWarehouse> getDataByPartDetailIds(List<Long> idList) {
        return bigOrderSewingCraftWarehouseDao.getDataByPartDetailIds(idList);
    }

    public List<CraftVO> getDataForExcelReport(Long randomCode) {
        return bigOrderSewingCraftWarehouseDao.getDataForExcelReport(randomCode);
    }

    public List<CraftVO> getDataForExcelReportOrderByWorkOrder(Long randomCode) {
        return bigOrderSewingCraftWarehouseDao.getDataForExcelReportOrderByWorkOrder(randomCode);
    }

    public List<CraftVO> getDataForFinanceByWorkOrder(Long randomCode) {
        return bigOrderSewingCraftWarehouseDao.getDataForFinanceByWorkOrder(randomCode);
    }

    public List<WorkTypeReport> selectWorkTypeReport(Map<String, Object> param) {
        return bigOrderSewingCraftWarehouseDao.selectWorkTypeReport(param);
    }
}
