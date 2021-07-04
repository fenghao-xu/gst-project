package com.ylzs.controller.bigticketno;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.*;
import com.ylzs.dao.bigticketno.*;
import com.ylzs.dao.craftstd.MakeTypeDao;
import com.ylzs.dao.factory.ProductionGroupDao;
import com.ylzs.dao.fms.StyleProductionWorkInformationDao;
import com.ylzs.dao.sewingcraft.SewingCraftActionDao;
import com.ylzs.dao.sewingcraft.SewingCraftWarehouseDao;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import com.ylzs.entity.bigstylecraft.BigStyleOperationLog;
import com.ylzs.entity.bigticketno.FmsTicketNo;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.entity.fms.*;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.service.fms.IStyleProductionWorkInformationService;
import com.ylzs.service.timeparam.CraftGradeEquipmentService;
import com.ylzs.service.timeparam.MotionCodeConfigService;
import com.ylzs.vo.bigstylereport.CraftVO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.search.aggregations.metrics.percentiles.hdr.InternalHDRPercentiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xuwei
 * @create 2020-06-10 8:16
 */
@RestController
@RequestMapping("/test")
public class TestController {
//    @Autowired
//    private StyleProductionWorkInformationDao StyleProductionWorkInformationDao;

    @Resource
    private FmsTicketNoDao fmsTicketNoDao;

    @Resource
    private ProductionGroupDao productionGroupDao;

    @Resource
    private MotionCodeConfigService motionCodeConfigService;

    @Resource
    private StyleSewingCraftActionDao styleSewingCraftActionDao;

    @Resource
    private MakeTypeDao makeTypeDao;

    @Resource
    private SewingCraftActionDao sewingCraftActionDao;

    @Resource
    private SewingCraftWarehouseDao sewingCraftWarehouseDao;

    @Resource
    private BigOrderSewingCraftActionDao bigOrderSewingCraftActionDao;

    @Resource
    private BigStyleAnalyseMasterDao bigStyleAnalyseMasterDao;

    @Resource
    private BigOrderMasterDao bigOrderMasterDao;

    @Resource
    private StyleSewingCraftWarehouseDao styleSewingCraftWarehouseDao;

    @Resource
    private BigOrderSewingCraftWarehouseDao bigOrderSewingCraftWarehouseDao;

    @Resource
    private BigStyleOperationLogDao bigStyleOperationLogDao;

    @Resource
    private CraftGradeEquipmentService gradeEquipmentService;

    @Resource
    private BigStyleAnalysePartCraftDetailDao bigStyleAnalysePartCraftDetailDao;


    /**
     * 刷款式工艺的工序等级
     */
    /*@RequestMapping("/updateBigStyleCraftLevelAndPrice")
    public String updateBigStyleCraftLevelAndPrice() {
        List<Map<String, Object>> data = getDataFromExcel(0);
        if (data != null && data.size() > 0) {
            int flag = 0;
            for (Map<String, Object> map : data) {
                if (map.get("craftCode") != null) {
                    //detail表要刷--工序等级
                    System.out.println("------------处理第" + (flag++) + "条----------");
                    bigStyleAnalysePartCraftDetailDao.updateCraftLevelAndPrice(map);
                    //款式工艺的工序表要刷---工序等级
                    styleSewingCraftWarehouseDao.updateCraftLevelAndPrice(map);
                }
            }
        }
        return "OK";
    }*/

    /**
     * 刷款式工艺的做工类型
     */
   /* @RequestMapping("/updateBigStyleCraftMakeTypeCode")
    public String updateBigStyleCraftMakeTypeCode() {
        List<Map<String, Object>> data = getMakeTypeCodeDataFromExcel(1);
        if (data != null && data.size() > 0) {
            int flag = 0;
            for (Map<String, Object> map : data) {
                System.out.println("------------处理第" + (flag++) + "条----------");
                if (map.get("craftCode") != null) {
                    styleSewingCraftWarehouseDao.updateCraftMakeTypeCode(map);
                }
            }
        }
        return "OK";
    }*/

