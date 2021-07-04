package com.ylzs.service.interfaceOutput.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.BigStyleAnalyseMasterDao;
import com.ylzs.dao.bigstylecraft.BigStyleAnalysePartCraftDetailDao;
import com.ylzs.dao.bigticketno.BigOrderMasterDao;
import com.ylzs.dao.bigticketno.BigOrderPartCraftDetailDao;
import com.ylzs.dao.custom.CustomStyleCraftCourseDao;
import com.ylzs.dao.custom.CustomStylePartCraftDao;
import com.ylzs.dao.plm.BigStyleMasterDataDao;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.entity.custom.CustomStylePartCraft;
import com.ylzs.entity.plm.BigStyleMasterData;
import com.ylzs.service.interfaceOutput.ISectionSMVService;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.vo.interfaceOutput.SectionSMV;
import com.ylzs.vo.interfaceOutput.SectionSMVDetail;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.ylzs.common.constant.BusinessConstants.PiReceiveUrlConfig.BIG_STYLE_PRE_SECTION_SMV_URL;

@Service
public class SectionSMVService implements ISectionSMVService {
    @Resource
    private ISendPiService sendPiService;

    @Resource
    private BigStyleMasterDataDao bigStyleMasterDataDao;

    @Resource
    private BigStyleAnalyseMasterDao bigStyleAnalyseMasterDao;

    @Resource
    private BigStyleAnalysePartCraftDetailDao bigStyleAnalysePartCraftDetailDao;

    @Resource
    private CustomStyleCraftCourseDao customStyleCraftCourseDao;

    @Resource
    private CustomStylePartCraftDao customStylePartCraftDao;

    @Resource
    private BigOrderMasterDao bigOrderMasterDao;

    @Resource
    private BigOrderPartCraftDetailDao bigOrderPartCraftDetailDao;




    @Override
    public String sendBigStylePreSectionSMV(String styleCode, BigDecimal preSMV, boolean isSend) {
        BigStyleMasterData style = bigStyleMasterDataDao.getBigStyleMasterDataOne(styleCode);
        if(style == null) {
            return null;
        }

        SectionSMV sectionSMV = new SectionSMV();
        sectionSMV.setDdbh(styleCode);
        sectionSMV.setXh("1");
        sectionSMV.setKsbh(styleCode);
        sectionSMV.setFsrq(new Date());
        sectionSMV.setType(2);
        sectionSMV.setKsms(style.getStyleName());

        SectionSMVDetail detail = new SectionSMVDetail();
        //暂时先固定，后续应该从数据库取
        detail.setGddm("0040");
        detail.setGdmc("车缝");
        detail.setUnit("MIN");
        detail.setBb("元");
        detail.setSmv(preSMV.toString());
        detail.setDj("");

        SectionSMVDetail detail1 = new SectionSMVDetail();

        detail1.setGddm("0010");
        detail1.setGdmc("裁/织");
        detail1.setUnit("MIN");
        detail1.setBb("元");
        detail1.setSmv("1");
        detail1.setDj("");

        List<SectionSMVDetail> details = new ArrayList<>();
        details.add(detail);
        details.add(detail1);
        sectionSMV.setDetails(details);

        String dataStr = sendPiService.getPIJSON("pi.StylePartLib", sectionSMV,null);
        if(!isSend) {
            return dataStr;
        } else {
            String ret = sendPiService.sendToPi(BIG_STYLE_PRE_SECTION_SMV_URL, dataStr);
            return ret==null?"":ret;
        }
    }

