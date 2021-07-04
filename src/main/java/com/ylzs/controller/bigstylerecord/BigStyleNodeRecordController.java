package com.ylzs.controller.bigstylerecord;


import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.controller.auth.IModuleInfo;
import com.ylzs.controller.auth.UserContext;
import com.ylzs.entity.bigstylerecord.BigStyleNodeRecord;
import com.ylzs.entity.system.User;
import com.ylzs.service.bigstylerecord.IBigStyleNodeRecordService;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.service.interfaceOutput.ISectionSMVService;
import com.ylzs.service.system.IUserService;
import com.ylzs.vo.BigStyleNodeRecordExportVo;
import com.ylzs.vo.DictionaryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.ylzs.common.util.Assert.notBlank;

@RestController
@RequestMapping("/bigStyleRecord")
@Api(tags = "大货款式记录")
public class BigStyleNodeRecordController implements IModuleInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(BigStyleNodeRecordController.class);
    @Resource
    IBigStyleNodeRecordService bigStyleNodeRecordService;
    @Resource
    ISectionSMVService sectionSMVService;
    @Resource
    IUserService userService;
    @Resource
    IDictionaryService dictionaryService;

    private Pair<Date,Date> getBeginEndDate(String beginTime, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date beginDate = null;
        Date endDate = null;

        try {
            beginDate = dateFormat.parse(beginTime);
            endDate = dateFormat.parse(endTime);
            cal.setTime(endDate);
            cal.add(Calendar.DATE, 1);
            endDate = cal.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ImmutablePair<>(beginDate, endDate);


    }



    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询大货款式记录")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<BigStyleNodeRecord>> getAll(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                   @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                   @RequestParam(name = "keywords", required = false) String keywords,
                                                   @RequestParam(name = "assignToUserCode", required = false) String assignToUserCode,
                                                   @RequestParam(name = "clothesCategoryCodes", required = false) String clothesCategoryCodes,
                                                   @RequestParam(name = "receiveTimeBegin", required = false) String receiveTimeBegin,
                                                   @RequestParam(name = "receiveTimeEnd", required = false) String receiveTimeEnd,
                                                   @RequestParam(name = "sampleReceiveTime1Begin", required = false) String sampleReceiveTime1Begin,
                                                   @RequestParam(name = "sampleReceiveTime1End", required = false) String sampleReceiveTime1End,
                                                   @RequestParam(name = "sampleReceiveTime2Begin", required = false) String sampleReceiveTime2Begin,
                                                   @RequestParam(name = "sampleReceiveTime2End", required = false) String sampleReceiveTime2End,

                                                   @RequestParam(name = "branchBeginTimeBegin", required = false) String branchBeginTimeBegin,
                                                   @RequestParam(name = "branchBeginTimeEnd", required = false) String branchBeginTimeEnd,
                                                   @RequestParam(name = "branchEndTimeBegin", required = false) String branchEndTimeBegin,
                                                   @RequestParam(name = "branchEndTimeEnd", required = false) String branchEndTimeEnd) {


        QueryWrapper<BigStyleNodeRecord> styleWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(clothesCategoryCodes)) {
            String[] categoryCodeArr = clothesCategoryCodes.split(",");
            styleWrapper.lambda().in(BigStyleNodeRecord::getClothesCategoryCode, categoryCodeArr);
        }
        if (StringUtils.isNotBlank(assignToUserCode)) {
            styleWrapper.lambda().eq(BigStyleNodeRecord::getAssignToUserCode, assignToUserCode);
        }


        Date beginDate = null;
        Date endDate = null;
        Pair<Date, Date> beginEndDatePair = null;
        if (StringUtils.isNotBlank(receiveTimeBegin) && StringUtils.isNotBlank(receiveTimeEnd)) {
            beginEndDatePair = getBeginEndDate(receiveTimeBegin, receiveTimeEnd);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getReceiveTime, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getReceiveTime, beginEndDatePair.getRight());
        }
        if (StringUtils.isNotBlank(sampleReceiveTime1Begin) && StringUtils.isNotBlank(sampleReceiveTime1End)) {
            beginEndDatePair = getBeginEndDate(sampleReceiveTime1Begin, sampleReceiveTime1End);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getSampleReceiveTime1, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getSampleReceiveTime1, beginEndDatePair.getRight());
        }
        if (StringUtils.isNotBlank(sampleReceiveTime2Begin) && StringUtils.isNotBlank(sampleReceiveTime2End)) {
            beginEndDatePair = getBeginEndDate(sampleReceiveTime2Begin, sampleReceiveTime2End);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getSampleReceiveTime2, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getSampleReceiveTime2, beginEndDatePair.getRight());
        }

        if (StringUtils.isNotBlank(branchBeginTimeBegin) && StringUtils.isNotBlank(branchBeginTimeEnd)) {
            beginEndDatePair = getBeginEndDate(branchBeginTimeBegin, branchBeginTimeEnd);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getBranchBeginTime, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getBranchBeginTime, beginEndDatePair.getRight());
        }

        if (StringUtils.isNotBlank(branchEndTimeBegin) && StringUtils.isNotBlank(branchEndTimeEnd)) {
            beginEndDatePair = getBeginEndDate(branchEndTimeBegin, branchEndTimeEnd);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getBranchEndTime, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getBranchEndTime, beginEndDatePair.getRight());
        }


        if(StringUtils.isNotBlank(keywords)) {
            styleWrapper.lambda().and(qw->qw.like(BigStyleNodeRecord::getStyleCode,keywords).or().like(BigStyleNodeRecord::getStyleDesc,keywords));
        }
        styleWrapper.lambda().orderByDesc(BigStyleNodeRecord::getReceiveTime);

        PageHelper.startPage(page, rows);
        List<BigStyleNodeRecord> styles = bigStyleNodeRecordService.list(styleWrapper);
        PageInfo<BigStyleNodeRecord> pageInfo = new PageInfo<>(styles);
        return Result.ok(styles, pageInfo.getTotal());
    }

    @RequestMapping(value = "/getCurrTime", method = RequestMethod.GET)
    @ApiOperation(value = "getCurrTime", notes = "查询服务器当前时间")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<Date> getCurrTime() {
        return Result.ok(new Date());
    }

    @RequestMapping(value = "/sendPreSectionSMV", method = RequestMethod.POST)
    @ApiOperation(value = "sendPreSectionSMV", notes = "发送预估工段工时")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<BigStyleNodeRecord> sendPreSectionSMV(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = true) UserContext currentUser,
                                       @RequestParam(name = "styleCode", required = true) String styleCode,
                                       @RequestParam(name = "estimateSpend", required = true) String estimateSpend) {
        BigDecimal spend = new BigDecimal(estimateSpend);
        Date sendDate = new Date();

        sectionSMVService.sendBigStylePreSectionSMV(styleCode, spend, true);
        BigStyleNodeRecord style = new BigStyleNodeRecord();
        style.setSendFmsTime(sendDate);
        style.setEstimateSewingTime(spend);
        style.setUpdateTime(sendDate);
        style.setUpdateUser(currentUser.getUserName());

        QueryWrapper<BigStyleNodeRecord> styleWrapper = new QueryWrapper<>();
        styleWrapper.lambda().eq(BigStyleNodeRecord::getStyleCode, styleCode);
        bigStyleNodeRecordService.update(style, styleWrapper);

        BigStyleNodeRecord newStyle = bigStyleNodeRecordService.getOne(styleWrapper);
        return Result.ok(newStyle);
    }

    @RequestMapping(value = "/updateAll", method = RequestMethod.POST)
    @ApiOperation(value = "updateAll", notes = "更新记录")
    @Authentication(auth = Authentication.AuthType.EDIT, required = false)
    public Result updateAll(@RequestAttribute(value = Const.REQUEST_KEY_USER, required = true) UserContext currentUser,
                            @RequestBody String jsonString) {
        JSONArray jsonArr = JSON.parseArray(jsonString);
        List<BigStyleNodeRecord> styles = new ArrayList<>();
        for(int i = 0; i < jsonArr.size(); i++) {
            JSONObject jsonItm = jsonArr.getJSONObject(i);
            notBlank(jsonItm.getString("styleCode"), "款式编码不能为空");
            BigStyleNodeRecord style = new BigStyleNodeRecord();
            style.setStyleCode(jsonItm.getString("styleCode"));
            style.setEstimateSewingTime(jsonItm.getBigDecimal("estimateSewingTime"));
            style.setSampleReceiveTime1(jsonItm.getDate("sampleReceiveTime1"));
            style.setSampleReceiveTime2(jsonItm.getDate("sampleReceiveTime2"));
//            style.setAssignToUserCode(jsonItm.getString("assignToUserCode"));
//            style.setAssignToUserName(jsonItm.getString("assignToUserName"));
            style.setRemark(jsonItm.getString("remark"));
            style.setUpdateUser(currentUser.getUserName());
            style.setUpdateTime(new Date());
            styles.add(style);
        }
        bigStyleNodeRecordService.updateAll(styles);
        return Result.ok();
    }

    @RequestMapping(value = "/getDropInfo", method = RequestMethod.GET)
    @ApiOperation(value = "getDropInfo", notes = "获取下拉信息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result getDropInfo() {
        String[] roleCodes= {"01", "02", "03", "04", "09", "10"};
        List<User> users = userService.getUserByRoleCode(roleCodes);
        List<DictionaryVo> clothesCategorys = dictionaryService.getDictoryAll("ClothesCategory");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("users", users);
        jsonObject.put("clothesCategorys", clothesCategorys);

        return Result.ok(jsonObject);
    }


    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "export", notes = "导出工位工序")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<BigStyleNodeRecord>> export(@RequestParam(defaultValue = Const.DEFAULT_PAGE) Integer page,
                                                   @RequestParam(defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                                   @RequestParam(name = "keywords", required = false) String keywords,
                                                   @RequestParam(name = "assignToUserCode", required = false) String assignToUserCode,
                                                   @RequestParam(name = "clothesCategoryCodes", required = false) String clothesCategoryCodes,
                                                   @RequestParam(name = "receiveTimeBegin", required = false) String receiveTimeBegin,
                                                   @RequestParam(name = "receiveTimeEnd", required = false) String receiveTimeEnd,
                                                   @RequestParam(name = "sampleReceiveTime1Begin", required = false) String sampleReceiveTime1Begin,
                                                   @RequestParam(name = "sampleReceiveTime1End", required = false) String sampleReceiveTime1End,
                                                  @RequestParam(name = "sampleReceiveTime2Begin", required = false) String sampleReceiveTime2Begin,
                                                   @RequestParam(name = "sampleReceiveTime2End", required = false) String sampleReceiveTime2End,
                                                   @RequestParam(name = "branchBeginTimeBegin", required = false) String branchBeginTimeBegin,
                                                   @RequestParam(name = "branchBeginTimeEnd", required = false) String branchBeginTimeEnd,
                                                   @RequestParam(name = "branchEndTimeBegin", required = false) String branchEndTimeBegin,
                                                   @RequestParam(name = "branchEndTimeEnd", required = false) String branchEndTimeEnd
            , HttpServletResponse response) {

        QueryWrapper<BigStyleNodeRecord> styleWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(clothesCategoryCodes)) {
            String[] categoryCodeArr = clothesCategoryCodes.split(",");
            styleWrapper.lambda().in(BigStyleNodeRecord::getClothesCategoryCode, categoryCodeArr);
        }
        if (StringUtils.isNotBlank(assignToUserCode)) {
            styleWrapper.lambda().eq(BigStyleNodeRecord::getAssignToUserCode, assignToUserCode);
        }


        Date beginDate = null;
        Date endDate = null;
        Pair<Date, Date> beginEndDatePair = null;
        if (StringUtils.isNotBlank(receiveTimeBegin) && StringUtils.isNotBlank(receiveTimeEnd)) {
            beginEndDatePair = getBeginEndDate(receiveTimeBegin, receiveTimeEnd);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getReceiveTime, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getReceiveTime, beginEndDatePair.getRight());
        }
        if (StringUtils.isNotBlank(sampleReceiveTime1Begin) && StringUtils.isNotBlank(sampleReceiveTime1End)) {
            beginEndDatePair = getBeginEndDate(sampleReceiveTime1Begin, sampleReceiveTime1End);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getSampleReceiveTime1, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getSampleReceiveTime1, beginEndDatePair.getRight());
        }
        if (StringUtils.isNotBlank(sampleReceiveTime2Begin) && StringUtils.isNotBlank(sampleReceiveTime2End)) {
            beginEndDatePair = getBeginEndDate(sampleReceiveTime2Begin, sampleReceiveTime2End);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getSampleReceiveTime2, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getSampleReceiveTime2, beginEndDatePair.getRight());
        }
        if (StringUtils.isNotBlank(branchBeginTimeBegin) && StringUtils.isNotBlank(branchBeginTimeEnd)) {
            beginEndDatePair = getBeginEndDate(branchBeginTimeBegin, branchBeginTimeEnd);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getBranchBeginTime, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getBranchBeginTime, beginEndDatePair.getRight());
        }

        if (StringUtils.isNotBlank(branchEndTimeBegin) && StringUtils.isNotBlank(branchEndTimeEnd)) {
            beginEndDatePair = getBeginEndDate(branchEndTimeBegin, branchEndTimeEnd);
            styleWrapper.lambda().ge(BigStyleNodeRecord::getBranchEndTime, beginEndDatePair.getLeft())
                    .lt(BigStyleNodeRecord::getBranchEndTime, beginEndDatePair.getRight());
        }

        if(StringUtils.isNotBlank(keywords)) {
            styleWrapper.lambda().and(qw->qw.like(BigStyleNodeRecord::getStyleCode,keywords).or().like(BigStyleNodeRecord::getStyleDesc,keywords));
        }
        styleWrapper.lambda().orderByDesc(BigStyleNodeRecord::getReceiveTime);
        List<BigStyleNodeRecord> styles = bigStyleNodeRecordService.list(styleWrapper);
        List<BigStyleNodeRecordExportVo> list = bigStyleNodeRecordService.getBigStyleNodeRecordVos(styles);
        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名

            writer.addHeaderAlias("styleCode", "款式编码");
            writer.addHeaderAlias("clothesCategoryName", "服装品类");
            writer.addHeaderAlias("styleDesc", "款式描述");
            writer.addHeaderAlias("sampleReceiveTime1", "样衣第一次接收时间");
            writer.addHeaderAlias("estimateSewingTime", "预估缝制工时（分）");
            writer.addHeaderAlias("sendFmsTime", "发送FMS");
            writer.addHeaderAlias("sampleReceiveTime2", "样衣第二次接收时间");
            writer.addHeaderAlias("productGroupName", "生产组别");
            writer.addHeaderAlias("scheduleTime", "预排产开始日期");
            writer.addHeaderAlias("deliveryTime", "预排产交货日期");
            writer.addHeaderAlias("assignToUserName", "分科员");

            writer.addHeaderAlias("branchBeginTime", "分科开始时间");
            writer.addHeaderAlias("branchEndTime", "分科结束时间");
            writer.addHeaderAlias("branchSpend", "分科时间");
            writer.addHeaderAlias("sampleStaySpend", "样衣停留时间");
            writer.addHeaderAlias("remark", "备注");
            writer.addHeaderAlias("receiveTime", "系统接收时间");
            writer.addHeaderAlias("updateUser", "更新人");
            writer.addHeaderAlias("updateTime", "更新时间");

            writer.write(list, true);

            String fileName = URLEncoder.encode("大货款式记录清单.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("bigStyleRecord export fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;
    }

    @Override
    public String getModuleCode() {
        return "bigStyleRecord";
    }
}
