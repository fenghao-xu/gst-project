package com.ylzs.service.thinkstyle.impl;

import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.thinkstyle.ThinkStyleCraftDao;
import com.ylzs.dao.thinkstyle.ThinkStylePartDao;
import com.ylzs.entity.thinkstyle.ThinkStylePart;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.service.thinkstyle.IThinkStylePartService;
import com.ylzs.vo.thinkstyle.ThinkStyleCraftVo;
import com.ylzs.vo.thinkstyle.ThinkStylePartVo;
import com.ylzs.vo.thinkstyle.ThinkStyleProcessRuleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ylzs.common.constant.BusinessConstants.Status.*;

/**
 * @description: 智库款工艺部件
 * @author: lyq
 * @date: 2020-03-05 18:49
 */

@Service
public class ThinkStylePartService extends OriginServiceImpl<ThinkStylePartDao, ThinkStylePart> implements IThinkStylePartService {
    @Resource
    private ThinkStylePartDao thinkStylePartDao;

    @Resource
    private ThinkStyleCraftDao thinkStyleCraftDao;

    @Resource
    private ThinkStyleProcessRuleService thinkStyleProcessRuleService;


    public List<ThinkStylePartVo> selectThinkStylePartVo(Long thinkStyleRandomCode, String clothingCategoryCode) {
        List<ThinkStylePartVo> thinkStylePartVos = thinkStylePartDao.selectThinkStylePartVo(thinkStyleRandomCode,clothingCategoryCode);
        if (ObjectUtils.isNotEmptyList(thinkStylePartVos)) {
            for (ThinkStylePartVo itm: thinkStylePartVos) {
                itm.setLineNo(-1);
                itm.setStatusName(getStatusName(itm.getStatus()));
                if(itm.getParentPartCode() == null) {
                    itm.setParentPartCode("");
                }
                if(itm.getTopPartCode() == null) {
                    itm.setTopPartCode("");
                }

                //把特殊工艺也直接返回
                if(itm.getIsSpecial() != null && itm.getIsSpecial()) {
                    List<ThinkStyleCraftVo> craftVos = thinkStyleCraftDao.getSpecialCraftVos(itm.getRandomCode());
                    itm.setCrafts(craftVos);

                    List<ThinkStyleProcessRuleVo> ruleVos = thinkStyleProcessRuleService.getProcessRuleVos(itm.getRandomCode());
                    itm.setRules(ruleVos);
                }
            }

            //构建第一层
            List<ThinkStylePartVo> rootList = new ArrayList<>();
            int i = 0;
            while(i < thinkStylePartVos.size()) {
                ThinkStylePartVo itm = thinkStylePartVos.get(i);
                if(itm.getTopPartCode().equals("") && itm.getParentPartCode().equals("")) {
                    rootList.add(itm);
                    thinkStylePartVos.remove(i);
                } else {
                    i++;
                }
            }

            //构建第二层
            i = 0;
            boolean bFind;
            while(i < thinkStylePartVos.size()) {
                ThinkStylePartVo itm = thinkStylePartVos.get(i);
                bFind = false;
                if(itm.getTopPartCode().equals(itm.getParentPartCode())) {
                    for(int j=0; j < rootList.size(); j++) {
                        ThinkStylePartVo itm0 = rootList.get(j);
                        if(itm0.getIsValid()) {
                            //找到父级节点 添加到列表
                            if (itm.getParentPartCode().equals(itm0.getDesignPartCode())) {
                                if (itm0.getChildren() == null) {
                                    itm0.setChildren(new ArrayList<>());
                                }
                                itm0.getChildren().add(itm);
                                itm.setLineNo(itm0.getChildren().size());
                                bFind = true;
                                break;
                            }
                        }
                    }

                }

                if(bFind) {
                    thinkStylePartVos.remove(i);
                } else {
                    i++;
                }
            }

            //构建第三层
            i=0;
            while(i<thinkStylePartVos.size()) {
                ThinkStylePartVo itm = thinkStylePartVos.get(i);
                ThinkStylePartVo itm0 = rootList.stream().filter(x->x.getIsValid() && itm.getTopPartCode().equals(x.getDesignPartCode())).findFirst().orElse(null);
                if(itm0 != null) {
                    ThinkStylePartVo itm1 = itm0.getChildren().stream().filter(x->x.getIsValid() && itm.getParentPartCode().equals(x.getDesignPartCode())).findFirst().orElse(null);
                    if(itm1 != null) {
                        if (itm1.getChildren() == null) {
                            itm1.setChildren(new ArrayList<>());
                        }
                        itm1.getChildren().add(itm);
                        itm.setLineNo(itm1.getChildren().size());
                        thinkStylePartVos.remove(i);
                        continue;
                    }
                }
                i++;
            }

            //未找到父节点则添加到尾部
            rootList.addAll(thinkStylePartVos);
            //完善设置序号
            for(i = 0; i < rootList.size(); i++) {
                rootList.get(i).setLineNo(i+1);
            }
            return rootList;

        }
        return thinkStylePartVos;
    }

    @Override
    public Map<String, ThinkStylePart> getThinkStylePartMap(String styleCode) {
        return thinkStylePartDao.getThinkStylePartMap(styleCode);
    }


    private String getStatusName(Integer status) {
        String result = null;
        if (status == null || status.equals(DRAFT_STATUS)) {
            result = "草稿";
        } else if (status.equals(AUDITED_STATUS)) {
            result = "已审核";
        } else if (status.equals(PUBLISHED_STATUS)) {
            result = "已发布";
        } else if (status.equals(INVALID_STATUS)) {
            result = "失效";
        } else {
            result = "未知";
        }
        return result;
    }

    private ThinkStylePartVo getThinkStylePartVo(ThinkStylePart obj) {
        ThinkStylePartVo result = new ThinkStylePartVo();
        try {
            BeanUtils.copyProperties(obj, result);
        } catch (Exception e) {

        }
        return result;
    }
}
