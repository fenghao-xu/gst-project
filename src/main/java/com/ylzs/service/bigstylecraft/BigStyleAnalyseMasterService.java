package com.ylzs.service.bigstylecraft;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.*;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.*;
import com.ylzs.dao.bigstylerecord.TimePriceOperationLogDao;
import com.ylzs.dao.staticdata.PatternSymmetryDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.dao.timeparam.CraftGradeEquipmentDao;
import com.ylzs.dao.timeparam.FabricGradeDao;
import com.ylzs.entity.bigstylecraft.*;
import com.ylzs.entity.bigstylerecord.TimePriceOperationLog;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.entity.staticdata.PatternSymmetry;
import com.ylzs.entity.system.User;
import com.ylzs.schedual.AutoUploadVideoSchedule;
import com.ylzs.service.bigstylerecord.IBigStyleNodeRecordService;
import com.ylzs.service.interfaceOutput.INewCraftService;
import com.ylzs.service.interfaceOutput.IOperationPathService;
import com.ylzs.service.interfaceOutput.ISectionSMVService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.sewingcraft.SewingCraftWarehouseService;
import com.ylzs.vo.DictionaryVo;
import com.ylzs.vo.bigstylereport.CraftVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author xuwei
 * @create 2020-03-19 15:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigStyleAnalyseMasterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BigStyleAnalyseMasterService.class);
    @Resource
    private BigStyleAnalyseMasterDao bigStyleAnalyseMasterDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private BigStyleAnalyseMasterSKCDao bigStyleAnalyseMasterSKCDao;

    @Resource
    private BigStyleAnalyseMasterPictureDao bigStyleAnalyseMasterPictureDao;

    @Resource
    private BigStyleAnalysePartCraftDao bigStyleAnalysePartCraftDao;

    @Resource
    private BigStyleAnalysePartCraftDetailDao bigStyleAnalysePartCraftDetailDao;

    @Resource
    private SewingCraftWarehouseService sewingCraftWarehouseService;

    @Resource
    private FabricGradeDao fabricGradeDao;

    @Resource
    private PatternSymmetryDao patternSymmetryDao;

    @Resource
    private UserDao userDao;

    @Resource
    private StyleSewingCraftActionDao styleSewingCraftActionDao;

    @Resource
    private StyleSewingCraftStdDao styleSewingCraftStdDao;

    @Resource
    private StyleSewingCraftPartPositionDao styleSewingCraftPartPositionDao;

    @Resource
    private StyleSewingCraftWarehouseDao styleSewingCraftWarehouseDao;

    @Resource
    private StyleSewingCraftWarehouseWorkplaceDao styleSewingCraftWarehouseWorkplaceDao;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private ISectionSMVService sectionSMVService;

    @Resource
    private CraftGradeEquipmentDao craftGradeEquipmentDao;

    @Resource
    private IOperationPathService operationPathService;

    @Resource
    private IBigStyleNodeRecordService bigStyleNodeRecordService;

    @Resource
    private INewCraftService newCraftService;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;

    @Resource
    private BigStyleOperationLogDao bigStyleOperationLogDao;

    @Resource
    private DictionaryDao dictionaryDao;
    @Resource
    private TimePriceOperationLogDao timePriceOperationLogDao;

    @Resource
    private HistoryBigStyleAnalyseMasterDao historyBigStyleAnalyseMasterDao;

    @Resource
    AutoUploadVideoSchedule autoUploadVideoSchedule;

    @Resource
    HistoryBigStyleAnalyseMasterPictureDao historyBigStyleAnalyseMasterPictureDao;


    public List<BigStyleAnalyseMaster> getDataByPager(Map<String, Object> param) {
        List<BigStyleAnalyseMaster> masters = bigStyleAnalyseMasterDao.getDataByPager(param);
        if (null == masters || masters.size() == 0) {
            return Collections.emptyList();
        }
        final Map<String, User> map = userDao.getUserMap();
        masters.stream().parallel().forEach(x -> {
            //查询大货款式工艺的图片
            if (map.containsKey(x.getCreateUser())) {
                x.setCreateUserName(map.get(x.getCreateUser()).getUserName());
            } else {
                x.setCreateUserName("");
            }
            if (map.containsKey(x.getUpdateUser())) {
                x.setUpdateUserName(map.get(x.getUpdateUser()).getUserName());
            } else {
                x.setUpdateUserName("");
            }
            if (map.containsKey(x.getReleaseUser())) {
                x.setReleaseUserName(map.get(x.getReleaseUser()).getUserName());
            } else {
                x.setReleaseUserName("");
            }
            searchPictures(x);
            //查询部件工艺以及里面的详情
            getPartAndDetailByStyleRandomCode(x);
            searchSKCode(x);
        });
        return masters;
    }

    public BigStyleAnalyseMaster searchFromBigStyleAnalyseByRandomCode(Long randomCode) {
        BigStyleAnalyseMaster data = bigStyleAnalyseMasterDao.searchFromBigStyleAnalyseByRandomCode(randomCode);
        if (null != data) {
            Map<String, User> map = userDao.getUserMap();
            if (map.containsKey(data.getReleaseUser())) {
                data.setReleaseUserName(map.get(data.getReleaseUser()).getUserName());
            } else {
                data.setReleaseUserName("");
            }
            if (map.containsKey(data.getCreateUser())) {
                data.setCreateUserName(map.get(data.getCreateUser()).getUserName());
            } else {
                data.setCreateUserName("");
            }
            if (map.containsKey(data.getUpdateUser())) {
                data.setUpdateUserName(map.get(data.getUpdateUser()).getUserName());
            } else {
                data.setUpdateUserName("");
            }
            getPartAndDetailByStyleRandomCode(data);
            searchPictures(data);
            searchSKCode(data);
            getTimePriceOperationLogList(data);
            try {
                getWorkTypesData(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public BigStyleAnalyseMaster searchFromBigStyleAnalyseByRandomCodeWithOutPartDeatil(Long randomCode) {
        BigStyleAnalyseMaster data = bigStyleAnalyseMasterDao.searchFromBigStyleAnalyseByRandomCode(randomCode);
        final Map<String, User> map = userDao.getUserMap();
        if (null != data) {
            searchPictures(data);
            if (map.containsKey(data.getCreateUser())) {
                data.setCreateUserName(map.get(data.getCreateUser()).getUserName());
            } else {
                data.setCreateUserName("");
            }
            if (map.containsKey(data.getReleaseUser())) {
                data.setReleaseUserName(map.get(data.getReleaseUser()).getUserName());
            } else {
                data.setReleaseUserName("");
            }
        }
        return data;
    }

    /**
     * 查询款的款色编码
     */
    private void searchSKCode(BigStyleAnalyseMaster vo) {
        List<BigStyleAnalyseSkc> data = bigStyleAnalyseMasterSKCDao.selectDataByStyleRandomCode(vo.getRandomCode());
        if (null != data && data.size() > 0) {
            vo.setSkc(data);
        }
    }

    /**
     * 查询部件工艺以及详情数据
     */
    private void getPartAndDetailByStyleRandomCode(BigStyleAnalyseMaster vo) {
        List<BigStyleAnalysePartCraft> partCraftList = bigStyleAnalysePartCraftDao.getPartAndDetailByStyleRandomCode(vo.getRandomCode());
        if (null != partCraftList && partCraftList.size() > 0) {
            vo.setPartCraftList(partCraftList);
        }
    }

    /**
     * get公众的标准时间和标准单价 汇总数据
     */
    private void getWorkTypesData(BigStyleAnalyseMaster bigStyleAnalyseMaster) {
        if (null == bigStyleAnalyseMaster) {
            return;
        }
        List<CraftVO> craftVOList = styleSewingCraftWarehouseDao.getDataForExcelReport(bigStyleAnalyseMaster.getRandomCode());
        Map<String, JSONObject> workTypeMap = new HashMap<>();
        List<JSONObject> data = new ArrayList<>();
        if (null != craftVOList && craftVOList.size() > 0) {
            for (CraftVO vo : craftVOList) {
                String key = vo.getWorkTypeName();
                BigDecimal newTime = vo.getStandardTime();
                BigDecimal newPrice = vo.getStandardPrice();
                if (null == newTime) {
                    newTime = BigDecimal.ZERO;
                }
                if (null == newPrice) {
                    newPrice = BigDecimal.ZERO;
                }
                if (workTypeMap.containsKey(key)) {
                    JSONObject obj = workTypeMap.get(key);
                    BigDecimal oldTime = obj.getBigDecimal("time");
                    BigDecimal oldPrice = obj.getBigDecimal("price");
                    obj.put("price", oldPrice.add(newPrice));
                    obj.put("time", oldTime.add(newTime));
                    workTypeMap.put(key, obj);
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("code", vo.getWorkTypeCode());
                    obj.put("name", vo.getWorkTypeName());
                    obj.put("price", newPrice);
                    obj.put("time", newTime);
                    workTypeMap.put(key, obj);
                }
            }
        }
        if (workTypeMap.size() > 0) {
            data.addAll(workTypeMap.values());
        }
        bigStyleAnalyseMaster.setWorkTypeList(data);
    }

    /**
     * 查询款的图片，当款的图片有多个的时候，级联查询会出现多条记录，则会导致翻页总数不对，所以只能
     * 单个款式工艺去查询图片
     */
    private void searchPictures(BigStyleAnalyseMaster vo) {
        List<String> pictures = bigStyleAnalyseMasterPictureDao.getUrlByStyleRandomCode(vo.getRandomCode());
        List<JSONObject> list = new ArrayList<>();
        if (null != pictures && pictures.size() > 0) {
            for (String pic : pictures) {
                JSONObject obj = new JSONObject();
                obj.put("picUrl", pic);
                list.add(obj);
            }
        }
        vo.setPictures(list);
    }

    private void getTimePriceOperationLogList(BigStyleAnalyseMaster vo) {
        List<TimePriceOperationLog> logs = timePriceOperationLogDao.getDataByBusinessTypeAndCode("time_price_operation_log", vo.getBigStyleAnalyseCode(), BusinessConstants.BusinessType.STYLE_CRAFT);
        vo.setTimePriceOperationLogList(logs);
    }

    public boolean updateStatus(Integer status, Long randomCode, String userCode) {
        boolean flag = false;
        try {
            bigStyleAnalyseMasterDao.updateStatus(status, randomCode, userCode, new Date());
            flag = true;

            if (BusinessConstants.Status.PUBLISHED_STATUS.equals(status)) {
                //发送大货款实际工段工时
                taskExecutor.execute(() -> {
                    BigStyleAnalyseMaster master = bigStyleAnalyseMasterDao.searchFromBigStyleAnalyseByRandomCode(randomCode);
                    if (null != master) {
                        //更新分科信息
                        bigStyleNodeRecordService.updateStyleNodeBranchInfo(master.getCtStyleCode(), master.getCreateTime(), master.getReleaseTime(), null);
                        //发送大货款实际工段工时
                        sectionSMVService.sendBigStyleActualSectionSMV(master.getBigStyleAnalyseCode(), true);
                        //发送大货款新工序
                        newCraftService.sendBigStyleNewCraft(master.getBigStyleAnalyseCode(), true);
                        try {
                            orderProcessingStatusService.addOrUpdate(master.getCtStyleCode(), OderProcessingStatusConstants.BigStyleStatusName.BIG_STYLE_NAME_1250, OderProcessingStatusConstants.BigStyleStatus.BIG_STYLE_1250, "", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 从款式工艺里面导入 部件工艺和工序数据
     */
    public List<BigStyleAnalyseMaster> searchFromBigStyleAnalyse(Map<String, Object> map) {
        List<BigStyleAnalyseMaster> masterList = Collections.emptyList();
        if (map.get("craftCode") != null || map.get("description") != null) {
            masterList = bigStyleAnalyseMasterDao.searchBigStyleAnalyseByCraftInfo(map);
        } else {

            masterList = bigStyleAnalyseMasterDao.searchFromBigStyleAnalyse(map);
        }
        if (null == masterList || masterList.size() == 0) {
            return Collections.emptyList();
        }
        masterList.stream().parallel().forEach(x -> {
            List<BigStyleAnalysePartCraft> partCrafts = bigStyleAnalysePartCraftDao.getPartAndDetailByStyleRandomCodeAndOrderByCraftNo(x.getRandomCode());
            x.setPartCraftList(partCrafts);
            searchPictures(x);
        });
        return masterList;
    }

    public void deleteByID(Long id) {
        bigStyleAnalyseMasterDao.deleteByID(id);
    }

    public JSONObject addOrUpdate(JSONObject dataObj, String operation) throws Exception {
        String bigStyleAnalyseCode = "";
        Long randomCode = null;
        JSONObject result = new JSONObject();
        //款式编码
        String ctStyleCode = dataObj.getString("ctStyleCode");
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            //修改的时候由前端页面传递过来
            randomCode = dataObj.getLong("randomCode");
            bigStyleAnalyseCode = dataObj.getString("bigStyleAnalyseCode");
            if (null == randomCode && StringUtils.isNotEmpty(bigStyleAnalyseCode)) {
                BigStyleAnalyseMaster mas = bigStyleAnalyseMasterDao.getBigStyleByCode(bigStyleAnalyseCode);
                if (mas != null) {
                    randomCode = mas.getRandomCode();
                }
            }
        } else {
            //新增的时候由系统产生
            bigStyleAnalyseCode = generateBigStyleAnalyseCode(ctStyleCode);
            randomCode = SnowflakeIdUtil.generateId();
        }
        String userCode = dataObj.getString("userCode");

        String requestId = UUIDUtil.uuid32();
        try {
            boolean getLock = false;
            getLock = redisUtil.tryGetDistributedLock("big:" + bigStyleAnalyseCode, requestId, RedisContants.FIVE_MIN_EXPIRE_TIME);
            if (getLock) {
                result.put("bigStyleAnalyseCode", bigStyleAnalyseCode);
                result.put("randomCode", randomCode);
                //大货款式分析基础数据的更新和修改
                addOrUpdateBigStyleAnalyseMaster(bigStyleAnalyseCode, randomCode, dataObj, operation);
                //大阔款式分析的 颜色
                JSONArray skcArray = dataObj.getJSONArray("skc");
                addOrUpdateBigStyleSkc(ctStyleCode, randomCode, skcArray, operation);
                //大阔款式分析的图片
                JSONArray picArray = dataObj.getJSONArray("pictures");
                addOrUpdateBigStylePicture(ctStyleCode, randomCode, picArray, operation);

                String partCrafts = dataObj.getString("partCraftList");
                //工艺部件
                List<BigStyleAnalysePartCraft> partCraftList = JSONObject.parseArray(partCrafts, BigStyleAnalysePartCraft.class);
                Map<String, BigDecimal> timePriceMap = new HashMap<>();
                addOrUpdateBigStyleAnalysePartCraft(dataObj, ctStyleCode, randomCode, partCraftList, operation, timePriceMap);

                BigStyleOperationLog op = new BigStyleOperationLog();
                TimePriceOperationLog timePriceOperationLog = new TimePriceOperationLog();
                try {
                    op.setReceiveData(dataObj.toJSONString());
                    op.setUserCode(userCode);
                    op.setUpdateType("big_style_analyse_master");
                    op.setCode(bigStyleAnalyseCode);
                    op.setCreateTime(new Date());

                    timePriceOperationLog.setBusinessType(BusinessConstants.BusinessType.STYLE_CRAFT);
                    timePriceOperationLog.setCode(bigStyleAnalyseCode);
                    timePriceOperationLog.setStandardPrice(timePriceMap.get("standardPrice"));
                    timePriceOperationLog.setStandardTime(timePriceMap.get("standardTime"));
                    timePriceOperationLog.setUpdateTime(new Date());
                    timePriceOperationLog.setUpdateUser(userCode);

                    taskExecutor.execute(() -> {
                        Map<String, User> userMap = userDao.getUserMap();
                        timePriceOperationLog.setUpdateUserName(userMap.get(userCode).getUserName());
                        timePriceOperationLogDao.insertData(timePriceOperationLog);
                        bigStyleOperationLogDao.insertData(op);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                LOGGER.error("---款式工艺获取分布式锁失败!!!-----------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("款式工艺获取分布式锁失败，" + e.getMessage());
        } finally {
            //一定要释放锁
            redisUtil.releaseDistributedLock("big:" + bigStyleAnalyseCode, requestId);
        }
        return result;
    }

    private void sleepMicroseconds(long timeout) {
        try {
            TimeUnit.MICROSECONDS.sleep(timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加部件工艺详情数据
     */
    private JSONObject addOrUpdateBigStyleAnalysePartCraftDetail(String partCraftMainCode, Long randomCode, List<BigStyleAnalysePartCraftDetail> partCraftDetailList,
                                                                 String operation, String patternSymmetry, Map<String, PatternSymmetry> map, String userCode, String saveType, final Map<String, BigDecimal> minueMap, ConcurrentHashMap<String, BigDecimal> totalMap) {
        JSONObject result = new JSONObject();
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            List<Long> ids = bigStyleAnalysePartCraftDetailDao.getIdsByStyleRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            if (null != ids && ids.size() > 0) {
                bigStyleAnalysePartCraftDetailDao.deleteByStyleIds(ids);
                sleepMicroseconds(1);
            }
        }

        //存储工序的详细数据，包括动作代码，建标，缝边位置等信息
        if (null != partCraftDetailList && partCraftDetailList.size() > 0) {
            BigStyleAnalysePartCraftDetail detail = partCraftDetailList.get(0);
            detail.setStyleRandomCode(randomCode);
            String craftCode = insertSewingCraftData(partCraftMainCode, detail, randomCode, patternSymmetry, map, 0, userCode, operation, saveType, minueMap, totalMap);
            detail.setCraftCode(craftCode);
            partCraftDetailList.subList(1, partCraftDetailList.size()).parallelStream().forEach(x -> {
                x.setStyleRandomCode(randomCode);
                String craftCode1 = insertSewingCraftData(partCraftMainCode, x, randomCode, patternSymmetry, map, 2, userCode, operation, saveType, minueMap, totalMap);
                x.setCraftCode(craftCode1);
            });
        }
        //统计这个部件下面的 工序时间和单价之和
        BigDecimal totalTime = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        if (null != partCraftDetailList && partCraftDetailList.size() > 0) {
            for (BigStyleAnalysePartCraftDetail detail : partCraftDetailList) {
                BigDecimal oldTime = detail.getStandardTime();
                BigDecimal oldPrice = detail.getStandardPrice();
                if (null == oldTime) {
                    oldTime = BigDecimal.ZERO;
                }
                if (null == oldPrice) {
                    oldPrice = BigDecimal.ZERO;
                }
                totalTime = totalTime.add(oldTime).setScale(3, BigDecimal.ROUND_HALF_UP);
                totalPrice = totalPrice.add(oldPrice).setScale(3, BigDecimal.ROUND_HALF_UP);
                //detail.setUpdateUser(userName);
            }
        }

        bigStyleAnalysePartCraftDetailDao.insertPartCraftDetailForeach(partCraftDetailList);
        result.put("time", totalTime);
        result.put("price", totalPrice);
        return result;
    }


    /**
     * 插入详细的工序主数据
     */
    private String insertSewingCraftData(String partCraftMainCode, BigStyleAnalysePartCraftDetail detail, Long randomCode, String patternSymmetry, Map<String, PatternSymmetry> map, int deleteFlag, String userCode, String operation, String saveType, final Map<String, BigDecimal> minueMap, ConcurrentHashMap<String, BigDecimal> totalMap) {
        if (null == detail) {
            return "";
        }
        String result = "";
        String sewingData = detail.getSewingCraftData();
        JSONObject dataObj = JSONObject.parseObject(sewingData);
        try {
            dataObj.put("randomCode", randomCode);
            dataObj.put("userCode", userCode);
            dataObj.put("operation", operation);//操作类型

            BigDecimal standardTime = dataObj.getBigDecimal("standardTime");
            BigDecimal standardPrice = dataObj.getBigDecimal("standardPrice");
            //如果这个数据是从工单工艺同步过来，这标准单价的工资要改成要默认的赢家工厂来算
            if ("order".equals(saveType) && minueMap.size() > 0) {
                if (null == standardTime) {
                    standardTime = BigDecimal.ZERO;
                }
                String craftGradeCode = dataObj.getString("craftGradeCode");
                String key = "9999_" + craftGradeCode;
                if (minueMap.containsKey(key)) {
                    standardPrice = standardTime.multiply(minueMap.get(key));
                }
                if (null == standardPrice) {
                    standardPrice = BigDecimal.ZERO;
                }
                dataObj.put("standardTime", standardTime);
                dataObj.put("standardPrice", standardPrice);
                totalMap.put(dataObj.getString("craftCode"), standardPrice);
            }
            //重新刷新detail里面的标准时间和标准单价，
            detail.setStandardTime(standardTime);
            detail.setStandardPrice(standardPrice);
            //重新计算标准时间和标准单价
            //reCalStandTimeAndPrice(dataObj, patternSymmetry, map);
            JSONObject obj = sewingCraftWarehouseService.addOrUpdataSewingCraft(partCraftMainCode, dataObj, BusinessConstants.Send2Pi.actionType_update, BusinessConstants.TableName.BIG_STYLE_ANALYSE, deleteFlag);
            result = obj.getString("craftCode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 重新计算标准时间和标准单价
     */
    private void reCalStandTimeAndPrice(JSONObject dataObj, String patternSymmetry, Map<String, PatternSymmetry> map) {
        //表面风格补贴 是否参与计算
        String isFabricStyleFix = dataObj.getString("isFabricStyleFix");
        BigDecimal fabricRatio = dataObj.getBigDecimal("fabricRatio");
        if (null == fabricRatio) {
            fabricRatio = BigDecimal.ZERO;
        }
        //时间浮余
        BigDecimal timeSupplement = dataObj.getBigDecimal("timeSupplement");
        if (null == timeSupplement) {
            timeSupplement = BigDecimal.ZERO;
        }
        //表面风格补贴
        BigDecimal fabricStyleFix = BigDecimal.ZERO;
        if ("1".equals(isFabricStyleFix) && StringUtils.isNotEmpty(patternSymmetry) && map.containsKey(patternSymmetry)) {
            fabricStyleFix = map.get(patternSymmetry).getSewingRatio();
        }
        if (null == fabricStyleFix) {
            fabricStyleFix = BigDecimal.ZERO;
        }
        BigDecimal oldTime = dataObj.getBigDecimal("standardTime");
        BigDecimal oldPrice = dataObj.getBigDecimal("standardPrice");
        if (null == oldTime) {
            oldTime = BigDecimal.ZERO;
        }
        if (null == oldPrice) {
            oldPrice = BigDecimal.ZERO;
        }
        //标准时间（分）=标准时间*（1+面料风格补贴+面料系数+时间浮余）
        BigDecimal newTime = oldTime.multiply(BigDecimal.ONE.add(fabricStyleFix).add(fabricRatio).add(timeSupplement).
                setScale(6, BigDecimal.ROUND_HALF_UP)).setScale(3, BigDecimal.ROUND_HALF_UP);
        //标准单价（元）=标准单价*（1+面料风格补贴+面料系数+时间浮余）
        BigDecimal newPrice = oldPrice.multiply(BigDecimal.ONE.add(fabricStyleFix).add(fabricRatio).add(timeSupplement).
                setScale(6, BigDecimal.ROUND_HALF_UP)).setScale(3, BigDecimal.ROUND_HALF_UP);
        dataObj.put("standardTime", newTime.doubleValue());
        dataObj.put("standardPrice", newPrice.doubleValue());
    }

    /**
     * 添加部件工艺数据
     */
    private void addOrUpdateBigStyleAnalysePartCraft(JSONObject dataObj, String ctStyleCode, Long randomCode, List<BigStyleAnalysePartCraft> partCraftList, String operation, Map<String, BigDecimal> timePriceMap) {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            List<Long> ids = bigStyleAnalysePartCraftDao.getIdByStyleRandomCode(randomCode);
            if (null != ids && ids.size() > 0) {
                bigStyleAnalysePartCraftDao.deleteByStyleIds(ids);
                sleepMicroseconds(1);
            }
            //bigStyleAnalysePartCraftDao.deleteByStyleRandomCode(randomCode);
        }
        final String userCode = dataObj.getString("userCode");
        //工单工艺 把工序同步到款式工艺的时候用到这个标识
        String saveType = dataObj.getString("saveType");
        final Map<String, BigDecimal> minueMap = new HashMap<>(0);
        if ("order".equals(saveType)) {
            List<CraftGradeEquipment> list = craftGradeEquipmentDao.getAllCraftGrade();
            if (null != list && list.size() > 0) {
                for (CraftGradeEquipment vo : list) {
                    minueMap.put(vo.getFactoryCode() + "_" + vo.getCraftGradeCode(), vo.getMinuteWage());
                }
            }
        }
        if (null != partCraftList && partCraftList.size() > 0) {
            partCraftList.stream().parallel().forEach(x -> {
                x.setUpdateUser(userCode);
                x.setUpdateTime(new Date());
                x.setStyleRandomCode(randomCode);
            });
            //列表上的一些工序数据，没有工序详情数据，不包含动作代码
            bigStyleAnalysePartCraftDao.insertPartCraftForeach(partCraftList);
            //处理部件工艺详情里面的数据
            final Map<String, PatternSymmetry> map = patternSymmetryDao.getPatternSymmetrysToMap();
            //这里不能用并行流，不然会大概的出现并发冲突的情况
            ConcurrentHashMap<String, BigDecimal> totalMap = new ConcurrentHashMap();
            //统计这个部件下面的 工序时间和单价之和
            BigDecimal totalSewTime = BigDecimal.ZERO;
            BigDecimal totalSewPrice = BigDecimal.ZERO;
            // Map<String, User> userMap = userDao.getUserMap();
            // String userName = userMap.containsKey(userCode) ? userMap.get(userCode).getUserName() : "";
            for (BigStyleAnalysePartCraft partCraft : partCraftList) {
                List<BigStyleAnalysePartCraftDetail> partCraftDetails = partCraft.getPartCraftDetailList();
                if (null != partCraftDetails && partCraftDetails.size() > 0) {
                    JSONObject data = addOrUpdateBigStyleAnalysePartCraftDetail(partCraft.getPartCraftMainCode(), randomCode, partCraftDetails, operation, partCraft.getPatternSymmetry(), map, userCode, saveType, minueMap, totalMap);
                    try {
                        BigDecimal oldTime = data.getBigDecimal("time");
                        BigDecimal oldPrice = data.getBigDecimal("price");
                        if (null == oldTime) {
                            oldTime = BigDecimal.ZERO;
                        }
                        if (null == oldPrice) {
                            oldPrice = BigDecimal.ZERO;
                        }
                        totalSewTime = totalSewTime.add(oldTime).setScale(3, BigDecimal.ROUND_HALF_UP);
                        totalSewPrice = totalSewPrice.add(oldPrice).setScale(3, BigDecimal.ROUND_HALF_UP);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    //这个部件工艺下面的工序全部删除的情况
                    deleteWhenPartCraftDetailIsNull(randomCode, partCraft.getPartCraftMainCode());
                }
            }
            //更新时间和单价，因为老是概率性的出现前端传过来的总时间和单价 跟工序累加的不一致
            try {
                BigDecimal standardTime = dataObj.getBigDecimal("standardTime");
                BigDecimal standardPrice = dataObj.getBigDecimal("standardPrice");
                boolean flag = false;
                if (null != standardTime && standardTime.compareTo(totalSewTime) != 0) {
                    flag = true;
                    LOGGER.error("time is not same :old=" + standardTime.doubleValue() + ",new=" + totalSewTime.doubleValue());
                }
                if (null != standardPrice && standardPrice.compareTo(totalSewPrice) != 0) {
                    flag = true;
                    LOGGER.error("price is not same :old=" + standardPrice.doubleValue() + ",new=" + totalSewPrice.doubleValue());
                }
                if (flag) {
                    LOGGER.error("update time and price,standardPrice = " + totalSewPrice.doubleValue() + ",standardTime=" + totalSewTime.doubleValue());
                    Map<String, Object> param = new HashMap<>();
                    param.put("standardPrice", totalSewPrice);
                    param.put("standardTime", totalSewTime);
                    param.put("randomCode", randomCode);
                    bigStyleAnalyseMasterDao.updatePriceAndTime(param);
                }
                timePriceMap.put("standardPrice", totalSewPrice);
                timePriceMap.put("standardTime", totalSewTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* partCraftList.stream().forEach(partCraft -> {
                List<BigStyleAnalysePartCraftDetail> partCraftDetails = partCraft.getPartCraftDetailList();
                if (null != partCraftDetails && partCraftDetails.size() > 0) {
                    JSONObject data = addOrUpdateBigStyleAnalysePartCraftDetail(partCraft.getPartCraftMainCode(), randomCode, partCraftDetails, operation, partCraft.getPatternSymmetry(), map, userCode, saveType, minueMap, totalMap);
                } else {
                    //这个部件工艺下面的工序全部删除的情况
                    deleteWhenPartCraftDetailIsNull(randomCode, partCraft.getPartCraftMainCode());
                }
            });*/
            //工单工艺同步款式工艺的时候
            if ("order".equals(saveType) && totalMap.size() > 0) {
                //合计所有的时间
                BigDecimal totalPrice = BigDecimal.ZERO.setScale(3, BigDecimal.ROUND_HALF_UP);
                Iterator<String> it = totalMap.keySet().iterator();
                while (it.hasNext()) {
                    BigDecimal newValue = totalMap.get(it.next());
                    if (null == newValue) {
                        newValue = BigDecimal.ZERO.setScale(3, BigDecimal.ROUND_HALF_UP);
                        ;
                    }
                    totalPrice = totalPrice.add(newValue).setScale(3, BigDecimal.ROUND_HALF_UP);
                    ;
                }
                //然后再更新
                try {
                    Map<String, Object> param = new HashMap<>();
                    param.put("standardPrice", totalPrice);
                    param.put("randomCode", randomCode);
                    bigStyleAnalyseMasterDao.updatePriceAndTime(param);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
           /* for (int i = 0; i < partCraftList.size(); i++) {
                BigStyleAnalysePartCraft partCraft = partCraftList.get(i);
                List<BigStyleAnalysePartCraftDetail> partCraftDetails = partCraft.getPartCraftDetailList();
                if (null != partCraftDetails && partCraftDetails.size() > 0) {
                    addOrUpdateBigStyleAnalysePartCraftDetail(partCraft.getPartCraftMainCode(), randomCode, partCraftDetails, operation, partCraft.getPatternSymmetry(), map,userCode);
                } else {
                    //这个部件工艺下面的工序全部删除的情况
                    deleteWhenPartCraftDetailIsNull(randomCode, partCraft.getPartCraftMainCode());
                }
            }*/
        }
    }

    private void deleteWhenPartCraftDetailIsNull(Long randomCode, String partCraftMainCode) {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = bigStyleAnalysePartCraftDetailDao.getIdsByStyleRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigStyleAnalysePartCraftDetailDao.deleteByStyleIds(ids);
                    sleepMicroseconds(1);
                }
                //bigStyleAnalysePartCraftDetailDao.deleteByStyleRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = styleSewingCraftActionDao.getIdsBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftActionDao.deleteDataByids(ids);
                    sleepMicroseconds(1);
                }
                //styleSewingCraftActionDao.deleteDataBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = styleSewingCraftStdDao.getIdByBysewingRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftStdDao.deleteDataByIds(ids);
                    sleepMicroseconds(1);
                }
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = styleSewingCraftPartPositionDao.getIDsBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftPartPositionDao.deleteDataByids(ids);
                    sleepMicroseconds(1);
                }
                //styleSewingCraftPartPositionDao.deleteDataBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = styleSewingCraftWarehouseDao.getIdsByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftWarehouseDao.deleteDataByIds(ids);
                    sleepMicroseconds(1);
                }
                //styleSewingCraftWarehouseDao.deleteDataByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = styleSewingCraftWarehouseWorkplaceDao.getIdByBysewingRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftWarehouseWorkplaceDao.deleteWorkplaceByIds(ids);
                    sleepMicroseconds(1);
                }
                //styleSewingCraftWarehouseWorkplaceDao.deleteWorkplaceBysewingRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
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

    /**
     * 大货款式分析---图片
     */
    private void addOrUpdateBigStylePicture(String ctStyleCode, Long randomCode, JSONArray picArray, String operation) {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            //bigStyleAnalyseMasterPictureDao.deleteByStyleRandomCode(randomCode);
            List<BigStyleAnalyseMasterPicture> list = bigStyleAnalyseMasterPictureDao.getDataByStyleRandomCode(randomCode);
            if (null != list && list.size() > 0) {
                for (BigStyleAnalyseMasterPicture pic : list) {
                    if (null != pic && pic.getId() != null) {
                        bigStyleAnalyseMasterPictureDao.deleteByID(pic.getId());
                        sleepMicroseconds(1);
                    }
                }
            }
        }
        List<BigStyleAnalyseMasterPicture> picList = new ArrayList<>();
        if (null == picArray || picArray.size() == 0) {
            return;
        }
        for (int i = 0; i < picArray.size(); i++) {
            JSONObject obj = picArray.getJSONObject(i);
            BigStyleAnalyseMasterPicture pic = new BigStyleAnalyseMasterPicture();
            pic.setCtStyleCode(ctStyleCode);
            pic.setStyleRandomCode(randomCode);
            pic.setPicUrl(obj.getString("picUrl"));
            picList.add(pic);
        }
        bigStyleAnalyseMasterPictureDao.insertPicForeach(picList);
    }

    /**
     * 大货款式分析---款色
     */
    private void addOrUpdateBigStyleSkc(String ctStyleCode, Long randomCode, JSONArray skcArray, String operation) {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            //bigStyleAnalyseMasterSKCDao.deleteByStyleRandomCode(randomCode);
            List<BigStyleAnalyseSkc> list = bigStyleAnalyseMasterSKCDao.selectDataByStyleRandomCode(randomCode);
            if (null != list && list.size() > 0) {
                for (BigStyleAnalyseSkc skc : list) {
                    if (skc != null && skc.getId() != null) {
                        bigStyleAnalyseMasterSKCDao.deleteByID(skc.getId());
                        sleepMicroseconds(1);
                    }
                }
            }
        }
        List<BigStyleMasterDataSKC> skcList = new ArrayList<>();
        if (null == skcArray || skcArray.size() == 0) {
            return;
        }
        for (int i = 0; i < skcArray.size(); i++) {
            JSONObject obj = skcArray.getJSONObject(i);
            BigStyleMasterDataSKC skc = new BigStyleMasterDataSKC();
            skc.setCtStyleCode(ctStyleCode);
            skc.setSkcColorCode(obj.getString("skcColorCode"));
            skc.setSkcColorName(obj.getString("skcColorName"));
            skc.setStyleRandomCode(randomCode);
            skc.setStyleSKCcode(obj.getString("styleSKCcode"));
            skcList.add(skc);
        }
        bigStyleAnalyseMasterSKCDao.insertSKCForeach(skcList);
    }

    /**
     * 大货款式分析基础数据的更新和修改
     */
    private void addOrUpdateBigStyleAnalyseMaster(String bigStyleAnalyseCode, Long randomCode, JSONObject dataObj, String operation) {
        //登录用户
        Map<String, Object> param = new HashMap<>();
        param.put("bigStyleAnalyseCode", bigStyleAnalyseCode);
        param.put("randomCode", randomCode);
        String userCode = dataObj.getString("userCode");
        param.put("createUser", userCode);
        param.put("createTime", new Date());
        //登录用户
        String status = dataObj.getString("status");
        param.put("status", status);
        if (BusinessConstants.Status.DRAFT_STATUS.equals(status)) {
            param.put("releaseTime", null);
            param.put("releaseUser", null);
        }
        //款式编码
        String styleName = dataObj.getString("styleName");
        param.put("styleName", styleName);
        try {
            Boolean sortedByNumber = dataObj.getBoolean("sortedByNumber");
            param.put("sortedByNumber", sortedByNumber);
            Boolean isCheckCraftCode = dataObj.getBoolean("isCheckCraftCode");
            param.put("isCheckCraftCode", isCheckCraftCode);
        } catch (Exception e) {
            e.printStackTrace();

        }
        //款式编码
        String ctStyleCode = dataObj.getString("ctStyleCode");
        param.put("ctStyleCode", ctStyleCode);
        //款式描述
        String styleDesc = dataObj.getString("styleDesc");
        param.put("styleDesc", styleDesc);
        //客户--子品类
        String subBrand = dataObj.getString("subBrand");
        param.put("subBrand", subBrand);
        //服装品类
        String clothesCategoryName = dataObj.getString("clothesCategoryName");
        param.put("clothesCategoryName", clothesCategoryName);
        //服装品类
        String clothesCategoryCode = dataObj.getString("clothesCategoryCode");
        param.put("clothesCategoryCode", clothesCategoryCode);
        //工艺主框架名称
        String mainFrameName = dataObj.getString("mainFrameName");
        param.put("mainFrameName", mainFrameName);
        //面料分值
        String materialGrade = dataObj.getString("materialGrade");
        param.put("materialGrade", materialGrade);
        //工艺主框架编码
        String mainFrameCode = dataObj.getString("mainFrameCode");
        param.put("mainFrameCode", mainFrameCode);
        String craftCategoryName = dataObj.getString("craftCategoryName");
        param.put("craftCategoryName", craftCategoryName);
        //工艺品类
        String craftCategoryCode = dataObj.getString("craftCategoryCode");
        param.put("craftCategoryCode", craftCategoryCode);

        //上市日期
        String forSalesTime = dataObj.getString("forSalesTime");
        param.put("forSalesTime", forSalesTime);

        //包装方式
        String packingMethodCoat = dataObj.getString("packingMethodCoat");
        param.put("packingMethodCoat", packingMethodCoat);

        //标准时间
        String standardTime = dataObj.getString("standardTime");
        if (StringUtils.isNotEmpty(standardTime)) {
            param.put("standardTime", standardTime);
        }
        //面料分值
        String fabricFraction = dataObj.getString("fabricFraction");
        if (StringUtils.isNotEmpty(fabricFraction)) {
            param.put("fabricFraction", fabricFraction);
        }

        //标准单价
        String standardPrice = dataObj.getString("standardPrice");
        if (StringUtils.isNotEmpty(standardPrice)) {
            param.put("standardPrice", standardPrice);
        }
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            LOGGER.info("---------updateSewingCraftWarehouse--------------");
            param.put("updateUser", userCode);
            param.put("updateTime", new Date());
            //param.put("createUser", dataObj.getString("createUser"));


            //bigStyleAnalyseMasterDao.updateBigStyleAnalyseMaster(param);
        }
        Long id = dataObj.getLong("id");
        BigStyleAnalyseMaster mas = bigStyleAnalyseMasterDao.getBigStyleByCode(bigStyleAnalyseCode);
        if (null != mas) {
            id = mas.getId();
            param.put("createUser", mas.getCreateUser());
            param.put("createTime", mas.getCreateTime());
        }
        if (null != id) {
            LOGGER.info("---------delete from big_style_analyse_master id:" + id);
            Integer count = bigStyleAnalyseMasterDao.deleteByID(id);
            sleepMicroseconds(1);
            if (count != null && count > 0) {
                bigStyleAnalyseMasterDao.addBigStyleAnalyseMaster(param);
            }
        } else {
            bigStyleAnalyseMasterDao.addBigStyleAnalyseMaster(param);
        }


        LOGGER.info("参数信息是:" + JSONObject.toJSONString(param));
        taskExecutor.execute(() -> {
            try {
                //更新大货款式记录的分科时间
                Date createTime = (Date) param.get("createTime");
                Date releaseTime = (Date) param.get("releaseTime");

                bigStyleNodeRecordService.updateStyleNodeBranchInfo(ctStyleCode, createTime, null, userCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 款式工艺路线自动生成编码规则：款号（7位）+2位流水号（每个款的流水号都从01开始），如LV0151601。只读，不可修改。
     */
    public String generateBigStyleAnalyseCode(String styleCode) throws Exception {
        if (StringUtils.isEmpty(styleCode)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        //款号（7位）+2位流水号（
        sb.append(styleCode + CreateSerialUtil.createSerial(redisUtil, redisTemplate, "BigStyleAnalyse", styleCode, 2, 1));
        LOGGER.info("生成的款式分析编码是:" + sb.toString());
        return sb.toString();
    }

    public Map<String, BigStyleAnalyseMaster> findAllReleaseUser() {
        List<BigStyleAnalyseMaster> allReleaseUser = bigStyleAnalyseMasterDao.findAllReleaseUser();
        final Map<String, User> map = userDao.getUserMap();
        Map<String, BigStyleAnalyseMaster> releaseUserMap = new HashMap<>();
        if (allReleaseUser.size() > 0 && null != allReleaseUser) {
            allReleaseUser.stream().forEach(x -> {
                if (map.containsKey(x.getUpdateUser())) {
                    x.setUpdateUserName(map.get(x.getUpdateUser()).getUserName());
                } else {
                    x.setUpdateUserName("");
                }
                if (map.containsKey(x.getReleaseUser())) {
                    x.setReleaseUserName(map.get(x.getReleaseUser()).getUserName());
                } else {
                    x.setReleaseUserName("");
                }
                releaseUserMap.put(x.getCtStyleCode(), x);
            });
        }
        return releaseUserMap;
    }


    public int insertByHistoryBigStyle(Map<String,Object> param) throws Exception {
        String styleAnalyseCode = param.getOrDefault("styleAnalyseCode","").toString();
        if(StringUtils.isBlank(styleAnalyseCode)) {
            param.put("err", "款号不能为空");
            return -1;
        }
        BigStyleAnalyseMaster historyStyle = historyBigStyleAnalyseMasterDao.selectByStyleAnalyseCode(styleAnalyseCode);
        if(null == historyStyle) {
            param.put("err", "未找到款号");
            return -1;
        }
        param.put("styleRandomCode", historyStyle.getRandomCode());
        Long styleRandomCodeNew = SnowflakeIdUtil.generateId();
        param.put("styleRandomCodeNew", styleRandomCodeNew);
        String bigStyleAnalyseCodeNew = generateBigStyleAnalyseCode(historyStyle.getBigStyleAnalyseCode());
        param.put("bigStyleAnalyseCodeNew", bigStyleAnalyseCodeNew);

        int ret = bigStyleAnalyseMasterDao.insertByHistoryBigStyle(param);
        if(ret == 0) {
            param.put("err", "保存失败");
            return -1;
        }

        List<String> urls = autoUploadVideoSchedule.uploadBigStyleImage(styleAnalyseCode);
        if(ObjectUtils.isNotEmptyList(urls)) {
            List<BigStyleAnalyseMasterPicture> picList =  new ArrayList<>();
            urls.stream().forEach(x->{
                BigStyleAnalyseMasterPicture stylePic = new BigStyleAnalyseMasterPicture();
                stylePic.setCtStyleCode(styleAnalyseCode);
                stylePic.setStyleRandomCode(styleRandomCodeNew);
                stylePic.setPicUrl(x);
                picList.add(stylePic);
            });
            bigStyleAnalyseMasterPictureDao.insertPicForeach(picList);
        }


        BigStyleAnalyseMaster bigStyle = searchFromBigStyleAnalyseByRandomCode(styleRandomCodeNew);
        param.put("bigStyle", bigStyle);
        return ret;
    }



}