    /**
     * 刷款式工艺的工序等级
     */
    @RequestMapping("/updateBigStyleCraftLevelAndPrice")
    public String updateBigStyleCraftLevelAndPrice() {
        List<Map<String, Object>> data = getDataFromExcel(0);
        if (data != null && data.size() > 0) {
            AtomicInteger flag = new AtomicInteger(0);
            //for (Map<String, Object> map : data) {
            data.parallelStream().forEach(map->{
                if (map.get("craftCode") != null) {
                    //detail表要刷--工序等级
                    System.out.println("------------处理第" + (flag.getAndIncrement()) + "条----------");
                    bigStyleAnalysePartCraftDetailDao.updateCraftLevelAndPrice(map);
                    //款式工艺的工序表要刷---工序等级
                    styleSewingCraftWarehouseDao.updateCraftLevelAndPrice(map);
                }
            });
        }
        return "OK";
    }

    /**
     * 刷款式工艺的做工类型
     */
    @RequestMapping("/updateBigStyleCraftMakeTypeCode")
    public String updateBigStyleCraftMakeTypeCode() {
        List<Map<String, Object>> data = getMakeTypeCodeDataFromExcel(1);
        if (data != null && data.size() > 0) {
            AtomicInteger flag = new AtomicInteger(0);
            data.parallelStream().forEach(map->{
                System.out.println("------------处理第" + (flag.getAndIncrement()) + "条----------");
                if (map.get("craftCode") != null) {
                    styleSewingCraftWarehouseDao.updateCraftMakeTypeCode(map);
                }
            });

        }
        return "OK";
    }

    /**
     * 刷款式工艺的 标准单价
     */
    @RequestMapping("/updateBigStylePrice")
    public String updateBigStylePrice() {
        List<SewingCraftWarehouse> sewList = styleSewingCraftWarehouseDao.getAllData();
        Map<String, BigDecimal> minueMap = getMinueMap();
        if (sewList != null && sewList.size() > 0) {
            //工单工艺的要重新计算标准单价 标准单价=标准时间*工厂工序等级分钟工资（根据工单号带过来的工厂为准）
            AtomicInteger flag = new AtomicInteger(0);
            // for (SewingCraftWarehouse sew : sewList) {
            sewList.parallelStream().forEach(sew->{

                System.out.println("------------处理第" + ( flag.getAndIncrement()) + "条----------");
                Map<String, Object> param = new HashMap<>();
                param.put("id", sew.getId());
                BigDecimal standardTime = sew.getStandardTime();
                if (standardTime == null) {
                    standardTime = BigDecimal.ZERO;
                }
                BigDecimal minuWage = minueMap.get("9999" + "_" + sew.getCraftGradeCode());
                if (minuWage == null) {
                    minuWage = BigDecimal.ZERO;
                }
                BigDecimal standardPrice = standardTime.multiply(minuWage).setScale(3, BigDecimal.ROUND_HALF_UP);
                param.put("standardPrice", standardPrice);
                styleSewingCraftWarehouseDao.updatePriceById(param);

                Map<String, Object> param1 = new HashMap<>();
                param1.put("standardPrice", standardPrice);
                param1.put("craftCode", sew.getCraftCode());
                param1.put("styleRandomCode", sew.getRandomCode());
                bigStyleAnalysePartCraftDetailDao.updatePriceByStyleRandomCodeAndCraftCode(param1);
            });

        }


        return "OK";
    }


    /**
     * 刷工序管理的 标准单价
     */
//    @RequestMapping("/updatePrice")
//    public String updatePrice() {
//        List<Map<String, Object>> data = getDataFromExcel(2);
//        Map<String, BigDecimal> minueMap = getMinueMap();
//        if (data != null && data.size() > 0) {
//            for (Map<String, Object> map : data) {
//                if (map.get("craftCode") != null) {
//                    //工单工艺的要重新计算标准单价 标准单价=标准时间*工厂工序等级分钟工资（根据工单号带过来的工厂为准）
//                    Map<String, Object> param = new HashMap<>();
//                    param.put("code", map.get("craftCode"));
//                    List<SewingCraftWarehouse> sewList = sewingCraftWarehouseDao.getDataByCraftCodeLike(param);
//                    if (null != sewList && sewList.size() > 0) {
//                        BigDecimal standardTime = sewList.get(0).getStandardTime();
//                        if (standardTime == null) {
//                            standardTime = BigDecimal.ZERO;
//                        }
//                        BigDecimal minuWage = minueMap.get("9999" + "_" + sewList.get(0).getCraftGradeCode());
//                        if (minuWage == null) {
//                            minuWage = BigDecimal.ZERO;
//                        }
//                        BigDecimal standardPrice = standardTime.multiply(minuWage).setScale(3, BigDecimal.ROUND_HALF_UP);
//                        map.put("standardPrice", standardPrice);
//                        sewingCraftWarehouseDao.updatePriceByCraftCode(map);
//                    }
//
//                }
//            }
//        }
//        return "OK";
//    }

