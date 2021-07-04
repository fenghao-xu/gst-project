package com.ylzs.controller.partThesaurus;

import com.ylzs.common.util.DateUtils;
import com.ylzs.common.util.ExcelUtil;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.entity.partThesaurus.SewPositionCategory;
import org.apache.commons.lang3.SystemUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ylzs.service.partThesaurus.ISewPositionCategoryService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 缝边位置与品类关系表
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-27 09:36:52
 */
@RestController
public class SewPositionCategoryController {

    private static final Logger logger = LoggerFactory.getLogger(SewPositionCategoryController.class);

    @Autowired
    private ISewPositionCategoryService sewPositionCategoryService;


    @RequestMapping(value = "/batchSave")
    public Result batchSave(String fileName)throws Exception{


        try {
            List<SewPositionCategory> list = ExcelUtil.readExcelToEntity(SewPositionCategory.class,fileName);
            for(SewPositionCategory category : list){

                Long randomCode = SnowflakeIdUtil.generateId();
                category.setRandomCode(randomCode);
                category.setStatus(1);
                category.setIsInvalid(false);
                category.setVersion(DateUtils.formatDate(DateUtils.yyyyMMdd_HHmmssSSS));
            }
            sewPositionCategoryService.saveOrUpdateBatch(list,100);
        }catch (Exception ex){

        }
        return Result.ok();
    }


}
