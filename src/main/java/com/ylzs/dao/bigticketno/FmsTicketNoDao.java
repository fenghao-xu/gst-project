package com.ylzs.dao.bigticketno;

import com.ylzs.entity.bigticketno.FmsTicketNo;
import com.ylzs.vo.bigstylereport.BigStyleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-06-09 19:27
 */
public interface FmsTicketNoDao {
    public void addOrUpdateData(FmsTicketNo fmsTicketNo);

    public List<FmsTicketNo> getAll(Map<String, Object> param);

    public List<BigStyleVO> getBigStyleAnalyseByTicketNo(@Param("productionTicketNo") String productionTicketNo);

    FmsTicketNo getOneByProductionTicketNo(@Param("productionTicketNo") String productionTicketNo);
}
