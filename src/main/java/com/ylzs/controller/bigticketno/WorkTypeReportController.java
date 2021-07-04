package com.ylzs.controller.bigticketno;

import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.MessageConstant;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.craftstd.WorkType;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.service.bigticketno.BigOrderSewingCraftWarehouseService;
import com.ylzs.service.custom.ICustomStylePartCraftService;
import com.ylzs.service.factory.IProductionGroupService;
import com.ylzs.service.staticdata.WorkTypeService;
import com.ylzs.vo.bigstylereport.WorkTypeReport;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author xuwei
 * @create 2020-08-20 10:35
 * 工种报表相关
 */
@RestController
@RequestMapping("/report")
public class WorkTypeReportController {
    @Resource
    private BigOrderSewingCraftWarehouseService bigOrderSewingCraftWarehouseService;

    @Resource
    private IProductionGroupService productionGroupService;
    @Resource
    private WorkTypeService workTypeService;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private ICustomStylePartCraftService customStylePartCraftService;

    @ApiOperation(value = "/workTypeReportQuery", notes = "工种报表", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/workTypeReportQuery", method = RequestMethod.GET)
    public Result<List<JSONObject>> workTypeReportQuery(@RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                                                        @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                                                        @RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                                                        @RequestParam(name = "productionCategory", required = false) String productionCategory,
                                                        @RequestParam(name = "orderNo", required = false) String orderNo,
                                                        @RequestParam(name = "factoryCode", required = false) String factoryCode,
                                                        @RequestParam(name = "ctStyleCode", required = false) String ctStyleCode) throws Exception {


        List<JSONObject> list = new ArrayList<>();
        final Map<String, Map<String, WorkTypeReport>> result = new HashMap<>();
        findDataToMap(result, createTimeBeginDate, createTimeEndDate, productionTicketNo, productionCategory, factoryCode, ctStyleCode, orderNo);
        List<WorkType> workTypes = workTypeService.getSewingWorkType();
        Map<String, ProductionGroup> groupMap = productionGroupService.getAllToMap();
        Iterator<String> outIt = result.keySet().iterator();
        while (outIt.hasNext()) {
            String productionTicketNoStr = outIt.next();
            JSONObject obj = new JSONObject();
            obj.put("productionTicketNo", productionTicketNoStr);
            Map<String, WorkTypeReport> map = result.get(productionTicketNoStr);
            Iterator<String> innerIt = map.keySet().iterator();
            String workTypeCode = innerIt.next();
            WorkTypeReport report = map.get(workTypeCode);
            obj.put("ctStyleCode", report.getCtStyleCode());
            obj.put("factoryName", report.getFactoryName());
            obj.put("productionCategoryName", groupMap.containsKey(report.getProductionCategory()) ? groupMap.get(report.getProductionCategory()).getProductionGroupName() : "");
            JSONArray arry = new JSONArray();
            //行列，也就是同一个工单的汇总
            BigDecimal rowTime = BigDecimal.ZERO;
            BigDecimal rowPrice = BigDecimal.ZERO;
            for (WorkType workType : workTypes) {
                JSONObject timObj = new JSONObject();
                timObj.put("workTypeCode", workType.getWorkTypeCode());
                if (map.containsKey(workType.getWorkTypeCode())) {
                    timObj.put("standardTime", map.get(workType.getWorkTypeCode()).getStandardTime().doubleValue());
                    timObj.put("standardPrice", map.get(workType.getWorkTypeCode()).getStandardPrice().doubleValue());
                    rowTime = rowTime.add(map.get(workType.getWorkTypeCode()).getStandardTime());
                    rowPrice = rowPrice.add(map.get(workType.getWorkTypeCode()).getStandardPrice());
                } else {
                    timObj.put("standardTime", null);
                    timObj.put("standardPrice", null);
                }
                timObj.put("workTypeName", workType.getWorkTypeName());
                //obj.put(workType.getWorkTypeName(), timObj);
                arry.add(timObj);

            }
            JSONObject timObj = new JSONObject();
            timObj.put("workTypeName", "Grand Total");
            timObj.put("workTypeCode", "Grand Total");
            timObj.put("standardTime", rowTime.doubleValue());
            timObj.put("standardPrice", rowPrice.doubleValue());
            arry.add(timObj);
            obj.put("workTypeList", arry);
            list.add(obj);
        }
        return Result.ok(MessageConstant.SUCCESS, list);

    }

    @ApiOperation(value = "/exportData", notes = "导出工种报表", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/exportData", method = RequestMethod.GET)
    public Result exportData(@RequestParam(name = "createTimeBeginDate", required = false) String createTimeBeginDate,
                             @RequestParam(name = "createTimeEndDate", required = false) String createTimeEndDate,
                             @RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                             @RequestParam(name = "productionCategory", required = false) String productionCategory,
                             @RequestParam(name = "orderNo", required = false) String orderNo,
                             @RequestParam(name = "factoryCode", required = false) String factoryCode,
                             @RequestParam(name = "ctStyleCode", required = false) String ctStyleCode,
                             HttpServletResponse response) throws Exception {
        Map<String, ProductionGroup> groupMap = productionGroupService.getAllToMap();
        List<WorkType> workTypes = workTypeService.getSewingWorkType();
        Map<String, Map<String, WorkTypeReport>> result = new HashMap<>();
        findDataToMap(result, createTimeBeginDate, createTimeEndDate, productionTicketNo, productionCategory, factoryCode, ctStyleCode, orderNo);
        XSSFWorkbook wb = null;
        ServletOutputStream fos = null;
        ExcelWriter writer = null;
        try {
            wb = new XSSFWorkbook();
            writer = new ExcelWriter(wb, "工种报表");
            int cellIndex = 0;

            Font font = writer.createFont();
            font.setFontHeightInPoints((short) 9);//字体大小
            font.setFontName("宋体");

            CellStyle centerStyle = wb.createCellStyle();
            centerStyle.setFont(font);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setBorderBottom(BorderStyle.THIN); // 下边框
            centerStyle.setBorderLeft(BorderStyle.THIN);
            centerStyle.setBorderRight(BorderStyle.THIN);
            centerStyle.setBorderTop(BorderStyle.THIN);// 上边框

            CellStyle leftStyle = wb.createCellStyle();
            leftStyle.setFont(font);
            leftStyle.setAlignment(HorizontalAlignment.LEFT);
            leftStyle.setBorderBottom(BorderStyle.THIN); // 下边框
            leftStyle.setBorderLeft(BorderStyle.THIN);
            leftStyle.setBorderRight(BorderStyle.THIN);
            leftStyle.setBorderTop(BorderStyle.THIN);// 上边框
            int rowNumber = 0;
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("工单号");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("款式编号/定制订单号");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("工厂");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("生产组别");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            int i = 0;
            for (; i < workTypes.size(); i++) {
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(workTypes.get(i).getWorkTypeName());
                writer.setStyle(centerStyle, cellIndex++, rowNumber);
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
                writer.setStyle(centerStyle, cellIndex++, rowNumber);
            }
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("Grand Total");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            rowNumber++;
            for (i = 0; i < 4; i++) {
                writer.getOrCreateCell(i, rowNumber).setCellValue("");
                writer.setStyle(centerStyle, i, rowNumber);
            }
            cellIndex = 4;
            for (i = 0; i < workTypes.size(); i++) {
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("标准时间");
                writer.setStyle(centerStyle, cellIndex++, rowNumber);
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("标准单价");
                writer.setStyle(centerStyle, cellIndex++, rowNumber);
            }
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("标准时间");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("标准单价");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            rowNumber++;
            Iterator<String> outIt = result.keySet().iterator();
            Map<String, BigDecimal> timeMap = new HashMap<>();
            Map<String, BigDecimal> priceMap = new HashMap<>();
            //最右边行列的汇总，也就是同一个工单的汇总
            BigDecimal totalRowTime = BigDecimal.ZERO;
            BigDecimal totalRowPrice = BigDecimal.ZERO;
            while (outIt.hasNext()) {
                cellIndex = 0;
                String productionTicketNoStr = outIt.next();
                Map<String, WorkTypeReport> map = result.get(productionTicketNoStr);
                Iterator<String> innerIt = map.keySet().iterator();
                String workTypeCode = innerIt.next();
                //工单号
                WorkTypeReport report = map.get(workTypeCode);
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(productionTicketNoStr);
                writer.setStyle(leftStyle, cellIndex++, rowNumber);
                //款号
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(report.getCtStyleCode());
                writer.setStyle(leftStyle, cellIndex++, rowNumber);
                //工厂
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(report.getFactoryName());
                writer.setStyle(leftStyle, cellIndex++, rowNumber);
                //生产组别
                if (groupMap.containsKey(report.getProductionCategory())) {
                    writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(groupMap.get(report.getProductionCategory()).getProductionGroupName());
                }
                writer.setStyle(leftStyle, cellIndex++, rowNumber);
                //纵列，也就是同一个工种的汇总
                BigDecimal totalTime = BigDecimal.ZERO;
                BigDecimal totalPrice = BigDecimal.ZERO;
                //行列，也就是同一个工单的汇总
                BigDecimal rowTime = BigDecimal.ZERO;
                BigDecimal rowPrice = BigDecimal.ZERO;

                for (i = 0; i < workTypes.size(); i++) {
                    if (map.containsKey(workTypes.get(i).getWorkTypeCode())) {
                        rowTime = rowTime.add(map.get(workTypes.get(i).getWorkTypeCode()).getStandardTime());
                        rowPrice = rowPrice.add(map.get(workTypes.get(i).getWorkTypeCode()).getStandardPrice());
                        writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(map.get(workTypes.get(i).getWorkTypeCode()).getStandardTime().doubleValue());
                        totalTime = totalTime.add(map.get(workTypes.get(i).getWorkTypeCode()).getStandardTime());
                        writer.setStyle(centerStyle, cellIndex++, rowNumber);
                        writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(map.get(workTypes.get(i).getWorkTypeCode()).getStandardPrice().doubleValue());
                        totalPrice = totalPrice.add(map.get(workTypes.get(i).getWorkTypeCode()).getStandardPrice());
                        writer.setStyle(centerStyle, cellIndex++, rowNumber);
                        if (timeMap.containsKey(workTypes.get(i).getWorkTypeCode())) {
                            BigDecimal timeOld = timeMap.get(workTypes.get(i).getWorkTypeCode());
                            if (timeOld == null) {
                                timeOld = BigDecimal.ZERO;
                            }
                            BigDecimal timeNew = map.get(workTypes.get(i).getWorkTypeCode()).getStandardTime();
                            if (null == timeNew) {
                                timeNew = BigDecimal.ZERO;
                            }
                            timeMap.put(workTypes.get(i).getWorkTypeCode(), timeNew.add(timeOld));
                        } else {
                            timeMap.put(workTypes.get(i).getWorkTypeCode(), map.get(workTypes.get(i).getWorkTypeCode()).getStandardTime());
                        }
                        if (priceMap.containsKey(workTypes.get(i).getWorkTypeCode())) {
                            BigDecimal priceOld = priceMap.get(workTypes.get(i).getWorkTypeCode());
                            if (priceOld == null) {
                                priceOld = BigDecimal.ZERO;
                            }
                            BigDecimal priceNew = map.get(workTypes.get(i).getWorkTypeCode()).getStandardPrice();
                            if (null == priceNew) {
                                priceNew = BigDecimal.ZERO;
                            }
                            priceMap.put(workTypes.get(i).getWorkTypeCode(), priceNew.add(priceOld));
                        } else {
                            priceMap.put(workTypes.get(i).getWorkTypeCode(), map.get(workTypes.get(i).getWorkTypeCode()).getStandardPrice());
                        }
                    } else {
                        writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
                        writer.setStyle(centerStyle, cellIndex++, rowNumber);
                        writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
                        writer.setStyle(centerStyle, cellIndex++, rowNumber);
                    }

                }
                totalRowTime = totalRowTime.add(rowTime);
                totalRowPrice = totalRowPrice.add(rowPrice);
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(rowTime.doubleValue());
                writer.setStyle(centerStyle, cellIndex++, rowNumber);
                writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(rowPrice.doubleValue());
                writer.setStyle(centerStyle, cellIndex++, rowNumber);
                rowNumber++;
            }
            cellIndex = 0;
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("Grand Total");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(result.size());
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            for (i = 0; i < workTypes.size(); i++) {
                if (timeMap.containsKey(workTypes.get(i).getWorkTypeCode())) {
                    writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(timeMap.get(workTypes.get(i).getWorkTypeCode()).doubleValue());
                    writer.setStyle(centerStyle, cellIndex++, rowNumber);
                    writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(priceMap.get(workTypes.get(i).getWorkTypeCode()).doubleValue());
                    writer.setStyle(centerStyle, cellIndex++, rowNumber);
                } else {
                    writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
                    writer.setStyle(centerStyle, cellIndex++, rowNumber);
                    writer.getOrCreateCell(cellIndex, rowNumber).setCellValue("");
                    writer.setStyle(centerStyle, cellIndex++, rowNumber);
                }

            }
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(totalRowTime.doubleValue());
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            writer.getOrCreateCell(cellIndex, rowNumber).setCellValue(totalRowPrice.doubleValue());
            writer.setStyle(centerStyle, cellIndex++, rowNumber);
            String fileName = URLEncoder.encode("工种报表.xlsx", "UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            fos = response.getOutputStream();
            wb.write(fos);
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (null != fos) {
                fos.close();
                fos = null;
            }
            if (null != wb) {
                wb.close();
                wb = null;
            }
        }
        return null;
    }

    private synchronized void addList(List<WorkTypeReport> workTypeReports, List<WorkTypeReport> temp) {
        if (null != temp) {
            workTypeReports.addAll(temp);
        }
    }

    private void findDataToMap(final Map<String, Map<String, WorkTypeReport>> result, String createTimeBeginDate, String createTimeEndDate,
                               String productionTicketNo, String productionCategory, String factoryCode, String ctStyleCode, final String orderNo) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotEmpty(productionTicketNo)) {
            param.put("productionTicketNo", productionTicketNo);
        }
        boolean productionCategoryFlag = true;
        if (StringUtils.isNotEmpty(productionCategory)) {
            List<ProductionGroup> groupList = productionGroupService.getByName(productionCategory);
            List<String> codes = new ArrayList<>();
            if (groupList != null && groupList.size() > 0) {
                for (ProductionGroup vo : groupList) {
                    codes.add(vo.getProductionGroupCode());
                }
                param.put("groupList", codes);
            } else {
                //生产组有过滤条件，但是没有找到，这个时候肯定不符合条件的数据，直接不执行后续的查询
                productionCategoryFlag = false;
            }
        }
        if (StringUtils.isNotEmpty(orderNo)) {
            param.put("orderNo", orderNo);
        }
        if (StringUtils.isNotEmpty(factoryCode)) {
            if ("于都".indexOf(factoryCode) != -1) {
                param.put("factoryCode", "8081");
            } else if ("龙华".indexOf(factoryCode) != -1) {
                param.put("factoryCode", "8082");
            } else {
                param.put("factoryCode", factoryCode);
            }

        }
        if (StringUtils.isNotEmpty(ctStyleCode)) {
            param.put("ctStyleCode", ctStyleCode);
        }
        if (StringUtils.isNotEmpty(createTimeBeginDate)) {
            param.put("createTimeBeginDate", createTimeBeginDate);
        }
        if (StringUtils.isNotEmpty(createTimeEndDate)) {
            param.put("createTimeEndDate", createTimeEndDate);
        }

        List<WorkTypeReport> workTypeReports = Collections.synchronizedList(new ArrayList());
        if (productionCategoryFlag) {
            CountDownLatch countDownLatch = new CountDownLatch(2);
            taskExecutor.execute(() -> {
                try {
                    //如果有订单号就不查大货的
                    if (StringUtils.isEmpty(orderNo)) {
                        List<WorkTypeReport> temp = bigOrderSewingCraftWarehouseService.selectWorkTypeReport(param);
                        addList(workTypeReports, temp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
            taskExecutor.execute(() -> {
                try {
                    List<WorkTypeReport> temp = customStylePartCraftService.selectWorkTypeReport(param);
                    addList(workTypeReports, temp);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != workTypeReports && workTypeReports.size() > 0) {
            for (WorkTypeReport vo : workTypeReports) {
                String productionTicketNoStr = vo.getProductionTicketNo();
                String workTypeCode = vo.getWorkTypeCode();
                BigDecimal price = vo.getStandardPrice();
                if (null == price) {
                    price = BigDecimal.ZERO;
                }
                BigDecimal time = vo.getStandardTime();
                if (null == time) {
                    time = BigDecimal.ZERO;
                }
                if (result.containsKey(productionTicketNoStr)) {
                    Map<String, WorkTypeReport> temp = result.get(productionTicketNoStr);
                    if (null == temp || temp.size() == 0) {
                        temp = new HashMap<String, WorkTypeReport>();
                    }
                    if (temp.containsKey(workTypeCode)) {
                        WorkTypeReport old = temp.get(workTypeCode);
                        BigDecimal oldPrice = old.getStandardPrice();
                        if (null == oldPrice) {
                            oldPrice = BigDecimal.ZERO;
                        }
                        BigDecimal newPrice = oldPrice.add(price);
                        old.setStandardPrice(newPrice);

                        BigDecimal oldTime = old.getStandardTime();
                        if (null == oldTime) {
                            oldTime = BigDecimal.ZERO;
                        }
                        BigDecimal newTime = oldTime.add(time);
                        old.setStandardTime(newTime);
                        temp.put(workTypeCode, old);
                    } else {
                        temp.put(workTypeCode, vo);
                    }
                } else {
                    Map<String, WorkTypeReport> temp = new HashMap<>();
                    temp.put(workTypeCode, vo);
                    result.put(productionTicketNoStr, temp);
                }
            }
        }
    }

}
