package com.ylzs;

import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.RedisUtil;
import com.ylzs.dao.bigstylecraft.BigStyleAnalyseMasterDao;
import com.ylzs.dao.bigstylecraft.BigStyleAnalysePartCraftDao;
import com.ylzs.dao.bigstylecraft.StyleSewingCraftActionDao;
import com.ylzs.dao.bigticketno.BigOrderMasterDao;
import com.ylzs.dao.craftAnalyse.CraftAnalyseDao;
import com.ylzs.dao.craftstd.CraftPartDao;
import com.ylzs.dao.custom.CustomStyleCraftCourseDao;
import com.ylzs.dao.designPart.DesignPartDao;
import com.ylzs.dao.plm.PICustomOrderDao;
import com.ylzs.dao.plm.PICustomOrderPartDao;
import com.ylzs.dao.plm.PICustomOrderPartMaterialDao;
import com.ylzs.dao.sewingcraft.SewingCraftWarehouseDao;
import com.ylzs.elasticsearch.sewingcraft.vo.SewingCraftWarehouseEsVo;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraft;
import com.ylzs.entity.craftstd.CraftPart;
import com.ylzs.entity.datadictionary.DictionaryType;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.timeparam.MotionCodeConfig;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import com.ylzs.service.datadictionary.IDictionaryTypeService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.service.staticdata.PartPositionService;
import com.ylzs.service.timeparam.MotionCodeConfigService;
import com.ylzs.vo.designpart.DesignPartVo;
import com.ylzs.vo.sewing.CraftAnalyseVo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GstProjectApplicationTests {


    @Resource
    private CraftAnalyseDao craftAnalyseDao ;

    @Resource
    private SewingCraftWarehouseDao craftWarehouseDao;


    @Test
    public void testMotion() {
        long start  = System.currentTimeMillis();
        Map<String,Object>param = new HashMap<>();
        param.put("start",0);
        param.put("pageSize",15);
      // List<CraftAnalyseVo> vos = craftAnalyseDao.getFromPartCraft(param);
         List<CraftAnalyseVo> vos = craftAnalyseDao.getFromThinkStyle(param);

       // List<CraftAnalyseVo> vos =  craftAnalyseDao.getFromBigStyle(param);
        //List<CraftAnalyseVo> vos =  craftAnalyseDao.getFromBigOrder(param);
       // List<CraftAnalyseVo> vos =  craftAnalyseDao.getFromCustomerOrder(param);

        long end  = System.currentTimeMillis();
        System.out.println("-----------("+(end-start)/1000+")----------------------");
        System.out.println(JSONObject.toJSONString(vos));

    }

    @Test
    public void testMotion1() {


    }

}