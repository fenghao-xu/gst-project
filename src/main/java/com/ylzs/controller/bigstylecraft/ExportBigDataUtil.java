package com.ylzs.controller.bigstylecraft;

import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.StringUtils;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.service.craftstd.IDictionaryService;
import com.ylzs.vo.bigstylereport.CraftVO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xuwei
 * @create 2020-06-16 9:49
 * 导出款式工艺和工单工艺的数据
 */
public class ExportBigDataUtil {


    public static void exportData(Integer type, String productionCategoryName, String styleDesc, String bigStyleAnalyseCode, String productionTicketNo,
                                  String subBrand, HttpServletResponse response, List<CraftVO> craftVOS,
                                  IDictionaryService dictionaryService, List<JSONObject> pictures,
                                  String createUserName, Date createTime, String releaseUserName, Date releasetTime, String weftElasticGrade, String warpElasticGrade) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        XSSFWorkbook wb = null;
        ExcelWriter writer = null;
        ServletOutputStream fos = null;
        File dir = null;
        File resultfile = null;
        try {
            String basePath = File.separator + "opt" + File.separator + "template" + File.separator;
            //String basePath = "D:" + File.separator;
            File file = new File(basePath + "template.xlsx");
            String path = basePath + System.currentTimeMillis();
            dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String typeName = "";
            if (10 == type) {
                typeName = "有价格路径";
            } else if (20 == type) {
                typeName = "无价格路径";
            } else if (30 == type) {
                typeName = "EPS有工价报表导出";
            } else if (40 == type) {
                typeName = "EPS无工价报表导出";
            }

            String fileName = "";
            String excelName = "";
            if (StringUtils.isNotEmpty(productionTicketNo)) {
                fileName = path + File.separator + productionTicketNo + typeName + ".xlsx";
                excelName = productionTicketNo + typeName + ".xlsx";
            } else {
                fileName = path + File.separator + bigStyleAnalyseCode + typeName + ".xlsx";
                excelName = bigStyleAnalyseCode + typeName + ".xlsx";
            }
            resultfile = new File(fileName);
            try {
                FileUtils.copyFile(file, resultfile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (resultfile.exists()) {
                wb = new XSSFWorkbook(new FileInputStream(resultfile));
                writer = new ExcelWriter(wb, "款式工艺路线报表");
                Font font = writer.createFont();
                font.setFontHeightInPoints((short) 9);//字体大小
                font.setFontName("宋体");

                Font boldFont = writer.createFont();
                boldFont.setFontHeightInPoints((short) 9);//字体大小
                boldFont.setFontName("宋体");
                boldFont.setBold(true);
                // writer.merge(0,0,0,4,"龙华工厂",false);
                writer.getOrCreateCell(0, 0).setCellValue("龙华工厂");
                writer.getOrCreateCell(9, 0).setCellValue(sdf.format(new Date()));
                if (StringUtils.isNotBlank(productionCategoryName)) {
                    writer.getOrCreateCell(5, 2).setCellValue(productionCategoryName);
                }


                //款式编码
                Map<String, Dictionary> map = dictionaryService.getValueAndTypeCodeAsMapKey(Arrays.asList("SubBrand"));
                String subbrandName = map.containsKey(subBrand + "_SubBrand") ? map.get(subBrand + "_SubBrand").getValueDesc() : "";
                writer.getOrCreateCell(3, 2).setCellValue(bigStyleAnalyseCode);
                writer.getOrCreateCell(10, 2).setCellValue(subbrandName);
                writer.getOrCreateCell(1, 3).setCellValue(styleDesc);
                writer.getOrCreateCell(6, 3).setCellValue(weftElasticGrade);
                writer.getOrCreateCell(10, 3).setCellValue(warpElasticGrade);
                //处理图片
                Sheet sheet = writer.getSheet();
                //开始处理工种
                Map<String, BigDecimal> workTypeMap = new HashMap<>();
                Map<String, BigDecimal> priceMap = new HashMap<>();
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal makeTypePriceTotal = BigDecimal.ZERO;
                if (null != craftVOS && craftVOS.size() > 0) {
                    for (CraftVO vo : craftVOS) {
                        String workTypeCode = vo.getWorkTypeCode();
                        String workTypeName = vo.getWorkTypeName();
                        String key = workTypeCode + "_" + workTypeName;
                        BigDecimal newTime = vo.getStandardTime();
                        BigDecimal newPrice = vo.getStandardPrice();
                        if (null == newTime) {
                            newTime = BigDecimal.ZERO;
                        }
                        if (null == newPrice) {
                            newPrice = BigDecimal.ZERO;
                        }
                        total = total.add(newTime);
                        makeTypePriceTotal = makeTypePriceTotal.add(newPrice);

                        if (!workTypeMap.containsKey(key)) {
                            workTypeMap.put(key, newTime);
                            priceMap.put(key, newPrice);
                        } else {
                            BigDecimal oldTime = workTypeMap.get(key);
                            if (null == oldTime) {
                                oldTime = BigDecimal.ZERO;
                            }
                            BigDecimal add = oldTime.add(newTime).setScale(3, BigDecimal.ROUND_HALF_UP);
                            workTypeMap.put(key, add);


                            BigDecimal oldPrice = priceMap.get(key);
                            if (null == oldPrice) {
                                oldPrice = BigDecimal.ZERO;
                            }
                            BigDecimal addPrice = oldPrice.add(newPrice).setScale(3, BigDecimal.ROUND_HALF_UP);
                            priceMap.put(key, addPrice);
                        }
                    }
                }
                int rowNumber = 5;
                CellStyle style = wb.createCellStyle();
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);
                style.setBorderBottom(BorderStyle.THIN); // 下边框
                style.setBorderTop(BorderStyle.THIN);// 上边框
                CellStyle boldstyle = wb.createCellStyle();
                boldstyle.setFont(boldFont);
                boldstyle.setAlignment(HorizontalAlignment.CENTER);
                boldstyle.setBorderBottom(BorderStyle.THIN); // 下边框
                boldstyle.setBorderTop(BorderStyle.THIN);// 上边框

                CellStyle leftBoldstyle = wb.createCellStyle();
                leftBoldstyle.setFont(boldFont);
                leftBoldstyle.setAlignment(HorizontalAlignment.LEFT);
                leftBoldstyle.setBorderBottom(BorderStyle.THIN); // 下边框
                leftBoldstyle.setBorderTop(BorderStyle.THIN);// 上边框

                CellStyle styleLeft = wb.createCellStyle();
                styleLeft.setFont(font);
                styleLeft.setAlignment(HorizontalAlignment.LEFT);
                styleLeft.setBorderBottom(BorderStyle.THIN); // 下边框
                styleLeft.setBorderTop(BorderStyle.THIN);// 上边框
                if (workTypeMap.size() > 0) {
                    Iterator<String> it = workTypeMap.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        BigDecimal time = workTypeMap.get(key);
                        BigDecimal price = priceMap.get(key);
                        String str[] = key.split("_");
                        //工种编码
                        CellStyle style1 = wb.createCellStyle();
                        style1.setFont(font);
                        style1.setAlignment(HorizontalAlignment.RIGHT);
                        style1.setBorderBottom(BorderStyle.THIN); // 下边框
                        style1.setBorderTop(BorderStyle.THIN);// 上边框

                        writer.getOrCreateCell(0, rowNumber).setCellValue(str[0]);
                        writer.setStyle(style, 0, rowNumber);
                        //工种名称
                        writer.merge(rowNumber, rowNumber, 1, 2, str[1], false);
                        writer.setStyle(style, 1, rowNumber);
                        writer.setStyle(style, 2, rowNumber);
                        //时间
                        writer.getOrCreateCell(3, rowNumber).setCellValue(time.doubleValue());
                        writer.setStyle(styleLeft, 3, rowNumber);
                        if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.EPS_HAVE_PRICE.equals(type)) {
                            writer.getOrCreateCell(4, rowNumber).setCellValue(price.doubleValue());
                            writer.setStyle(styleLeft, 4, rowNumber);
                        } else {
                            writer.getOrCreateCell(4, rowNumber).setCellValue("");
                            writer.setStyle(styleLeft, 4, rowNumber);
                        }
                        rowNumber++;
                    }
                    writer.getOrCreateCell(0, rowNumber).setCellValue("合计");
                    writer.setStyle(boldstyle, 0, rowNumber);
                    //writer.merge(rowNumber, rowNumber, 1, 4, total.setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString(), false);
                    //时间
                    writer.getOrCreateCell(3, rowNumber).setCellValue(total.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                    writer.setStyle(leftBoldstyle, 3, rowNumber);
                    //单价
                    if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.EPS_HAVE_PRICE.equals(type)) {
                        writer.getOrCreateCell(4, rowNumber).setCellValue(makeTypePriceTotal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                        writer.setStyle(leftBoldstyle, 4, rowNumber);
                    } else {
                        writer.getOrCreateCell(4, rowNumber).setCellValue("");
                        writer.setStyle(leftBoldstyle, 4, rowNumber);
                    }
                    writer.setStyle(boldstyle, 1, rowNumber);
                    writer.setStyle(boldstyle, 2, rowNumber);
//                    CellStyle style1 = wb.createCellStyle();
//                    style1.setFont(boldFont);
//                    style1.setAlignment(HorizontalAlignment.RIGHT);
//                    style1.setBorderBottom(BorderStyle.THIN); // 下边框
//                    style1.setBorderTop(BorderStyle.THIN);// 上边框
//                    writer.setStyle(style1, 4, rowNumber);
//                    writer.setStyle(style1, 1, rowNumber);
//                    writer.setStyle(style1, 2, rowNumber);
//                    writer.setStyle(style1, 3, rowNumber);
                }

                //处理图片
                int pictureHeight = 7;
                if (null != pictures && pictures.size() > 0) {
                    insertPictureToExcel(pictures.get(0).getString("picUrl"), path, sheet, wb, 5, 4, 10, 4 + pictureHeight);
                }
                CellStyle style2 = wb.createCellStyle();
                style2.setFont(font);
                style2.setAlignment(HorizontalAlignment.CENTER);
                style2.setBorderBottom(BorderStyle.THIN); // 下边框
                style2.setBorderLeft(BorderStyle.THIN);
                style2.setBorderRight(BorderStyle.THIN);
                style2.setBorderTop(BorderStyle.THIN);// 上边框
                style2.setWrapText(true);

                CellStyle nowarpStyle = wb.createCellStyle();
                nowarpStyle.setFont(font);
                nowarpStyle.setAlignment(HorizontalAlignment.CENTER);
                nowarpStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                nowarpStyle.setBorderLeft(BorderStyle.THIN);
                nowarpStyle.setBorderRight(BorderStyle.THIN);
                nowarpStyle.setBorderTop(BorderStyle.THIN);// 上边框
                nowarpStyle.setHidden(true);

                CellStyle style3 = wb.createCellStyle();
                style3.setFont(boldFont);
                style3.setAlignment(HorizontalAlignment.CENTER);
                style3.setBorderBottom(BorderStyle.THIN); // 下边框
                style3.setBorderLeft(BorderStyle.THIN);
                style3.setBorderRight(BorderStyle.THIN);
                style3.setBorderTop(BorderStyle.THIN);// 上边框
                style3.setWrapText(true);
                rowNumber = workTypeMap.size() >= pictureHeight ? rowNumber + 1 : 5 + pictureHeight;

                if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.NOT_HAVE_PRICE.equals(type)) {
                    writer.getOrCreateCell(0, rowNumber).setCellValue("结构部件");
                    writer.setStyle(nowarpStyle, 0, rowNumber);
                    writer.getOrCreateCell(1, rowNumber).setCellValue("工序流");
                    writer.setStyle(style3, 1, rowNumber);
                } else {
                    writer.getOrCreateCell(0, rowNumber).setCellValue("行号");
                    writer.setStyle(style3, 0, rowNumber);
                    writer.getOrCreateCell(1, rowNumber).setCellValue("结构部件");
                    writer.setStyle(nowarpStyle, 1, rowNumber);
                }

                writer.setStyle(style3, 1, rowNumber);
                writer.getOrCreateCell(2, rowNumber).setCellValue("工票号");
                writer.setStyle(style3, 2, rowNumber);


                writer.getOrCreateCell(3, rowNumber).setCellValue("工序编码");
                writer.setStyle(style3, 3, rowNumber);
                writer.getOrCreateCell(4, rowNumber).setCellValue("工序描述");
                writer.setStyle(style3, 4, rowNumber);

                /*try {
                    writer.merge(rowNumber, rowNumber, 4, 5, "工序描述", false);
                } catch (Exception e) {
                    writer.getOrCreateCell(4, rowNumber).setCellValue("工序描述");
                }
                writer.setStyle(style3, 4, rowNumber);
                writer.setStyle(style3, 5, rowNumber);*/


                writer.merge(rowNumber, rowNumber, 5, 7, "机器名称", false);
                writer.setStyle(style3, 5, rowNumber);
                writer.setStyle(style3, 6, rowNumber);
                writer.setStyle(style3, 7, rowNumber);
                writer.getOrCreateCell(8, rowNumber).setCellValue("订单等级");
                writer.setStyle(style3, 8, rowNumber);
                writer.getOrCreateCell(9, rowNumber).setCellValue("时间(分)");
                writer.setStyle(style3, 9, rowNumber);
                if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.EPS_HAVE_PRICE.equals(type)) {
                    writer.getOrCreateCell(10, rowNumber).setCellValue("单价");
                    writer.setStyle(style3, 10, rowNumber);
                    writer.getOrCreateCell(11, rowNumber).setCellValue("工序等级");
                    writer.setStyle(style3, 11, rowNumber);

                    if (type == 10 || type == 20) {
                        writer.getOrCreateCell(12, rowNumber).setCellValue("生产部件编码");
                        writer.setStyle(style3, 12, rowNumber);

                        writer.getOrCreateCell(13, rowNumber).setCellValue("后续工序编码");
                        writer.setStyle(style3, 13, rowNumber);
                        writer.getOrCreateCell(14, rowNumber).setCellValue("后续工序出现次数");
                        writer.setStyle(style3, 14, rowNumber);
                    }
                } else {
                    writer.getOrCreateCell(10, rowNumber).setCellValue("工序等级");
                    writer.setStyle(style3, 10, rowNumber);

                    if (type == 10 || type == 20) {
                        writer.getOrCreateCell(11, rowNumber).setCellValue("生产部件编码");
                        writer.setStyle(style3, 11, rowNumber);
                        writer.getOrCreateCell(12, rowNumber).setCellValue("后续工序编码");
                        writer.setStyle(style3, 12, rowNumber);
                        writer.getOrCreateCell(13, rowNumber).setCellValue("后续工序出现次数");
                        writer.setStyle(style3, 13, rowNumber);
                    }
                }
                BigDecimal timeTotal = BigDecimal.ZERO;
                BigDecimal priceTotal = BigDecimal.ZERO;
                rowNumber++;
                CellStyle leftStyle = wb.createCellStyle();
                leftStyle.setFont(font);
                leftStyle.setAlignment(HorizontalAlignment.LEFT);
                leftStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                leftStyle.setBorderLeft(BorderStyle.THIN);
                leftStyle.setBorderRight(BorderStyle.THIN);
                leftStyle.setBorderTop(BorderStyle.THIN);// 上边框