    /**
     * key是工厂_工序等级
     */
    private Map<String, BigDecimal> getMinueMap() {
        List<CraftGradeEquipment> list = gradeEquipmentService.getAllCraftGrade();
        Map<String, BigDecimal> minueMap = new HashMap<>(0);
        if (null != list && list.size() > 0) {
            for (CraftGradeEquipment vo : list) {
                minueMap.put(vo.getFactoryCode() + "_" + vo.getCraftGradeCode(), vo.getMinuteWage());
            }
        }
        return minueMap;
    }

    /**
     * 刷工序管理的工序等级
     */
    @RequestMapping("/updateCraftLevelAndPrice")
    public String updateCraftLevelAndPrice() {
        List<Map<String, Object>> data = getDataFromExcel(0);
        if (data != null && data.size() > 0) {
            for (Map<String, Object> map : data) {
                if (map.get("craftCode") != null) {
                    sewingCraftWarehouseDao.updateCraftLevelAndPrice(map);
                }
            }
        }
        return "OK";
    }

    /**
     * 刷工序管理的做工类型
     */
    @RequestMapping("/updateCraftMakeTypeCode")
    public String updateCraftMakeTypeCode() {
        List<Map<String, Object>> data = getMakeTypeCodeDataFromExcel(1);
        if (data != null && data.size() > 0) {
            for (Map<String, Object> map : data) {
                if (map.get("craftCode") != null) {
                    sewingCraftWarehouseDao.updateCraftMakeTypeCode(map);
                }
            }
        }
        return "OK";
    }

