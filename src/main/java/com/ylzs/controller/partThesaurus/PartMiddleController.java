package com.ylzs.controller.partThesaurus;

import com.ylzs.common.util.DateUtils;
import com.ylzs.common.util.ExcelUtil;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.entity.partThesaurus.PartMiddle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.partThesaurus.IPartMiddleService;

import java.util.List;


/**
 * 设计部件
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-26 17:08:11
 */
@RestController
@RequestMapping(value = "/designPart")
public class PartMiddleController {

    @Autowired
    private IPartMiddleService designPartService;


    @RequestMapping(value = "/batchSave")
    public Result batchSave(String fileName)throws Exception{


        try {
            List<PartMiddle> list = ExcelUtil.readExcelToEntity(PartMiddle.class,fileName);
            for(PartMiddle partMiddle : list){

                Long randomCode = SnowflakeIdUtil.generateId();
                partMiddle.setRandomCode(randomCode);
                partMiddle.setStatus(1);
                partMiddle.setIsInvalid(false);
                partMiddle.setVersion(DateUtils.formatDate(DateUtils.yyyyMMdd_HHmmssSSS));
            }
            designPartService.saveBatch(list);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Result.ok();
    }


}
