package com.ylzs.controller.sewingcraft;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.entity.craftstd.CraftCategory;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.sewingcraft.ICraftAnalyseService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.vo.sewing.CraftAnalyseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static java.util.Collections.synchronizedList;

@Api(tags = "工序分析")
@RestController
@RequestMapping(value = "/craft-analyse")
public class CraftAnalyseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CraftAnalyseController.class);

    @Resource
    private ICraftCategoryService craftCategoryService;
    @Resource
    private SewingCraftWarehouseService sewingCraftWarehouseService;
    @Resource
    private ICraftAnalyseService craftAnalyseService;


    @Resource
    ThreadPoolTaskExecutor taskExecutor;


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //yyyy-MM-dd HH:mm:ss
        dateFormat.setLenient(false);//是否严格解析时间 false则严格解析 true宽松解析
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * @return 获取页面下拉列表信息
     */
    @RequestMapping(value = "/getDropDownData", method = RequestMethod.GET)
    @ApiOperation(value = "getDropDownData", notes = "查询下拉信息")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<JSONObject> getDropDownData() {
        //返回结果
        List<CraftCategory> craftCategorys = craftCategoryService.getAllValidCraftCategory();
        JSONObject result = new JSONObject();
        result.put("craftCategorys", craftCategorys);
        Map<String, String> sourceTypeMap = new HashMap<>();
        sourceTypeMap.put("1", "部件工艺");
        sourceTypeMap.put("2", "智库款工艺");
        sourceTypeMap.put("3", "款式工艺路线");
        sourceTypeMap.put("4", "订单工艺路线");
        sourceTypeMap.put("5", "工单工艺路线");
        result.put("craftSourceType", sourceTypeMap);

        return Result.ok(result);
    }


    /**
     * @param page               第几页
     * @param rows               行数
     * @param craftCode          工序编码
     * @param productionTicketNo 工单号
     * @param customOrderNo      定制订单号
     * @param outCode            部件工艺/智库款工艺/款式工艺路线编码
     * @param craftCategoryCode  工艺品类编码
     * @param isPic              有图片
     * @param isVideo            有视频
     * @param craftSourceType    工序来源 1部件工艺 2智库款工艺 3款式工艺路线 4订单工艺路线 5工单工艺路线
     * @param status             状态 1000草稿 1020已发布
     * @return 工序分析列表
     */
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "getAll", notes = "查询工序报表")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<CraftAnalyseVo>> getAll(@RequestParam @ApiParam(name = "page", value = "第几页 默认1", defaultValue = Const.DEFAULT_PAGE) Integer page,
                                               @RequestParam @ApiParam(name = "rows", value = "显示行数 默认30", defaultValue = Const.DEFAULT_ROWS) Integer rows,
                                               @RequestParam @ApiParam(name = "craftCode", value = "工序编码", required = false) String craftCode,
                                               @RequestParam @ApiParam(name = "productionTicketNo", value = "工单号", required = false) String productionTicketNo,
                                               @RequestParam @ApiParam(name = "customOrderNo", value = "定制订单号", required = false) String customOrderNo,
                                               @RequestParam @ApiParam(name = "partCraftCode", value = "部件工艺编码", required = false) String partCraftCode,
                                               @RequestParam @ApiParam(name = "thinkTyleCode", value = "智库款工艺编码", required = false) String thinkTyleCode,
                                               @RequestParam @ApiParam(name = "bigStyleAnalyseCode", value = "款式工艺编码", required = false) String bigStyleAnalyseCode,
                                               @RequestParam @ApiParam(name = "craftCategoryCode", value = "工艺品类编码", required = false) String craftCategoryCode,
                                               @RequestParam @ApiParam(name = "isPic", value = "有图片", required = false) Boolean isPic,
                                               @RequestParam @ApiParam(name = "isVideo", value = "有视频", required = false) Boolean isVideo,
                                               @RequestParam @ApiParam(name = "craftSourceType", value = "工序来源 1部件工艺 2智库款工艺 3款式工艺路线 4订单工艺路线 5工单工艺路线", required = false) String craftSourceType,
                                               @RequestParam @ApiParam(name = "status", value = "状态 1000草稿 1020已发布", required = false) Integer status) {
        Map<String, Object> param = new HashMap();
        if (StringUtils.isNotEmpty(craftCode)) {
            craftCode = StringUtils.replaceBlank(craftCode);
        }
        param.put("craftCode", craftCode);

        if (StringUtils.isNotEmpty(productionTicketNo)) {
            productionTicketNo = StringUtils.replaceBlank(productionTicketNo);
        }
        param.put("productionTicketNo", productionTicketNo);

        if (StringUtils.isNotEmpty(customOrderNo)) {
            customOrderNo = StringUtils.replaceBlank(customOrderNo);
        }
        param.put("customOrderNo", customOrderNo);

        if (StringUtils.isNotEmpty(partCraftCode)) {
            partCraftCode = StringUtils.replaceBlank(partCraftCode);
        }
        param.put("partCraftCode", partCraftCode);
        param.put("start", (page - 1) * rows);
        param.put("pageSize", rows);

        if (StringUtils.isNotEmpty(thinkTyleCode)) {
            thinkTyleCode = StringUtils.replaceBlank(thinkTyleCode);
        }
        param.put("thinkTyleCode", thinkTyleCode);

        if (StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            bigStyleAnalyseCode = StringUtils.replaceBlank(bigStyleAnalyseCode);
        }
        param.put("bigStyleAnalyseCode", bigStyleAnalyseCode);

        param.put("craftCategoryCode", craftCategoryCode);
        param.put("isPic", isPic);
        param.put("isVideo", isVideo);
        param.put("craftSourceType", craftSourceType);
        param.put("status", status);
        //  PageHelper.startPage(page, rows);
        List<CraftAnalyseVo> craftAnalyseVos = getData(customOrderNo, productionTicketNo, partCraftCode, bigStyleAnalyseCode, thinkTyleCode, param);
        List<CraftAnalyseVo> result = new ArrayList<>();
        try {
            int length = craftAnalyseVos.size();
            result = craftAnalyseVos.subList((page - 1) * rows, length > rows ? rows : length);
        } catch (Exception e) {
            result = Collections.emptyList();
        }
        return Result.ok(result, (long) craftAnalyseVos.size());
    }

    List<CraftAnalyseVo> getData(String customOrderNo, String productionTicketNo, String partCraftCode, String bigStyleAnalyseCode, String thinkTyleCode, Map<String, Object> param) {
        final List<CraftAnalyseVo> craftAnalyseVos = synchronizedList(new ArrayList<CraftAnalyseVo>());
        //定制订单有输入，查询定制订单的
        List<CraftAnalyseVo> tempList = new ArrayList<>();
        if (StringUtils.isNotEmpty(customOrderNo)) {
            tempList = craftAnalyseService.getFromCustomerOrder(param);//仅仅只查定制订单工艺编码
        } else if (StringUtils.isNotEmpty(productionTicketNo)) {
            tempList = craftAnalyseService.getFromBigOrder(param);////仅仅只查工单工艺编码
        } else if (StringUtils.isNotEmpty(partCraftCode)) {
            tempList = craftAnalyseService.getFromPartCraft(param);//仅仅只查部件工艺编码
        } else if (StringUtils.isEmpty(productionTicketNo) && StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            tempList = craftAnalyseService.getFromBigStyle(param);//仅仅只查款式工艺
        } else if (StringUtils.isEmpty(customOrderNo) && StringUtils.isNotEmpty(thinkTyleCode)) {//仅仅只查智库款工艺
            tempList = craftAnalyseService.getFromThinkStyle(param);//仅仅只查智库款工艺
        } else {
            final CountDownLatch count = new CountDownLatch(5);
            taskExecutor.execute(() -> {
                try {
                    List<CraftAnalyseVo> tmp = craftAnalyseService.getFromCustomerOrder(param);//仅仅只查定制订单工艺编码
                    if (null != tmp && tmp.size() > 0) {
                        craftAnalyseVos.addAll(tmp);
                    }
                } finally {
                    count.countDown();
                }

            });
            taskExecutor.execute(() -> {
                try {
                    List<CraftAnalyseVo> tmp = craftAnalyseService.getFromBigOrder(param);////仅仅只查工单工艺编码
                    if (null != tmp && tmp.size() > 0) {
                        craftAnalyseVos.addAll(tmp);
                    }
                } finally {
                    count.countDown();
                }

            });
            taskExecutor.execute(() -> {
                try {
                    List<CraftAnalyseVo> tmp = craftAnalyseService.getFromPartCraft(param);//仅仅只查部件工艺编码
                    if (null != tmp && tmp.size() > 0) {
                        craftAnalyseVos.addAll(tmp);
                    }
                } finally {
                    count.countDown();
                }

            });
            taskExecutor.execute(() -> {
                try {
                    List<CraftAnalyseVo> tmp = craftAnalyseService.getFromBigStyle(param);//大货款式工艺
                    if (null != tmp && tmp.size() > 0) {
                        craftAnalyseVos.addAll(tmp);
                    }
                } finally {
                    count.countDown();
                }

            });
            taskExecutor.execute(() -> {
                try {
                    List<CraftAnalyseVo> tmp = craftAnalyseService.getFromThinkStyle(param);//仅仅只查智库款工艺
                    if (null != tmp && tmp.size() > 0) {
                        craftAnalyseVos.addAll(tmp);
                    }
                } finally {
                    count.countDown();
                }

            });
            try {
                count.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (craftAnalyseVos.size() == 0 && null != tempList && tempList.size() > 0) {
            craftAnalyseVos.addAll(tempList);
        }
        return craftAnalyseVos;
    }

    /**
     * @param craftCode          工序编码
     * @param productionTicketNo 工单号
     * @param customOrderNo      定制订单号
     * @param outCode            部件工艺/智库款工艺/款式工艺路线编码
     * @param craftCategoryCode  工艺品类
     * @param isPic              有图片
     * @param isVideo            有视频
     * @param craftSourceType    工序来源 1部件工艺 2智库款工艺 3款式工艺路线 4订单工艺路线 5工单工艺路线
     * @param status             状态 1000草稿 1020已发布
     * @return 工序分析报表
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "export", notes = "导出工序报表")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public Result<List<CraftAnalyseVo>> export(@RequestParam @ApiParam(name = "craftCode", value = "工序编码", required = false) String craftCode,
                                               @RequestParam @ApiParam(name = "productionTicketNo", value = "工单号", required = false) String productionTicketNo,
                                               @RequestParam @ApiParam(name = "customOrderNo", value = "定制订单号", required = false) String customOrderNo,
                                               @RequestParam @ApiParam(name = "partCraftCode", value = "部件工艺编码", required = false) String partCraftCode,
                                               @RequestParam @ApiParam(name = "thinkTyleCode", value = "智库款工艺编码", required = false) String thinkTyleCode,
                                               @RequestParam @ApiParam(name = "bigStyleAnalyseCode", value = "款式工艺编码", required = false) String bigStyleAnalyseCode,
                                               @RequestParam @ApiParam(name = "craftCategoryCode", value = "工艺品类编码", required = false) String craftCategoryCode,
                                               @RequestParam @ApiParam(name = "isPic", value = "有图片", required = false) Boolean isPic,
                                               @RequestParam @ApiParam(name = "isVideo", value = "有视频", required = false) Boolean isVideo,
                                               @RequestParam @ApiParam(name = "craftSourceType", value = "工序来源 1部件工艺 2智库款工艺 3款式工艺路线 4订单工艺路线 5工单工艺路线", required = false) String craftSourceType,
                                               @RequestParam @ApiParam(name = "status", value = "状态 1000草稿 1020已发布", required = false) Integer status,
                                               HttpServletResponse response) {
        Map<String, Object> param = new HashMap();
        if (StringUtils.isNotEmpty(craftCode)) {
            craftCode = StringUtils.replaceBlank(craftCode);
        }
        param.put("craftCode", craftCode);

        if (StringUtils.isNotEmpty(productionTicketNo)) {
            productionTicketNo = StringUtils.replaceBlank(productionTicketNo);
        }
        param.put("productionTicketNo", productionTicketNo);

        if (StringUtils.isNotEmpty(customOrderNo)) {
            customOrderNo = StringUtils.replaceBlank(customOrderNo);
        }
        param.put("customOrderNo", customOrderNo);

        if (StringUtils.isNotEmpty(partCraftCode)) {
            partCraftCode = StringUtils.replaceBlank(partCraftCode);
        }
        param.put("partCraftCode", partCraftCode);
        if (StringUtils.isNotEmpty(thinkTyleCode)) {
            thinkTyleCode = StringUtils.replaceBlank(thinkTyleCode);
        }
        param.put("thinkTyleCode", thinkTyleCode);

        if (StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
            bigStyleAnalyseCode = StringUtils.replaceBlank(bigStyleAnalyseCode);
        }
        param.put("bigStyleAnalyseCode", bigStyleAnalyseCode);

        param.put("craftCategoryCode", craftCategoryCode);
        param.put("isPic", isPic);
        param.put("isVideo", isVideo);
        param.put("craftSourceType", craftSourceType);
        param.put("status", status);
        List<CraftAnalyseVo> craftAnalyseVos = getData(customOrderNo, productionTicketNo, partCraftCode, bigStyleAnalyseCode, thinkTyleCode, param);

        //通过工具类创建writer
        ExcelWriter writer = ExcelUtil.getWriter();
        ServletOutputStream out = null;
        try {
            //别名

            writer.addHeaderAlias("rowNo", "行号");
            writer.addHeaderAlias("isNew", "新增");
            writer.addHeaderAlias("craftCode", "工序编码");
            writer.addHeaderAlias("craftDescript", "工序描述");
            writer.addHeaderAlias("craftPartName", "结构部件");
            writer.addHeaderAlias("machineName", "机器设备");
            writer.addHeaderAlias("isPic", "图片");
            writer.addHeaderAlias("isVideo", "视频");
            writer.addHeaderAlias("standardTime", "时间");
            writer.addHeaderAlias("standardPrice", "单价");
            writer.addHeaderAlias("craftSource", "工序来源");
            writer.addHeaderAlias("outCode", "部件工艺/智库款工艺/款式工艺路线编码");

            writer.addHeaderAlias("outName", "部件工艺/智库款工艺/款式工艺路线名称");
            writer.addHeaderAlias("outDescript", "部件工艺/智库款工艺/款式工艺路线描述");
            writer.addHeaderAlias("customOrderNo", "定制订单号");
            writer.addHeaderAlias("productionTicketNo", "工单号");
            writer.addHeaderAlias("statusName", "状态");
            writer.addHeaderAlias("craftCategoryName", "工艺品类");
            writer.addHeaderAlias("craftPartName", "结构部件");
            writer.addHeaderAlias("updateUser", "更新人");
            writer.addHeaderAlias("updateTime", "更新时间");
            writer.addHeaderAlias("picUrls", "图片URL");
            writer.addHeaderAlias("videoUrls", "视频URL");
            writer.addHeaderAlias("pictures", "图片视频URL");
            writer.write(craftAnalyseVos, true);
            writer.setColumnWidth(20, 20);

            String fileName = URLEncoder.encode("工序报表.xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            writer.flush(out);
        } catch (Exception e) {
            LOGGER.error("CraftAnalyseController export fails", e);
        } finally {
            writer.close();
            if (null != out) {
                IoUtil.close(out);
            }
        }
        return null;
    }

}