    @Override
    public String sendBigStyleActualSectionSMV(String bigStyleAnalyseCode, boolean isSend) {
        BigStyleAnalyseMaster style = bigStyleAnalyseMasterDao.getBigStyleAnalyseByCode(bigStyleAnalyseCode);
        if(style == null) {
            return null;
        } else if(!BusinessConstants.Status.PUBLISHED_STATUS.equals(style.getStatus())) {
            return null;
        }

        BigStyleMasterData masterStyle = bigStyleMasterDataDao.getBigStyleMasterDataOne(style.getCtStyleCode());
        if(masterStyle == null) {
            return null;
        }

        List<BigStyleAnalysePartCraftDetail> sections = bigStyleAnalysePartCraftDetailDao.getBigStyleSectionTime(style.getRandomCode());
        if(ObjectUtils.isEmptyList(sections)) {
            return null;
        }

        SectionSMV sectionSMV = new SectionSMV();
        sectionSMV.setDdbh("");
        sectionSMV.setXh("1");
        sectionSMV.setKsbh(style.getCtStyleCode());
        sectionSMV.setFsrq(new Date());
        sectionSMV.setType(1);
        sectionSMV.setKsms(style.getStyleName());

        List<SectionSMVDetail> details = new ArrayList<>();
        for(BigStyleAnalysePartCraftDetail section: sections) {
            SectionSMVDetail detail = new SectionSMVDetail();

            detail.setGddm(section.getCraftCode());
            detail.setGdmc(section.getCraftName());
            detail.setUnit("MIN");
            detail.setBb("元");
            detail.setSmv(section.getStandardTime().toString());
            detail.setDj(section.getStandardPrice().toString());
            details.add(detail);
        }

        //针梭配款式，发布时自动在工段里增加“织片”“缝盘”两个工段
        if("true".equals(masterStyle.getIsKnitTatMatch())) {
            SectionSMVDetail detail1 = new SectionSMVDetail();

            detail1.setGddm("0011");
            detail1.setGdmc("织片");
            detail1.setUnit("MIN");
            detail1.setBb("元");
            detail1.setSmv("1.000");
            detail1.setDj("1.000");
            details.add(detail1);

            SectionSMVDetail detail2 = new SectionSMVDetail();

            detail2.setGddm("0041");
            detail2.setGdmc("缝盘");
            detail2.setUnit("MIN");
            detail2.setBb("元");
            detail2.setSmv("1.000");
            detail2.setDj("1.000");
            details.add(detail2);
        }

        SectionSMVDetail detail3 = new SectionSMVDetail();
        detail3.setGddm("0010");
        detail3.setGdmc("裁/织");
        detail3.setUnit("MIN");
        detail3.setBb("元");
        detail3.setSmv("0.678");
        detail3.setDj("0.1356");
        details.add(detail3);

        sectionSMV.setDetails(details);

        String dataStr = sendPiService.getPIJSON("pi.StylePartLib", sectionSMV,null);
        if(!isSend) {
            return dataStr;
        } else {
            String ret = sendPiService.sendToPi(BIG_STYLE_PRE_SECTION_SMV_URL, dataStr);
            return ret==null?"":ret;
        }
    }

    @Override
    public String sendSectionSMV(String dataString) {
        return sendPiService.sendToPi(BIG_STYLE_PRE_SECTION_SMV_URL, dataString);
    }

    @Override
    public String sendCustomStyleActualSectionSMV(String orderNo, String orderLineId, boolean isSend) {
        QueryWrapper<CustomStyleCraftCourse> styleCraftCourseQueryWrapper = new QueryWrapper<>();
        styleCraftCourseQueryWrapper.lambda().eq(CustomStyleCraftCourse::getOrderNo, orderNo)
                .eq(CustomStyleCraftCourse::getOrderLineId, orderLineId)
                .eq(CustomStyleCraftCourse::getStatus, BusinessConstants.Status.PUBLISHED_STATUS);
        CustomStyleCraftCourse style = customStyleCraftCourseDao.selectOne(styleCraftCourseQueryWrapper);
        if(style == null) {
            return null;
        }
        List<CustomStylePartCraft> sections = customStylePartCraftDao.getCustomStyleSectionTime(style.getRandomCode());
        if(ObjectUtils.isEmptyList(sections)) {
            return null;
        }

        SectionSMV sectionSMV = new SectionSMV();
        sectionSMV.setDdbh(style.getOrderNo() + "-" + style.getOrderLineId());
        sectionSMV.setXh("1");
        sectionSMV.setKsbh(style.getStyleCode());
        sectionSMV.setFsrq(new Date());
        sectionSMV.setType(1);
        sectionSMV.setKsms(style.getStyleDescript());

        List<SectionSMVDetail> details = new ArrayList<>();
        for(CustomStylePartCraft section: sections) {
            SectionSMVDetail detail = new SectionSMVDetail();

            detail.setGddm(section.getCraftCode());
            detail.setGdmc(section.getCraftName());
            detail.setUnit("MIN");
            detail.setBb("元");
            detail.setSmv(section.getStandardTime().toString());
            detail.setDj(section.getStandardPrice().toString());
            details.add(detail);
        }

        //添加裁剪的工段
        SectionSMVDetail detail3 = new SectionSMVDetail();
        detail3.setGddm("0010");
        detail3.setGdmc("裁/织");
        detail3.setUnit("MIN");
        detail3.setBb("元");
        detail3.setSmv("0.678");
        detail3.setDj("0.1356");
        details.add(detail3);



        sectionSMV.setDetails(details);
        String dataStr = sendPiService.getPIJSON("pi.StylePartLib", sectionSMV,null);
        if(!isSend) {
            return dataStr;
        } else {
            String ret = sendPiService.sendToPi(BIG_STYLE_PRE_SECTION_SMV_URL, dataStr);
            return ret==null?"":ret;
        }
    }

