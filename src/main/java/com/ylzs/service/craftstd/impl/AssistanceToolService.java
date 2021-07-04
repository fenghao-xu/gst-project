package com.ylzs.service.craftstd.impl;

import com.ylzs.service.craftstd.IAssistanceToolService;
import com.ylzs.service.impl.OriginServiceImpl;
import org.springframework.stereotype.Service;
import com.ylzs.dao.craftstd.AssistanceToolDao;
import com.ylzs.entity.craftstd.AssistanceTool;

/**
 * 辅助工具实现服务
 */
@Service
public class AssistanceToolService extends OriginServiceImpl<AssistanceToolDao, AssistanceTool> implements IAssistanceToolService {

}
