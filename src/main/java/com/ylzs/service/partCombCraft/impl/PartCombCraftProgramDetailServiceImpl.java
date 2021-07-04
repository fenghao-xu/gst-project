package com.ylzs.service.partCombCraft.impl;

import com.ylzs.dao.partCombCraft.PartCombCraftProgramDetailDao;
import com.ylzs.entity.partCombCraft.PartCombCraftProgramDetail;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.partCombCraft.IPartCombCraftProgramDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weikang
 * @Description 部件组合工艺规则明细
 * @Date 2020/3/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PartCombCraftProgramDetailServiceImpl extends OriginServiceImpl<PartCombCraftProgramDetailDao, PartCombCraftProgramDetail> implements IPartCombCraftProgramDetailService {
}
