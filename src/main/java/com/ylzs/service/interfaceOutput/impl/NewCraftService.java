package com.ylzs.service.interfaceOutput.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.util.Const;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.*;
import com.ylzs.dao.bigticketno.*;
import com.ylzs.dao.craftmainframe.ProductionPartDao;
import com.ylzs.dao.craftstd.CraftStdToolDao;
import com.ylzs.dao.craftstd.MakeTypeDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.dao.timeparam.SameLevelCraftDao;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.craftstd.*;
import com.ylzs.entity.sewingcraft.SewingCraftStd;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;
import com.ylzs.entity.timeparam.SameLevelCraft;
import com.ylzs.service.craftstd.ICraftCategoryService;
import com.ylzs.service.interfaceOutput.INewCraftService;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.vo.interfaceOutput.NewCraftItemOuput;
import com.ylzs.vo.interfaceOutput.NewCraftOutput;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.ylzs.common.constant.BusinessConstants.PiReceiveUrlConfig.NEW_CRAFT_URL;

@Service
public class NewCraftService implements INewCraftService {
    @Resource
    private ISendPiService sendPiService;
    @Resource
    private BigStyleAnalysePartCraftDetailDao bigStyleAnalysePartCraftDetailDao;
    @Resource
    private StyleSewingCraftStdDao styleSewingCraftStdDao;
    @Resource
    private StyleSewingCraftWarehouseDao styleSewingCraftWarehouseDao;
    @Resource
    private StyleSewingCraftWarehouseWorkplaceDao styleSewingCraftWarehouseWorkplaceDao;
    @Resource
    private BigStyleAnalyseMasterDao bigStyleAnalyseMasterDao;
    @Resource
    private ProductionPartDao productionPartDao;

    @Resource
    private BigOrderMasterDao bigOrderMasterDao;
    @Resource
    private BigOrderPartCraftDao bigOrderPartCraftDao;
    @Resource
    private BigOrderPartCraftDetailDao bigOrderPartCraftDetailDao;
    @Resource
    private BigOrderSewingCraftStdDao bigOrderSewingCraftStdDao;
    @Resource
    private BigOrderSewingCraftWarehouseDao bigOrderSewingCraftWarehouseDao;
    @Resource
    private BigOrderSewingCraftWarehouseWorkplaceDao bigOrderSewingCraftWarehouseWorkplaceDao;

    @Resource
    private CraftStdToolDao craftStdToolDao;
    @Resource
    private SameLevelCraftDao sameLevelCraftDao;
    @Resource
    private DictionaryDao dictionaryDao;
    @Resource
    private ICraftCategoryService craftCategoryService;

    @Resource
    private MakeTypeDao makeTypeDao;


