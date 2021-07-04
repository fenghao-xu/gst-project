package com.ylzs.service.bigticketno;

import com.ylzs.dao.bigticketno.FmsTicketNoDao;
import com.ylzs.entity.bigticketno.FmsTicketNo;
import com.ylzs.vo.bigstylereport.BigStyleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-06-10 8:14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FmsTicketNoService {
    @Resource
    private FmsTicketNoDao fmsTicketNoDao;

    public void addOrUpdateData(FmsTicketNo fmsTicketNo) {
        fmsTicketNoDao.addOrUpdateData(fmsTicketNo);
    }

    public List<FmsTicketNo> getAll(Map<String,Object> param) {

        return fmsTicketNoDao.getAll(param);
    }
    public List<BigStyleVO> getBigStyleAnalyseByTicketNo(String productionTicketNo){
       return  fmsTicketNoDao.getBigStyleAnalyseByTicketNo(productionTicketNo);
    }

    public FmsTicketNo getOneByProductionTicketNo(String productionTicketNo) {
        return fmsTicketNoDao.getOneByProductionTicketNo(productionTicketNo);
    }


}
