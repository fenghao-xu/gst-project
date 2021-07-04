package com.ylzs.service.sewingcraft;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.*;
import com.ylzs.dao.bigstylecraft.*;
import com.ylzs.dao.bigticketno.*;
import com.ylzs.dao.craftmainframe.CraftMainFrameDao;
import com.ylzs.dao.craftstd.CraftFileDao;
import com.ylzs.dao.craftstd.CraftPartDao;
import com.ylzs.dao.craftstd.MachineDao;
import com.ylzs.dao.craftstd.MakeTypeDao;
import com.ylzs.dao.partCraft.PartCraftDetailDao;
import com.ylzs.dao.partCraft.PartCraftMasterDataDao;
import com.ylzs.dao.sewingcraft.*;
import com.ylzs.dao.staticdata.PatternSymmetryDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.dao.timeparam.CraftGradeEquipmentDao;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftstd.CraftPart;
import com.ylzs.entity.craftstd.Machine;
import com.ylzs.entity.craftstd.MakeType;
import com.ylzs.entity.sewingcraft.SewingCraftAction;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.staticdata.CraftGradeEquipment;
import com.ylzs.entity.staticdata.PatternSymmetry;
import com.ylzs.entity.system.User;
import com.ylzs.service.IOriginService;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.vo.SewingCraftResource;
import com.ylzs.vo.SewingCraftVo;
import com.ylzs.vo.partCraft.PartCraftCraftFlowVO;
import com.ylzs.vo.sewing.VSewingCraftVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xuwei
 * @create 2020-03-02 9:59
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SewingCraftWarehouseService extends OriginServiceImpl<SewingCraftWarehouseDao, SewingCraftWarehouse> implements IOriginService<SewingCraftWarehouse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SewingCraftWarehouseService.class);
    @Resource
    private SewingCraftWarehouseDao sewingCraftWarehouseDao;

    private static final String SEWING_REDIS = "sewing";
    private static final String STYLE_REDIS = "styleing";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private SewingCraftWarehouseWorkplaceDao sewingCraftWarehouseWorkplaceDao;

    @Resource
    private SewingCraftActionDao sewingCraftActionDao;

    @Resource
    private SewingCraftPartPositionDao sewingCraftPartPositionDao;

    @Resource
    private SewingCraftStdDao sewingCraftStdDao;

    @Resource
    private CraftMainFrameDao craftMainFrameDao;
    @Resource
    private CraftFileDao craftFileDao;

    @Resource
    private CraftPartDao craftPartDao;

    @Resource
    private StyleSewingCraftWarehouseWorkplaceDao styleSewingCraftWarehouseWorkplaceDao;

    @Resource
    private StyleSewingCraftActionDao styleSewingCraftActionDao;

    @Resource
    private StyleSewingCraftPartPositionDao styleSewingCraftPartPositionDao;

    @Resource
    private StyleSewingCraftStdDao styleSewingCraftStdDao;

    @Resource
    private StyleSewingCraftWarehouseDao styleSewingCraftWarehouseDao;

    @Resource
    private MachineDao machineDao;

    @Resource
    CraftGradeEquipmentDao craftGradeEquipmentDao;

    @Resource
    private PatternSymmetryDao patternSymmetryDao;


    @Resource
    private BigOrderSewingCraftWarehouseWorkplaceDao bigOrderSewingCraftWarehouseWorkplaceDao;

    @Resource
    private BigOrderSewingCraftActionDao bigOrderSewingCraftActionDao;

    @Resource
    private BigOrderSewingCraftPartPositionDao bigOrderSewingCraftPartPositionDao;

    @Resource
    private BigOrderSewingCraftStdDao bigOrderSewingCraftStdDao;

    @Resource
    private BigOrderSewingCraftWarehouseDao bigOrderSewingCraftWarehouseDao;

    @Resource
    private PartCraftDetailDao partCraftDetailDao;

    @Resource
    private PartCraftMasterDataDao partCraftMasterDataDao;

    @Resource
    private MakeTypeDao makeTypeDao;

    @Resource
    private UserDao userDao;

    private final static String SYSTEM_AUTO_CREATE = "系统自动生成";

    /**
     * 查询车工工序列表信息
     *
     * @return
     */
    public List<SewingCraftVo> searchSewingCraftData(HashMap param) {
        List<SewingCraftVo> list = sewingCraftWarehouseWorkplaceDao.searchSewingCraftData(param);
        if (list != null && !list.isEmpty()) {
            for (SewingCraftVo vo : list) {
                vo.setPicUrl(craftFileDao.getPicUrlOne(vo.getRandomCode()));
            }
        }
        return list;
    }

    public List<SewingCraftWarehouse> getDataByCraftCodeLike(Map<String, Object> param) {
        return sewingCraftWarehouseDao.getDataByCraftCodeLike(param);
    }

    /**
     * list转成string
     */
    public static String[] changeListToString(JSONArray array, String codeParam, String namePparam) {
        if (null == array || array.size() == 0) {
            return null;
        }
        String result[] = new String[2];
        String code = "";
        String name = "";
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            //拼接到 一起，中间用逗号隔开
            code = code + obj.getString(codeParam);
            name = name + obj.getString(namePparam);
            if (i < array.size() - 1) {
                code = code + ",";
                name = name + ",";
            }
        }
        result[0] = code;
        result[1] = name;
        return result;
    }

    /**
     * 在款式工艺里面单独产生工序编码的接口
     */
    public String justGenerateSewingCraftCode(JSONObject dataObj) {
        JSONArray craftCategoryList = dataObj.getJSONArray("craftCategoryList");
        JSONArray craftPartList = dataObj.getJSONArray("craftPartList");
        //做工类型
        String makeTypeCode = dataObj.getString("makeTypeCode");
        try {
            return generateSewingCraftCode(craftCategoryList, craftPartList, makeTypeCode, STYLE_REDIS);
        } catch (Exception e) {
            LOGGER.error("在款式工艺里面单独产生工序编码失败:" + e.getMessage());
        }
        return "";
    }

    /**
     * "实时保存款式工艺的单个工序
     */
    public JSONObject saveBigStyleSingleCraft(String partCraftMainCode, JSONObject dataObj, Long bigStyleRandomCode, String tableName) throws Exception {
        JSONArray craftCategoryList = dataObj.getJSONArray("craftCategoryList");
        String category[] = changeListToString(craftCategoryList, "craftCategoryCode", "craftCategoryName");
        String craftCategoryName = "";
        String craftCategoryCode = "";
        if (category != null) {
            craftCategoryName = category[1];
            craftCategoryCode = category[0];
        }
        JSONObject result = new JSONObject();
        JSONArray craftPartList = dataObj.getJSONArray("craftPartList");
        String part[] = changeListToString(craftPartList, "craftPartCode", "craftPartName");
        String partName = "";
        String partCode = "";
        if (category != null) {
            partName = part[1];
            partCode = part[0];
        }
        JSONArray workplaceCraftList = dataObj.getJSONArray("workplaceCraftList");
        JSONArray sewPartPositionList = dataObj.getJSONArray("sewPartPositionList");
        JSONArray motionsList = dataObj.getJSONArray("motionsList");
        JSONArray craftStdList = dataObj.getJSONArray("craftStdList");
        //登录用户
        String userCode = dataObj.getString("userCode");
        //做工类型
        String makeTypeCode = dataObj.getString("makeTypeCode");
        String sewingCraftCode = "";
        BigDecimal oldStandTime = BigDecimal.ZERO;
        BigDecimal oldStandPrice = BigDecimal.ZERO;
        sewingCraftCode = dataObj.getString("craftCode");

        //在大货款式工艺，新增和复制的场合会出现这种情况，前端传过来系统自动生成
        if (SYSTEM_AUTO_CREATE.equals(sewingCraftCode)) {
            sewingCraftCode = generateSewingCraftCode(craftCategoryList, craftPartList, makeTypeCode, STYLE_REDIS);
            sewingCraftCode = sewingCraftCode + "T";
        }

        result.put("craftCode", sewingCraftCode);
        result.put("randomCode", bigStyleRandomCode);
        //草稿状态
        Integer status = BusinessConstants.Status.DRAFT_STATUS;
        //机器设备
        String machineCode = dataObj.getString("machineCode");
        //机器设备名称
        String machineName = dataObj.getString("machineName");
        //添加工位工序
        String operation = BusinessConstants.Send2Pi.actionType_update;
        int deleteFlag = 100;
        insertOrUpdateSewingCraftWarehouseWorkplace(partCraftMainCode, bigStyleRandomCode, workplaceCraftList, craftPartList, machineCode, machineName, status, userCode, operation, tableName, sewingCraftCode, deleteFlag);
        //添加动作
        insertOrUpdateSewingCraftMotion(partCraftMainCode, bigStyleRandomCode, motionsList, status, userCode, operation, tableName, sewingCraftCode, deleteFlag);
        //添加缝边位置
        addOrUpdatePartPosition(partCraftMainCode, bigStyleRandomCode, sewPartPositionList, status, userCode, operation, tableName, sewingCraftCode, deleteFlag);
        //基础数据
       /* if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
            //款式工艺里面的工序基础数据，放在basedata里面
            dataObj = dataObj.getJSONObject("basedata");
        }*/
        addOrUpdateSewingCraftBaseData(partCraftMainCode, bigStyleRandomCode, sewingCraftCode, dataObj, category, part, status, userCode, operation, tableName, deleteFlag);
        //工艺建标
        addOrUpdateSewingCraftStd(partCraftMainCode, bigStyleRandomCode, craftStdList, operation, tableName, sewingCraftCode, deleteFlag);
        //当工序词库更新后，更新部件工艺对应的数据
        return result;
    }

    /**
     * 新增车缝工序词库
     */
    public JSONObject addOrUpdataSewingCraft(String partCraftMainCode, JSONObject dataObj, String operation, String tableName, int deleteFlag) throws Exception {
        JSONArray craftCategoryList = dataObj.getJSONArray("craftCategoryList");
        String category[] = changeListToString(craftCategoryList, "craftCategoryCode", "craftCategoryName");
        String craftCategoryName = "";
        String craftCategoryCode = "";
        if (category != null) {
            craftCategoryName = category[1];
            craftCategoryCode = category[0];
        }
        JSONObject result = new JSONObject();
        JSONArray craftPartList = dataObj.getJSONArray("craftPartList");
        String part[] = changeListToString(craftPartList, "craftPartCode", "craftPartName");
        String partName = "";
        String partCode = "";
        if (category != null) {
            partName = part[1];
            partCode = part[0];
        }
        JSONArray workplaceCraftList = dataObj.getJSONArray("workplaceCraftList");
        JSONArray sewPartPositionList = dataObj.getJSONArray("sewPartPositionList");
        JSONArray motionsList = dataObj.getJSONArray("motionsList");
        JSONArray craftStdList = dataObj.getJSONArray("craftStdList");
        //登录用户
        String userCode = dataObj.getString("userCode");
        //做工类型
        String makeTypeCode = dataObj.getString("makeTypeCode");
        String sewingCraftCode = "";
        Long randomCode = null;
        BigDecimal oldStandTime = BigDecimal.ZERO;
        BigDecimal oldStandPrice = BigDecimal.ZERO;
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            //修改的时候由前端页面传递过来
            randomCode = dataObj.getLong("randomCode");
            sewingCraftCode = dataObj.getString("craftCode");
            //只有在修改工序管理的时候才进来，因为要把标准单价和标准时间同步过去，工序描述以及工序流
            if (BusinessConstants.TableName.SEWING_CRAFT.equals(tableName)) {
                Map<String, Object> param = new HashMap<>(0);
                param.put("code", sewingCraftCode);
                List<SewingCraftWarehouse> warehouses = sewingCraftWarehouseDao.getDataByCraftCodeLike(param);
                if (warehouses != null && warehouses.size() > 0) {
                    oldStandPrice = warehouses.get(0).getStandardPrice();
                    oldStandTime = warehouses.get(0).getStandardTime();
                }
            }
            //在大货款式工艺，新增和复制的场合会出现这种情况，前端传过来系统自动生成
            if (SYSTEM_AUTO_CREATE.equals(sewingCraftCode)) {
                sewingCraftCode = generateSewingCraftCode(craftCategoryList, craftPartList, makeTypeCode, STYLE_REDIS);
                sewingCraftCode = sewingCraftCode + "T";
            }
        } else {
            //新增的时候由系统产生
            sewingCraftCode = generateSewingCraftCode(craftCategoryList, craftPartList, makeTypeCode, SEWING_REDIS);
            randomCode = SnowflakeIdUtil.generateId();
        }

        result.put("craftCode", sewingCraftCode);
        result.put("randomCode", randomCode);
        LOGGER.info("车缝工序编码是:" + sewingCraftCode);
        //草稿状态
        Integer status = BusinessConstants.Status.DRAFT_STATUS;
        //机器设备
        String machineCode = dataObj.getString("machineCode");
        //机器设备名称
        String machineName = dataObj.getString("machineName");
        //添加工位工序
        insertOrUpdateSewingCraftWarehouseWorkplace(partCraftMainCode, randomCode, workplaceCraftList, craftPartList, machineCode, machineName, status, userCode, operation, tableName, sewingCraftCode, deleteFlag);
        //添加动作
        insertOrUpdateSewingCraftMotion(partCraftMainCode, randomCode, motionsList, status, userCode, operation, tableName, sewingCraftCode, deleteFlag);
        //添加缝边位置
        addOrUpdatePartPosition(partCraftMainCode, randomCode, sewPartPositionList, status, userCode, operation, tableName, sewingCraftCode, deleteFlag);
        //基础数据
       /* if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
            //款式工艺里面的工序基础数据，放在basedata里面
            dataObj = dataObj.getJSONObject("basedata");
        }*/
        addOrUpdateSewingCraftBaseData(partCraftMainCode, randomCode, sewingCraftCode, dataObj, category, part, status, userCode, operation, tableName, deleteFlag);
        //工艺建标
        addOrUpdateSewingCraftStd(partCraftMainCode, randomCode, craftStdList, operation, tableName, sewingCraftCode, deleteFlag);
        //当工序词库更新后，更新部件工艺对应的数据

        if (BusinessConstants.Send2Pi.actionType_update.equals(operation) && BusinessConstants.TableName.SEWING_CRAFT.equals(tableName)) {
            try {
                //更新工序名称和描述
                Map<String, Object> param = new HashMap<>(0);
                param.put("craftName", dataObj.getString("craftName"));
                param.put("craftRemark", dataObj.getString("description"));
                param.put("craftCode", sewingCraftCode);


                String standardTime = dataObj.getString("standardTime");
                //标准单价
                String standardPrice = dataObj.getString("standardPrice");
                BigDecimal newStandTime = new BigDecimal(standardTime).setScale(3, BigDecimal.ROUND_HALF_UP);
                BigDecimal newStandPrice = new BigDecimal(standardPrice).setScale(3, BigDecimal.ROUND_HALF_UP);
                //只要 有一个不同，就要去执行更新操作
                boolean priceChange = false;
                boolean timeChange = false;
                if (!oldStandPrice.equals(newStandPrice)) {
                    param.put("standardPrice", newStandPrice);
                    priceChange = true;
                }
                if (!oldStandTime.equals(newStandTime)) {
                    param.put("standardTime", newStandTime);
                    timeChange = true;
                }
                List<PartCraftCraftFlowVO> needChangePartCraftList = partCraftDetailDao.getNeededChangedData(sewingCraftCode);
                Map<String, String> workplaceMap = new HashMap<>();
                if (workplaceCraftList != null && workplaceCraftList.size() > 0) {
                    for (int i = 0; i < workplaceCraftList.size(); i++) {
                        JSONObject work = workplaceCraftList.getJSONObject(i);
                        String mainFrameCode = work.getString("mainFrameCode");
                        String craftFlowNum = work.getString("craftFlowNum");
                        workplaceMap.put(mainFrameCode, craftFlowNum);
                    }
                }
                if (null != needChangePartCraftList && needChangePartCraftList.size() > 0) {
                    //更新标准单价和时间
                    for (PartCraftCraftFlowVO vo : needChangePartCraftList) {
                        if (priceChange) {
                            BigDecimal diffPrice = newStandPrice.subtract(oldStandPrice).setScale(3, BigDecimal.ROUND_HALF_UP);
                            BigDecimal old = vo.getStandardPrice();
                            vo.setStandardPrice(old.add(diffPrice).setScale(3, BigDecimal.ROUND_HALF_UP));
                        }
                        if (timeChange) {
                            BigDecimal diffTime = newStandTime.subtract(oldStandTime).setScale(3, BigDecimal.ROUND_HALF_UP);
                            BigDecimal old = vo.getStandardTime();
                            vo.setStandardTime(old.add(diffTime).setScale(3, BigDecimal.ROUND_HALF_UP));
                        }
                        //更新工序流,因为不同的部件工艺，他的主框架不一样，只能单独来更新工序流
                        if (workplaceMap.containsKey(vo.getMainFrameCode()) && StringUtils.isNotEmpty(vo.getCraftCode()) && vo.getPartCraftMainRandomCode() != null) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("craftCode", vo.getCraftCode());
                            map.put("craftFlowNum", workplaceMap.get(vo.getMainFrameCode()));
                            map.put("partCraftMainRandomCode", vo.getPartCraftMainRandomCode());
                            partCraftDetailDao.updateCraftFlowNum(map);
                        }
                        partCraftMasterDataDao.updatePriceAndTime(vo);
                    }
                }
                partCraftDetailDao.updateCraftRemarkAndName(param);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    /**
     * 插入或者修改车工艺建标
     */
    public void addOrUpdateSewingCraftStd(String partCraftMainCode, Long randomCode, JSONArray craftStdList, String operation, String tableName, String craftCode, int deleteFlag) {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            LOGGER.info("---------deleteSewingCraftBaseData--------------");
            if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                List<Long> ids = styleSewingCraftStdDao.getIDSBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftStdDao.deleteDataByIds(ids);
                }
                // styleSewingCraftStdDao.deleteDataBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                List<Long> ids = bigOrderSewingCraftStdDao.getIDSBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftStdDao.deleteDataByIds(ids);
                }
                // styleSewingCraftStdDao.deleteDataBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
            } else {
                sewingCraftStdDao.deleteDataBySewingCraftRandomCode(randomCode);
            }
            sleepMicroseconds(1);
        }
        if (null == craftStdList || craftStdList.size() == 0) {
            return;
        }
        for (int i = 0; i < craftStdList.size(); i++) {
            JSONObject obj = craftStdList.getJSONObject(i);
            Map<String, Object> param = new HashMap<>();
            param.put("sewingCraftRandomCode", randomCode);
            if (StringUtils.isEmpty(obj.getString("craftStdCode"))) {
                continue;
            }
            param.put("craftStdCode", obj.getString("craftStdCode"));
            param.put("craftStdName", obj.getString("craftStdName"));
            param.put("partCraftMainCode", partCraftMainCode);
            LOGGER.info("---------addSewingCraftBaseData--------------");
            if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                param.put("craftCode", craftCode);
                styleSewingCraftStdDao.addSewingCraftStd(param);
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                param.put("craftCode", craftCode);
                bigOrderSewingCraftStdDao.addSewingCraftStd(param);
            } else {
                sewingCraftStdDao.addSewingCraftStd(param);
            }
        }
    }

    public boolean updateStatus(Integer status, Long randomCode, String userCode) {
        boolean flag = false;
        try {
            sewingCraftWarehouseDao.updateStatus(status, randomCode, userCode, new Date());
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 插入或者修改车缝工序的基本信息
     */
    public void addOrUpdateSewingCraftBaseData(String partCraftMainCode, Long randomCode, String sewingCraftCode, JSONObject dataObj, String category[], String part[], Integer status, String userCode, String operation, String tableName, int deleteFlag) {
        Map<String, Object> param = new HashMap<>();
        //做工类型
        String makeTypeCode = dataObj.getString("makeTypeCode");
        //工序名称
        String craftName = dataObj.getString("craftName");
        //工序描述
        String description = dataObj.getString("description");
        //工序等级
        String craftGradeCode = dataObj.getString("craftGradeCode");
        //机器设备
        String machineCode = dataObj.getString("machineCode");
        //针距
        String stitchLength = dataObj.getString("stitchLength");
        //机器设备名称
        String machineName = dataObj.getString("machineName");
        //同级工序编码
        String sameLevelCraftNumericalCode = dataObj.getString("sameLevelCraftNumericalCode");
        //宽放系数编码
        String allowanceCode = dataObj.getString("allowanceCode");
        Long allowanceRandomCode = 0L;
        try {
            allowanceRandomCode = dataObj.getLong("allowanceRandomCode");
        } catch (Exception e) {

        }
        //捆扎时间编码
        String strappingCode = dataObj.getString("strappingCode");
        //表面风格
        String isFabricStyleFix = dataObj.getString("isFabricStyleFix");
        //工艺标准
        //String craftStdCode = dataObj.getString("craftStdCode");
        //面料分值方案
        String fabricScorePlanCode = dataObj.getString("fabricScorePlanCode");
        //固定时间
        String fixedTime = dataObj.getString("fixedTime");
        //车缝长度
        String sewingLength = dataObj.getString("sewingLength");
        //参数长度
        String paramLength = dataObj.getString("paramLength");

        //计算标准时间和单价
        //calStandTimeAndPrice(dataObj);
        //标准时间
        String standardTime = dataObj.getString("standardTime");
        //标准单价
        String standardPrice = dataObj.getString("standardPrice");
        //浮动时间
        String floatingTime = dataObj.getString("floatingTime");
        //工种
        String workTypeCode = dataObj.getString("workTypeCode");
        //做工说明
        String makeDescription = dataObj.getString("makeDescription");
        //品质要求
        String qualitySpec = dataObj.getString("qualitySpec");

        Boolean isCancelSend = Boolean.FALSE;
        if (StringUtils.isNotEmpty(dataObj.getString("isCancelSend"))) {
            isCancelSend = dataObj.getBoolean("isCancelSend");
        }

        //是图片是否发送到裁剪
        param.put("isSendCutPic", dataObj.getBoolean("isSendCutPic"));
        param.put("isCancelSend", isCancelSend);
        param.put("randomCode", randomCode);
        param.put("craftCode", sewingCraftCode);
        param.put("craftName", craftName);
        param.put("workTypeCode", workTypeCode);
        param.put("sameLevelCraftNumericalCode", sameLevelCraftNumericalCode);

        param.put("qualitySpec", qualitySpec);
        param.put("makeDescription", makeDescription);


        param.put("craftCategoryCode", category[0]);
        param.put("craftCategoryName", category[1]);
        param.put("craftPartCode", part[0]);
        param.put("craftPartName", part[1]);

        param.put("makeTypeCode", makeTypeCode);
        param.put("description", description);
        param.put("craftGradeCode", craftGradeCode);
        param.put("machineCode", machineCode);
        if (StringUtils.isNotEmpty(stitchLength)) {
            param.put("stitchLength", stitchLength);
        }

        param.put("machineName", machineName);

        if (StringUtils.isNotEmpty(strappingCode)) {
            param.put("strappingCode", strappingCode);
        }
        if (StringUtils.isNotEmpty(allowanceCode)) {
            param.put("allowanceCode", allowanceCode);
            param.put("allowanceRandomCode", allowanceRandomCode);
        }
        if ("1".equals(isFabricStyleFix)) {
            param.put("isFabricStyleFix", Boolean.TRUE);
        } else {
            param.put("isFabricStyleFix", Boolean.FALSE);
        }
        param.put("fabricScorePlanCode", fabricScorePlanCode);
        param.put("fixedTime", fixedTime);
        param.put("floatingTime", floatingTime);
        if (StringUtils.isNotEmpty(paramLength)) {
            param.put("paramLength", paramLength);
        }
        if (StringUtils.isNotEmpty(sewingLength)) {
            param.put("sewingLength", sewingLength);
        }
        param.put("partCraftMainCode", partCraftMainCode);
        ////计算标准时间和标准单价

        if (StringUtils.isNotEmpty(standardTime)) {
            param.put("standardTime", standardTime);
        }
        if (StringUtils.isNotEmpty(standardPrice)) {
            param.put("standardPrice", standardPrice);
        }
        String baseStandardTime = dataObj.getString("baseStandardTime");
        if (StringUtils.isNotEmpty(baseStandardTime)) {
            param.put("baseStandardTime", baseStandardTime);
        }
        String baseStandardPrice = dataObj.getString("baseStandardPrice");
        if (StringUtils.isNotEmpty(baseStandardPrice)) {
            param.put("baseStandardPrice", baseStandardPrice);
        }
        //param.put("craftStdCode", craftStdCode);
        param.put("createUser", userCode);
        param.put("createTime", new Date());
        param.put("status", status);
        if (BusinessConstants.Status.DRAFT_STATUS.equals(status)) {
            param.put("releaseTime", null);
            param.put("releaseUser", null);
        }
        if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
            if (0 == deleteFlag) {
                List<Long> ids = styleSewingCraftWarehouseDao.getIdsByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftWarehouseDao.deleteDataByIds(ids);
                }
                //styleSewingCraftWarehouseDao.deleteDataByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } else if (100 == deleteFlag) {//款式工艺，实时保存工序用
                List<Long> ids = styleSewingCraftWarehouseDao.getIdsByRandomCodeAndPartCraftMainCodeAndCraftCode(randomCode, partCraftMainCode, sewingCraftCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftWarehouseDao.deleteDataByIds(ids);
                }
            }
            //时间浮余
            String timeSupplement = dataObj.getString("timeSupplement");
            if (StringUtils.isNotEmpty(timeSupplement)) {
                param.put("timeSupplement", timeSupplement);
            }
            //订单等级
            String orderGrade = dataObj.getString("orderGrade");
            if (StringUtils.isNotEmpty(orderGrade)) {
                param.put("orderGrade", orderGrade);
            }
            String operation11 = dataObj.getString("operation");
            if (BusinessConstants.Send2Pi.actionType_update.equals(operation11)) {

            }
            //创建人和创建和创建时间，更新人和更新时间都从前端传过来
            param.put("updateUser", dataObj.getString("updateUser"));
            param.put("createUser", dataObj.getString("createUser"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if (StringUtils.isNotEmpty(dataObj.getString("updateTime"))) {
                    param.put("updateTime", sdf.parse(dataObj.getString("updateTime")));
                }
                if (StringUtils.isNotEmpty(dataObj.getString("createTime"))) {
                    param.put("createTime", sdf.parse(dataObj.getString("createTime")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //面料系数
            String fabricRatio = dataObj.getString("fabricRatio");
            if (StringUtils.isNotEmpty(fabricRatio)) {
                param.put("fabricRatio", fabricRatio);
            }
            //面料分值
            String fabricFraction = dataObj.getString("fabricFraction");
            if (StringUtils.isNotEmpty(fabricFraction)) {
                param.put("fabricFraction", fabricFraction);
            }
            //面料等级
            String fabricGradeCode = dataObj.getString("fabricGradeCode");
            if (StringUtils.isNotEmpty(fabricGradeCode)) {
                param.put("fabricGradeCode", fabricGradeCode);
            }
            styleSewingCraftWarehouseDao.addSewingCraftWarehouse(param);
        } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
            if (0 == deleteFlag) {
                List<Long> ids = bigOrderSewingCraftWarehouseDao.getIdsByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftWarehouseDao.deleteDataByIds(ids);
                }
                //styleSewingCraftWarehouseDao.deleteDataByRandomCodeAndPartCraftMainCode(randomCode, partCraftMainCode);
            } else if (100 == deleteFlag) {//款式工艺，实时保存工序用
                List<Long> ids = bigOrderSewingCraftWarehouseDao.getIdsByRandomCodeAndPartCraftMainCodeAndCraftCode(randomCode, partCraftMainCode, sewingCraftCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftWarehouseDao.deleteDataByIds(ids);
                }
            }
            //时间浮余
            String timeSupplement = dataObj.getString("timeSupplement");
            if (StringUtils.isNotEmpty(timeSupplement)) {
                param.put("timeSupplement", timeSupplement);
            }
            //订单等级
            String orderGrade = dataObj.getString("orderGrade");
            if (StringUtils.isNotEmpty(orderGrade)) {
                param.put("orderGrade", orderGrade);
            }
            String operation11 = dataObj.getString("operation");
            if (BusinessConstants.Send2Pi.actionType_update.equals(operation11)) {

            }
            //创建人和创建和创建时间，更新人和更新时间都从前端传过来
            param.put("updateUser", dataObj.getString("updateUser"));
            param.put("createUser", dataObj.getString("createUser"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if (StringUtils.isNotEmpty(dataObj.getString("updateTime"))) {
                    param.put("updateTime", sdf.parse(dataObj.getString("updateTime")));
                }
                if (StringUtils.isNotEmpty(dataObj.getString("createTime"))) {
                    param.put("createTime", sdf.parse(dataObj.getString("createTime")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //面料系数
            String fabricRatio = dataObj.getString("fabricRatio");
            if (StringUtils.isNotEmpty(fabricRatio)) {
                param.put("fabricRatio", fabricRatio);
            }
            //面料分值
            String fabricFraction = dataObj.getString("fabricFraction");
            if (StringUtils.isNotEmpty(fabricFraction)) {
                param.put("fabricFraction", fabricFraction);
            }
            //同步到工序管理的状态
            param.put("sysnStatus", dataObj.getInteger("sysnStatus"));
            //面料等级
            String fabricGradeCode = dataObj.getString("fabricGradeCode");
            if (StringUtils.isNotEmpty(fabricGradeCode)) {
                param.put("fabricGradeCode", fabricGradeCode);
            }
            bigOrderSewingCraftWarehouseDao.addSewingCraftWarehouse(param);
        } else {
            if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
                LOGGER.info("---------updateSewingCraftWarehouse--------------");
                param.put("updateUser", userCode);
                param.put("updateTime", new Date());
                sewingCraftWarehouseDao.updateSewingCraftWarehouse(param);
            } else {
                LOGGER.info("---------addSewingCraftWarehouse--------------");
                sewingCraftWarehouseDao.addSewingCraftWarehouse(param);
            }
        }
        LOGGER.info("参数信息是:" + JSONObject.toJSONString(param));
    }

    //计算标准时间和标准单价
    public void calStandTimeAndPrice(JSONObject dataObj) {
        //捆扎时间
        BigDecimal strappingCode = dataObj.getBigDecimal("strappingCode");
        if (null == strappingCode) {
            strappingCode = BigDecimal.ZERO;
        }
        String factoryCode = dataObj.getString("factoryCode");
        if (StringUtils.isEmpty(factoryCode)) {
            //如果为空就默认用赢家的
            factoryCode = "9999";
        }
        //宽放系数
        BigDecimal allowanceCode = dataObj.getBigDecimal("allowanceCode");
        if (null == allowanceCode) {
            allowanceCode = BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;

        //机器设备
        String machineCode = dataObj.getString("machineCode");
        //工序等级
        String craftGradeCode = dataObj.getString("craftGradeCode");
        //做工类型
        String makeTypeCode = dataObj.getString("makeTypeCode");
        Machine machine = machineDao.getByCodeAndMakeTypeCode(machineCode, makeTypeCode);

        JSONArray motionsList = dataObj.getJSONArray("motionsList");
        //车缝长度=车缝动作代码（M开头的动作代码）的车缝长度*频率之和,非M开头的值就是0
        BigDecimal sewLength = BigDecimal.ZERO;
        if (null == machine) {
            LOGGER.info("通过机器编码查询不到机器设备：");
            dataObj.put("standardTime", BigDecimal.ZERO.toString());
            dataObj.put("standardPrice", BigDecimal.ZERO.toString());
        } else {
            if (machine.getManualFloatover() == null) {
                machine.setManualFloatover(0f);
            }
            if (machine.getMachineFloatover() == null) {
                machine.setMachineFloatover(0f);
            }
            if (null != motionsList && motionsList.size() > 0) {
                for (int i = 0; i < motionsList.size(); i++) {
                    JSONObject obj = motionsList.getJSONObject(i);
                    BigDecimal frequency = BigDecimal.ONE;
                    BigDecimal frequencyObj = obj.getBigDecimal("frequency");
                    if (null != frequencyObj) {
                        frequency = frequencyObj;
                    }
                    String code = obj.getString("motionCode");
                    if (StringUtils.isEmpty(code)) {
                        continue;
                    }
                    if (!code.startsWith("M") || code.indexOf("=") != -1) {
                        sewLength = sewLength.add(BigDecimal.ZERO);
                    } else {
                        try {
                            sewLength = sewLength.add(frequency.multiply(new BigDecimal(code.substring(1, 3))).setScale(3, BigDecimal.ROUND_HALF_UP));
                        } catch (Exception e) {
                            e.printStackTrace();
                            sewLength = sewLength.add(BigDecimal.ZERO);
                        }
                    }
                    //机器TMU
                    Integer machineTime = obj.getInteger("machineTime");
                    if (null == machineTime) {
                        machineTime = new Integer(0);
                    }
                    Integer machineTMU = machineTime;
                    //人工TMU
                    Integer manualTime = obj.getInteger("manualTime");
                    if (null == manualTime) {
                        manualTime = new Integer(0);
                    }
                    Integer manualTMU = manualTime;
                    //GST工序标准时间(分)=[(机器TMU*(1+机器浮余+人工浮余)/1667+（人工TMU*(1+人工浮余)）/2000]*[(1+宽放系数)+捆扎时间/2000]之和
                    //(机器TMU*(1+机器浮余+人工浮余)/1667
                    BigDecimal one = new BigDecimal(1)
                            .add(new BigDecimal(machine.getMachineFloatover()))
                            .add(new BigDecimal(machine.getManualFloatover()))
                            .multiply(new BigDecimal(machineTMU)).divide(new BigDecimal(1667), 6, BigDecimal.ROUND_HALF_UP);
                    ;
                    //人工TMU*(1+人工浮余)）/2000
                    BigDecimal two = new BigDecimal(1).add(new BigDecimal(machine.getManualFloatover()))
                            .multiply(new BigDecimal(manualTMU))
                            .divide(new BigDecimal(2000), 6, BigDecimal.ROUND_HALF_UP);
                    BigDecimal three = one.add(two);
                    BigDecimal four = new BigDecimal(1).add(allowanceCode);
                    BigDecimal five = three.multiply(four).setScale(6, BigDecimal.ROUND_HALF_UP);
                    BigDecimal six = strappingCode.divide(new BigDecimal(2000), 6, BigDecimal.ROUND_HALF_UP);
                    BigDecimal seven = six.add(five);
                    //每一个动作代码累积求和算出标准时间
                    total = total.add(seven);
                }
                total = total.setScale(3, BigDecimal.ROUND_HALF_UP);
                dataObj.put("sewLength", sewLength.doubleValue());
                dataObj.put("standardTime", total.toString());
                CraftGradeEquipment craftGrade = craftGradeEquipmentDao.
                        getCraftGradeByTypeAndCode(BusinessConstants.CraftGradeEquipmentType.CRAFT_GRADE_SEWING, craftGradeCode, factoryCode);
                BigDecimal standPrice = BigDecimal.ZERO;
                if (null != craftGrade) {
                    //标准单价（元）=GST工序标准时间（分）*工序等级分钟工资（
                    standPrice = total.multiply(craftGrade.getMinuteWage()).setScale(3, BigDecimal.ROUND_HALF_UP);
                }
                dataObj.put("standardPrice", standPrice.toString());
            }
        }
        //增加补贴计算
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
        //处理部件工艺详情里面的数据
        final Map<String, PatternSymmetry> map = patternSymmetryDao.getPatternSymmetrysToMap();
        String patternSymmetry = dataObj.getString("patternSymmetry");
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
        oldTime = oldTime.setScale(3, BigDecimal.ROUND_HALF_UP);
        oldPrice = oldPrice.setScale(3, BigDecimal.ROUND_HALF_UP);
        dataObj.put("baseStandardTime", oldTime.doubleValue());
        dataObj.put("baseStandardPrice", oldPrice.doubleValue());
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
     * 添加或者修改缝边位置
     */
    public void addOrUpdatePartPosition(String partCraftMainCode, Long randomCode, JSONArray sewPartPositionList, Integer status, String userCode, String operation, String tableName, String craftCode, int deleteFlag) throws Exception {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            LOGGER.info("---------deleteSewingCraftPartPosition--------------");
            if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                List<Long> ids = styleSewingCraftPartPositionDao.getIDsBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftPartPositionDao.deleteDataByids(ids);
                }
                // styleSewingCraftPartPositionDao.deleteDataBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                List<Long> ids = bigOrderSewingCraftPartPositionDao.getIDsBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftPartPositionDao.deleteDataByids(ids);
                }
                // styleSewingCraftPartPositionDao.deleteDataBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
            } else {
                sewingCraftPartPositionDao.deleteDataBySewingCraftRandomCode(randomCode);
            }
            sleepMicroseconds(1);

        }
        if (null == sewPartPositionList || sewPartPositionList.size() == 0) {
            return;
        }
        for (int i = 0; i < sewPartPositionList.size(); i++) {
            JSONObject obj = sewPartPositionList.getJSONObject(i);
            Map<String, Object> param = new HashMap<>();
            param.put("sewingCraftRandomCode", randomCode);
            param.put("partPositionCode", obj.getString("partPositionCode"));
            param.put("partPositionName", obj.getString("partPositionName"));
            param.put("createUser", userCode);
            param.put("createTime", new Date());
            param.put("status", status);
            param.put("partCraftMainCode", partCraftMainCode);
            //因为第一次新建的时候可能没有选缝边位置吗，但是修改的时候添加了缝边位置码，所以对缝边位置来说，这一次还是新建
            if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
                LOGGER.info("---------updateSewingCraftPartPosition--------------");
                param.put("updateUser", userCode);
                param.put("updateTime", new Date());
            }
            LOGGER.info("---------addSewingCraftPartPosition--------------");

            if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                param.put("craftCode", craftCode);
                styleSewingCraftPartPositionDao.addSewingCraftPartPosition(param);
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                param.put("craftCode", craftCode);
                bigOrderSewingCraftPartPositionDao.addSewingCraftPartPosition(param);
            } else {
                sewingCraftPartPositionDao.addSewingCraftPartPosition(param);
            }
            LOGGER.info("参数信息是:" + JSONObject.toJSONString(param));
        }
    }

    /**
     * 添加或者修改动作
     */
    private void insertOrUpdateSewingCraftMotion(String partCraftMainCode, Long randomCode, JSONArray motionsList, Integer status, String userCode, String operation, String tableName, String craftCode, int deleteFlag) throws Exception {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            LOGGER.info("---------deleteSewingCraftAction--------------");
            if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                List<Long> ids = styleSewingCraftActionDao.getIdsBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftActionDao.deleteDataByids(ids);
                }
                //styleSewingCraftActionDao.deleteDataBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                List<Long> ids = bigOrderSewingCraftActionDao.getIdsBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftActionDao.deleteDataByids(ids);
                }
                //styleSewingCraftActionDao.deleteDataBySewingCraftRandomCodeAndCraftCode(randomCode, craftCode);
            } else {
                sewingCraftActionDao.deleteDataBySewingCraftRandomCode(randomCode);
            }
            sleepMicroseconds(1);
        }
        if (null == motionsList || motionsList.size() == 0) {
            return;
        }
        List<SewingCraftAction> motions = new ArrayList<>();
        for (int i = 0; i < motionsList.size(); i++) {
            JSONObject obj = motionsList.getJSONObject(i);
            SewingCraftAction action = new SewingCraftAction();
            action.setSewingCraftRandomCode(randomCode);
            if (StringUtils.isNotEmpty(obj.getString("orderNum"))) {
                action.setOrderNum(Integer.parseInt(obj.getString("orderNum")));
            }
            action.setMark(obj.getString("mark"));
            action.setMotionCode(obj.getString("motionCode"));
            action.setMotionName(obj.getString("motionName"));
            action.setFrequency(new BigDecimal(obj.getString("frequency")));
            action.setDescription(obj.getString("description"));
            if (StringUtils.isNotEmpty(obj.getString("speed"))) {
                action.setSpeed(Integer.parseInt(obj.getString("speed")));
            }
            if (StringUtils.isNotEmpty(obj.getString("machineTime"))) {
                action.setMachineTime(Integer.parseInt(obj.getString("machineTime")));
            }
            if (StringUtils.isNotEmpty(obj.getString("manualTime"))) {
                action.setManualTime(Integer.parseInt(obj.getString("manualTime")));
            }
            if (StringUtils.isNotEmpty(obj.getString("machineTimeBase"))) {
                action.setMachineTimeBase(Integer.parseInt(obj.getString("machineTimeBase")));
            }
            if (StringUtils.isNotEmpty(obj.getString("manualTimeBase"))) {
                action.setManualTimeBase(Integer.parseInt(obj.getString("manualTimeBase")));
            }
            action.setCraftCode(craftCode);
            action.setPartCraftMainCode(partCraftMainCode);
            if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
                LOGGER.info("---------updateSewingCraftAction--------------");
            }
            motions.add(action);
        }
        //