    @Override
    public String sendBigStyleNewCraft(String styleAnalyseCode, boolean isSend) {
        BigStyleAnalyseMaster style = bigStyleAnalyseMasterDao.getBigStyleAnalyseByCode(styleAnalyseCode);
        if (style == null) {
            return null;
        }

        List<BigStyleAnalysePartCraftDetail> details = bigStyleAnalysePartCraftDetailDao.getBigStyleAnalyseNewCraft(style.getRandomCode());
        if (ObjectUtils.isEmptyList(details)) {
            return null;
        }
        List<SewingCraftWarehouse> crafts = styleSewingCraftWarehouseDao.getSewingCraftWarehouseNewCraft(style.getRandomCode());
        List<SewingCraftWarehouseWorkplace> workplaces = styleSewingCraftWarehouseWorkplaceDao.getSewingCraftWarehouseWorkplaceNewCraft(style.getRandomCode());
        //List<SewingCraftStd> sewStds = styleSewingCraftStdDao.getSewingCraftStdNewCraft(style.getRandomCode());
        List<CraftFile> craftFiles = styleSewingCraftStdDao.getBigStyleCraftStdFileNewCraft(style.getRandomCode());
        Map<String, ProductionPart> productionMap = productionPartDao.getMapByMainFrameCode(style.getMainFrameCode());
        Map<String, CraftStd> craftStdMap = styleSewingCraftStdDao.getBigStyleCraftStdMap(style.getRandomCode(), true);
        List<Long> craftStdIds = craftStdMap.values().stream().map(CraftStd::getId).collect(Collectors.toList());
        List<CraftCategory> craftCategoryList = craftCategoryService.getAllValidCraftCategory();
        List<CraftStdTool> craftStdTools;
        if (ObjectUtils.isNotEmptyList(craftStdIds)) {
            craftStdTools = craftStdToolDao.getCraftStdTool(craftStdIds);
        } else {
            craftStdTools = new ArrayList<>();
        }
        List<String> sameLevelCodes = crafts.stream().filter(x -> StringUtils.isNotBlank(x.getSameLevelCraftNumericalCode()))
                .map(SewingCraftWarehouse::getSameLevelCraftNumericalCode).collect(Collectors.toList());
        Map<String, SameLevelCraft> sameLevelCraftMap;
        if (ObjectUtils.isNotEmptyList(sameLevelCodes)) {
            sameLevelCraftMap = sameLevelCraftDao.getSameLevelCraftMap(sameLevelCodes);
        } else {
            sameLevelCraftMap = new HashMap<>();
        }
        Map<String, Dictionary> workTypeMap = dictionaryDao.getDictionaryMap("WorkType");
        Map<String,MakeType> makeTypeMap = makeTypeDao.getMakeTypeMap();


        List<NewCraftItemOuput> newCrafts = new ArrayList<>();
        for (BigStyleAnalysePartCraftDetail detail : details) {
            SewingCraftWarehouse craft = crafts.stream().filter(x -> x.getCraftCode().equals(detail.getCraftCode()) &&
                    x.getPartCraftMainCode().equals(detail.getPartCraftMainCode())).findFirst().orElse(null);
            if (craft == null) {
                continue;
            }

            SewingCraftWarehouseWorkplace workplace = workplaces.stream().filter(x -> x.getCraftCode().equals(detail.getCraftCode()) &&
                    x.getPartCraftMainCode().equals(detail.getPartCraftMainCode())).findFirst().orElse(null);
            if (workplace == null) {
                continue;
            }
            CraftStd std = craftStdMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);

            NewCraftItemOuput itm = new NewCraftItemOuput();
            itm.setGxdm(detail.getCraftCode());
            itm.setGxmc(detail.getCraftName());
            itm.setJqdm(craft.getMachineCode());
            itm.setJqmc(craft.getMachineName());
            itm.setSmv(craft.getStandardTime().toString());
            itm.setDj(craft.getStandardPrice().toString());
            itm.setZglb(craft.getMakeTypeCode());
            if(StringUtils.isNotBlank(craft.getMakeTypeCode())) {
                MakeType makeType = makeTypeMap.getOrDefault(craft.getMakeTypeCode(),null);
                if(makeType != null) {
                    itm.setZgmc(makeType.getMakeTypeName());
                }
            }

            if(StringUtils.isNotBlank(craft.getCraftCategoryCode())) {
                String[] keyArr = craft.getCraftCategoryCode().split(",");
                String codes = "";
                for(String key: keyArr) {
                    for(CraftCategory cat: craftCategoryList) {
                        if(key.equals(cat.getCraftCategoryCode())) {
                            if(codes == "") {
                                codes = cat.getClothesBigCategoryCode();
                            } else {
                                codes = codes + "," + cat.getClothesBigCategoryCode();
                            }
                        }
                    }
                }
                itm.setCraftCategoryCode(codes);

            } else {
                itm.setCraftCategoryCode(craft.getCraftCategoryCode());
            }
            itm.setCraftCategoryName(craft.getCraftCategoryName());

            itm.setBmdm(craft.getWorkTypeCode());
            Dictionary dic = workTypeMap.getOrDefault(craft.getWorkTypeCode(), null);
            if (dic != null) {
                itm.setBmmc(dic.getValueDesc());
            }


            ProductionPart productionPart = productionMap.getOrDefault(workplace.getProductionPartCode(), null);
            if (productionPart != null) {
                itm.setScbj(productionPart.getCustomProductionAreaCode());
                itm.setScbjmc(productionPart.getCustomProductionAreaName());
            } else {
                itm.setScbj(workplace.getProductionPartCode());
                itm.setScbjmc(workplace.getProductionPartName());
            }

            itm.setGybj(craft.getCraftPartCode());
            itm.setGybjmc(craft.getCraftPartName());
            boolean isWX = detail.getCraftCode().startsWith("09");
            itm.setWxbz(isWX ? "Y" : "N");
            itm.setPzsm("");
            itm.setZgsm("");
            if (StringUtils.isBlank(craft.getQualitySpec()) || StringUtils.isBlank(craft.getMakeDescription())) {
                if (std != null) {
                    itm.setPzsm(std.getRequireQuality());
                    itm.setZgsm(std.getMakeDesc());
                }
            } else {
                itm.setPzsm(craft.getQualitySpec());
                itm.setZgsm(craft.getMakeDescription());
            }

            itm.setGjdm("");
            itm.setGjmc("");
            if (std != null) {
                craftStdTools.stream().filter(x -> x.getCraftStdId().equals(std.getId()))
                        .findFirst()
                        .ifPresent(a -> {
                            itm.setGjdm(a.getToolCode());
                            itm.setGjmc(a.getToolName());
                        });
            }


            craftFiles.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_HAND_IMG))
                    .findFirst()
                    .ifPresent(a -> {
                        itm.setFjPath(a.getFileUrl());
                    });
            craftFiles.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_STD_VIDEO))
                    .findFirst()
                    .ifPresent(a -> {
                        itm.setVideoURL(a.getFileUrl());
                    });
            //设置默认值
            if (StringUtils.isBlank(itm.getFjPath()) || StringUtils.isBlank(itm.getVideoURL())) {
                String urlGst = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.GST_RESOURCE_URL, "");
                if (StringUtils.isNotBlank(urlGst)) {
                    if (StringUtils.isBlank(itm.getFjPath())) {
                        itm.setFjPath(urlGst + itm.getGxdm() + "-1.jpeg");
                    }
                    if (StringUtils.isBlank(itm.getVideoURL())) {
                        itm.setVideoURL(urlGst + itm.getGxdm() + ".mp4");
                    }
                }
            }
            itm.setSgzdm(detail.getCraftCode());
            itm.setSgzmc(detail.getCraftName());
            itm.setCutSign("N");

            itm.setSjgxdm("");
            itm.setSjgxmc("");
            if (craft.getSameLevelCraftNumericalCode() != null) {
                itm.setSjgxdm(craft.getSameLevelCraftNumericalCode());
                SameLevelCraft sameLevelCraft = sameLevelCraftMap.getOrDefault(craft.getSameLevelCraftNumericalCode(), null);
                if (sameLevelCraft != null) {
                    itm.setSjgxmc(sameLevelCraft.getSameLevelCraftName());
                    itm.setHardLevel(sameLevelCraft.getHardLevel());
                }
            }
            LimitLength(itm, 20);
            newCrafts.add(itm);
        }
        if (ObjectUtils.isEmptyList(newCrafts)) {
            return null;
        }

        NewCraftOutput newCraftOutput = new NewCraftOutput();
        newCraftOutput.setSite("8081"); //临时
        newCraftOutput.setFromSysFlag(BusinessConstants.FromSysType.FROM_CAPP);
        newCraftOutput.setOperations(newCrafts);

        DataParent<NewCraftOutput> parent = new DataParent<>();
        parent.setInterfaceType("pi.operationLibKsfx");
        parent.setSynTime(new Date());
        parent.setCount("1");

        List<DataChild<NewCraftOutput>> items = new ArrayList<>();
        DataChild<NewCraftOutput> item = new DataChild<NewCraftOutput>();
        item.setItemId("0");
        item.setItem(newCraftOutput);
        items.add(item);
        parent.setItems(items);

        if (isSend) {
            sendPiService.postObject(NEW_CRAFT_URL, parent);
            return "";
        } else {
            return JSON.toJSONString(parent,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullNumberAsZero);
        }
    }

    private void LimitLength(NewCraftItemOuput itm, int len) {
        if(StringUtils.isNotBlank(itm.getSgzmc()) && itm.getSgzmc().length() > len) {
            itm.setSgzmc(itm.getSgzmc().substring(0, len));
        }
        if(StringUtils.isNotBlank(itm.getPzsm()) && itm.getPzsm().length() > len) {
            itm.setPzsm(itm.getPzsm().substring(0, len));
        }
        if(StringUtils.isNotBlank(itm.getZgsm()) && itm.getZgsm().length() > len) {
            itm.setZgsm(itm.getZgsm().substring(0, len));
        }
        if(StringUtils.isNotBlank(itm.getGxmc()) && itm.getGxmc().length() > len) {
            itm.setGxmc(itm.getGxmc().substring(0, len));
        }
        if(StringUtils.isNotBlank(itm.getSjgxmc()) && itm.getSjgxmc().length() > len) {
            itm.setSjgxmc(itm.getSgzmc().substring(0, len));
        }

    }

    @Override
    public String sendBigOrderNewCraft(String productionTicketNo, boolean isSend) {
        Map<String, Object> param = new HashMap<>();
        param.put("productionTicketNo", productionTicketNo);
        List<BigStyleAnalyseMaster> styles = bigOrderMasterDao.searchFromBigStyleAnalyse(param);
        if (ObjectUtils.isEmptyList(styles)) {
            return null;
        }
        BigStyleAnalyseMaster style = styles.get(0);


        List<BigStyleAnalysePartCraftDetail> details = bigOrderPartCraftDetailDao.getBigOrderNewCraft(style.getRandomCode());
        if (ObjectUtils.isEmptyList(details)) {
            return null;
        }


        List<SewingCraftWarehouse> crafts = bigOrderSewingCraftWarehouseDao.getSewingCraftWarehouseNewCraft(style.getRandomCode());
        List<SewingCraftWarehouseWorkplace> workplaces = bigOrderSewingCraftWarehouseWorkplaceDao.getSewingCraftWarehouseWorkplaceNewCraft(style.getRandomCode());
        List<SewingCraftStd> sewStds = bigOrderSewingCraftStdDao.getSewingCraftStdNewCraft(style.getRandomCode());
        List<CraftFile> craftFiles = bigOrderSewingCraftStdDao.getBigOrderCraftStdFileNewCraft(style.getRandomCode());
        Map<String, ProductionPart> productionMap = productionPartDao.getMapByMainFrameCode(style.getMainFrameCode());
        Map<String, CraftStd> craftStdMap = bigOrderSewingCraftStdDao.getBigOrderCraftStdMap(style.getRandomCode(), true);
        List<Long> craftStdIds = craftStdMap.values().stream().map(CraftStd::getId).collect(Collectors.toList());
        List<CraftCategory> craftCategoryList = craftCategoryService.getAllValidCraftCategory();
        List<CraftStdTool> craftStdTools;
        if (ObjectUtils.isNotEmptyList(craftStdIds)) {
            craftStdTools = craftStdToolDao.getCraftStdTool(craftStdIds);
        } else {
            craftStdTools = new ArrayList<>();
        }
        List<String> sameLevelCodes = crafts.stream().filter(x -> StringUtils.isNotBlank(x.getSameLevelCraftNumericalCode()))
                .map(SewingCraftWarehouse::getSameLevelCraftNumericalCode).collect(Collectors.toList());
        Map<String, SameLevelCraft> sameLevelCraftMap;
        if (ObjectUtils.isNotEmptyList(sameLevelCodes)) {
            sameLevelCraftMap = sameLevelCraftDao.getSameLevelCraftMap(sameLevelCodes);
        } else {
            sameLevelCraftMap = new HashMap<>();
        }
        Map<String, Dictionary> workTypeMap = dictionaryDao.getDictionaryMap("WorkType");
        Map<String,MakeType> makeTypeMap = makeTypeDao.getMakeTypeMap();




        List<NewCraftItemOuput> newCrafts = new ArrayList<>();
        for (BigStyleAnalysePartCraftDetail detail : details) {
            SewingCraftWarehouse craft = crafts.stream().filter(x -> x.getCraftCode().equals(detail.getCraftCode()) &&
                    x.getPartCraftMainCode().equals(detail.getPartCraftMainCode())).findFirst().orElse(null);
            if (craft == null) {
                continue;
            }

            SewingCraftWarehouseWorkplace workplace = workplaces.stream().filter(x -> x.getCraftCode().equals(detail.getCraftCode()) &&
                    x.getPartCraftMainCode().equals(detail.getPartCraftMainCode())).findFirst().orElse(null);
            if (workplace == null) {
                continue;
            }
            CraftStd std = craftStdMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);


            NewCraftItemOuput itm = new NewCraftItemOuput();
            itm.setGxdm(detail.getCraftCode());
            itm.setGxmc(detail.getCraftName());
            itm.setJqdm(craft.getMachineCode());
            itm.setJqmc(craft.getMachineName());
            itm.setSmv(craft.getStandardTime().toString());
            itm.setDj(craft.getStandardPrice().toString());
            itm.setZglb(craft.getMakeTypeCode());
            if(StringUtils.isNotBlank(craft.getMakeTypeCode())) {
                MakeType makeType = makeTypeMap.getOrDefault(craft.getMakeTypeCode(),null);
                if(makeType != null) {
                    itm.setZgmc(makeType.getMakeTypeName());
                }
            }

            if(StringUtils.isNotBlank(craft.getCraftCategoryCode())) {
                String[] keyArr = craft.getCraftCategoryCode().split(",");
                String codes = "";
                for(String key: keyArr) {
                    for(CraftCategory cat: craftCategoryList) {
                        if(key.equals(cat.getCraftCategoryCode())) {
                            if(codes == "") {
                                codes = cat.getClothesBigCategoryCode();
                            } else {
                                codes = codes + "," + cat.getClothesBigCategoryCode();
                            }
                        }
                    }
                }
                itm.setCraftCategoryCode(codes);

            } else {
                itm.setCraftCategoryCode(craft.getCraftCategoryCode());
            }
            itm.setCraftCategoryName(craft.getCraftCategoryName());

            itm.setBmdm(craft.getWorkTypeCode());
            Dictionary dic = workTypeMap.getOrDefault(craft.getWorkTypeCode(), null);
            if (dic != null) {
                itm.setBmmc(dic.getValueDesc());
            }

            ProductionPart productionPart = productionMap.getOrDefault(workplace.getProductionPartCode(), null);
            if (productionPart != null) {
                itm.setScbj(productionPart.getCustomProductionAreaCode());
                itm.setScbjmc(productionPart.getCustomProductionAreaName());
            } else {
                itm.setScbj(workplace.getProductionPartCode());
                itm.setScbjmc(workplace.getProductionPartName());
            }

            itm.setGybj(craft.getCraftPartCode());
            itm.setGybjmc(craft.getCraftPartName());
            boolean isWX = detail.getCraftCode().startsWith("09");
            itm.setWxbz(isWX ? "Y" : "N");
            itm.setPzsm("");
            itm.setZgsm("");
            if (StringUtils.isBlank(craft.getQualitySpec()) || StringUtils.isBlank(craft.getMakeDescription())) {
                if (std != null) {
                    itm.setPzsm(std.getRequireQuality());
                    itm.setZgsm(std.getMakeDesc());
                }
            } else {
                itm.setPzsm(craft.getQualitySpec());
                itm.setZgsm(craft.getMakeDescription());
            }

            itm.setGjdm("");
            itm.setGjmc("");
            if (std != null) {
                craftStdTools.stream().filter(x -> x.getCraftStdId().equals(std.getId()))
                        .findFirst()
                        .ifPresent(a -> {
                            itm.setGjdm(a.getToolCode());
                            itm.setGjmc(a.getToolName());
                        });
            }

            craftFiles.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_HAND_IMG))
                    .findFirst()
                    .ifPresent(a -> {
                        itm.setFjPath(a.getFileUrl());
                    });
            craftFiles.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_STD_VIDEO))
                    .findFirst()
                    .ifPresent(a -> {
                        itm.setVideoURL(a.getFileUrl());
                    });
            //设置默认值
            if (StringUtils.isBlank(itm.getFjPath()) || StringUtils.isBlank(itm.getVideoURL())) {
                String urlGst = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.GST_RESOURCE_URL, "");
                if (StringUtils.isNotBlank(urlGst)) {
                    if (StringUtils.isBlank(itm.getFjPath())) {
                        itm.setFjPath(urlGst + itm.getGxdm() + "-1.jpeg");
                    }
                    if (StringUtils.isBlank(itm.getVideoURL())) {
                        itm.setVideoURL(urlGst + itm.getGxdm() + ".mp4");
                    }
                }
            }
            itm.setSgzdm(detail.getCraftCode());
            itm.setSgzmc(detail.getCraftName());
            itm.setCutSign("N");

            itm.setSjgxdm("");
            itm.setSjgxmc("");
            if (craft.getSameLevelCraftNumericalCode() != null) {
                itm.setSjgxdm(craft.getSameLevelCraftNumericalCode());
                SameLevelCraft sameLevelCraft = sameLevelCraftMap.getOrDefault(craft.getSameLevelCraftNumericalCode(), null);
                if (sameLevelCraft != null) {
                    itm.setSjgxmc(sameLevelCraft.getSameLevelCraftName());
                    itm.setHardLevel(sameLevelCraft.getHardLevel());
                }
            }
            LimitLength(itm, 20);
            newCrafts.add(itm);
        }
        if (ObjectUtils.isEmptyList(newCrafts)) {
            return null;
        }

        NewCraftOutput newCraftOutput = new NewCraftOutput();
        newCraftOutput.setSite("8081");
        newCraftOutput.setFromSysFlag(BusinessConstants.FromSysType.FROM_CAPP);
        newCraftOutput.setOperations(newCrafts);

        DataParent<NewCraftOutput> parent = new DataParent<>();
        parent.setInterfaceType("pi.operationLibKsfx");
        parent.setSynTime(new Date());
        parent.setCount("1");

        List<DataChild<NewCraftOutput>> items = new ArrayList<>();
        DataChild<NewCraftOutput> item = new DataChild<NewCraftOutput>();
        item.setItemId("0");
        item.setItem(newCraftOutput);
        items.add(item);
        parent.setItems(items);

        if (isSend) {
            sendPiService.postObject(NEW_CRAFT_URL, parent);
            return "";
        } else {
            return JSON.toJSONString(parent,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullNumberAsZero);
        }
    }
}
