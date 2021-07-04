package com.ylzs.service.processCombCraft.impl;

import com.ylzs.dao.processCombCraft.ProcessCombCraftProgramDao;
import com.ylzs.entity.processCombCraft.ProcessCombCraftProgram;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.processCombCraft.IProcessCombCraftProgramService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weikang
 * @Description 工序组合工艺方案
 * @Date 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessCombCraftProgramServiceImpl extends OriginServiceImpl<ProcessCombCraftProgramDao, ProcessCombCraftProgram> implements IProcessCombCraftProgramService {
}