    /**
     * 从Excel里面读取款式数据
     */
    public List<Map<String, Object>> getDataFromExcel(int sheetNum) {
        List<Map<String, Object>> data = new ArrayList<>();
        // 得到Excel工作簿对象
        Workbook wb = null;
        File file = new File("D:" + File.separator + "1.xlsx");
        Map<String, String> map = new HashMap<String, String>();
        try {
            if (file.exists()) {// 是2007
                wb = new XSSFWorkbook(file);
            } else {
                file = new File("D:" + File.separator + "1.xls");
                if (!file.exists()) {
                    return null;
                }
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                // 得到Excel工作簿对象
                wb = new HSSFWorkbook(fs);
            }
            Sheet sheet = wb.getSheet("0319手工手缝总定-IT (只刷工序等级)");
            int rowLength = sheet.getLastRowNum();
            for (int i = 1; i <= rowLength; i++) {
                Map<String, Object> obj = new HashMap<>();
                Row row = sheet.getRow(i);
                Cell cell0 = row.getCell(0);
                String Code = getCellStrValue(cell0);
                if (stringIsNull(Code)) {
                    continue;
                }
                obj.put("craftCode", Code.toUpperCase());


                Cell cell3 = row.getCell(3);
                String cell3Value = getCellStrValue(cell3);
                if (stringIsNull(cell3Value)) {
                    continue;
                }
                obj.put("craftGradeCode", cell3Value);// S/Freeze/B/C分类(大货)code

                Cell cell5 = row.getCell(5);
                String cell5Value = getCellStrValue(cell5);
                if (stringIsNull(cell5Value)) {
                    continue;
                }
                obj.put("standardPrice", cell5Value);
                data.add(obj);

            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * 从Excel里面读取款式数据
     */
    public List<Map<String, Object>> getMakeTypeCodeDataFromExcel(int sheetNum) {
        List<Map<String, Object>> data = new ArrayList<>();
        // 得到Excel工作簿对象
        Workbook wb = null;
        File file = new File("D:" + File.separator + "1.xlsx");
        Map<String, String> map = new HashMap<String, String>();
        try {
            if (file.exists()) {// 是2007
                wb = new XSSFWorkbook(file);
            } else {
                file = new File("D:" + File.separator + "1.xls");
                if (!file.exists()) {
                    return null;
                }
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                // 得到Excel工作簿对象
                wb = new HSSFWorkbook(fs);
            }
            Sheet sheet = wb.getSheet("0319手工手缝总定-IT（只刷做工类型）");
            int rowLength = sheet.getLastRowNum();
            for (int i = 1; i <= rowLength; i++) {
                Map<String, Object> obj = new HashMap<>();
                Row row = sheet.getRow(i);
                Cell cell0 = row.getCell(0);
                String Code = getCellStrValue(cell0);
                if (stringIsNull(Code)) {
                    continue;
                }
                obj.put("craftCode", Code.toUpperCase());


                Cell cell5 = row.getCell(5);
                String cell5Value = getCellStrValue(cell5);
                if (stringIsNull(cell5Value)) {
                    continue;
                }
                obj.put("makeTypeCode", cell5Value);
                data.add(obj);

            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    private boolean stringIsNull(String str) {
        if (null == str || str.isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean stringNotNull(String str) {
        if (null == str || str.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 清理big_style_operation_log多余的数据
     */
    public void deleteStyleOperationLog() {
        List<BigStyleOperationLog> logs = bigStyleOperationLogDao.getAll();
        Map<String, List<Long>> data = new HashMap<>();
        if (null != logs && logs.size() > 0) {
            for (BigStyleOperationLog log : logs) {
                String code = log.getCode();
                Long id = log.getId();
                if (!data.containsKey(code)) {
                    List<Long> list = new ArrayList<>();
                    data.put(code, list);
                }
                data.get(code).add(id);
            }
        }
    }

    /**
     * 查款是否有重复的工票号
     */
    @RequestMapping("/export")
    public void export() {
        Map<String, String> map = getBomDataFromExcel(0);
        List<BigStyleAnalyseMaster> lists = bigOrderMasterDao.getDataByPager(new HashMap<>());
        for (BigStyleAnalyseMaster big : lists) {
            String productionCategory = big.getProductionCategory();
            if (map.containsKey(productionCategory) && big.getId() != null) {
                bigOrderMasterDao.updateAdaptCode("10", big.getId());
            }
        }
    }

    public static Map<String, String> getBomDataFromExcel(int sheetNum) {
        Map<String, String> map = new HashMap<>();
        Workbook wb = null;
        File file = new File("D:" + File.separator + "1.xlsx");
        try {
            if (file.exists()) {// 是2007
                wb = new XSSFWorkbook(file);
            } else {
                file = new File("D:" + File.separator + "1.xls");
                if (!file.exists()) {
                    return null;
                }
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                // 得到Excel工作簿对象
                wb = new HSSFWorkbook(fs);
            }
            Sheet sheet = wb.getSheetAt(sheetNum);
            int rowLength = sheet.getLastRowNum();
            for (int i = 1; i <= rowLength; i++) {
                Row row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }
                Cell cell0 = row.getCell(2);
                String bigCode = getCellStrValue(cell0);
                if (StringUtils.isEmpty(bigCode)) {
                    continue;
                }

                Cell useCell = row.getCell(4);
                String use = getCellStrValue(useCell);
                if (use.equals("10")) {
                    map.put(bigCode, use);
                }

            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    /**
     * 查款是否有重复的工票号
     */
    @RequestMapping("/test3")
    public void test3() {
        Map<String, Object> param = new HashMap<>();
        List<BigStyleAnalyseMaster> masters = bigStyleAnalyseMasterDao.getDataByPager(param);

        List<BigStyleAnalyseMaster> orders = bigOrderMasterDao.getDataByPager(param);

        Map<String, Set<String>> data = new HashMap<>();
        if (null != masters && masters.size() > 0) {
            for (BigStyleAnalyseMaster vo : masters) {
                List<CraftVO> vos = styleSewingCraftWarehouseDao.getDataForExcelReport(vo.getRandomCode());
                Map<String, String> map = new HashMap<>();
                if (null != vos && vos.size() > 0) {
                    for (CraftVO craft : vos) {
                        String workNo = craft.getWorkOrderNo();
                        if (StringUtils.isNotEmpty(workNo)) {
                            if (map.containsKey(workNo)) {
                                //说明存在重复的
                                if (!data.containsKey(vo.getBigStyleAnalyseCode())) {
                                    data.put(vo.getBigStyleAnalyseCode(), new HashSet<>());
                                }
                                data.get(vo.getBigStyleAnalyseCode()).add(workNo);
                            } else {
                                map.put(workNo, workNo);
                            }
                        }

                    }
                }
            }
        }
        if (null != orders && orders.size() > 0) {
            for (BigStyleAnalyseMaster vo : orders) {
                List<CraftVO> vos = bigOrderSewingCraftWarehouseDao.getDataForExcelReport(vo.getRandomCode());
                Map<String, String> map = new HashMap<>();
                if (null != vos && vos.size() > 0) {
                    for (CraftVO craft : vos) {
                        String workNo = craft.getWorkOrderNo();
                        if (StringUtils.isNotEmpty(workNo)) {
                            if (map.containsKey(workNo)) {
                                //说明存在重复的
                                if (!data.containsKey(vo.getProductionTicketNo())) {
                                    data.put(vo.getProductionTicketNo(), new HashSet<>());
                                }
                                data.get(vo.getProductionTicketNo()).add(workNo);
                            } else {
                                map.put(workNo, workNo);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(JSONObject.toJSONString(data));

    }

    @RequestMapping("/test1")
    public void test1() {
        List<BigStyleAnalyseMaster> masters = bigStyleAnalyseMasterDao.getPriceAndTime();
        List<BigStyleAnalyseMaster> sewList = bigStyleAnalyseMasterDao.getPriceAndTimeSew();
        List<String> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        if (masters != null && sewList != null) {
            Map<String, BigStyleAnalyseMaster> masMap = new HashMap<>();
            Map<String, BigStyleAnalyseMaster> sewMap = new HashMap<>();
            for (BigStyleAnalyseMaster vo : masters) {
                masMap.put(vo.getBigStyleAnalyseCode(), vo);
            }
            for (BigStyleAnalyseMaster vo : sewList) {
                sewMap.put(vo.getBigStyleAnalyseCode(), vo);
            }
            Iterator<String> it = masMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                BigStyleAnalyseMaster masVo = masMap.get(key);
                if (sewMap.containsKey(key)) {
                    BigStyleAnalyseMaster sewVo = sewMap.get(key);
                    boolean flag = false;
                    if (masVo.getStandardTime() != null && sewVo.getStandardTime() != null &&
                            !masVo.getStandardTime().equals(sewVo.getStandardTime())) {
                        map.put(masVo.getBigStyleAnalyseCode(), masVo.getBigStyleAnalyseCode());
                        flag = true;
                    }
                    if (masVo.getStandardPrice() != null && sewVo.getStandardPrice() != null &&
                            !masVo.getStandardPrice().equals(sewVo.getStandardPrice())) {
                        map.put(masVo.getBigStyleAnalyseCode(), masVo.getBigStyleAnalyseCode());
                        flag = true;
                    }
                    if (masVo.getRandomCode() != null && flag) {
                        Map<String, Object> param = new HashMap<>();
                        param.put("standardPrice", sewVo.getStandardPrice());
                        param.put("standardTime", sewVo.getStandardTime());
                        param.put("randomCode", masVo.getRandomCode());
                        bigStyleAnalyseMasterDao.updatePriceAndTime(param);
                    }
                }
            }

        }
        list.addAll(map.values());
        System.out.println(JSONObject.toJSONString(list));
    }

    @RequestMapping("/test2")
    public void test2() {
        List<BigStyleAnalyseMaster> masters = bigOrderMasterDao.getPriceAndTime();
        List<BigStyleAnalyseMaster> sewList = bigOrderMasterDao.getPriceAndTimeSew();
        List<String> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        if (masters != null && sewList != null) {
            Map<String, BigStyleAnalyseMaster> masMap = new HashMap<>();
            Map<String, BigStyleAnalyseMaster> sewMap = new HashMap<>();
            for (BigStyleAnalyseMaster vo : masters) {
                masMap.put(vo.getBigStyleAnalyseCode(), vo);
            }
            for (BigStyleAnalyseMaster vo : sewList) {
                sewMap.put(vo.getBigStyleAnalyseCode(), vo);
            }
            Iterator<String> it = masMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                BigStyleAnalyseMaster masVo = masMap.get(key);
                if (sewMap.containsKey(key)) {
                    BigStyleAnalyseMaster sewVo = sewMap.get(key);
                    if (masVo.getStandardTime() != null && sewVo.getStandardTime() != null &&
                            !masVo.getStandardTime().equals(sewVo.getStandardTime())) {
                        map.put(masVo.getBigStyleAnalyseCode(), masVo.getBigStyleAnalyseCode());
                    }
                    if (masVo.getStandardPrice() != null && sewVo.getStandardPrice() != null &&
                            !masVo.getStandardPrice().equals(sewVo.getStandardPrice())) {
                        map.put(masVo.getBigStyleAnalyseCode(), masVo.getBigStyleAnalyseCode());
                    }


                }
            }

        }
        list.addAll(map.values());
        System.out.println(JSONObject.toJSONString(list));
    }


    @RequestMapping("/insert")
    public void insert() {
        List<SewingCraftAction> list = styleSewingCraftActionDao.getZCodeList();
        for (SewingCraftAction action : list) {
            BigDecimal fre = action.getFrequency();
            String code = action.getMotionCode();
            String bigCode = action.getCraftCode();
//            if("XSFGK081".equals(bigCode)&& "Z2000".equals(code)){
//                System.out.println();
//            }
            BigDecimal time = new BigDecimal(code.substring(1));
            if (null == time) {
                time = BigDecimal.ZERO;
            }
            BigDecimal total = time.multiply(fre);
            styleSewingCraftActionDao.updateTime(total, action.getId());

        }

        List<SewingCraftAction> sewList = sewingCraftActionDao.getZCodeList();
        for (SewingCraftAction action : sewList) {
            BigDecimal fre = action.getFrequency();
            String code = action.getMotionCode();
            String bigCode = action.getCraftCode();
//            if("XSFGK081".equals(bigCode)&& "Z2000".equals(code)){
//                System.out.println();
//            }
            BigDecimal time = new BigDecimal(code.substring(1));
            if (null == time) {
                time = BigDecimal.ZERO;
            }
            BigDecimal total = time.multiply(fre);
            sewingCraftActionDao.updateTime(total, action.getId());

        }

        List<SewingCraftAction> orderList = bigOrderSewingCraftActionDao.getZCodeList();
        for (SewingCraftAction action : orderList) {
            BigDecimal fre = action.getFrequency();
            String code = action.getMotionCode();
            String bigCode = action.getCraftCode();
//            if("XSFGK081".equals(bigCode)&& "Z2000".equals(code)){
//                System.out.println();
//            }
            BigDecimal time = new BigDecimal(code.substring(1));
            if (null == time) {
                time = BigDecimal.ZERO;
            }
            BigDecimal total = time.multiply(fre);
            bigOrderSewingCraftActionDao.updateTime(total, action.getId());

        }

    }

    private static String getCellStrValue(Cell cell) {
        if (null == cell) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private void getData(List<JSONObject> map) {
        for (int j = 1; j <= 4; j++) {
            Workbook wb = null;
            try {
                File file = new File("D:" + File.separator + j + ".xls");
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                // 得到Excel工作簿对象
                wb = new HSSFWorkbook(fs);
                Sheet sheet = wb.getSheetAt(1);
                int rowLength = sheet.getLastRowNum();
                int rowNumber = 0;
                for (int i = 2; i <= rowLength; i++) {
                    Row row = sheet.getRow(i);
                    Cell cell0 = row.getCell(1);
                    Cell cell2 = row.getCell(3);
                    JSONObject obj = new JSONObject();
                    obj.put("craftCode", getCellStrValue(cell2));
                    obj.put("num", getCellStrValue(cell0));
                    map.add(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (wb != null) {
                    try {
                        wb.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @RequestMapping("/make")
    public void make() {
        List<MakeType> list = makeTypeDao.getAllMakeType();
        Map<String, String> map = new HashMap<>();
        getMake(map);
        if (null != list && list.size() > 0) {
            for (MakeType vo : list) {
                String code = vo.getMakeTypeCode();
                if (map.containsKey(code)) {
                    String num = map.get(code);
                    MakeType type = new MakeType();
                    type.setMakeTypeCode(code);
                    type.setMakeTypeNumericalCode(num);
                    makeTypeDao.updateMakeType(type);
                }
            }
        }
    }

    private void getMake(Map<String, String> map) {

        Workbook wb = null;
        try {
            File file = new File("D:" + File.separator + "1.xls");
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
            // 得到Excel工作簿对象
            wb = new HSSFWorkbook(fs);
            Sheet sheet = wb.getSheetAt(0);
            int rowLength = sheet.getLastRowNum();
            int rowNumber = 0;
            for (int i = 2; i <= rowLength; i++) {
                Row row = sheet.getRow(i);
                Cell cell0 = row.getCell(0);
                Cell cell2 = row.getCell(1);
                map.put(getCellStrValue(cell0), getCellStrValue(cell2));
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


    }


    private FmsTicketNo change(Item item, Map<String, ProductionGroup> map) throws Exception {
        FmsTicketNo no = new FmsTicketNo();
        no.setCreateTime(new Date());
        no.setUpdateTime(new Date());
        no.setProductionTicketNo(item.getAufnr());
        no.setStyleCode(item.getMatnr());
        no.setStyleType(item.getExtwg());
        no.setClothesCategoryCode(item.getMatkl());
        no.setBrand(item.getBrandId());
        no.setFactoryCode(item.getWerks());
        no.setProductionCategory(item.getWorkCenterId());
        String orderLineNo = "1";
        OrderItem orderItem = null;
        if (ObjectUtils.isNotEmptyList(item.getOrderItem())) {
            orderItem = item.getOrderItem().get(0);
        }

        if (ObjectUtils.isNotEmpt(orderItem) && ObjectUtils.isNotEmpt(orderItem.getZmtmitm())) {
            int orderLiNo = Integer.valueOf(orderItem.getZmtmitm());
            orderLineNo = String.valueOf(orderLiNo);
        }
        no.setOrderLineId(orderLineNo);
        if (map.containsKey(item.getWorkCenterId())) {
            no.setProductionCategoryName(map.get(item.getWorkCenterId()).getProductionGroupName());
        }
        no.setUnit(item.getGmein());
        List<OrderItem> list = item.getOrderItem();
        BigDecimal value = BigDecimal.ZERO;
        if (null != list && list.size() > 0) {
            for (OrderItem oo : list) {
                value = value.add(oo.getGsmng()).setScale(3, BigDecimal.ROUND_HALF_UP);
            }
        }
        no.setNumber(value.setScale(0, BigDecimal.ROUND_HALF_UP).toPlainString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        if (StringUtils.isNotEmpty(item.getGstrs())) {
            no.setStartDay(sdf.parse(item.getGstrs()));
        }
        if (StringUtils.isNotEmpty(item.getGltrs())) {
            no.setEndDay(sdf.parse(item.getGltrs()));
        }
        no.setStartTime(item.getGsuzs());
        no.setEndTime(item.getGluzs());
        no.setStyleCodeColor(item.getPlnbez());
        no.setMtmOrder(item.getMtmorder());
        if (StringUtils.isNotEmpty(item.getMtmorder())) {
            no.setCustomStyleCode(item.getMatnr());
        }
        return no;
    }

    @Resource
    private BigOrderPartCraftDetailDao bigOrderPartCraftDetailDao;

    /**
     * 刷工单工艺的工序等级
     */
    @RequestMapping("/updateOrderCraftLevelAndPrice")
    public String updateOrderCraftLevelAndPrice() {
        List<Map<String, Object>> data = getDataFromExcel(0);
        if (data != null && data.size() > 0) {
            AtomicInteger flag = new AtomicInteger(0);
            // for (Map<String, Object> map : data) {
            data.parallelStream().forEach(map->{
                if (map.get("craftCode") != null) {
                    //detail表要刷--工序等级
                    System.out.println("------------处理第" + (flag.getAndIncrement()) + "条----------");
                    bigOrderPartCraftDetailDao.updateCraftLevelAndPrice(map);
                    //工单工艺的工序表要刷---工序等级
                    bigOrderSewingCraftWarehouseDao.updateCraftLevelAndPrice(map);
                }
            });
        }
        return "OK";
    }

    /**
     * 刷工单工艺的做工类型
     */
    @RequestMapping("/updateOrderCraftMakeTypeCode")
    public String updateOrderCraftMakeTypeCode() {
        List<Map<String, Object>> data = getMakeTypeCodeDataFromExcel(1);
        if (data != null && data.size() > 0) {
            AtomicInteger flag = new AtomicInteger(0);
            data.parallelStream().forEach(map->{
                System.out.println("------------处理第" + (flag.getAndIncrement()) + "条----------");
                if (map.get("craftCode") != null) {
                    bigOrderSewingCraftWarehouseDao.updateCraftMakeTypeCode(map);
                }
            });

        }
        return "OK";
    }

    /**
     * 刷工单工艺的 标准单价
     */
    @RequestMapping("/updateOrderPrice")
    public String updateOrderPrice() {
        List<SewingCraftWarehouse> sewList = bigOrderSewingCraftWarehouseDao.getAllData();
        Map<String, BigDecimal> minueMap = getMinueMap();
        if (sewList != null && sewList.size() > 0) {
            //工单工艺的要重新计算标准单价 标准单价=标准时间*工厂工序等级分钟工资（根据工单号带过来的工厂为准）
            int flag = 0;
            for (SewingCraftWarehouse sew : sewList) {
                System.out.println("------------处理第" + (flag++) + "条----------");
                Map<String, Object> param = new HashMap<>();
                param.put("id", sew.getId());
                BigDecimal standardTime = sew.getStandardTime();
                if (standardTime == null) {
                    standardTime = BigDecimal.ZERO;
                }
                BigStyleAnalyseMaster mas =  bigOrderMasterDao.getBigStyleAnalyseMasterByRandomCode(sew.getRandomCode());
                BigDecimal minuWage = minueMap.get(mas.getFactoryCode() + "_" + sew.getCraftGradeCode());
                if (minuWage == null) {
                    minuWage = BigDecimal.ZERO;
                }
                BigDecimal standardPrice = standardTime.multiply(minuWage).setScale(3, BigDecimal.ROUND_HALF_UP);
                param.put("standardPrice", standardPrice);
                bigOrderSewingCraftWarehouseDao.updatePriceById(param);

                Map<String, Object> param1 = new HashMap<>();
                param1.put("standardPrice", standardPrice);
                param1.put("craftCode", sew.getCraftCode());
                param1.put("styleRandomCode", sew.getRandomCode());
                bigOrderPartCraftDetailDao.updatePriceByStyleRandomCodeAndCraftCode(param1);
            }
        }


        return "OK";
    }




    /**
     * 刷订单工艺的工序等级
     */
    @RequestMapping("/updateOrderCraftLevelAndPrice")
    public String updateOrderCraftLevelAndPrice() {
        List<Map<String, Object>> data = getDataFromExcel(0);
        if (data != null && data.size() > 0) {
            AtomicInteger flag = new AtomicInteger(0);
            //for (Map<String, Object> map : data) {
            data.parallelStream().forEach(map->{
                if (map.get("craftCode") != null) {
                    //detail表要刷--工序等级
                    System.out.println("------------处理第" + (flag.getAndIncrement()) + "条----------");
                    bigStyleAnalysePartCraftDetailDao.updateCraftLevelAndPrice(map);
                    //款式工艺的工序表要刷---工序等级
                    styleSewingCraftWarehouseDao.updateCraftLevelAndPrice(map);
                }
            });
        }
        return "OK";
    }

    /**
     * 刷款式工艺的做工类型
     */
    @RequestMapping("/updateOrderCraftMakeTypeCode")
    public String updateOrderCraftMakeTypeCode() {
        List<Map<String, Object>> data = getMakeTypeCodeDataFromExcel(1);
        if (data != null && data.size() > 0) {
            AtomicInteger flag = new AtomicInteger(0);
            data.parallelStream().forEach(map->{
                System.out.println("------------处理第" + (flag.getAndIncrement()) + "条----------");
                if (map.get("craftCode") != null) {
                    styleSewingCraftWarehouseDao.updateCraftMakeTypeCode(map);
                }
            });

        }
        return "OK";
    }
}