    @Override
    public String sendBigOrderActualSectionSMV(String productionTicketNo, boolean isSend) {
        Map<String,Object> param = new HashMap<>();
        param.put("productionTicketNo", productionTicketNo);
        List<BigStyleAnalyseMaster> styles = bigOrderMasterDao.searchFromBigStyleAnalyse(param);
        BigStyleAnalyseMaster style = null;
        if(ObjectUtils.isNotEmptyList(styles)) {
            for(BigStyleAnalyseMaster sty: styles) {
                if(BusinessConstants.Status.PUBLISHED_STATUS.equals(sty.getStatus())) {
                    style = sty;
                    break;
                }
            }
        }
        if(style == null) {
            return null;
        }



        BigStyleMasterData masterStyle = bigStyleMasterDataDao.getBigStyleMasterDataOne(style.getCtStyleCode());
        if(masterStyle == null) {
            return null;
        }

        List<BigStyleAnalysePartCraftDetail> sections = bigOrderPartCraftDetailDao.getBigOrderSectionTime(style.getRandomCode());
        if(ObjectUtils.isEmptyList(sections)) {
            return null;
        }

        SectionSMV sectionSMV = new SectionSMV();
        sectionSMV.setSite(style.getFactoryCode());
        sectionSMV.setDdbh("");
        sectionSMV.setXh("1");
        sectionSMV.setKsbh(style.getCtStyleCode());
        sectionSMV.setFsrq(new Date());
        sectionSMV.setType(3); //发送FMS，同时也发送ME
        sectionSMV.setKsms(style.getStyleName());
        sectionSMV.setOrderId(productionTicketNo);

        List<SectionSMVDetail> details = new ArrayList<>();
        for(BigStyleAnalysePartCraftDetail section: sections) {
            SectionSMVDetail detail = new SectionSMVDetail();

            detail.setGddm(section.getCraftCode());
            detail.setGdmc(section.getCraftName());
            detail.setUnit("MIN");
            detail.setBb("元");
            detail.setSmv(section.getStandardTime().toString());
            detail.setDj(section.getStandardPrice().toString());
            details.add(detail);
        }

        //针梭配款式，发布时自动在工段里增加“织片”“缝盘”两个工段
        if("true".equals(masterStyle.getIsKnitTatMatch())) {
            SectionSMVDetail detail1 = new SectionSMVDetail();

            detail1.setGddm("0011");
            detail1.setGdmc("织片");
            detail1.setUnit("MIN");
            detail1.setBb("元");
            detail1.setSmv("1.000");
            detail1.setDj("1.000");
            details.add(detail1);

            SectionSMVDetail detail2 = new SectionSMVDetail();

            detail2.setGddm("0041");
            detail2.setGdmc("缝盘");
            detail2.setUnit("MIN");
            detail2.setBb("元");
            detail2.setSmv("1.000");
            detail2.setDj("1.000");
            details.add(detail2);
        }

        SectionSMVDetail detail3 = new SectionSMVDetail();
        detail3.setGddm("0010");
        detail3.setGdmc("裁/织");
        detail3.setUnit("MIN");
        detail3.setBb("元");
        detail3.setSmv("0.678");
        detail3.setDj("0.1356");
        details.add(detail3);

        sectionSMV.setDetails(details);

        String dataStr = sendPiService.getPIJSON("pi.StylePartLib", sectionSMV,null);
        if(!isSend) {
            return dataStr;
        } else {
            String ret = sendPiService.sendToPi(BIG_STYLE_PRE_SECTION_SMV_URL, dataStr);
            return ret==null?"":ret;
        }
    }
}