                CellStyle leftWrapStyle = wb.createCellStyle();
                leftWrapStyle.setFont(font);
                leftWrapStyle.setAlignment(HorizontalAlignment.LEFT);
                leftWrapStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                leftWrapStyle.setBorderLeft(BorderStyle.THIN);
                leftWrapStyle.setBorderRight(BorderStyle.THIN);
                leftWrapStyle.setBorderTop(BorderStyle.THIN);// 上边框
                leftWrapStyle.setWrapText(true);
                leftWrapStyle.setHidden(false);

                int lineNumber = 1;
                if (null != craftVOS && craftVOS.size() > 0) {
                    for (CraftVO vo : craftVOS) {
                        if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.NOT_HAVE_PRICE.equals(type)) {
                            writer.getOrCreateCell(0, rowNumber).setCellValue(vo.getCraftPartName());
                            writer.setStyle(nowarpStyle, 0, rowNumber);
                            if (vo.getCraftFlowNum() != null) {
                                writer.getOrCreateCell(1, rowNumber).setCellValue(vo.getCraftFlowNum().toString());
                            } else {
                                writer.getOrCreateCell(1, rowNumber).setCellValue("");
                            }
                            writer.setStyle(style2, 1, rowNumber);
                        } else {
                            writer.getOrCreateCell(0, rowNumber).setCellValue(lineNumber);
                            writer.setStyle(style2, 0, rowNumber);
                            lineNumber++;
                            writer.getOrCreateCell(1, rowNumber).setCellValue(vo.getCraftPartName());
                            writer.setStyle(nowarpStyle, 1, rowNumber);
                        }

                        if (StringUtils.isNotEmpty(vo.getWorkOrderNo())) {
                            writer.getOrCreateCell(2, rowNumber).setCellValue(Integer.parseInt(vo.getWorkOrderNo()));
                        }
                        writer.setStyle(style2, 2, rowNumber);


                        writer.getOrCreateCell(3, rowNumber).setCellValue(vo.getCraftCode());
                        writer.setStyle(style2, 3, rowNumber);

                        writer.getOrCreateCell(4, rowNumber).setCellValue(vo.getCraftRemark());
                        writer.setStyle(leftWrapStyle, 4, rowNumber);
                        //writer.merge(rowNumber, rowNumber, 4, 5, vo.getCraftRemark(), false);
                        //writer.setStyle(leftWrapStyle, 4, rowNumber);
                        //writer.setStyle(leftWrapStyle, 5, rowNumber);
                        writer.merge(rowNumber, rowNumber, 5, 7, vo.getMachineName(), false);
                        writer.setStyle(leftStyle, 5, rowNumber);
                        writer.setStyle(leftStyle, 6, rowNumber);
                        writer.setStyle(leftStyle, 7, rowNumber);
                        if (vo.getOrderGrade() != null) {
                            writer.getOrCreateCell(8, rowNumber).setCellValue(vo.getOrderGrade());
                        } else {
                            writer.getOrCreateCell(8, rowNumber).setCellValue("");
                        }
                        writer.setStyle(style2, 8, rowNumber);

                        if (vo.getStandardTime() != null) {
                            writer.getOrCreateCell(9, rowNumber).setCellValue(vo.getStandardTime().doubleValue());
                            timeTotal = timeTotal.add(vo.getStandardTime());
                        } else {
                            writer.getOrCreateCell(9, rowNumber).setCellValue("0");
                        }

                        writer.setStyle(style2, 9, rowNumber);
                        if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.EPS_HAVE_PRICE.equals(type)) {
                            if (vo.getStandardPrice() != null) {
                                writer.getOrCreateCell(10, rowNumber).setCellValue(vo.getStandardPrice().doubleValue());
                                priceTotal = priceTotal.add(vo.getStandardPrice());
                            } else {
                                writer.getOrCreateCell(10, rowNumber).setCellValue("0");
                            }
                            writer.setStyle(style2, 10, rowNumber);
                            writer.getOrCreateCell(11, rowNumber).setCellValue(vo.getCraftGradeCode());
                            writer.setStyle(style2, 11, rowNumber);


                            if (type == 10 || type == 20) {
                                writer.getOrCreateCell(12, rowNumber).setCellValue(vo.getProductionPartCode() != null ? vo.getProductionPartCode() : "");
                                writer.setStyle(style2, 12, rowNumber);


                                writer.getOrCreateCell(13, rowNumber).setCellValue(vo.getNextCraftCode() != null ? vo.getNextCraftCode() : "");
                                writer.setStyle(style2, 13, rowNumber);

                                if (vo.getNextCraftCodeCount() != null) {
                                    writer.getOrCreateCell(14, rowNumber).setCellValue(vo.getNextCraftCodeCount());
                                } else {
                                    writer.getOrCreateCell(14, rowNumber).setCellValue("");
                                }
                                writer.setStyle(style2, 14, rowNumber);
                            }

                        } else {
                            writer.getOrCreateCell(10, rowNumber).setCellValue(vo.getCraftGradeCode());
                            writer.setStyle(style2, 10, rowNumber);

                            if (type == 10 || type == 20) {
                                writer.getOrCreateCell(11, rowNumber).setCellValue(vo.getProductionPartCode() != null ? vo.getProductionPartCode() : "");
                                writer.setStyle(style2, 11, rowNumber);

                                writer.getOrCreateCell(12, rowNumber).setCellValue(vo.getNextCraftCode() != null ? vo.getNextCraftCode() : "");
                                writer.setStyle(style2, 12, rowNumber);

                                if (vo.getNextCraftCodeCount() != null) {
                                    writer.getOrCreateCell(13, rowNumber).setCellValue(vo.getNextCraftCodeCount());
                                } else {
                                    writer.getOrCreateCell(13, rowNumber).setCellValue("");
                                }
                                writer.setStyle(style2, 13, rowNumber);
                            }
                        }
                        rowNumber++;
                    }
                }
                CellStyle rightStyle = wb.createCellStyle();
                rightStyle.setFont(font);
                rightStyle.setAlignment(HorizontalAlignment.RIGHT);
                rightStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                rightStyle.setBorderLeft(BorderStyle.THIN);
                rightStyle.setBorderRight(BorderStyle.THIN);
                rightStyle.setBorderTop(BorderStyle.THIN);// 上边框
                writer.merge(rowNumber, rowNumber, 0, 8, "合计", false);
                for (int i = 0; i <= 8; i++) {
                    writer.setStyle(rightStyle, i, rowNumber);
                }
                writer.getOrCreateCell(9, rowNumber).setCellValue(timeTotal.doubleValue());
                writer.setStyle(style2, 9, rowNumber);
                if (BusinessConstants.ExportTemplate.HAVE_PRICE.equals(type) || BusinessConstants.ExportTemplate.EPS_HAVE_PRICE.equals(type)) {
                    writer.merge(rowNumber, rowNumber, 10, 11, priceTotal.doubleValue(), false);
                    writer.setStyle(leftStyle, 10, rowNumber);
                    writer.setStyle(leftStyle, 11, rowNumber);
                }
                rowNumber++;
                CellStyle bottomStyle = wb.createCellStyle();
                bottomStyle.setFont(font);
                bottomStyle.setAlignment(HorizontalAlignment.CENTER);
                bottomStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                bottomStyle.setWrapText(true);
                writer.getOrCreateCell(0, rowNumber).setCellValue("创建人");
                writer.setStyle(bottomStyle, 0, rowNumber);
                writer.getOrCreateCell(1, rowNumber).setCellValue(createUserName);
                writer.setStyle(bottomStyle, 1, rowNumber);
                writer.getOrCreateCell(2, rowNumber).setCellValue("创建日期");
                writer.setStyle(bottomStyle, 2, rowNumber);
                writer.getOrCreateCell(3, rowNumber).setCellValue("");
                writer.setStyle(bottomStyle, 3, rowNumber);
                String createTimeStr = createTime != null ? sdf.format(createTime) : "";
                writer.getOrCreateCell(4, rowNumber).setCellValue(createTimeStr);
                writer.setStyle(bottomStyle, 4, rowNumber);
                writer.getOrCreateCell(5, rowNumber).setCellValue("发布人");
                writer.setStyle(bottomStyle, 5, rowNumber);
                writer.getOrCreateCell(6, rowNumber).setCellValue(releaseUserName);
                writer.setStyle(bottomStyle, 6, rowNumber);
                writer.merge(rowNumber, rowNumber, 7, 8, "发布日期", false);
                writer.setStyle(bottomStyle, 7, rowNumber);
                writer.setStyle(bottomStyle, 8, rowNumber);
                String releaseTimeStr = releasetTime != null ? sdf.format(releasetTime) : "";
                writer.merge(rowNumber, rowNumber, 9, 11, releaseTimeStr, false);
                writer.setStyle(bottomStyle, 9, rowNumber);
                writer.setStyle(bottomStyle, 10, rowNumber);
                writer.setStyle(bottomStyle, 11, rowNumber);
                excelName = URLEncoder.encode(excelName, "UTF-8");
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + excelName);
                fos = response.getOutputStream();
                writer.flush(fos);
            }

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
            if (null != resultfile) {
                resultfile.delete();
            }

            if (null != dir) {
                dir.delete();
            }
        }
    }

    private static void insertPictureToExcel(String picUrl, String path, Sheet sheet, Workbook wb, int a, int b,
                                             int c, int d) {
        if (StringUtils.isEmpty(picUrl)) {
            return;
        }
        String imageType = ".png";
        if (picUrl != null && !picUrl.isEmpty() && picUrl.lastIndexOf(".") != -1) {// 取图片的后缀
            imageType = picUrl.substring(picUrl.lastIndexOf("."));
        }

        try {
            URL url = new URL(picUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setConnectTimeout(1000 * 5);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            // 文件大小
            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
            IOUtils.copy(inputStream, fileOut);
            Drawing patriarch = sheet.createDrawingPatriarch();
            /**
             * 该构造函数有8个参数 前四个参数是控制图片在单元格的位置，分别是图片距离单元格left，top，right，bottom的像素距离
             * 后四个参数，前连个表示图片左上角所在的cellNum和 rowNum，后天个参数对应的表示图片右下角所在的cellNum和 rowNum，
             * excel中的cellNum和rowNum的index都是从0开始的
             *int a, int b, int col2, int row2
             */
            // 图片一导出到单元格B2中
            XSSFClientAnchor anchor = new XSSFClientAnchor(Units.EMU_PER_PIXEL,
                    Units.EMU_PER_PIXEL, Units.EMU_PER_PIXEL * (-1), Units.EMU_PER_PIXEL * (-1),
                    a, b, c, d);
            // anchor.setAnchorType(XSSFClientAnchor.MOVE_AND_RESIZE);
            patriarch.createPicture(anchor, wb.addPicture(fileOut.toByteArray(), XSSFWorkbook.PICTURE_TYPE_PNG));
            if (null != inputStream) {
                inputStream.close();
                inputStream = null;
            }
            if (null != fileOut) {
                fileOut.close();
                fileOut = null;
            }
            httpURLConnection.disconnect();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void exportFinanceData(Integer type, String productionCategoryName, String styleDesc, String bigStyleAnalyseCode, String productionTicketNo,
                                         String subBrand, HttpServletResponse response, List<CraftVO> craftVOS,
                                         IDictionaryService dictionaryService, List<JSONObject> pictures,
                                         String createUserName, Date createTime, String releaseUserName, Date releasetTime, String weftElasticGrade, String warpElasticGrade) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        XSSFWorkbook wb = null;
        ExcelWriter writer = null;
        ServletOutputStream fos = null;
        File dir = null;
        File resultfile = null;
        try {
            String basePath = File.separator + "opt" + File.separator + "template" + File.separator;
            //String basePath = "D:" + File.separator;
            File file = new File(basePath + "financeTemplate.xlsx");
            String path = basePath + System.currentTimeMillis();
            dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String typeName = "";
            if (50 == type) {
                typeName = "财务报表";
            }
            String fileName = "";
            String excelName = "";
            if (StringUtils.isNotEmpty(productionTicketNo)) {
                fileName = path + File.separator + productionTicketNo + typeName + ".xlsx";
                excelName = productionTicketNo + typeName + ".xlsx";
            } else {
                fileName = path + File.separator + bigStyleAnalyseCode + typeName + ".xlsx";
                excelName = bigStyleAnalyseCode + typeName + ".xlsx";
            }
            resultfile = new File(fileName);
            try {
                FileUtils.copyFile(file, resultfile);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (resultfile.exists()) {
                wb = new XSSFWorkbook(new FileInputStream(resultfile));
                writer = new ExcelWriter(wb, "财务报表导出");
                Font font = writer.createFont();
                font.setFontHeightInPoints((short) 9);//字体大小
                font.setFontName("宋体");
                CellStyle nowarpStyle = wb.createCellStyle();
                nowarpStyle.setFont(font);
                nowarpStyle.setAlignment(HorizontalAlignment.CENTER);
                nowarpStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                nowarpStyle.setBorderLeft(BorderStyle.THIN);
                nowarpStyle.setBorderRight(BorderStyle.THIN);
                nowarpStyle.setBorderTop(BorderStyle.THIN);// 上边框
                nowarpStyle.setHidden(true);

                CellStyle leftStyle = wb.createCellStyle();
                leftStyle.setFont(font);
                leftStyle.setAlignment(HorizontalAlignment.LEFT);
                leftStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                leftStyle.setBorderLeft(BorderStyle.THIN);
                leftStyle.setBorderRight(BorderStyle.THIN);
                leftStyle.setBorderTop(BorderStyle.THIN);// 上边框

                CellStyle leftWrapStyle = wb.createCellStyle();
                leftWrapStyle.setFont(font);
                leftWrapStyle.setAlignment(HorizontalAlignment.LEFT);
                leftWrapStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                leftWrapStyle.setBorderLeft(BorderStyle.THIN);
                leftWrapStyle.setBorderRight(BorderStyle.THIN);
                leftWrapStyle.setBorderTop(BorderStyle.THIN);// 上边框
                leftWrapStyle.setWrapText(true);
                leftWrapStyle.setHidden(false);

                CellStyle centerStyle = wb.createCellStyle();
                centerStyle.setFont(font);
                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                centerStyle.setBorderBottom(BorderStyle.THIN); // 下边框
                centerStyle.setBorderLeft(BorderStyle.THIN);
                centerStyle.setBorderRight(BorderStyle.THIN);
                centerStyle.setBorderTop(BorderStyle.THIN);// 上边框
                int rowNumber = 1;
                //行号
                int lineNumber = 1;
                BigDecimal timeTotal = BigDecimal.ZERO;
                BigDecimal priceTotal = BigDecimal.ZERO;
                if (null != craftVOS && craftVOS.size() > 0) {
                    for (CraftVO vo : craftVOS) {
                        //行号
                        writer.getOrCreateCell(0, rowNumber).setCellValue(lineNumber++);
                        writer.setStyle(centerStyle, 0, rowNumber);
                        //结构部件
                        writer.getOrCreateCell(1, rowNumber).setCellValue(vo.getCraftPartName());
                        writer.setStyle(nowarpStyle, 1, rowNumber);
                        //工票号
                        if (StringUtils.isNotEmpty(vo.getWorkOrderNo())) {
                            writer.getOrCreateCell(2, rowNumber).setCellValue(Integer.parseInt(vo.getWorkOrderNo()));
                        }
                        writer.setStyle(centerStyle, 2, rowNumber);
                        //工序编码
                        writer.getOrCreateCell(3, rowNumber).setCellValue(vo.getCraftCode());
                        writer.setStyle(centerStyle, 3, rowNumber);
                        //工序描述
                        writer.getOrCreateCell(4, rowNumber).setCellValue(vo.getCraftRemark());
                        writer.setStyle(leftWrapStyle, 4, rowNumber);
                        //机器名称
                        writer.getOrCreateCell(5, rowNumber).setCellValue(vo.getMachineName());
                        writer.setStyle(leftStyle, 5, rowNumber);
                        //订单等级
                        writer.getOrCreateCell(6, rowNumber).setCellValue(vo.getOrderGrade());
                        writer.setStyle(centerStyle, 6, rowNumber);

                        //工序等级
                        writer.getOrCreateCell(7, rowNumber).setCellValue(vo.getCraftGradeCode());
                        writer.setStyle(centerStyle, 7, rowNumber);

                        //时间
                        if (null != vo.getStandardTime()) {
                            writer.getOrCreateCell(8, rowNumber).setCellValue(vo.getStandardTime().doubleValue());
                            timeTotal = timeTotal.add(vo.getStandardTime());
                        }
                        writer.setStyle(centerStyle, 8, rowNumber);

                        //单价
                        if (null != vo.getStandardPrice()) {
                            priceTotal = priceTotal.add(vo.getStandardPrice());
                            writer.getOrCreateCell(9, rowNumber).setCellValue(vo.getStandardPrice().doubleValue());
                        }
                        writer.setStyle(leftStyle, 9, rowNumber);
                        rowNumber++;
                    }
                }
                excelName = URLEncoder.encode(excelName, "UTF-8");
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + excelName);
                fos = response.getOutputStream();
                writer.flush(fos);
            }

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
            if (null != resultfile) {
                resultfile.delete();
            }

            if (null != dir) {
                dir.delete();
            }
        }
    }


}
