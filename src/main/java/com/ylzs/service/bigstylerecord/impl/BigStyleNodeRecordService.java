package com.ylzs.service.bigstylerecord.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ylzs.common.util.SnowflakeIdUtil;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.BigStyleAnalyseMasterDao;
import com.ylzs.dao.bigstylerecord.BigStyleNodeRecordMapper;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.dao.system.UserDao;
import com.ylzs.entity.aps.CappPiPreScheduleResult;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylerecord.BigStyleNodeRecord;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.factory.ProductionGroup;
import com.ylzs.entity.plm.BigStyleMasterData;
import com.ylzs.entity.plm.BigStyleMasterDataSKC;
import com.ylzs.entity.system.User;
import com.ylzs.service.bigstylerecord.IBigStyleNodeRecordService;
import com.ylzs.service.factory.IProductionGroupService;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.vo.BigStyleNodeRecordExportVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class BigStyleNodeRecordService extends OriginServiceImpl<BigStyleNodeRecordMapper, BigStyleNodeRecord> implements IBigStyleNodeRecordService {
    @Resource
    DictionaryDao dictionaryDao;

    @Resource
    IProductionGroupService productionGroupService;

    @Resource
    BigStyleAnalyseMasterDao bigStyleAnalyseMasterDao;

    @Resource
    UserDao userDao;

    @Override
    public void updateAll(List<BigStyleNodeRecord> styles) {
        for (BigStyleNodeRecord style : styles) {
            UpdateWrapper<BigStyleNodeRecord> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(BigStyleNodeRecord::getSampleReceiveTime1, style.getSampleReceiveTime1())
                    .set(BigStyleNodeRecord::getSampleReceiveTime2, style.getSampleReceiveTime2())
                    .set(BigStyleNodeRecord::getUpdateTime, style.getUpdateTime())
                    .set(BigStyleNodeRecord::getUpdateUser, style.getUpdateUser())
                    //.set(BigStyleNodeRecord::getAssignToUserCode, style.getAssignToUserCode())
                    //.set(BigStyleNodeRecord::getAssignToUserName, style.getAssignToUserName())
                    .set(BigStyleNodeRecord::getEstimateSewingTime, style.getEstimateSewingTime())
                    .set(BigStyleNodeRecord::getRemark, style.getRemark())
                    .eq(BigStyleNodeRecord::getStyleCode, style.getStyleCode());
            update(updateWrapper);
        }

    }

    @Override
    public void addOrUpdateBigStyleNodeRecord(BigStyleMasterData bigStyleMasterData, List<BigStyleMasterDataSKC> skcList) {
        BigStyleNodeRecord rec = new BigStyleNodeRecord();
        rec.setStyleCode(bigStyleMasterData.getCtStyleCode());
        rec.setStyleDesc(bigStyleMasterData.getStyleName());
        rec.setReceiveTime(new Date());
        Long randomCode = SnowflakeIdUtil.generateId();
        rec.setRandomCode(randomCode);
        String images = "";
        if (ObjectUtils.isNotEmptyList(skcList)) {
            for (BigStyleMasterDataSKC skc : skcList) {
                if (StringUtils.isNotBlank(skc.getSkcImageURL())) {
                    images += skc.getSkcImageURL() + ",";
                }
            }
            rec.setStyleImageUrl(images);
        }
        rec.setClothesCategoryCode(bigStyleMasterData.getGrandCategory());
        if (StringUtils.isNotBlank(rec.getClothesCategoryCode())) {
            List<Dictionary> dics = dictionaryDao.getDictoryByValueAndType(rec.getClothesCategoryCode(),
                    "ClothesCategory");
            if (ObjectUtils.isNotEmptyList(dics)) {
                rec.setClothesCategoryName(dics.get(0).getValueDesc());
            }
        }


        UpdateWrapper<BigStyleNodeRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(BigStyleNodeRecord::getStyleDesc, rec.getStyleDesc())
                .set(BigStyleNodeRecord::getStyleImageUrl, rec.getStyleImageUrl())
                .set(BigStyleNodeRecord::getClothesCategoryCode, rec.getClothesCategoryCode())
                .set(BigStyleNodeRecord::getClothesCategoryName, rec.getClothesCategoryName())
                .eq(BigStyleNodeRecord::getStyleCode, rec.getStyleCode());
        this.saveOrUpdate(rec, updateWrapper);
    }

    @Override
    public void updateStyleNodeRecord(CappPiPreScheduleResult preResult) {
        String groupCode = preResult.getWorkcenterCode();
        String groupName = preResult.getWorkcenterCode();

        List<ProductionGroup> groups = productionGroupService.getProductCodeByName(preResult.getWorkcenterCode());
        if (null != groups && groups.size() > 0) {
            ProductionGroup group = groups.get(0);
            groupCode = group.getProductionGroupCode();
            groupName = group.getProductionGroupName();
        }

        UpdateWrapper<BigStyleNodeRecord> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(BigStyleNodeRecord::getProductGroupCode, groupCode)
                .set(BigStyleNodeRecord::getProductGroupName, groupName)
                .set(BigStyleNodeRecord::getScheduleTime, preResult.getScheduleTime())
                .set(BigStyleNodeRecord::getDeliveryTime, preResult.getDeliveryTime())
                .eq(BigStyleNodeRecord::getStyleCode, preResult.getProduct());
        update(updateWrapper);
    }

    @Override
    public void calcStyleNodeRecord(List<BigStyleNodeRecord> styles) {
        List<String> codes = styles.stream().map(BigStyleNodeRecord::getStyleCode).collect(Collectors.toList());
        if (ObjectUtils.isEmptyList(codes)) {
            return;
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        DecimalFormat decimalFormat = new DecimalFormat("#");
        Map<String, BigStyleAnalyseMaster> codeToMaster = bigStyleAnalyseMasterDao.getStyleCodeMasterMap(codes);
        for (BigStyleNodeRecord style : styles) {
            BigStyleAnalyseMaster master = codeToMaster.getOrDefault(style.getStyleCode(), null);
            if (master == null) {
                continue;
            }
            style.setBranchBeginTime(master.getCreateTime());
            style.setBranchEndTime(master.getReleaseTime());
            if (style.getBranchBeginTime() != null && style.getBranchEndTime() != null) {

                if (fmt.format(style.getBranchBeginTime()).equals(fmt.format(style.getBranchEndTime()))) {
                    //同一天
                    double h = (style.getBranchEndTime().getTime() - style.getBranchBeginTime().getTime()) / (1000.00 * 60.00 * 60.00);
                    double m = (h - (int) h) * 60.00;
                    style.setBranchSpend(decimalFormat.format(h) + "小时" + decimalFormat.format(m) + "分");
                } else {
                    //跨天
                    int n = (int) (style.getBranchEndTime().getTime() / (1000.00 * 24.00 * 60.00 * 60.00) - style.getBranchEndTime().getTime() / (1000.00 * 24.00 * 60.00 * 60.00));
                    double h = (style.getBranchEndTime().getTime() - style.getBranchBeginTime().getTime()) / (1000.00 * 60.00 * 60.00);
                    h -= 10.5 * n;
                    double m = (h - (int) h) * 60.00;
                    style.setBranchSpend(decimalFormat.format(h) + "小时" + decimalFormat.format(m) + "分");
                }
            }

            if (style.getSampleReceiveTime2() != null && style.getBranchEndTime() != null) {
                double h = (style.getBranchEndTime().getTime() - style.getSampleReceiveTime2().getTime()) / (1000 * 60 * 60);
                double m = (h - (int) h) * 60.00;
                style.setSampleStaySpend(decimalFormat.format(h) + "小时" + decimalFormat.format(m) + "分");
            }
        }
    }

    @Override
    public void updateStyleNodeBranchInfo(String styleCode, Date branchBeginTime, Date branchEndTime, String createUserCode) {
        if (null == branchBeginTime && null == branchEndTime) {
            return;
        }

        QueryWrapper<BigStyleNodeRecord> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(BigStyleNodeRecord::getStyleCode, styleCode);
        try {
            BigStyleNodeRecord style = getOne(queryWrapper, false);
            if(null == style) {
                return;
            }
            if(StringUtils.isNotBlank(createUserCode)) {
                style.setAssignToUserCode(createUserCode);

                QueryWrapper<User> qryUser = new QueryWrapper<>();
                qryUser.lambda()
                        .select(User::getUserName)
                        .eq(User::getUserCode, createUserCode)
                        .and(qryUser1->qryUser1.isNull(User::getIsInvalid).or().eq(User::getIsInvalid, false));
                User user = userDao.selectOne(qryUser);
                if(user != null) {
                    style.setAssignToUserName(user.getUserName());
                }
            }


            style.setBranchBeginTime(branchBeginTime);
            style.setBranchEndTime(branchEndTime);
            style.setBranchSpend(null);
            style.setSampleStaySpend(null);


            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            DecimalFormat decimalFormat = new DecimalFormat("#");

            if (style.getBranchBeginTime() != null && style.getBranchEndTime() != null) {

                if (fmt.format(style.getBranchBeginTime()).equals(fmt.format(style.getBranchEndTime()))) {
                    //同一天
                    double h = (style.getBranchEndTime().getTime() - style.getBranchBeginTime().getTime()) / (1000.00 * 60.00 * 60.00);
                    double m = (h - (int) h) * 60.00;
                    style.setBranchSpend(decimalFormat.format(h) + "小时" + decimalFormat.format(m) + "分");
                } else {
                    //跨天
                    int n = (int) (style.getBranchEndTime().getTime() / (1000.00 * 24.00 * 60.00 * 60.00) - style.getBranchEndTime().getTime() / (1000.00 * 24.00 * 60.00 * 60.00));
                    double h = (style.getBranchEndTime().getTime() - style.getBranchBeginTime().getTime()) / (1000.00 * 60.00 * 60.00);
                    h -= 10.5 * n;
                    double m = (h - (int) h) * 60.00;
                    style.setBranchSpend(decimalFormat.format(h) + "小时" + decimalFormat.format(m) + "分");
                }
            }

            if (style.getSampleReceiveTime2() != null && style.getBranchEndTime() != null) {
                double h = (style.getBranchEndTime().getTime() - style.getSampleReceiveTime2().getTime()) / (1000 * 60 * 60);
                double m = (h - (int) h) * 60.00;
                style.setSampleStaySpend(decimalFormat.format(h) + "小时" + decimalFormat.format(m) + "分");
            }

            UpdateWrapper<BigStyleNodeRecord> upStyle = new UpdateWrapper<>();
            upStyle.lambda().eq(BigStyleNodeRecord::getStyleCode, styleCode)
                    .set(BigStyleNodeRecord::getBranchBeginTime, style.getBranchBeginTime())
                    .set(BigStyleNodeRecord::getBranchEndTime, style.getBranchEndTime())
                    .set(BigStyleNodeRecord::getBranchSpend, style.getBranchSpend())
                    .set(BigStyleNodeRecord::getSampleStaySpend, style.getSampleStaySpend())
                    .set(BigStyleNodeRecord::getAssignToUserCode, style.getAssignToUserCode())
                    .set(BigStyleNodeRecord::getAssignToUserName, style.getAssignToUserName());

            update(upStyle);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<BigStyleNodeRecordExportVo> getBigStyleNodeRecordVos(List<BigStyleNodeRecord> styles) {
        List<BigStyleNodeRecordExportVo> bigStyleNodeRecordExportVos = new ArrayList<>();
        int i = 1;
        for (BigStyleNodeRecord itm : styles) {
            BigStyleNodeRecordExportVo result = new BigStyleNodeRecordExportVo();
            try {
                BeanUtils.copyProperties(itm, result);

            } catch (Exception e) {

            }
            bigStyleNodeRecordExportVos.add(result);
        }
        return bigStyleNodeRecordExportVos;
    }
}



