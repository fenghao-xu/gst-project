package com.ylzs.service.processCombCraft.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.RedisContants;
import com.ylzs.common.util.*;
import com.ylzs.dao.processCombCraft.ProcessCombCraftDao;
import com.ylzs.dao.processCombCraft.ProcessCombCraftProgramDao;
import com.ylzs.dao.processCombCraft.ProcessCombCraftRuleDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.entity.partCombCraft.resp.UserResp;
import com.ylzs.entity.processCombCraft.ProcessCombCraft;
import com.ylzs.entity.processCombCraft.ProcessCombCraftProgram;
import com.ylzs.entity.processCombCraft.ProcessCombCraftRule;
import com.ylzs.entity.processCombCraft.req.ProcessCombCraftProgramReq;
import com.ylzs.entity.processCombCraft.req.ProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.req.QueryProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.req.UpdateProcessCombCraftReq;
import com.ylzs.entity.processCombCraft.resp.*;
import com.ylzs.entity.system.User;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCombCraft.impl.PartCombCraftServiceImpl;
import com.ylzs.service.processCombCraft.IProcessCombCraftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author weikang
 * @Description 工序组合工艺
 * @Date 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessCombCraftServiceImpl extends OriginServiceImpl<ProcessCombCraftDao, ProcessCombCraft> implements IProcessCombCraftService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartCombCraftServiceImpl.class);

    @Resource
    private ProcessCombCraftDao combCraftDao;

    @Resource
    private ProcessCombCraftRuleDao ruleDao;

    @Resource
    private ProcessCombCraftProgramDao programDao;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private UserDao userDao;

    @Override
    public Result addProcessCombCraft(ProcessCombCraftReq req) {
        ProcessCombCraft combCraft = req.getProcessCombCraft();
        if (combCraft == null) {
            LOGGER.info("addProcessCombCraft 工序组合工艺主数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "工艺主数据为空");
        }
        List<ProcessCombCraftProgramReq> programs = req.getProgramReq();
        if (CollUtil.isEmpty(programs)) {
            LOGGER.info("addProcessCombCraft 工序组合工艺方案主数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "方案数据为空");
        }
        List<ProcessCombCraftRule> rules = req.getRules();
        if (CollUtil.isEmpty(rules)) {
            LOGGER.info("addProcessCombCraft 工序组合工艺规则主数据为空");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "规则数据为空");
        }
        long count = programs.stream().distinct().count();
        if (count < programs.size()) {
            LOGGER.info("addProcessCombCraft 工序组合工艺方案重复");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "方案重复");
        }
        String userCode = req.getUserCode();
        if (StringUtils.isBlank(userCode)) {
            return Result.build(BusinessConstants.CommonConstant.ZERO, "用户信息为空，不能操作");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("clothingCategoryCode", combCraft.getClothingCategoryCode());
        if (combCraft.getRandomCode() != BusinessConstants.CommonConstant.ZERO) {
            map.put("randomCode", combCraft.getRandomCode());
        }
        //查询名称是否重复
        int nameCount = combCraftDao.selectNameCount(combCraft.getRandomCode(), combCraft.getProcessCombCraftName());
        if (nameCount > BusinessConstants.CommonConstant.ZERO) {
            LOGGER.info("combCraftDao.selectNameCount 工艺名称重复");
            return Result.build(BusinessConstants.CommonConstant.ZERO, "工艺名称重复");
        }
        //根据服装品类查询未生效的工序组合工艺
        List<String> programResps = combCraftDao.selectListByCategoryCode(map);
        if (CollUtil.isNotEmpty(programResps)) {
            //对工序组合工艺数据排序
            List<String> programRespList = convertProgramResp(programResps);
            for (ProcessCombCraftProgramReq program : programs) {
                if (programRespList.contains(program.getProcessCraftCodes())) {
                    LOGGER.info("addProcessCombCraft 工序组合已存在");
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "工序组合" + program.getProcessNumber() + "已存在");
                }
            }
        }
        //对草稿进行修改
        if (combCraft.getRandomCode().longValue() != 0L) {
            combCraft.setUpdateUser(userCode);
            UpdateWrapper<ProcessCombCraft> craftUpdateWrapper = new UpdateWrapper<>();
            craftUpdateWrapper.eq("random_code", combCraft.getRandomCode());
            int uPart = combCraftDao.update(combCraft, craftUpdateWrapper);
            if (uPart == BusinessConstants.CommonConstant.ZERO) {
                LOGGER.error("修改工序组合工艺失败【{}】", combCraft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "修改工艺失败");
            }

            //对原草稿规则进行删除
            map.clear();
            map.put("process_comb_craft_random_code", combCraft.getRandomCode());
            int dRule = ruleDao.deleteByMap(map);
            if (dRule == BusinessConstants.CommonConstant.ZERO) {
                LOGGER.error("覆盖工序组合工艺规则失败【{}】", combCraft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖规则失败");
            }
            //对原草稿方案进行删除
            int dProgram = programDao.deleteByMap(map);
            if (dProgram == BusinessConstants.CommonConstant.ZERO) {
                LOGGER.error("覆盖工序组合工艺方案失败【{}】", combCraft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "覆盖方案失败");
            }
        } else {
            combCraft.setCreateUser(userCode);
            combCraft.setUpdateUser(userCode);
            combCraft.setUpdateTime(new Date());
            combCraft.setRandomCode(SnowflakeIdUtil.generateId());
            combCraft.setProcessCombCraftCode(BusinessConstants.Prefix.PROCESS_COMB_PREFIX + createSerial());
            int iComb = combCraftDao.insert(combCraft);
            if (iComb == BusinessConstants.CommonConstant.ZERO) {
                LOGGER.error("新增工序组合工艺失败【{}】", combCraft.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "新增工艺失败");
            }
        }
        Result result = insertProcessCombCraftData(combCraft, programs, rules, userCode);
        if (result != null) return result;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_code", userCode);
        User user = userDao.selectOne(queryWrapper);
        combCraft.setUpdateUser(user.getUserName());
        return Result.ok(combCraft);
    }

    /**
     * 对工序组合工艺数据排序
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
     * 新增规则和方案
     *
     * @param combCraft 部件工艺数据
     * @param programs  方案数据
     * @param rules     规则数据
     * @param userCode  用户编码
     * @return
     */
    private Result insertProcessCombCraftData(ProcessCombCraft combCraft, List<ProcessCombCraftProgramReq> programs, List<ProcessCombCraftRule> rules, String userCode) {
        //新增规则
        for (ProcessCombCraftRule rule : rules) {
            rule.setCreateUser(userCode);
            rule.setUpdateUser(userCode);
            rule.setUpdateTime(new Date());
            rule.setRandomCode(SnowflakeIdUtil.generateId());
            rule.setProcessCombCraftRandomCode(combCraft.getRandomCode());
            rule.setStatus(combCraft.getStatus());
            int iRule = ruleDao.insert(rule);
            if (iRule < BusinessConstants.CommonConstant.ONE) {
                LOGGER.error("新增工序组合工艺规则失败【{}】", rule.toString());
                return Result.build(BusinessConstants.CommonConstant.ZERO, "新增规则失败");
            }
        }
        //新增方案
        for (ProcessCombCraftProgramReq programReq : programs) {
            List<ProcessCombCraftProgram> programList = programReq.getPrograms();
            for (ProcessCombCraftProgram program : programList) {
                program.setCreateUser(userCode);
                program.setUpdateUser(userCode);
                program.setUpdateTime(new Date());
                program.setRandomCode(SnowflakeIdUtil.generateId());
                program.setProcessCombCraftRandomCode(combCraft.getRandomCode());
                program.setStatus(combCraft.getStatus());
                int iProgram = programDao.insert(program);
                if (iProgram < BusinessConstants.CommonConstant.ONE) {
                    LOGGER.error("新增工序组合工艺方案失败【{}】", program.toString());
                    return Result.build(BusinessConstants.CommonConstant.ZERO, "新增工序方案失败");
                }
            }
        }
        return null;
    }

    @Override
    public Result selectProcessCombCraftData(String randomCode) {
        CountDownLatch countDownLatch = new CountDownLatch(BusinessConstants.CommonConstant.THREE);
        ProcessCombCraftData data = new ProcessCombCraftData();
        //查询工艺组合工艺主数据
        taskExecutor.execute(() -> {
            try {
                ProcessCombCraft processCombCraft = combCraftDao.selectProcessCombCraft(Long.parseLong(randomCode));
                if (processCombCraft != null) data.setCombCraft(processCombCraft);
            } catch (Exception e) {
                LOGGER.error("selectProcessCombCraftData.partCombCraftDao.selectOne fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });
        //查询工序组合工艺方案
        taskExecutor.execute(() -> {
            try {
                QueryWrapper<ProcessCombCraftProgram> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("process_comb_craft_random_code", Long.parseLong(randomCode));
                List<ProcessCombCraftProgram> resps = programDao.selectList(queryWrapper);
                Map<Integer, List<ProcessCombCraftProgram>> collect = resps.stream().collect(Collectors.groupingBy(ProcessCombCraftProgram::getProcessNumber));
                if (CollUtil.isNotEmpty(collect)) data.setProgramResps(collect);
            } catch (Exception e) {
                LOGGER.error("selectProcessCombCraftData.selectList fail", e);
            } finally {
                countDownLatch.countDown();
            }
        });
        //查询工序组合工艺规则
        taskExecutor.execute(() -> {
            try {
                List<ProcessCombCraftRule> ruleList = ruleDao.selectRuleListByCraftRandomCode(Long.parseLong(randomCode));
                if (CollUtil.isNotEmpty(ruleList)) {
                    data.setRuleList(ruleList);
                }
            } catch (Exception e) {
                LOGGER.error("selectProcessCombCraftData.selectRuleListByCraftRandomCode fail", e);
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
    public Result selectProcessCombCraftProgram(String randomCode) {
        List<QueryProcessCombCraftProgramResp> programResp = programDao.selectProgramListByCraftRandomCode(Long.parseLong(randomCode));
        Map<Integer, List<QueryProcessCombCraftProgramResp>> collect = programResp.stream().collect(Collectors.groupingBy(QueryProcessCombCraftProgramResp::getProcessNumber));
        return Result.ok(collect);
    }

    @Override
    public List<ProcessCombCraft> selectProcessCombCraftChecklist(QueryProcessCombCraftReq craftReq) {
        return combCraftDao.selectProcessCombCraftChecklist(craftReq);
    }

    @Override
    public Result updateProcessCombCraft(UpdateProcessCombCraftReq req) {
        String userCode = req.getUserCode();
        Long randomCode = req.getRandomCode();
        ProcessCombCraft processCombCraft = new ProcessCombCraft();
        //草稿 -->删除作废
        if (req.getOperateType() == BusinessConstants.CommonConstant.ONE) {
            processCombCraft.setStatus(BusinessConstants.Status.IN_VALID);
            //发布-->失效
        } else {
            processCombCraft.setStatus(BusinessConstants.Status.INVALID_STATUS);
        }
        processCombCraft.setUpdateUser(req.getUserCode());
        //修改主数据
        UpdateWrapper<ProcessCombCraft> craftUpdateWrapper = new UpdateWrapper<>();
        craftUpdateWrapper.eq("random_code", randomCode);
        int uPart = combCraftDao.update(processCombCraft, craftUpdateWrapper);
        if (uPart == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改工序组合工艺状态失败【{}】", processCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改工艺状态失败");
        }
        //修改方案
        ProcessCombCraftProgram program = new ProcessCombCraftProgram();
        processCombCraft.setStatus(processCombCraft.getStatus());
        processCombCraft.setUpdateUser(userCode);
        UpdateWrapper<ProcessCombCraftProgram> programUpdateWrapper = new UpdateWrapper<>();
        programUpdateWrapper.eq("process_comb_craft_random_code", randomCode);
        int uProgram = programDao.update(program, programUpdateWrapper);
        if (uProgram == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改工序组合工艺方案状态失败【{}】", processCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改方案状态失败");
        }
        //修改规则
        ProcessCombCraftRule rule = new ProcessCombCraftRule();
        rule.setStatus(processCombCraft.getStatus());
        rule.setUpdateUser(userCode);
        UpdateWrapper<ProcessCombCraftRule> ruleUpdateWrapper = new UpdateWrapper<>();
        ruleUpdateWrapper.eq("process_comb_craft_random_code", randomCode);
        int uRule = ruleDao.update(rule, ruleUpdateWrapper);
        if (uRule == BusinessConstants.CommonConstant.ZERO) {
            LOGGER.error("修改工序组合工艺规则状态失败【{}】", processCombCraft.toString());
            return Result.build(BusinessConstants.CommonConstant.ZERO, "修改规则状态失败");
        }
        return Result.ok();
    }

    @Override
    public Result selectProcessCreateUser() {
        List<UserResp> userResps = combCraftDao.selectProcessCreateUser();
        return Result.ok(userResps);
    }

    @Override
    public List<ProcessCombCraftAllDataResp> selectProcessDataByCategoryCode(String clothingCategoryCode) {
        List<ProcessCombCraftAllDataResp> resps = CollUtil.newArrayList();
        try {
            if (StringUtils.isBlank(clothingCategoryCode)) {
                return null;
            }
            resps = combCraftDao.selectProcessListByCategoryCode(clothingCategoryCode);
        } catch (Exception e) {
            LOGGER.info("selectProcessDataByCategoryCode fails", e);
        }
        return resps;
    }

    /**
     * 产生序列号
     */
    private String createSerial() {
        String serialNumber = "";
        boolean getLock;
        String requestId = UUIDUtil.uuid32();
        try {
            getLock = redisUtil.tryGetDistributedLock(BusinessConstants.Redis.PROCESS_COMB_CRAFT_LOCK_KEY, requestId, RedisContants.EXPIRE_TIME);
            if (getLock) {

                Object serialno = redisUtil.get(BusinessConstants.Redis.PROCESS_COMB_CRAFT_SERIAL_NUMBER);
                //第一个序列号生成
                if (null == serialno) {
                    serialNumber = String.format("%06d", 1);
                    //将缓存放入
                    redisUtil.set(BusinessConstants.Redis.PROCESS_COMB_CRAFT_SERIAL_NUMBER, 1);
                } else {
                    //先把序列号加1；
                    redisUtil.incr(BusinessConstants.Redis.PROCESS_COMB_CRAFT_SERIAL_NUMBER, 1);
                    serialNumber = String.format("%06d", redisUtil.get(BusinessConstants.Redis.PROCESS_COMB_CRAFT_SERIAL_NUMBER));
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
            redisUtil.releaseDistributedLock(BusinessConstants.Redis.PROCESS_COMB_CRAFT_LOCK_KEY, requestId);
        }
        return serialNumber;
    }
}
