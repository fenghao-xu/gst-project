package com.ylzs.schedual;

import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.service.custom.ICustomStyleCraftCourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @className AutoUpdateCustomStyleSchedule
 * @Description
 * @Author sky
 * @create 2020-05-16 13:36:08
 */
@Component
public class AutoUpdateCustomStyleSchedule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutoUpdateCustomStyleSchedule.class);

    @Autowired
    private ICustomStyleCraftCourseService customStyleCraftCourseService;

    @Async
    @Scheduled(cron = "0 0 0 * * ? ")
    public void autoReleaseCustomStyle(){
        LOGGER.info("execute custom order release command");
        List<CustomStyleCraftCourse> releaseList = customStyleCraftCourseService.queryCustomOrderForTheCurrentDateList();
        if(ObjectUtils.isNotEmptyList(releaseList)){
            try {
                for (CustomStyleCraftCourse craftCourse : releaseList) {
                    customStyleCraftCourseService.releaseCustomStyle(craftCourse);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
