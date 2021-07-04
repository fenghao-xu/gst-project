package com.ylzs.service.partCombCraft.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.*;
import com.ylzs.dao.partCombCraft.PartCombCraftDao;
import com.ylzs.dao.partCombCraft.PartCombCraftProgramDetailDao;
import com.ylzs.dao.partCombCraft.PartCombCraftRuleDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.entity.partCombCraft.PartCombCraft;
import com.ylzs.entity.partCombCraft.PartCombCraftProgramDetail;
import com.ylzs.entity.partCombCraft.PartCombCraftRule;
import com.ylzs.entity.partCombCraft.req.PartCombCraftProgramReq;
import com.ylzs.entity.partCombCraft.req.PartCombCraftReq;
import com.ylzs.entity.partCombCraft.req.QueryPartCombCraftReq;
import com.ylzs.entity.partCombCraft.req.UpdatePartCombCraftReq;
import com.ylzs.entity.partCombCraft.resp.*;
import com.ylzs.entity.system.User;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCombCraft.IPartCombCraftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author weikang
 * @Description 部件组合工艺
 * @Date 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PartCombCraftServiceImpl extends OriginServiceImpl<PartCombCraftDao, PartCombCraft> implements IPartCombCraftService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartCombCraftServiceImpl.class);

    @Resource
    private PartCombCraftProgramDetailDao detailDao;

    @Resource
    private PartCombCraftDao partCombCraftDao;

    @Resource
    private PartCombCraftRuleDao combCraftRuleDao;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private UserDao userDao;

    @Override
    public Result addPartCombCraft(PartCombCraftReq req) {
        PartCombCraft partCombCraft = req.getCombCraft();
        if (partCombCraft == null) {
            LOGGER.info("addPartCombCraft 部件组合工艺主数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "工艺主数据为空");
        }
        List<PartCombCraftRule> partCombCraftRules = req.getCraftRuleList();
        if (CollUtil.isEmpty(partCombCraftRules)) {
            LOGGER.info("addPartCombCraft 部件组合工艺规则为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "规则为空");
        }
        List<PartCombCraftProgramReq> partProgramReq = req.getPartProgramReq();
        if (CollUtil.isEmpty(partProgramReq)) {
            LOGGER.info("addPartCombCraft 部件组合工艺方案数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "方案数据为空");
        }
        String userCode = req.getUserCode();
        if (StringUtils.isBlank(userCode)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "用户信息为空，不能操作");
        }
        long count = partProgramReq.stream().distinct().count();
        if (count < partProgramReq.size()) {
            LOGGER.info("addPartCombCraft 部件组合工艺方案重复");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "方案重复");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("clothingCategoryCode", partCombCraft.getClothingCategoryCode());
        if (partCombCraft.getRandomCode() != null && partCombCraft.getRandomCode().longValue() != 0L) {
            map.put("randomCode", partCombCraft.getRandomCode());
        }
        //查询名称是否重复
        int nameCount = partCombCraftDao.selectNameCount(partCombCraft.getRandomCode(),partCombCraft.getPartCombCraftName());
        if(nameCount>BusinessConstants.CommonConstant.ZERO){
            LOGGER.info("partCombCraftDao.selectNameCount 工艺名称重复");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "工艺名称重复");
        }
        //根据服装品类查询未生效的部件组合工艺
        List<String> programResps = detailDao.selectListByCategoryCode(map);
        if (CollUtil.isNotEmpty(programResps)) {
            //对部件组合工艺数据排序
            List<String> programList = convertProgramResp(programResps);
            for (PartCombCraftProgramReq combCraftProgramReq : partProgramReq) {
                String designCodeAndPositionCode = combCraftProgramReq.getDesignCodeAndPositionCode();
                if (programList.contains(designCodeAndPositionCode)) {
                    LOGGER.info("addPartCombCraft 部件组合已存在【{}】", designCodeAndPositionCode);
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "部件组合" + combCraftProgramReq.getPartNumber() + "已存在");
                }
            }
        }
        //对草稿进行修改
        if (partCombCraft.getRandomCode() != null && partCombCraft.getRandomCode().longValue() != 0L) {
            Result result = commonGetResult(partCombCraft, map, userCode);
            if (result != null) return result;
        } else {
            //新增部件组合工艺主数据
            partCombCraft.setCreateUser(userCode);
            partCombCraft.setCreateTime(new Date());
            partCombCraft.setUpdateUser(userCode);
            partCombCraft.setUpdateTime(new Date());
            partCombCraft.setRandomCode(SnowflakeIdUtil.generateId());
            partCombCraft.setPartCombCraftCode(BusinessConstants.Prefix.PART_COMB_PREFIX + createSerial());
            int iPart = partCombCraftDao.insert(partCombCraft);
            if (iPart < BusinessConstants.CommonConstant.ONE) {
                LOGGER.error("新增部件组合工艺失败【{}】", partCombCraft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "新增工艺失败");
            }
        }
        Result result = insertPartCombProgramAndRule(partCombCraft, partCombCraftRules, partProgramReq, userCode);
        if (result != null) return result;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode);
        User user = userDao.selectOne(queryWrapper);
        partCombCraft.setUpdateUser(user.getUserName());
        return Result.ok(partCombCraft);
    }

    /**
     * 对部件组合工艺数据排序
     *
     * @param programResps
     * @return
     */
    private List<String> convertProgramResp(List<String> programResps) {
        List<String> programRespList = new ArrayList<>();
        for (String programResp : programResps) {
            String[] array = programResp.split(",");
            Arrays.sort(array);
            String join = StringUtils.join(array, ",");
            programRespList.add(join);
        }
        return programRespList;
    }

    /**
     * 新增部件组合工艺规则和部件
     *
     * @param partCombCraft      工艺主数据
     * @param partCombCraftRules 工艺规则
     * @param partProgramReq     部件组合工艺方案
     * @param userCode           用户编码
     * @return
     */
    private Result insertPartCombProgramAndRule(PartCombCraft partCombCraft, List<PartCombCraftRule> partCombCraftRules, List<PartCombCraftProgramReq> partProgramReq, String userCode) {
        //新增工艺规则
        for (PartCombCraftRule rule : partCombCraftRules) {
            rule.setRandomCode(SnowflakeIdUtil.generateId());
            rule.setPartCombCraftRandomCode(partCombCraft.getRandomCode());
            rule.setCreateUser(userCode);
            rule.setUpdateUser(userCode);
            rule.setUpdateTime(new Date());
            rule.setStatus(partCombCraft.getStatus());
            int iRule = combCraftRuleDao.insert(rule);
            if (iRule < BusinessConstants.CommonConstant.ONE) {
                LOGGER.error("新增部件组合工艺规则失败【{}】", rule.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "新增规则失败");
            }
        }

        //新增工艺方案
        for (PartCombCraftProgramReq combCraftProgramReq : partProgramReq) {
            List<PartCombCraftProgramDetail> programDetailList = combCraftProgramReq.getProgramDetailList();
            for (PartCombCraftProgramDetail detail : programDetailList) {
                detail.setCreateUser(userCode);
                detail.setUpdateUser(userCode);
                detail.setUpdateTime(new Date());
                detail.setRandomCode(SnowflakeIdUtil.generateId());
                detail.setStatus(partCombCraft.getStatus());
                detail.setPartCombCraftRandomCode(partCombCraft.getRandomCode());
                int iDetail = detailDao.insert(detail);
                if (iDetail < BusinessConstants.CommonConstant.ONE) {
                    LOGGER.error("新增部件组合工艺方案失败【{}】", detail.toString());
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "新增方案失败");
                }
            }
        }
        return null;
    }

    private Result commonGetResult(PartCombCraft partCombCraft, Map<String, Object> map, String userCode) {
        partCombCraft.setUpdateUser(userCode);
        partCombCraft.setUpdateTime(new Date());

        UpdateWrapper<PartCombCraft> craftUpdateWrapper = new UpdateWrapper<>();
        craftUpdateWrapper.eq("random_code", partCombCraft.getRandomCode());
        int uPart = partCombCraftDao.update(partCombCraft, craftUpdateWrapper);
        if (uPart == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改部件组合工艺失败【{}】", partCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改工艺失败");
        }
        //对原草稿规则进行删除
        map.clear();
        map.put("part_comb_craft_random_code", partCombCraft.getRandomCode());
        int dRule = combCraftRuleDao.deleteByMap(map);
        if (dRule == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("覆盖部件组合工艺规则失败【{}】", partCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖规则失败");
        }
        //对草稿工艺方案进行删除
        int dDetail = detailDao.deleteByMap(map);
        if (dDetail == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("覆盖部件组合工艺方案失败【{}】", partCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖方案失败");
        }
        return null;
    }

    @Override
    public Result updatePartCombCraft(UpdatePartCombCraftReq req) {
        String userCode = req.getUserCode();
        Long randomCode = req.getRandomCode();
        PartCombCraft partCombCraft = new PartCombCraft();
        //草稿 -->删除作废
        if (req.getOperateType().intValue() == BusinessConstants.CommonConstant.ONE) {
            partCombCraft.setStatus(BusinessConstants.Status.IN_VALID);
            //发布-->失效
        } else if (req.getOperateType().intValue() == BusinessConstants.CommonConstant.TWO) {
            partCombCraft.setStatus(BusinessConstants.Status.INVALID_STATUS);
        }
        partCombCraft.setUpdateUser(userCode);
        //修改主数据
        UpdateWrapper<PartCombCraft> craftUpdateWrapper = new UpdateWrapper<>();
        craftUpdateWrapper.eq("random_code", randomCode);
        int uPart = partCombCraftDao.update(partCombCraft, craftUpdateWrapper);
        if (uPart == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改部件组合工艺状态失败【{}】", partCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改工艺状态失败");
        }

        //修改方案
        PartCombCraftProgramDetail detail = new PartCombCraftProgramDetail();
        detail.setStatus(partCombCraft.getStatus());
        UpdateWrapper<PartCombCraftProgramDetail> detailUpdateWrapper = new UpdateWrapper<>();
        detailUpdateWrapper.eq("part_comb_craft_random_code", randomCode);
        int uDetail = detailDao.update(detail, detailUpdateWrapper);
        if (uDetail == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改部件组合工艺方案状态失败【{}】", partCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改工艺方案状态失败");
        }

        //修改工艺规则
        PartCombCraftRule rule = new PartCombCraftRule();
        rule.setStatus(partCombCraft.getStatus());
        UpdateWrapper<PartCombCraftRule> ruleUpdateWrapper = new UpdateWrapper<>();
        ruleUpdateWrapper.eq("part_comb_craft_random_code", randomCode);
        int uRule = combCraftRuleDao.update(rule, ruleUpdateWrapper);
        if (uRule == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改部件组合工艺规则状态失败【{}】", partCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改工艺规则状态失败");
        }

        return Result.ok();
    }

    @Override
    public List<PartCombCraft> selectPartCombCraftChecklist(QueryPartCombCraftReq craftReq) {
        return partCombCraftDao.selectPartCombCraftChecklist(craftReq);
    }

    @Override
    public Result selectPartCombCraftProgramDetail(String randomCode) {
        List<PartCombCraftProgramDetailResp> resps = detailDao.selectListByCraftRandomCode(randomCode);
        Map<Integer, List<PartCombCraftProgramDetailResp>> collect = resps.stream().collect(Collectors.groupingBy(PartCombCraftProgramDetailResp::getPartNumber));
        return Result.ok(collect);
    }

    @Override
    public Result selectPartCombCraftData(String randomCode) {
        CountDownLatch countDownLatch = new CountDownLatch(BusinessConstants.CommonConstant.THREE);
        PartCombCraftDataResp data = new PartCombCraftDataResp();
        //查询部件组合工艺主数据
        taskExecutor.execute(() -> {
            try {
                PartCombCraft craft = partCombCraftDao.selectPartCombCraft(Long.parseLong(randomCode));
                if (craft != null) data.setPartCombCraft(craft);
            } catch (Exception e) {
                LOGGER.error("selectPartCombCraftData.partCombCraftDao.selectOne fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });
        //查询部件工艺规则数据
        taskExecutor.execute(() -> {
            try {
                List<PartCombCraftRule> ruleList = combCraftRuleDao.selectRuleListByCraftRandomCode(Long.parseLong(randomCode));
                if (CollUtil.isNotEmpty(ruleList)) {
                    data.setRuleList(ruleList);
                }
            } catch (Exception e) {
                LOGGER.error("selectPartCombCraftData.selectActionListByRandomCode fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        //查询部件组合工艺部件详情数据
        taskExecutor.execute(() -> {
            try {
                QueryWrapper<PartCombCraftProgramDetail> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("part_comb_craft_random_code", Long.parseLong(randomCode));
                List<PartCombCraftProgramDetail> resps = detailDao.selectList(queryWrapper);
                Map<Integer, List<PartCombCraftProgramDetail>> collect = resps.stream().collect(Collectors.groupingBy(PartCombCraftProgramDetail::getPartNumber));
                if (CollUtil.isNotEmpty(collect)) data.setDetailResps(collect);
            } catch (Exception e) {
                LOGGER.error("selectPartCombCraftData.partCombCraftDao.selectList fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (Exception e) {
            LOGGER.error("selectMaterialCraftData system error", e);
            return Result.fail(0);
        }

        return Result.ok(data);
    }

    @Override
    public Result selectPartCreateUser() {
        List<UserResp> users = partCombCraftDao.selectPartCreateUser();
        return Result.ok(users);
    }

    @Override
    public List<PartCombCraftAllDataResp> selectPartDataByCategoryCode(String clothingCategoryCode, String[] codeArray) {
        List<PartCombCraftAllDataResp> resps = CollUtil.newArrayList();
        try {
            if (StringUtils.isBlank(clothingCategoryCode)) {
                return resps;
            }
            resps = partCombCraftDao.selectListByCategoryCode(clothingCategoryCode, codeArray);
        } catch (Exception e) {
            LOGGER.info("selectPartDataByCategoryCode fails", e);
        }
        return resps;
    }


    public static void main(String[] args) {
        List<PartCombCraftRuleResp> list1 = new ArrayList<>() ;
        PartCombCraftRuleResp resp = new PartCombCraftRuleResp();
        resp.setSourceCraftCodeAndName("55-18");
        resp.setRuleIndex(1);
        resp.setProcessType(1);
        list1.add(resp);
        PartCombCraftRuleResp resp2 = new PartCombCraftRuleResp();
        resp2.setSourceCraftCodeAndName("83-17");
        resp2.setRuleIndex(2);
        resp2.setProcessType(2);
        list1.add(resp2);
        Stream<Integer> integerStream = list1.stream().filter(resp3 -> resp3.getRuleIndex() == 1).map(PartCombCraftRuleResp::getProcessType);
        List<Integer> collect = integerStream.collect(Collectors.toList());
        System.out.println(collect);
//        List<PartCombCraftRuleResp> list2 = new ArrayList<>() ;
//        PartCombCraftRuleResp resp3 = new PartCombCraftRuleResp();
//        resp3.setRuleIndex(1);
//        resp3.setProcessType(2);
//        list2.add(resp3);
//        PartCombCraftRuleResp resp4 = new PartCombCraftRuleResp();
//        resp4.setActionCraftCodeAndName("28-35,29-35");
//        resp4.setRuleIndex(2);
//        resp4.setProcessType(2);
//        list2.add(resp4);
//        System.out.println(list2.contains(2));
//        List<PartCombCraftRuleResp> newVoList = list1.parallelStream()
//                .map(x -> list2.stream()
//                        .filter(y -> y!=null&&y.getRuleIndex()!=null &&(x.getRuleIndex().equals(y.getRuleIndex())))
//                        .findFirst()
//                        .map(y -> {
//                            x.setActionCraftCodeAndName(y.getActionCraftCodeAndName());
//                            return x;
//                        }).orElse(null))
//                .collect(Collectors.toList());
    }


    /**
     * 产生序列号
     */
    private String createSerial() {
        String serialNumber = "";
        boolean getLock;
        String requestId = UUIDUtil.uuid32();
        try {
            getLock = redisUtil.tryGetDistributedLock(BusinessConstants.Redis.PART_COMB_CRAFT_LOCK_KEY, requestId, RedisContants.EXPIRE_TIME);
            if (getLock) {

                Object serialno = redisUtil.get(BusinessConstants.Redis.PART_COMB_CRAFT_SERIAL_NUMBER);
                //第一个序列号生成
                if (null == serialno) {
                    serialNumber = String.format("%06d", 1);
                    //将缓存放入
                    redisUtil.set(BusinessConstants.Redis.PART_COMB_CRAFT_SERIAL_NUMBER, 1);
                } else {
                    //先把序列号加1；
                    redisUtil.incr(BusinessConstants.Redis.PART_COMB_CRAFT_SERIAL_NUMBER, 1);
                    serialNumber = String.format("%06d", redisUtil.get(BusinessConstants.Redis.PART_COMB_CRAFT_SERIAL_NUMBER));
                }
                LOGGER.info("---部件组合工艺编码生成的序列号是:" + serialNumber);
            } else {
                LOGGER.info("---部件组合工艺编码获取分布式锁失败!!!-----------");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("获取分布式锁失败，" + e.getMessage());
        } finally {
            //一定要释放锁
            redisUtil.releaseDistributedLock(BusinessConstants.Redis.PART_COMB_CRAFT_LOCK_KEY, requestId);
        }
        return serialNumber;
    }
}