//        Map<String, Object> param = new HashMap<>();
        // param.put("sewingCraftRandomCode", randomCode);
        //param.put("orderNum", obj.getString("orderNum"));
//            param.put("motionCode", obj.getString("motionCode"));
//            param.put("motionName", obj.getString("motionName"));
//            param.put("frequency", obj.getString("frequency"));
//            param.put("description", obj.getString("description"));
//            param.put("speed", obj.getString("speed"));
//            param.put("machineTime", obj.getString("machineTime"));
//            param.put("manualTime", obj.getString("manualTime"));
//            param.put("machineTimeBase", obj.getString("machineTimeBase"));
//            param.put("manualTimeBase", obj.getString("manualTimeBase"));
//            param.put("createUser", userCode);
//            param.put("createTime", new Date());
//            param.put("status", status);
//            param.put("craftCode", craftCode);
//            param.put("partCraftMainCode", partCraftMainCode);
        ////因为第一次新建的时候可能没有选动作低吗，但是修改的时候添加了动作代码，所以对动作来说，这一次还是新建
        LOGGER.info("---------addSewingCraftAction--------------");
        if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
            styleSewingCraftActionDao.addSewingCraftAction(motions);
        } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
            bigOrderSewingCraftActionDao.addSewingCraftAction(motions);
        } else {
            sewingCraftActionDao.addSewingCraftAction(motions);
        }
        LOGGER.info("参数信息是:" + JSONObject.toJSONString(motions));
    }

    private void sleepMicroseconds(long timeout) {
        try {
            TimeUnit.MICROSECONDS.sleep(timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入或者修改工位工序
     */
    private void insertOrUpdateSewingCraftWarehouseWorkplace(String partCraftMainCode, Long randomCode, JSONArray workplaceCraftList,
                                                             JSONArray craftPartList,
                                                             String machineCode, String machineName, Integer status, String userCode, String operation, String tableName, String craftCode, int deleteFlag) throws Exception {
        if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
            LOGGER.info("---------deleteData--------------");
            if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                List<Long> ids = styleSewingCraftWarehouseWorkplaceDao.getIdByBysewingRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    styleSewingCraftWarehouseWorkplaceDao.deleteWorkplaceByIds(ids);
                }
                //styleSewingCraftWarehouseWorkplaceDao.deleteWorkplaceBysewingRandomCodeAndCraftCode(randomCode, craftCode);
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                List<Long> ids = bigOrderSewingCraftWarehouseWorkplaceDao.getIdByBysewingRandomCodeAndCraftCode(randomCode, craftCode);
                if (null != ids && ids.size() > 0) {
                    bigOrderSewingCraftWarehouseWorkplaceDao.deleteWorkplaceByIds(ids);
                }
                //styleSewingCraftWarehouseWorkplaceDao.deleteWorkplaceBysewingRandomCodeAndCraftCode(randomCode, craftCode);
            } else {
                sewingCraftWarehouseWorkplaceDao.deleteWorkplaceBysewingRandomCode(randomCode);
            }
            sleepMicroseconds(1);
        }
        if (null == workplaceCraftList || workplaceCraftList.size() == 0) {
            return;
        }
        Map<String, CraftPart> partMap = craftPartDao.getAllCraftPartToMap();
        for (int i = 0; i < workplaceCraftList.size(); i++) {
            JSONObject work = workplaceCraftList.getJSONObject(i);
            Map<String, Object> param = new HashMap<>();
            param.put("sewingRandomCode", randomCode);
            param.put("workplaceCraftCode", work.getString("workplaceCraftCode"));
            param.put("workplaceCraftName", work.getString("workplaceCraftName"));
            param.put("craftFlowNum", work.getString("craftFlowNum"));
            param.put("station", work.getString("station"));
            param.put("stationDevice", work.getString("stationDevice"));
            //工艺品类和工艺品类名称
            String craftCategoryCode = work.getString("craftCategoryCode");
            param.put("craftCategoryName", work.getString("craftCategoryName"));
            param.put("craftCategoryCode", craftCategoryCode);

            //生产部件
            param.put("productionPartCode", work.getString("productionPartCode"));
            param.put("productionPartName", work.getString("productionPartName"));
            //结构部件---结果部件一定属于某一个特定的工艺品类
            Map<String, CraftMainFrame> mainFrameMap = craftMainFrameDao.getAllMainFrameToMap();
            for (int j = 0; j < craftPartList.size(); j++) {
                JSONObject obj = craftPartList.getJSONObject(j);
                String parC = obj.getString("craftPartCode");
                String parN = obj.getString("craftPartName");
                //因为在车缝工序新增页面，工艺品类和结构部件是多选，但是每一个结构部件只对应一个工艺品类
                if (partMap.get(parC) != null && craftCategoryCode.equals(partMap.get(parC).getCraftCategoryCode())) {
                    param.put("partCode", parC);
                    param.put("partName", parN);
                }
            }
            param.put("mainFrameName", work.getString("mainFrameName"));
            String mainFrameCode = work.getString("mainFrameCode");
            param.put("mainFrameCode", mainFrameCode);

            param.put("machineName", machineName);
            param.put("machineCode", machineCode);
            if (mainFrameMap.containsKey(mainFrameCode) && mainFrameMap.get(mainFrameCode).getIsDefault() == true) {
                param.put("isDefault", "1");
            } else {
                param.put("isDefault", "0");
            }
            param.put("createUser", userCode);
            param.put("createTime", new Date());
            param.put("status", status);
            param.put("craftCode", craftCode);
            param.put("partCraftMainCode", partCraftMainCode);
            //因为第一次新建的时候可能没有选工位工序，但是修改的时候添加了工位工序，所以对工位工序来说，这一次还是新建
            if (BusinessConstants.Send2Pi.actionType_update.equals(operation)) {
                LOGGER.info("---------updateData--------------");
                param.put("updateUser", userCode);
                param.put("updateTime", new Date());
            }
            LOGGER.info("---------addData--------------");
            if (BusinessConstants.TableName.BIG_STYLE_ANALYSE.equals(tableName)) {
                styleSewingCraftWarehouseWorkplaceDao.addData(param);
            } else if (BusinessConstants.TableName.BIG_ORDER.equals(tableName)) {
                bigOrderSewingCraftWarehouseWorkplaceDao.addData(param);
            } else {
                sewingCraftWarehouseWorkplaceDao.addData(param);
            }

            LOGGER.info("参数信息是:" + JSONObject.toJSONString(param));
        }

    }

    /**
     * 生成车缝工序词库的编码
     * 1)、当勾选品类数量为1时，部件数量为1时，
     * 生成编码规则为：工艺品类代码（1位）+结构部件代码（2位）+做工类型（2位）+流水号（5位）， 如XASAT0001  ；
     * 2）、当勾选品类数量多个（大于1）时，部件数量为1时，
     * 生成编码规则为：工艺品类代码 固定为T（1位）+结构部件代码（2位）+做工类型（2位）+流水号（5位）,   如  TASAT0001 ；
     * 3）、当勾选品类数量为1时，部件数量为多个时，  工艺品类代码（1位）+工艺部件代码固定为TY（2位）+做工类型（2位）+流水号（5位）, 如  XTYAT0001 ；
     * 4）、当勾选品类数量多个（大于1）时，部件数量为多个时，
     * 生成编码规则为：工艺品类代码 固定为T（1位）+工艺部件代码固定为TY（2位）+做工类型（2位）+流水号（5位）,  如  TTYAT0001 ；
     * 只读，不可修改；（结构部件就是之前的工艺部件）
     */
    public String generateSewingCraftCode(JSONArray craftCategoryList, JSONArray craftPartList, String makeTypeCode, String sewing) throws Exception {
        if (null == craftCategoryList || craftCategoryList.size() == 0 ||
                null == craftPartList || craftPartList.size() == 0 || StringUtils.isEmpty(makeTypeCode)) {
            return "";
        }
        //工艺品类的数量
        int craftCategoryLength = craftCategoryList.size();
        //工艺部件的数量
        int craftPartLength = craftPartList.size();
        StringBuffer sb = new StringBuffer();
        //当勾选品类数量为1时，部件数量为1时，
        // 生成编码规则为：工艺品类代码（1位）+结构部件代码（2位）+做工类型（2位）+流水号（5位）， 如XASAT0001  ；
        JSONObject category = craftCategoryList.getJSONObject(0);
        JSONObject part = craftPartList.getJSONObject(0);
        if (1 == craftCategoryLength && 1 == craftPartLength) {

            //取 艺品类代码第一位（1位）
            sb.append(category.getString("craftCategoryCode").substring(0, 1));
            //结构部件代码（2位）
            sb.append(part.getString("craftPartCode").substring(part.getString("craftPartCode").length() - 2));
            //做工类型（2位）
            sb.append(makeTypeCode.substring(0, 2));
        }
        //当勾选品类数量多个（大于1）时，部件数量为1时，
        //生成编码规则为：工艺品类代码 固定为T（1位）+结构部件代码（2位）+做工类型（2位）+流水号（5位）,   如  TASAT0001
        else if (1 < craftCategoryLength && 1 == craftPartLength) {
            sb.append("T");
            //结构部件代码（2位）
            sb.append(part.getString("craftPartCode").substring(part.getString("craftPartCode").length() - 2));
            //做工类型（2位）
            sb.append(makeTypeCode.substring(0, 2));
        }
        //当勾选品类数量为1时，部件数量为多个时
        //工艺品类代码（1位）+工艺部件代码固定为TY（2位）+做工类型（2位）+流水号（5位）,
        else if (1 == craftCategoryLength && 1 < craftPartLength) {
            //取 艺品类代码第一位（1位）
            sb.append(category.getString("craftCategoryCode").substring(0, 1));
            sb.append("TY");
            //做工类型（2位）
            sb.append(makeTypeCode.substring(0, 2));

        }
        //当勾选品类数量多个（大于1）时，部件数量为多个时，
        // 工艺品类代码 固定为T（1位）+工艺部件代码固定为TY（2位）+做工类型（2位）+流水号（5位）,  如  TTYAT0001 ；
        else if (1 < craftCategoryLength && 1 < craftPartLength) {
            sb.append("TTY");
            //做工类型（2位）
            sb.append(makeTypeCode.substring(0, 2));
        }
        sb.append(CreateSerialUtil.createSerial(redisUtil, redisTemplate, sewing, sb.toString(), 5, 1));
        LOGGER.info("生产的车缝工序词库的编码是:" + sb.toString());
        return sb.toString();
    }


    public SewingCraftWarehouse getDataByRandom(Long randomCode) {
        return sewingCraftWarehouseDao.getDataByRandom(randomCode);
    }

    public List<SewingCraftWarehouse> getDataByCraftCodeList(List<String> codeList) {
        return sewingCraftWarehouseDao.getDataByCraftCodeList(codeList);
    }

    /**
     * 分页查询所有的数据
     */
    public List<SewingCraftWarehouse> getDataByMainFramePartCode(Map<String, Object> param) {
        List<SewingCraftWarehouse> data = sewingCraftWarehouseDao.getDataByMainFramePartCode(param);
        if (null == data || data.size() == 0) {
            return Collections.emptyList();
        }
        //并行去查询每一条工位工序 相关联的数据
        data.stream().parallel().forEach(x -> {
            //查所选择的建标系统的 线稿图和视频
            getSketchPicAndVedio(x);
            //查询动作代码
            getMitonList(x);
        });
        return data;
    }

    /**
     * 分页查询所有的数据
     */
    public List<SewingCraftWarehouse> getDataByPager(String keywords, String craftCode, String craftName, String description, String craftCategoryCode, String craftPartCode, Integer status, String mainFrameCode, List<String> createUserList, List<String> releaseUserList, String createTimeBeginDate, String createTimeEndDate, String releaseTimeBeginDate, String releaseTimeEndDate) {
        Map<String, Object> param = new HashMap<>();
        if (StringUtils.isNotEmpty(craftCode)) {
            param.put("craftCode", craftCode);
        }
        if (StringUtils.isNotEmpty(craftName)) {
            param.put("craftName", craftName);
        }
        if (StringUtils.isNotEmpty(description)) {
            param.put("description", description);
        }
        param.put("craftCategoryCode", craftCategoryCode);
        param.put("craftPartCode", craftPartCode);
        param.put("keywords", keywords);
        param.put("status", status);
        param.put("mainFrameCode", mainFrameCode);
        param.put("createUser", createUserList);
        param.put("releaseUser", releaseUserList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isNotEmpty(createTimeBeginDate)) {
                Date createTimeBegin = sdf.parse(createTimeBeginDate);
                param.put("createTimeBeginDate", createTimeBegin);
            }
            if (StringUtils.isNotEmpty(createTimeEndDate)) {
                Date createTimeEnd = sdf.parse(createTimeEndDate);
                param.put("createTimeEndDate", createTimeEnd);
            }
            if (StringUtils.isNotEmpty(releaseTimeBeginDate)) {
                Date releaseTimeBegin = sdf.parse(releaseTimeBeginDate);
                param.put("releaseTimeBeginDate", releaseTimeBegin);
            }
            if (StringUtils.isNotEmpty(releaseTimeEndDate)) {
                Date releaseTimeEnd = sdf.parse(releaseTimeEndDate);
                param.put("releaseTimeEndDate", releaseTimeEnd);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        List<SewingCraftWarehouse> data = sewingCraftWarehouseDao.getDataByPager(param);
        if (null == data || data.size() == 0) {
            return Collections.emptyList();
        }
        final Map<String, User> map = userDao.getUserMap();
        final Map<String, MakeType> makeTypeMap = makeTypeDao.getMakeTypeMap();
        //并行去查询每一条工位工序 相关联的数据
        data.stream().parallel().forEach(x -> {
            if (map.containsKey(x.getCreateUser())) {
                x.setCreateUserName(map.get(x.getCreateUser()).getUserName());
            } else {
                x.setCreateUserName("");
                if (StringUtils.isNotEmpty(x.getCreateUser())) {
                    //针对导入的数据
                    String[] split = x.getCreateUser().split("-");
                    if (split != null && split.length == 2) {
                        x.setCreateUserName(split[1]);
                    }
                }
            }
            if (map.containsKey(x.getUpdateUser())) {
                x.setUpdateUserName(map.get(x.getUpdateUser()).getUserName());
            } else {
                x.setUpdateUserName("");
                if (StringUtils.isNotEmpty(x.getUpdateUser())) {
                    //针对导入的数据
                    String[] split = x.getUpdateUser().split("-");
                    if (split != null && split.length == 2) {
                        x.setUpdateUserName(split[1]);
                    }
                }
            }
            if (map.containsKey(x.getReleaseUser())) {
                x.setReleaseUserName(map.get(x.getReleaseUser()).getUserName());
            } else {
                x.setReleaseUserName("");
                if (StringUtils.isNotEmpty(x.getReleaseUser())) {
                    //针对导入的数据
                    String[] split = x.getReleaseUser().split("-");
                    if (split != null && split.length == 2) {
                        x.setReleaseUserName(split[1]);
                    }
                }
            }
            if (makeTypeMap.containsKey(x.getMakeTypeCode())) {
                x.setMakeTypeCode(makeTypeMap.get(x.getMakeTypeCode()).getMakeTypeName());

            }
            getRelatedData(x);
        });
        return data;
    }

    /**
     * 把车缝工序词库对应的工序建标和工位工序的信息和附带查出来
     */
    private void getRelatedData(SewingCraftWarehouse vo) {
        if (null == vo) {
            return;
        }
        //查所选择的建标系统的 线稿图和视频
        getSketchPicAndVedio(vo);
        //查所选择的建标系统的 工位工序
        getWorkplaceNameList(vo);
    }

    /**
     * 查询车缝工序里面的动作代码
     */
    private void getMitonList(SewingCraftWarehouse vo) {
        if (null == vo) {
            return;
        }
        List<SewingCraftAction> actions = sewingCraftActionDao.getDataBySewingCraftRandomCode(vo.getRandomCode());
        vo.setMotionsList(actions);
    }

    /**
     * 查所选择的建标系统的 工位工序
     */
    private void getWorkplaceNameList(SewingCraftWarehouse vo) {
        if (null == vo) {
            return;
        }
        List<String> workplaceNameList = sewingCraftWarehouseWorkplaceDao.getWorkplaceBysewingRandomCode(vo.getRandomCode());
        vo.setWorkplaceNameList(workplaceNameList);
    }

    /**
     * 查询单个车缝工序，选中的建标
     */
    private void getSketchPicAndVedio(SewingCraftWarehouse vo) {
        if (null == vo) {
            return;
        }
        if (null == vo.getSketchPicUrl()) {
            vo.setSketchPicUrl(new ArrayList<>());
        }
        if (null == vo.getVedioUrl()) {
            vo.setVedioUrl(new ArrayList<>());
        }
        //查所选择的建标系统的 线稿图和视频
        List<SewingCraftResource> resources = null;
        if (null != resources && resources.size() > 0) {
            for (SewingCraftResource res : resources) {
                if (res.getResouceType().intValue() == Const.RES_TYPE_HAND_IMG) {
                    vo.getSketchPicUrl().add(res.getUrl());
                } else if (res.getResouceType().intValue() == Const.RES_TYPE_STD_VIDEO) {
                    vo.getVedioUrl().add(res.getUrl());
                }
            }
        }
    }

    public Map<String, SewingCraftWarehouse> getCraftFlowNumByCraftCodeAndMainFrameCode(List<String> craftCodeList, String mainFrameCode) {
        return sewingCraftWarehouseDao.getCraftFlowNumByCraftCodeAndMainFrameCode(craftCodeList, mainFrameCode);
    }

    public List<SewingCraftVo> getCraftWareByParams(String params, String clothingCategoryCode) {
        List<SewingCraftVo> list = sewingCraftWarehouseWorkplaceDao.searchSewingCraftDataByParams(params, clothingCategoryCode);
        return list;
    }

    public List<VSewingCraftVo> getCraftCodeDataAll(String[] craftCodeArray, int materialGrade, String mainFrameCode) {
        List<VSewingCraftVo> list = new ArrayList<>();
        try {
            //list = sewingCraftWarehouseDao.getCraftCodeDataAll(craftCodeArray, materialGrade, mainFrameCode);
            list.parallelStream().forEach(house -> {
                house.setMotionsList(sewingCraftActionDao.getDataBySewingCraftRandomCode(house.getRandomCode()));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public List<VSewingCraftVo> getCraftCodeCraftFlowNumDataAll(String[] craftCodeArray, String mainFrameCode) {
        List<VSewingCraftVo> list = new ArrayList<>();
        try {
            list = sewingCraftWarehouseDao.getCraftCodeCraftFlowNumDataAll(craftCodeArray, mainFrameCode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public Result getSewingCraftRandomCode(String craftCode) {
        Long randomCode = sewingCraftWarehouseDao.getSewingCraftRandomCode(craftCode);
        return Result.ok(randomCode);
    }

    public int updateRelateCraftInfo(String craftCode) {
        return sewingCraftWarehouseDao.updateRelateCraftInfo(craftCode);
    }

    public String getCraftInUsed(String craftCode) {
        return sewingCraftWarehouseDao.getCraftInUsed(craftCode);
    }
}
