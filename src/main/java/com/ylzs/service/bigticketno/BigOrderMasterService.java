package com.ylzs.service.bigticketno;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.*;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.BigStyleOperationLogDao;
import com.ylzs.dao.bigstylerecord.TimePriceOperationLogDao;
import com.ylzs.dao.bigticketno.*;
import com.ylzs.dao.factory.ProductionGroupDao;
import com.ylzs.dao.staticdata.PatternSymmetryDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.dao.timeparam.FabricGradeDao;
import com.ylzs.entity.bigstylecraft.*;
import com.ylzs.entity.bigstylerecord.TimePriceOperationLog;
import com.ylzs.entity.bigticketno.FmsTicketNo;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.staticdata.PatternSymmetry;
import com.ylzs.entity.system.User;
import com.ylzs.service.interfaceOutput.INewCraftService;
import com.ylzs.service.interfaceOutput.IOperationPathService;
import com.ylzs.service.interfaceOutput.ISectionSMVService;
import com.ylzs.service.pi.ISendPiService;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xuwei
 * @create 2020-03-19 15:46
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BigOrderMasterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BigOrderMasterService.class);
    @Resource
    private BigOrderMasterDao bigOrderMasterDao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private BigOrderMasterSKCDao bigOrderMasterSKCDao;

    @Resource
    private BigOrderMasterPictureDao bigOrderMasterPictureDao;

    @Resource
    private BigOrderPartCraftDao bigOrderPartCraftDao;

    @Resource
    private BigOrderPartCraftDetailDao bigOrderPartCraftDetailDao;

    @Resource
    private SewingCraftWarehouseService sewingCraftWarehouseService;

    @Resource
    private FabricGradeDao fabricGradeDao;

    @Resource
    private PatternSymmetryDao patternSymmetryDao;

    @Resource
    private UserDao userDao;

    @Resource
    private BigOrderSewingCraftActionDao bigOrderSewingCraftActionDao;

    @Resource
    private BigOrderSewingCraftStdDao bigOrderSewingCraftStdDao;

    @Resource
    private BigOrderSewingCraftPartPositionDao bigOrderSewingCraftPartPositionDao;

    @Resource
    private BigOrderSewingCraftWarehouseDao bigOrderSewingCraftWarehouseDao;

    @Resource
    private BigOrderSewingCraftWarehouseWorkplaceDao bigOrderSewingCraftWarehouseWorkplaceDao;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private INewCraftService newCraftService;
    @Resource
    private IOperationPathService operationPathService;
    @Resource
    private ISectionSMVService sectionSMVService;

    @Resource
    private BigStyleOperationLogDao bigStyleOperationLogDao;

    @Resource
    private ProductionGroupDao productionGroupDao;

    @Resource
    private TimePriceOperationLogDao timePriceOperationLogDao;

    @Resource
    private FmsTicketNoService fmsTicketNoService;

    @Resource
    private ISendPiService sendPiService;

    @Resource
    private DictionaryDao dictionaryDao;

    public void deleteByID(Long id) {
        bigOrderMasterDao.deleteByID(id);
    }

    public List<BigStyleAnalyseMaster> getDataByPager(Map<String, Object> param, final String productionCategoryName, final Map<String, ProductionGroup> groupMap) {
        List<BigStyleAnalyseMaster> masters1 = bigOrderMasterDao.getDataByPager(param);
        List<BigStyleAnalyseMaster> masters = masters1;
        if (StringUtils.isNotEmpty(productionCategoryName)) {
            masters = masters1.stream().filter(x -> {
                if (groupMap.containsKey(x.getProductionCategory())
                        && groupMap.get(x.getProductionCategory()).getProductionGroupName() != null
                        && groupMap.get(x.getProductionCategory()).getProductionGroupName().indexOf(productionCategoryName) != -1) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (null == masters || masters.size() == 0) {
            return Collections.emptyList();
        }
        final Map<String, User> map = userDao.getUserMap();

        masters.stream().parallel().forEach(x -> {
            //查询生产组别
            if (groupMap.containsKey(x.getProductionCategory())) {
                x.setProductionCategoryName(groupMap.get(x.getProductionCategory()).getProductionGroupName());
            }
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

            //处理适应性编码
//            if(BusinessConstants.AdaptCode.WISDOM_LINE.equals(x.getAdaptCode())){
//                x.setAdaptCode(BusinessConstants.AdaptCode.WISDOM_LINE_TEN_NAME);
//            }else{
//                x.setAdaptCode(BusinessConstants.AdaptCode.NONE_WISDOM_LINE_NAME);
//            }
        });
        return masters;
    }

    public BigStyleAnalyseMaster searchFromBigStyleAnalyseByRandomCode(Long randomCode) {
        BigStyleAnalyseMaster data = bigOrderMasterDao.searchFromBigStyleAnalyseByRandomCode(randomCode);
        if (null != data) {
            Map<String, User> map = userDao.getUserMap();
            final Map<String, ProductionGroup> groupMap = productionGroupDao.getAllToMap();
            //查询生产组别
            if (groupMap.containsKey(data.getProductionCategory())) {
                data.setProductionCategoryName(groupMap.get(data.getProductionCategory()).getProductionGroupName());
            }
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

    private void getTimePriceOperationLogList(BigStyleAnalyseMaster vo) {
        List<TimePriceOperationLog> logs = timePriceOperationLogDao.getDataByBusinessTypeAndCode("time_price_order_operation_log", vo.getProductionTicketNo(), BusinessConstants.BusinessType.WORKNO_CRAFT);
        vo.setTimePriceOperationLogList(logs);
    }

    /**
     * get公众的标准时间和标准单价 汇总数据
     */
    private void getWorkTypesData(BigStyleAnalyseMaster bigStyleAnalyseMaster) {
        if (null == bigStyleAnalyseMaster) {
            return;
        }
        List<CraftVO> craftVOList = bigOrderSewingCraftWarehouseDao.getDataForExcelReport(bigStyleAnalyseMaster.getRandomCode());
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

    public BigStyleAnalyseMaster searchFromBigStyleAnalyseByRandomCodeWithOutPartDeatil(Long randomCode) {
        BigStyleAnalyseMaster data = bigOrderMasterDao.searchFromBigStyleAnalyseByRandomCode(randomCode);
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
        List<BigStyleAnalyseSkc> data = bigOrderMasterSKCDao.selectDataByStyleRandomCode(vo.getRandomCode());
        if (null != data && data.size() > 0) {
            vo.setSkc(data);
        }
    }

    /**
     * 查询部件工艺以及详情数据
     */
    private void getPartAndDetailByStyleRandomCode(BigStyleAnalyseMaster vo) {
        List<BigStyleAnalysePartCraft> partCraftList = bigOrderPartCraftDao.getPartAndDetailByStyleRandomCode(vo.getRandomCode());
        if (null != partCraftList && partCraftList.size() > 0) {
            vo.setPartCraftList(partCraftList);
        }
    }

    /**
     * 查询款的图片，当款的图片有多个的时候，级联查询会出现多条记录，则会导致翻页总数不对，所以只能
     * 单个款式工艺去查询图片
     */
    private void searchPictures(BigStyleAnalyseMaster vo) {
        List<String> pictures = bigOrderMasterPictureDao.getUrlByStyleRandomCode(vo.getRandomCode());
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

    public boolean updateStatus(Integer status, Long randomCode, String userCode) {
        boolean flag = false;
        try {
            bigOrderMasterDao.updateStatus(status, randomCode, userCode, new Date());
            flag = true;

            if (BusinessConstants.Status.PUBLISHED_STATUS.equals(status)) {
                taskExecutor.execute(() -> {
                    BigStyleAnalyseMaster master = bigOrderMasterDao.getBigStyleAnalyseMasterByRandomCode(randomCode);
                    if (null != master) {
                        //发送工单实际工段工时
                        sectionSMVService.sendBigOrderActualSectionSMV(master.getProductionTicketNo(), true);
                        //发送工单新工序
                        newCraftService.sendBigOrderNewCraft(master.getProductionTicketNo(), true);
                        //发送智化标识
                        sendBigOrderThinkFlag(master.getProductionTicketNo(), true);
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
        List<BigStyleAnalyseMaster> masterList = bigOrderMasterDao.searchFromBigStyleAnalyse(map);
        if (null == masterList || masterList.size() == 0) {
            return Collections.emptyList();
        }
        masterList.stream().parallel().forEach(x -> {
            List<BigStyleAnalysePartCraft> partCrafts = bigOrderPartCraftDao.getPartAndDetailByStyleRandomCode(x.getRandomCode());
            x.setPartCraftList(partCrafts);
            searchPictures(x);
        });
        return masterList;
    }

    public JSONObject addOrUpdate(JSONObject dataObj, String operation) throws Exception {
        String productionTicketNo = dataObj.getString("productionTicketNo");
        try {
            //生产工单号
            Map<String, Object> map = new HashMap<>();
            map.put("productionTicketNo", productionTicketNo);
            if (BusinessConstants.Send2Pi.actionType_create.equals(operation)) {
                List<BigStyleAnalyseMaster> masters = bigOrderMasterDao.searchFromBigStyleAnalyse(map);
                if (null != masters && masters.size() > 0) {
                    for (BigStyleAnalyseMaster mas : masters) {
                        //bigOrderMasterDao.deleteByRandomCode(mas.getRandomCode());
                        if (mas.getId() != null) {
                            bigOrderMasterDao.deleteByID(mas.getId());
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String bigStyleAnalyseCode = "";
        Long randomCode = null;
        JSONObject result = new JSONObject();
        //款式编码
        String ctStyleCode = dataObj.getString("ctStyleCode");
        bigStyleAnalyseCode = dataObj.getString("bigStyleAnalyseCode");
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            //修改的时候由前端页面传递过来
            randomCode = dataObj.getLong("randomCode");
        } else {
            //新增的时候由系统产生
            //bigStyleAnalyseCode = generateBigStyleAnalyseCode(ctStyleCode);
            randomCode = SnowflakeIdUtil.generateId();
        }
        String requestId = UUIDUtil.uuid32();
        String userCode = dataObj.getString("userCode");
        try {
            boolean getLock = false;
            getLock = redisUtil.tryGetDistributedLock("order:" + productionTicketNo + bigStyleAnalyseCode, requestId, RedisContants.FIVE_MIN_EXPIRE_TIME);
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
                    op.setUpdateType("big_order_master");
                    op.setCode(productionTicketNo);
                    op.setCreateTime(new Date());

                    timePriceOperationLog.setBusinessType(BusinessConstants.BusinessType.WORKNO_CRAFT);
                    timePriceOperationLog.setCode(productionTicketNo);
                    timePriceOperationLog.setStandardPrice(timePriceMap.get("standardPrice"));
                    timePriceOperationLog.setStandardTime(timePriceMap.get("standardTime"));
                    timePriceOperationLog.setUpdateTime(new Date());
                    timePriceOperationLog.setUpdateUser(userCode);

                    taskExecutor.execute(() -> {
                        Map<String, User> userMap = userDao.getUserMap();
                        timePriceOperationLog.setUpdateUserName(userMap.get(userCode).getUserName());
                        timePriceOperationLogDao.insertOrderData(timePriceOperationLog);
                        bigStyleOperationLogDao.insertData(op);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                LOGGER.error("---工单工艺获取分布式锁失败!!!-----------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("工单工艺获取分布式锁失败，" + e.getMessage());
        } finally {
            //一定要释放锁
            redisUtil.releaseDistributedLock("order:" + productionTicketNo + bigStyleAnalyseCode, requestId);
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
                                                                 String operation, String patternSymmetry, Map<String, PatternSymmetry> map, String userCode) {
        JSONObject result = new JSONObject();
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            List<Long> ids = bigOrderPartCraftDetailDao.getIdsByStyleRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            if (null != ids && ids.size() > 0) {
                bigOrderPartCraftDetailDao.deleteByStyleIds(ids);
                sleepMicroseconds(1);
            }
            //bigStyleAnalysePartCraftDetailDao.deleteByStyleRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
        }
        //存储工序的详细数据，包括动作代码，建标，缝边位置等信息
        if (null != partCraftDetailList && partCraftDetailList.size() > 0) {

            //partCraftDetailList.stream().parallel().forEach(detail -> {
           /* partCraftDetailList.stream().forEach(detail -> {
                detail.setStyleRandomCode(randomCode);
                insertSewingCraftData(detail, randomCode, patternSymmetry, map);
            });*/
            BigStyleAnalysePartCraftDetail detail = partCraftDetailList.get(0);
            detail.setStyleRandomCode(randomCode);
            String craftCode = insertSewingCraftData(partCraftMainCode, detail, randomCode, patternSymmetry, map, 0, userCode, operation);
            detail.setCraftCode(craftCode);
            partCraftDetailList.subList(1, partCraftDetailList.size()).parallelStream().forEach(x -> {
                x.setStyleRandomCode(randomCode);
                String craftCode1 = insertSewingCraftData(partCraftMainCode, x, randomCode, patternSymmetry, map, 2, userCode, operation);
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
        bigOrderPartCraftDetailDao.insertPartCraftDetailForeach(partCraftDetailList);
        result.put("time", totalTime);
        result.put("price", totalPrice);
        return result;
    }


    /**
     * 插入详细的工序主数据
     */
    private String insertSewingCraftData(String partCraftMainCode, BigStyleAnalysePartCraftDetail detail, Long randomCode, String patternSymmetry, Map<String, PatternSymmetry> map, int deleteFlag, String userCode, String operation) {
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
            //重新刷新detail里面的标准时间和标准单价，
            BigDecimal standardTime = dataObj.getBigDecimal("standardTime");
            BigDecimal standardPrice = dataObj.getBigDecimal("standardPrice");
            detail.setStandardTime(standardTime);
            detail.setStandardPrice(standardPrice);
            //重新计算标准时间和标准单价
            //reCalStandTimeAndPrice(dataObj, patternSymmetry, map);
            JSONObject obj = sewingCraftWarehouseService.addOrUpdataSewingCraft(partCraftMainCode, dataObj, BusinessConstants.Send2Pi.actionType_update, BusinessConstants.TableName.BIG_ORDER, deleteFlag);
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
            List<Long> ids = bigOrderPartCraftDao.getIdByStyleRandomCode(randomCode);
            if (null != ids && ids.size() > 0) {
                bigOrderPartCraftDao.deleteByStyleIds(ids);
                sleepMicroseconds(1);
            }
            //bigStyleAnalysePartCraftDao.deleteByStyleRandomCode(randomCode);
        }
        final String userCode = dataObj.getString("userCode");
        if (null != partCraftList && partCraftList.size() > 0) {
            partCraftList.stream().parallel().forEach(x -> {
                x.setUpdateUser(userCode);
                x.setUpdateTime(new Date());
                x.setStyleRandomCode(randomCode);
            });
            //列表上的一些工序数据，没有工序详情数据，不包含动作代码
            bigOrderPartCraftDao.insertPartCraftForeach(partCraftList);
            //处理部件工艺详情里面的数据
            final Map<String, PatternSymmetry> map = patternSymmetryDao.getPatternSymmetrysToMap();
            //这里不能用并行流，不然会大概的出现并发冲突的情况
            //统计这个部件下面的 工序时间和单价之和
            BigDecimal totalSewTime = BigDecimal.ZERO;
            BigDecimal totalSewPrice = BigDecimal.ZERO;
            //Map<String, User> userMap = userDao.getUserMap();
            //String userName = userMap.containsKey(userCode) ? userMap.get(userCode).getUserName() : "";
            for (BigStyleAnalysePartCraft partCraft : partCraftList) {
                List<BigStyleAnalysePartCraftDetail> partCraftDetails = partCraft.getPartCraftDetailList();
                if (null != partCraftDetails && partCraftDetails.size() > 0) {
                    JSONObject data = addOrUpdateBigStyleAnalysePartCraftDetail(partCraft.getPartCraftMainCode(), randomCode, partCraftDetails, operation, partCraft.getPatternSymmetry(), map, userCode);
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
            timePriceMap.put("standardPrice", totalSewPrice);
            timePriceMap.put("standardTime", totalSewTime);
        }
    }

    private void deleteWhenPartCraftDetailIsNull(Long randomCode, String partCraftMainCode) {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = bigOrderPartCraftDetailDao.getIdsByStyleRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderPartCraftDetailDao.deleteByStyleIds(ids);
                    sleepMicroseconds(1);
                }
                //bigStyleAnalysePartCraftDetailDao.deleteByStyleRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = bigOrderSewingCraftActionDao.getIdsBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftActionDao.deleteDataByids(ids);
                }
                //styleSewingCraftActionDao.deleteDataBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = bigOrderSewingCraftStdDao.getIdByBysewingRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftStdDao.deleteDataByIds(ids);
                }
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = bigOrderSewingCraftPartPositionDao.getIDsBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftPartPositionDao.deleteDataByids(ids);
                }
                //styleSewingCraftPartPositionDao.deleteDataBySewingCraftRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = bigOrderSewingCraftWarehouseDao.getIdsByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftWarehouseDao.deleteDataByIds(ids);
                }
                //styleSewingCraftWarehouseDao.deleteDataByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } finally {
                countDownLatch.countDown();
            }
        });
        taskExecutor.execute(() -> {
            try {
                List<Long> ids = bigOrderSewingCraftWarehouseWorkplaceDao.getIdByBysewingRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftWarehouseWorkplaceDao.deleteWorkplaceByIds(ids);
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
            // bigOrderMasterPictureDao.deleteByStyleRandomCode(randomCode);
            List<BigStyleAnalyseMasterPicture> list = bigOrderMasterPictureDao.getDataByStyleRandomCode(randomCode);
            if (null != list && list.size() > 0) {
                for (BigStyleAnalyseMasterPicture pic : list) {
                    if (null != pic && pic.getId() != null) {
                        bigOrderMasterPictureDao.deleteByID(pic.getId());
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
        bigOrderMasterPictureDao.insertPicForeach(picList);
    }

    /**
     * 大货款式分析---款色
     */
    private void addOrUpdateBigStyleSkc(String ctStyleCode, Long randomCode, JSONArray skcArray, String operation) {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            //bigOrderMasterSKCDao.deleteByStyleRandomCode(randomCode);
            List<BigStyleAnalyseSkc> list = bigOrderMasterSKCDao.selectDataByStyleRandomCode(randomCode);
            if (null != list && list.size() > 0) {
                for (BigStyleAnalyseSkc skc : list) {
                    if (null != skc && skc.getId() != null) {
                        bigOrderMasterSKCDao.deleteByID(skc.getId());
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
        bigOrderMasterSKCDao.insertSKCForeach(skcList);
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

        //生产工单号
        String productionTicketNo = dataObj.getString("productionTicketNo");
        param.put("productionTicketNo", productionTicketNo);
        //工厂编码
        String factoryCode = dataObj.getString("factoryCode");
        param.put("factoryCode", factoryCode);
        //生产组别
        String productionCategory = dataObj.getString("productionCategory");
        param.put("productionCategory", productionCategory);

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

        //适应编码
        String adaptCode = dataObj.getString("adaptCode");
        param.put("adaptCode", adaptCode);

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
            param.put("createUser", dataObj.getString("createUser"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if (StringUtils.isNotEmpty(dataObj.getString("createTime"))) {
                    param.put("createTime", sdf.parse(dataObj.getString("createTime")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //bigOrderMasterDao.updateBigStyleAnalyseMaster(param);
        }
        Long id = dataObj.getLong("id");
        if (null != id) {
            LOGGER.info("---------delete from big_order_master id:" + id);
            Map<String, Object> noParam = new HashMap<>();
            noParam.put("productionTicketNo", productionTicketNo);
            List<BigStyleAnalyseMaster> list = bigOrderMasterDao.searchFromBigStyleAnalyse(noParam);
            if (list != null && list.size() > 0) {
                Integer count = bigOrderMasterDao.deleteByID(list.get(0).getId());
                sleepMicroseconds(1);
                if (null != count && count > 0) {
                    bigOrderMasterDao.addBigStyleAnalyseMaster(param);
                }
            } else {
                bigOrderMasterDao.addBigStyleAnalyseMaster(param);
            }

        } else {
            bigOrderMasterDao.addBigStyleAnalyseMaster(param);
        }


        LOGGER.info("参数信息是:" + JSONObject.toJSONString(param));

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


    public String sendBigOrderThinkFlag(String productionTicketNo, boolean isSend) {
        HashMap param = new HashMap();
        param.put("productionTicketNo", productionTicketNo);
        List<BigStyleAnalyseMaster> bigOrders = bigOrderMasterDao.searchFromBigStyleAnalyse(param);
        if (ObjectUtils.isEmptyList(bigOrders)) return null;
        BigStyleAnalyseMaster bigOrder = bigOrders.get(0);
        if (!BusinessConstants.Status.PUBLISHED_STATUS.equals(bigOrder.getStatus())) return null;
        FmsTicketNo fmsTicketNo = fmsTicketNoService.getOneByProductionTicketNo(productionTicketNo);
        if (null == fmsTicketNo || !"10".equals(fmsTicketNo.getAdaptCode())) return null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("styleCode", fmsTicketNo.getStyleCode());
        jsonObject.put("intellectualizationIdentification", "10".equals(fmsTicketNo.getAdaptCode()) ? "1" : "0");
        jsonObject.put("systemCode", "");
        jsonObject.put("testResult", "");
        jsonObject.put("testCause", "");
        jsonObject.put("testRemark", "");
        //大货1 定制2
        jsonObject.put("orderType", ("06".equals(fmsTicketNo.getStyleType()) || "08".equals(fmsTicketNo.getStyleType()) ? "1" : "2"));
        String dataStr = jsonObject.toJSONString();
        if (isSend) {
            sendPiService.sendToPi(BusinessConstants.PiReceiveUrlConfig.BIG_ORDER_THINK_FLAG_URL, dataStr);
            return "";
        } else {
            return dataStr;
        }
    }

    /**
     * 从工单工艺里面导入 部件工艺和工序数据
     */
    public List<BigStyleAnalyseMaster> searchFromBigOrder(Map<String, Object> map) {
        List<BigStyleAnalyseMaster> masterList = Collections.emptyList();
        if (map.get("craftCode") != null || map.get("description") != null) {
            masterList = bigOrderMasterDao.searchBigOrderByCraftInfo(map);
        } else {
            masterList = bigOrderMasterDao.searchFromBigStyleAnalyse(map);
        }
        if (null == masterList || masterList.size() == 0) {
            return Collections.emptyList();
        }
        final Map<String, Dictionary> dicMap = dictionaryDao.getDictionaryMap("SubBrand");
        masterList.stream().parallel().forEach(x -> {
            List<BigStyleAnalysePartCraft> partCrafts = bigOrderPartCraftDao.getPartAndDetailByStyleRandomCodeAndOrderByCraftNo(x.getRandomCode());
            x.setPartCraftList(partCrafts);
            //查询工单工艺 工序里面的动作，这个本来是不应该要有的
            if (null != partCrafts && partCrafts.size() > 0) {
                for (BigStyleAnalysePartCraft part : partCrafts) {
                    List<BigStyleAnalysePartCraftDetail> partCraftDetails = part.getPartCraftDetailList();
                    if (null != partCraftDetails && partCraftDetails.size() > 0) {
                        for (BigStyleAnalysePartCraftDetail detail : partCraftDetails) {
                            List<SewingCraftAction> actions = bigOrderSewingCraftActionDao.getDataBySewingCraftRandomCodeAndCraftCode(x.getRandomCode(),detail.getCraftCode());
                            detail.setMotionList(actions);
                        }
                    }
                }
            }

            if (dicMap.containsKey(x.getSubBrand())) {
                x.setSubBrand(dicMap.get(x.getSubBrand()).getValueDesc());
            }
            searchPictures(x);
        });
        return masterList;
    }
}
