package com.ylzs.service.interfaceOutput.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ylzs.common.cache.StaticDataCache;
import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.util.Const;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.bigstylecraft.*;
import com.ylzs.dao.bigticketno.*;
import com.ylzs.dao.craftmainframe.CraftMainFrameRouteDao;
import com.ylzs.dao.craftmainframe.ProductionPartDao;
import com.ylzs.dao.craftstd.MakeTypeDao;
import com.ylzs.dao.craftstd.WorkTypeDao;
import com.ylzs.dao.custom.CustomStyleCraftCourseDao;
import com.ylzs.dao.custom.CustomStylePartCraftDao;
import com.ylzs.dao.custom.CustomStylePartDao;
import com.ylzs.dao.mes.PartPartMiddleConfigDao;
import com.ylzs.dao.sewingcraft.SewingCraftWarehouseDao;
import com.ylzs.dao.sewingcraft.SewingCraftWarehouseWorkplaceDao;
import com.ylzs.dao.system.DictionaryDao;
import com.ylzs.dao.workplacecraft.WorkplaceCraftDao;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.bigstylecraft.BigStyleAnalysePartCraftDetail;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.entity.craftmainframe.FlowNumConfig;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.entity.craftstd.Dictionary;
import com.ylzs.entity.craftstd.*;
import com.ylzs.entity.custom.CustomStyleCraftCourse;
import com.ylzs.entity.custom.CustomStylePartCraft;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.entity.mes.PartPartMiddleConfig;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouse;
import com.ylzs.entity.sewingcraft.SewingCraftWarehouseWorkplace;
import com.ylzs.entity.workplacecraft.WorkplaceCraft;
import com.ylzs.service.craftmainframe.IProductionPartService;
import com.ylzs.service.interfaceOutput.IOperationPathService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.service.plm.IPICustomOrderService;
import com.ylzs.service.thinkstyle.IThinkStylePartService;
import com.ylzs.vo.interfaceOutput.CraftOperationPath;
import com.ylzs.vo.interfaceOutput.CuttingArrayOp;
import com.ylzs.vo.interfaceOutput.SewingArrayOp;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.ylzs.common.constant.BusinessConstants.PiReceiveUrlConfig.CUSTOM_STYLE_OPERATION_PATH_URL;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@Service
public class OperationPathService implements IOperationPathService {
    @Resource
    private CustomStyleCraftCourseDao customStyleCraftCourseDao;
    @Resource
    private CustomStylePartCraftDao customStylePartCraftDao;
    @Resource
    private CustomStylePartDao customStylePartDao;
    @Resource
    private ProductionPartDao productionPartDao;
    @Resource
    private WorkplaceCraftDao workplaceCraftDao;
    @Resource
    private SewingCraftWarehouseWorkplaceDao sewingCraftWarehouseWorkplaceDao;
    @Resource
    private CraftMainFrameRouteDao craftMainFrameRouteDao;
    @Resource
    private SewingCraftWarehouseDao sewingCraftWarehouseDao;
    @Resource
    private ISendPiService sendPiService;

    @Resource
    private BigStyleAnalyseMasterDao bigStyleAnalyseMasterDao;
    @Resource
    private BigStyleAnalysePartCraftDao bigStyleAnalysePartCraftDao;
    @Resource
    private BigStyleAnalysePartCraftDetailDao bigStyleAnalysePartCraftDetailDao;
    @Resource
    private StyleSewingCraftWarehouseWorkplaceDao styleSewingCraftWarehouseWorkplaceDao;

    @Resource
    private StyleSewingCraftWarehouseDao styleSewingCraftWarehouseDao;
    @Resource
    private StyleSewingCraftStdDao styleSewingCraftStdDao;

    //??????????????????
    @Resource
    private BigOrderMasterDao bigOrderMasterDao;

    @Resource
    private BigOrderPartCraftDao bigOrderPartCraftDao;

    @Resource
    private BigOrderPartCraftDetailDao bigOrderPartCraftDetailDao;

    @Resource
    private BigOrderSewingCraftWarehouseWorkplaceDao bigOrderSewingCraftWarehouseWorkplaceDao;

    @Resource
    private BigOrderSewingCraftWarehouseDao bigOrderSewingCraftWarehouseDao;

    @Resource
    private BigOrderSewingCraftStdDao bigOrderSewingCraftStdDao;

    @Resource
    private DictionaryDao dictionaryDao;

    @Resource
    private MakeTypeDao makeTypeDao;

    @Resource
    private WorkTypeDao workTypeDao;

    @Resource
    private PartPartMiddleConfigDao partPartMiddleConfigDao;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;

    @Resource
    private IPICustomOrderService piCustomOrderService;

    @Resource
    private IThinkStylePartService thinkStylePartService;

    @Resource
    private IProductionPartService productionPartService;


    /**
     * @return ????????????????????????
     */
    public List<CuttingArrayOp> GetBigCutPath() {
        String jsonString = "[\n" +
                "          {\n" +
                "            \"xh\": \"7\",\n" +
                "            \"gxdm\": \"FB\",\n" +
                "            \"hxgxdm\": \"\",\n" +
                "            \"gxmc\": \"??????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"40.6800\",\n" +
                "            \"dj\": \"0.1356\",\n" +
                "            \"pzsm\": \"\",\n" +
                "            \"gzsm\": \"\",\n" +
                "            \"sgzdm\": \"FB\",\n" +
                "            \"pattDesc\": \"\",\n" +
                "            \"imageURL\": \"\",\n" +
                "            \"bjdm1\": \"\",\n" +
                "            \"bjmc1\": \"\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          }\n" +
                "        ]";
        List<CuttingArrayOp> cuttingArrayOps = JSON.parseObject(jsonString,
                new TypeReference<List<CuttingArrayOp>>() {
                });
        return cuttingArrayOps;
    }

    /**
     * @return ????????????????????????-?????????
     */
    public List<CuttingArrayOp> GetCustomCutPathNoPattern() {
        String jsonString = "[\n" +
                "          {\n" +
                "            \"xh\": \"2\",\n" +
                "            \"gxdm\": \"CA\",\n" +
                "            \"hxgxdm\": \"CK\",\n" +
                "            \"gxmc\": \"??????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"172.5000\",\n" +
                "            \"dj\": \"0.7750\",\n" +
                "            \"pzsm\": \"1?????????????????? 2??????????????????????????? 3?????????????????????\",\n" +
                "            \"gzsm\": \"1?????????????????????????????????FRID??? 2?????????????????????????????? 3??????????????????????????????????????? 4???????????????????????????????????? 5????????????????????????\",\n" +
                "            \"sgzdm\": \"CA\",\n" +
                "            \"pattDesc\": \"\",\n" +
                "            \"imageURL\": \"\",\n" +
                "            \"bjdm1\": \"\",\n" +
                "            \"bjmc1\": \"\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"xh\": \"4\",\n" +
                "            \"gxdm\": \"CK\",\n" +
                "            \"hxgxdm\": \"FI\",\n" +
                "            \"gxmc\": \"??????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"178.0088\",\n" +
                "            \"dj\": \"0.6210\",\n" +
                "            \"pzsm\": \"1???????????????????????? 2?????????????????????????????????????????????????????????????????????\",\n" +
                "            \"gzsm\": \"1???????????????????????? 2?????????????????????????????? 3???????????????????????? 4????????????????????????????????? 5??????????????????????????????????????????????????????\",\n" +
                "            \"sgzdm\": \"CK\",\n" +
                "            \"pattDesc\": \"\",\n" +
                "            \"imageURL\": \"\",\n" +
                "            \"bjdm1\": \"\",\n" +
                "            \"bjmc1\": \"\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"xh\": \"5\",\n" +
                "            \"gxdm\": \"FI\",\n" +
                "            \"hxgxdm\": \"\",\n" +
                "            \"gxmc\": \"??????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"9.0300\",\n" +
                "            \"dj\": \"0.0301\",\n" +
                "            \"pzsm\": \"1???????????????????????? 2?????????????????????????????????????????????????????????????????????\",\n" +
                "            \"gzsm\": \"1???????????? 2???????????? 3??????????????? 4???????????? 5??????????????? 6????????????????????? 7?????????????????????\",\n" +
                "            \"sgzdm\": \"FI\",\n" +
                "            \"pattDesc\": \"\",\n" +
                "            \"imageURL\": \"\",\n" +
                "            \"bjdm1\": \"\",\n" +
                "            \"bjmc1\": \"\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          }\n" +
                "        ]";
        List<CuttingArrayOp> cuttingArrayOps = JSON.parseObject(jsonString,
                new TypeReference<List<CuttingArrayOp>>() {
                });
        return cuttingArrayOps;
    }


    /**
     * @return ????????????????????????-?????????
     */
    public List<CuttingArrayOp> GetCustomCutPathWithPattern() {
        String jsonString = "[\n" +
                "          {\n" +
                "            \"xh\": \"2\",\n" +
                "            \"gxdm\": \"CA\",\n" +
                "            \"hxgxdm\": \"CK\",\n" +
                "            \"gxmc\": \"??????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"112.5000\",\n" +
                "            \"dj\": \"0.4750\",\n" +
                "            \"pzsm\": \"1?????????????????? 2??????????????????????????? 3?????????????????????\",\n" +
                "            \"gzsm\": \"1?????????????????????????????????FRID??? 2?????????????????????????????? 3??????????????????????????????????????? 4???????????????????????????????????? 5????????????????????????\",\n" +
                "            \"sgzdm\": \"CA\",\n" +
                "            \"pattDesc\": \"\",\n" +
                "            \"imageURL\": \"\",\n" +
                "            \"bjdm1\": \"\",\n" +
                "            \"bjmc1\": \"\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"xh\": \"4\",\n" +
                "            \"gxdm\": \"CK\",\n" +
                "            \"hxgxdm\": \"FI\",\n" +
                "            \"gxmc\": \"??????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"100.8254\",\n" +
                "            \"dj\": \"0.3499\",\n" +
                "            \"pzsm\": \"1???????????????????????? 2?????????????????????????????????????????????????????????????????????\",\n" +
                "            \"gzsm\": \"1???????????????????????? 2?????????????????????????????? 3???????????????????????? 4????????????????????????????????? 5??????????????????????????????????????????????????????\",\n" +
                "            \"sgzdm\": \"CK\",\n" +
                "            \"pattDesc\": \"\",\n" +
                "            \"imageURL\": \"\",\n" +
                "            \"bjdm1\": \"\",\n" +
                "            \"bjmc1\": \"\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"xh\": \"5\",\n" +
                "            \"gxdm\": \"FI\",\n" +
                "            \"hxgxdm\": \"XH1\",\n" +
                "            \"gxmc\": \"??????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"9.0300\",\n" +
                "            \"dj\": \"0.0301\",\n" +
                "            \"pzsm\": \"1???????????????????????? 2?????????????????????????????????????????????????????????????????????\",\n" +
                "            \"gzsm\": \"1???????????? 2???????????? 3??????????????? 4???????????? 5??????????????? 6????????????????????? 7?????????????????????\",\n" +
                "            \"sgzdm\": \"FI\",\n" +
                "            \"pattDesc\": \"\",\n" +
                "            \"imageURL\": \"\",\n" +
                "            \"bjdm1\": \"\",\n" +
                "            \"bjmc1\": \"\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"xh\": \"29\",\n" +
                "            \"gxdm\": \"XH1\",\n" +
                "            \"hxgxdm\": \"\",\n" +
                "            \"gxmc\": \"???????????????\",\n" +
                "            \"bjdm\": \"MB\",\n" +
                "            \"bjmc\": \"??????\",\n" +
                "            \"smv\": \"0.0000\",\n" +
                "            \"dj\": \"0.0000\",\n" +
                "            \"pzsm\": \"\",\n" +
                "            \"gzsm\": \"TT4003????????????-F ????????? ??????\",\n" +
                "            \"sgzdm\": \"XH1\",\n" +
                "            \"pattDesc\": \"?????? 1???5421#?????????2565#;2???5550#?????????2610#;\",\n" +
                "            \"imageURL\": \"http://10.7.121.10//centricP//20181029141345_TuAn_TT4003.png\",\n" +
                "            \"bjdm1\": \"TT4003\",\n" +
                "            \"bjmc1\": \"????????????-F ?????????\",\n" +
                "            \"fjPath\": \"\",\n" +
                "            \"videoURL\": \"\",\n" +
                "            \"groupnext\": \"\",\n" +
                "            \"groupchild\": \"\"\n" +
                "          }\n" +
                "        ]";
        List<CuttingArrayOp> cuttingArrayOps = JSON.parseObject(jsonString,
                new TypeReference<List<CuttingArrayOp>>() {
                });
        return cuttingArrayOps;
    }


    @Override
    public String sendCustomStylePath(String orderNo, String lineId, String version, boolean isSend) {
        CustomStyleCraftCourse style = null;
        if (StringUtils.isNotBlank(version)) {
            style = customStyleCraftCourseDao.getOrderCustomStyleByVersion(orderNo, lineId, version);
            if(null != style && !BusinessConstants.Status.PUBLISHED_STATUS.equals(style.getStatus())) {
                style = null;
            }
        } else {
            List<CustomStyleCraftCourse> styleList = customStyleCraftCourseDao.getOrderCustmStyleBaseList(orderNo, lineId);
            if (ObjectUtils.isNotEmptyList(styleList)) {
                for(CustomStyleCraftCourse sty: styleList) {
                    if(BusinessConstants.Status.PUBLISHED_STATUS.equals(sty.getStatus())) {
                        style = sty;
                        break;
                    }
                }
            }
        }
        if (style == null) {
            return null;
        }

        //????????????id??????
        List<Long> partRandomCodes = customStylePartDao.getCustomStyleRandomCodeByMainRnadomCode(style.getRandomCode());
        if (ObjectUtils.isEmptyList(partRandomCodes)) {
            return null;
        }

        //?????????????????????????????????
        Map<Long, DesignPart> designPartMap = customStylePartDao.getCustomDesignPart(style.getRandomCode());

        //????????????
        List<CustomStylePartCraft> crafts = customStylePartCraftDao.getPartRandomCodeCraftList(partRandomCodes);
        if (ObjectUtils.isEmptyList(crafts)) {
            return null;
        }

        //???????????????
        List<String> craftCodes = crafts.stream().map(CustomStylePartCraft::getCraftCode).collect(Collectors.toList());
        Map<String, SewingCraftWarehouse> sewingMap = new HashMap<>();
        if (ObjectUtils.isEmptyList(craftCodes)) {
            List<SewingCraftWarehouse> sewingList = sewingCraftWarehouseDao.getDataByCraftCodeList(craftCodes);
            if (ObjectUtils.isNotEmptyList(sewingList)) {
                sewingList.stream().forEach(x -> sewingMap.put(x.getCraftCode(), x));
            }
        }

        Map<String, CraftStd> craftStdMap = customStylePartCraftDao.getCustomCraftStdMap(partRandomCodes);

        //????????????????????????
        List<CustomStylePartCraft> craftSorts = crafts.stream().sorted(Comparator.comparing(CustomStylePartCraft::getCraftFlowNum)).collect(Collectors.toList());

        //????????????????????????
        Map<String, ProductionPart> productionPartMap = productionPartDao.getMapByMainFrameCode(style.getMainFrameCode());

        //????????????????????????
        List<CraftMainFrameRoute> routes = craftMainFrameRouteDao.getByMainFrameCode(style.getMainFrameCode());

        //????????????????????????
        Map<String, WorkplaceCraft> workplaceCraftMap = customStylePartCraftDao.getWorkplaceCraftMap(style.getMainFrameCode(), partRandomCodes);

        //??????????????????
        List<CraftFile> craftFiles = customStylePartCraftDao.getCustomCraftStdFile(partRandomCodes);
        Map<String, List<CraftFile>> craftFileMap = craftFiles.stream().collect(Collectors.groupingBy(CraftFile::getKeyStr));

        //????????????
        Map<String, WorkType> workTypeMap = workTypeDao.getWorkTypeMap();
        //????????????
        Map<String, MakeType> makeTypeMap = makeTypeDao.getMakeTypeMap();
        //??????
        Map<String, Dictionary> sectionMap = dictionaryDao.getDictionaryMap("Section");

        //??????????????????
        Map<String, PartPartMiddleConfig> partMiddleConfigMap = partPartMiddleConfigDao.getPartPartMiddleConfigMap(null, null);

        //??????????????????
        Map<String, String> productionMinCraftMap = new HashMap<>();
        Map<String, String> productionMaxCraftMap = new HashMap<>();
        Map<String, Integer> productionPartMaxFlowMap = new HashMap<>();
        Map<String, Integer> productionPartMinFlowMap = new HashMap<>();
        Map<String, SewingArrayOp> craftSewingOpMap = new HashMap<>();
        List<FlowNumConfig> flowNumConfigs = productionPartService.getFlowNumConfigAll();


        List<SewingArrayOp> sewingArrayOps = new ArrayList<>();
        List<SewingArrayOp> preArrayOps = new ArrayList<>();
        List<String> cutUrls = new ArrayList<>();

        for (int i = 0; i < craftSorts.size(); i++) {
            CustomStylePartCraft craft = craftSorts.get(i);
            WorkplaceCraft workplace = workplaceCraftMap.getOrDefault(craft.getCraftCode(), null);

            //????????????????????????????????????
            FlowNumConfig flowNumConfig = flowNumConfigs.stream().filter(x->craft.getCraftFlowNum() != null
                    && craft.getCraftFlowNum().toString().startsWith(x.getFlowNum())).findFirst().orElse(null);
            if(flowNumConfig == null) {
                continue;
            }

            String productionPartCode = flowNumConfig.getProductionPartCode();
            //ProductionPart productionPart = productionPartMap.getOrDefault(workplace.getProductionPartCode(), null);
            ProductionPart productionPart = productionPartMap.getOrDefault(productionPartCode, null);
            if (productionPart == null) {
                continue;
            }

            boolean isXW = false;
            if (productionPart.getProductionPartCode() != null) {
                isXW = productionPart.getProductionPartCode().endsWith("XW");
            }

            SewingArrayOp op = new SewingArrayOp();
            op.setScbjdm(productionPart.getProductionPartCode());
            op.setScbjmc(productionPart.getProductionPartName());
            //op.setScbj(productionPart.getCustomProductionAreaCode());
            //op.setScbjdm(productionPart.getCustomProductionAreaCode());
            //op.setScbjmc(productionPart.getCustomProductionAreaName());

            op.setXh(craft.getCraftFlowNum());
            op.setGxdm(craft.getCraftCode());


            op.setGxmc(craft.getCraftName());
            op.setGxms(craft.getCraftDescript());
            if (StringUtils.isNotBlank(craft.getCraftPartCode())) {
                PartPartMiddleConfig partMiddleConfig = partMiddleConfigMap.getOrDefault(craft.getCraftPartCode(), null);
                if (partMiddleConfig != null) {
                    op.setBjdm(partMiddleConfig.getPartMiddleCode());
                }
            }
            op.setBjmc(craft.getCraftPartName());
            op.setSMV(craft.getStandardTime().multiply(new BigDecimal(60)).toString());
            op.setDj(craft.getStandardPrice().toString());
            op.setZhxh("");

            CraftStd craftStd = craftStdMap.getOrDefault(craft.getCraftCode(), null);
            if (craftStd != null) {
                op.setPzsm(craftStd.getRequireQuality());
                op.setGzsm(craftStd.getMakeDesc());
            } else {
                op.setPzsm(craft.getQualitySpec());
                op.setGzsm(craft.getMakeDescription());
            }

            if(workplace != null) {
                op.setMtmbjdm(workplace.getWorkplaceCraftCode());
            }
            op.setSgzdm(craft.getCraftCode());
            op.setSgzmc(craft.getCraftName());
            op.setPattDesc("");
            DesignPart designPart = designPartMap.getOrDefault(craft.getStylePartRandomCode(), null);
            if (designPart != null) {
                op.setImageURL(designPart.getDesignImage());
            }
            op.setBjdm1(craft.getDesignPartCode());
            op.setBjmc1(craft.getDesignPartName());
            op.setMtmpart(productionPartCode);

            String zgsm = craft.getDesignPartName();
            String gzsm = op.getGzsm();
            if (gzsm == null) {
                gzsm = "";
            }
            if (gzsm.endsWith("\n") || StringUtils.isBlank(gzsm)) {
                op.setGzsm(gzsm + zgsm);
            } else {
                op.setGzsm(gzsm + "\r\n" + zgsm);
            }


            List<CraftFile> files = craftFileMap.getOrDefault(craft.getCraftCode(), null);
            if (ObjectUtils.isNotEmptyList(files)) {
                List<String> handPics = files.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_HAND_IMG))
                        .map(CraftFile::getFileUrl).collect(Collectors.toList());
                if (ObjectUtils.isNotEmptyList(handPics)) {
                    op.setFjPath(StringUtils.join(handPics, ","));
                    SewingCraftWarehouse sewing = sewingMap.getOrDefault(craft.getCraftCode(), null);
                    if (sewing != null && Boolean.TRUE.equals(sewing.getIsSendCutPic())) {
                        cutUrls.addAll(handPics);
                    }
                }

                List<String> videos = files.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_STD_VIDEO))
                        .map(CraftFile::getFileUrl).collect(Collectors.toList());
                if (ObjectUtils.isNotEmptyList(videos)) {
                    op.setVideoURL(StringUtils.join(videos, ","));
                }
            }

            //???????????????
            if (StringUtils.isBlank(op.getFjPath()) || StringUtils.isBlank(op.getVideoURL())) {
                String urlGst = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.GST_RESOURCE_URL, "");
                if (StringUtils.isNotBlank(urlGst)) {
                    if (StringUtils.isBlank(op.getFjPath())) {
                        op.setFjPath(urlGst + op.getGxdm() + "-1.jpeg");
                    }
                    if (StringUtils.isBlank(op.getVideoURL())) {
                        op.setVideoURL(urlGst + op.getGxdm() + ".mp4");
                    }
                }
            }

            if (!isXW) {
                sewingArrayOps.add(op);

                int lastMax = productionPartMaxFlowMap.getOrDefault(productionPart.getProductionPartCode(), -1);
                int lastMin = productionPartMinFlowMap.getOrDefault(productionPart.getProductionPartCode(), -1);
                int currFlowNum = Integer.parseInt(craft.getCraftFlowNum());

                if (currFlowNum > lastMax || lastMax == -1) {
                    productionPartMaxFlowMap.put(productionPart.getProductionPartCode(), currFlowNum);
                    productionMaxCraftMap.put(productionPart.getProductionPartCode(), craft.getCraftCode());
                }
                if (currFlowNum < lastMin || lastMin == -1) {
                    productionPartMinFlowMap.put(productionPart.getProductionPartCode(), currFlowNum);
                    productionMinCraftMap.put(productionPart.getProductionPartCode(), craft.getCraftCode());
                }
                craftSewingOpMap.put(productionPart.getProductionPartCode() + craft.getCraftCode(), op);
            } else {
                preArrayOps.add(op);
            }
        }

        for (int i = 0; i < sewingArrayOps.size() - 1; i++) {
            sewingArrayOps.get(i).setHxgxdm(sewingArrayOps.get(i + 1).getGxdm());
        }
        for (int i = 0; i < preArrayOps.size() - 1; i++) {
            preArrayOps.get(i).setHxgxdm(preArrayOps.get(i + 1).getGxdm());
        }

        //??????????????????
        sewingArrayOps = insertVirtualCraft(sewingArrayOps, workTypeMap, makeTypeMap, sectionMap, partMiddleConfigMap);

        Map<String, Long> partCodeNumMap = routes.stream().collect(groupingBy(CraftMainFrameRoute::getNextProductionPartCode, counting()));

        //????????????????????????
        for (CraftMainFrameRoute route : routes) {
            String currCraftCode = productionMaxCraftMap.getOrDefault(route.getProductionPartCode(), null);
            String nextCraftCode = productionMinCraftMap.getOrDefault(route.getNextProductionPartCode(), null);
            if (currCraftCode == null || nextCraftCode == null) {
                continue;
            }
            SewingArrayOp op = craftSewingOpMap.getOrDefault(route.getProductionPartCode() + currCraftCode, null);
            if (op == null) {
                continue;
            }
            op.setHxgxdm(nextCraftCode);

            //????????????????????????????????????????????????
            long num = partCodeNumMap.get(route.getNextProductionPartCode());
            if (num > 1) {
                op = craftSewingOpMap.getOrDefault(route.getNextProductionPartCode() + nextCraftCode, null);
                if (op != null) {
                    op.setZhxh("1");
                }
            }
        }


        //??????
        String[] xhNameArr = {"???", "???", "???", "???", "???", "???", "???", "???", "???", "???"};
        int idx = 0;
        List<CuttingArrayOp> cuttingArrayOps = GetCustomCutPathNoPattern();
        for (DesignPart designPart : designPartMap.values()) {
            //????????? ????????????????????????
            if ("CP".equals(designPart.getPatternMode())) {
                CuttingArrayOp cuttingOp = new CuttingArrayOp();
                cuttingOp.setXh(Integer.toString(idx + 1));
                cuttingOp.setGxdm("XH" + String.valueOf(idx + 1));
                cuttingOp.setHxgxdm("");
                cuttingOp.setGxmc("????????????" + xhNameArr[idx]);
                cuttingOp.setBjdm("MB");
                cuttingOp.setBjmc("??????");
                cuttingOp.setSmv("0.0000");
                cuttingOp.setDj("0.0000");
                cuttingOp.setPzsm("");
                cuttingOp.setGzsm(designPart.getDesignName() + "-" + designPart.getRemark() + " " + designPart.getPatternTechnology());
                cuttingOp.setSgzdm("XH" + String.valueOf(idx + 1));
                cuttingOp.setPattDesc(designPart.getPatternTechnology() + " " + designPart.getGongYiExplain() + " " + designPart.getPatternMsg());
                if (StringUtils.isNotBlank(designPart.getImageDurlMtm())) {
                    cuttingOp.setImageURL(designPart.getImageDurlMtm());
                } else {
                    cuttingOp.setImageURL(designPart.getDesignImage());
                }
                cuttingOp.setBjdm1(designPart.getDesignCode());
                cuttingOp.setBjmc1(designPart.getDesignName() + "-" + designPart.getRemark());
                cuttingOp.setFjPath("");
                cuttingOp.setVideoURL("");
                cuttingOp.setGroupnext("");
                cuttingOp.setGroupchild("");
                cuttingArrayOps.add(cuttingOp);
            }
        }
        //?????????????????????
        for (int i = 0; i < cuttingArrayOps.size() - 1; i++) {
            CuttingArrayOp cuttingOp = cuttingArrayOps.get(i);
            if (StringUtils.isBlank(cuttingOp.getHxgxdm())) {
                cuttingOp.setHxgxdm(cuttingArrayOps.get(i + 1).getGxdm());
            }
        }

        //????????????????????????
        if (ObjectUtils.isNotEmptyList(cutUrls)) {
            //????????????
            cuttingArrayOps.stream().filter(x -> "FI".equals(x.getGxdm())).findFirst().ifPresent(a -> {
                a.setFjPath(StringUtils.join(cutUrls, ","));
            });
        }

        //???????????????
        if ("B".equals(style.getFabricDirection()) || "A".equals(style.getFabricDirection())) {
            //????????????
            CuttingArrayOp op = cuttingArrayOps.stream().filter(x -> "CA".equals(x.getGxdm())).findFirst().orElse(null);
            if (op != null) {
                String suffix = "B".equals(style.getFabricDirection()) ? "??????????????????" : "??????????????????";
                op.setPzsm(op.getPzsm() + suffix);
            }
        }

        CraftOperationPath operPath = new CraftOperationPath();
        operPath.setSite("8081");
        operPath.setDdbh(style.getOrderNo());
        operPath.setXh(style.getOrderLineId());
        operPath.setKsbh(style.getStyleCode());
        operPath.setPreArrayOp(preArrayOps);
        operPath.setCuttingArrayOp(cuttingArrayOps);
        operPath.setSewingArrayOp(sewingArrayOps);

        if (isSend) {
            sendPiService.postObject(CUSTOM_STYLE_OPERATION_PATH_URL, operPath);
            return "";
        } else {
            return JSON.toJSONString(operPath,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullNumberAsZero);
        }
    }

    @Override
    public String sendBigStylePath(String styleAnalyseCode, boolean isSend) {
        BigStyleAnalyseMaster style = bigStyleAnalyseMasterDao.getBigStyleAnalyseByCode(styleAnalyseCode);
        if (style == null) {
            return null;
        }
        if (!BusinessConstants.Status.PUBLISHED_STATUS.equals(style.getStatus())) {
            return null;
        }

        //??????????????????
        Map<String, DesignPart> designPartMap = bigStyleAnalysePartCraftDao.getBigStyleDesignPart(style.getRandomCode());


        //????????????
        List<BigStyleAnalysePartCraftDetail> craftSorts = bigStyleAnalysePartCraftDetailDao.getBigStyleAnalysePartCraftDetail(style.getRandomCode());
        if (ObjectUtils.isEmptyList(craftSorts)) {
            return null;
        }

        //????????????
        List<SewingCraftWarehouseWorkplace> sewingWorkplaces = styleSewingCraftWarehouseWorkplaceDao.getSewingCraftWarehouseWorkplace(style.getRandomCode());
        Map<String, SewingCraftWarehouseWorkplace> sewingWorkplaceMap = new HashMap<>();
        for (SewingCraftWarehouseWorkplace itm : sewingWorkplaces) {
            sewingWorkplaceMap.put(itm.getPartCraftMainCode() + itm.getCraftCode(), itm);
        }

        //????????????

        List<SewingCraftWarehouse> craftWarehouses = styleSewingCraftWarehouseDao.getSewingCraftWarehouse(style.getRandomCode());
        Map<String, SewingCraftWarehouse> sewingCraftMap = new HashMap<>();
        for (SewingCraftWarehouse itm : craftWarehouses) {
            sewingCraftMap.put(itm.getPartCraftMainCode() + itm.getCraftCode(), itm);
        }


        //????????????????????????
        Map<String, ProductionPart> productionPartMap = productionPartDao.getMapByMainFrameCode(style.getMainFrameCode());

        //????????????????????????
        List<CraftMainFrameRoute> routes = craftMainFrameRouteDao.getByMainFrameCode(style.getMainFrameCode());

        //??????????????????
        List<CraftFile> craftFiles = bigStyleAnalyseMasterDao.getCraftFileByRandomCode(style.getRandomCode());
        Map<String, List<CraftFile>> craftFileMap = craftFiles.stream().collect(Collectors.groupingBy(CraftFile::getKeyStr));

        Map<String, CraftStd> craftStdMap = styleSewingCraftStdDao.getBigStyleCraftStdMap(style.getRandomCode(), null);

        //????????????
        Map<String, WorkType> workTypeMap = workTypeDao.getWorkTypeMap();
        //????????????
        Map<String, MakeType> makeTypeMap = makeTypeDao.getMakeTypeMap();
        //??????
        Map<String, Dictionary> sectionMap = dictionaryDao.getDictionaryMap("Section");

        //??????????????????
        Map<String, PartPartMiddleConfig> partMiddleConfigMap = partPartMiddleConfigDao.getPartPartMiddleConfigMap(null, null);


        //??????????????????
        Map<String, String> productionMinCraftMap = new HashMap<>();
        Map<String, String> productionMaxCraftMap = new HashMap<>();
        Map<String, Integer> productionPartMaxFlowMap = new HashMap<>();
        Map<String, Integer> productionPartMinFlowMap = new HashMap<>();
        Map<String, SewingArrayOp> craftSewingOpMap = new HashMap<>();
        List<FlowNumConfig> flowNumConfigs = productionPartService.getFlowNumConfigAll();


        List<SewingArrayOp> sewingArrayOps = new ArrayList<>();
        List<SewingArrayOp> preArrayOps = new ArrayList<>();

        for (int i = 0; i < craftSorts.size(); i++) {
            BigStyleAnalysePartCraftDetail craft = craftSorts.get(i);
            SewingCraftWarehouseWorkplace workplace = sewingWorkplaceMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);
            SewingCraftWarehouse sewingCraft = sewingCraftMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);
            if (sewingCraft == null) {
                continue;
            }


            //????????????????????????????????????
            FlowNumConfig flowNumConfig = flowNumConfigs.stream().filter(x->craft.getCraftNo() != null
                    && craft.getCraftNo().toString().startsWith(x.getFlowNum())).findFirst().orElse(null);
            if(flowNumConfig == null) {
                continue;
            }
            String productionPartCode = flowNumConfig.getProductionPartCode();
            //ProductionPart productionPart = productionPartMap.getOrDefault(workplace.getProductionPartCode(), null);
            ProductionPart productionPart = productionPartMap.getOrDefault(productionPartCode, null);
            if (productionPart == null) {
                continue;
            }

            boolean isXW = false;
            if (productionPart.getProductionPartCode() != null) {
                isXW = productionPart.getProductionPartCode().endsWith("XW");
            }

            SewingArrayOp op = new SewingArrayOp();
            op.setScbjdm(productionPart.getProductionPartCode());
            op.setScbjmc(productionPart.getProductionPartName());
            //op.setScbj(productionPart.getCustomProductionAreaCode());
            //op.setScbjdm(productionPart.getCustomProductionAreaCode());
            //op.setScbjmc(productionPart.getCustomProductionAreaName());


            op.setXh(craft.getCraftNo());
            op.setGxdm(craft.getCraftCode());

            op.setGxmc(craft.getCraftName());
            op.setGxms(craft.getCraftRemark());
            if (StringUtils.isNotBlank(sewingCraft.getCraftPartCode())) {
                PartPartMiddleConfig partMiddleConfig = partMiddleConfigMap.getOrDefault(sewingCraft.getCraftPartCode(), null);
                if (partMiddleConfig != null) {
                    op.setBjdm(partMiddleConfig.getPartMiddleCode());
                }
            }
            op.setBjmc(sewingCraft.getCraftPartName());
            op.setSMV(sewingCraft.getStandardTime().multiply(new BigDecimal(60)).toString());
            op.setDj(sewingCraft.getStandardPrice().toString());
            op.setZhxh("");
            if(workplace != null) {
                op.setMtmbjdm(workplace.getWorkplaceCraftCode());
            }
            op.setSgzdm(craft.getCraftCode());
            op.setSgzmc(craft.getCraftName());
            op.setPattDesc("");
            DesignPart designPart = designPartMap.getOrDefault(craft.getPartCraftMainCode(), null);
            if (designPart != null) {
                op.setImageURL(designPart.getDesignImage());
                op.setBjdm1(designPart.getDesignCode());
                op.setBjmc1(designPart.getDesignName());
            }
            op.setMtmpart(productionPartCode);

            if (sewingCraft.getIsCancelSend() == null || !sewingCraft.getIsCancelSend()) {
                CraftStd std = craftStdMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);
                if (std != null) {
                    op.setPzsm(std.getRequireQuality());
                    op.setGzsm(std.getMakeDesc());
                } else {
                    op.setPzsm(sewingCraft.getQualitySpec());
                    op.setGzsm(sewingCraft.getMakeDescription());
                }


                List<CraftFile> files = craftFileMap.getOrDefault(craft.getCraftCode(), null);
                if (ObjectUtils.isNotEmptyList(files)) {
                    List<String> handPics = files.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_HAND_IMG))
                            .map(CraftFile::getFileUrl).collect(Collectors.toList());
                    if (ObjectUtils.isNotEmptyList(handPics)) {
                        op.setFjPath(StringUtils.join(handPics, ","));
                    }

                    List<String> videos = files.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_STD_VIDEO))
                            .map(CraftFile::getFileUrl).collect(Collectors.toList());
                    if (ObjectUtils.isNotEmptyList(videos)) {
                        op.setVideoURL(StringUtils.join(videos, ","));
                    }
                }

                //???????????????
                if (StringUtils.isBlank(op.getFjPath()) || StringUtils.isBlank(op.getVideoURL())) {
                    String urlGst = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.GST_RESOURCE_URL, "");
                    if (StringUtils.isNotBlank(urlGst)) {
                        if (StringUtils.isBlank(op.getFjPath())) {
                            op.setFjPath(urlGst + op.getGxdm() + "-1.jpeg");
                        }
                        if (StringUtils.isBlank(op.getVideoURL())) {
                            op.setVideoURL(urlGst + op.getGxdm() + ".mp4");
                        }
                    }
                }
            }

            if (!isXW) {
                sewingArrayOps.add(op);

                int lastMax = productionPartMaxFlowMap.getOrDefault(productionPart.getProductionPartCode(), -1);
                int lastMin = productionPartMinFlowMap.getOrDefault(productionPart.getProductionPartCode(), -1);
                int currFlowNum = Integer.parseInt(craft.getCraftNo());

                if (currFlowNum > lastMax || lastMax == -1) {
                    productionPartMaxFlowMap.put(productionPart.getProductionPartCode(), currFlowNum);
                    productionMaxCraftMap.put(productionPart.getProductionPartCode(), craft.getCraftCode());
                }
                if (currFlowNum < lastMin || lastMin == -1) {
                    productionPartMinFlowMap.put(productionPart.getProductionPartCode(), currFlowNum);
                    productionMinCraftMap.put(productionPart.getProductionPartCode(), craft.getCraftCode());
                }
                craftSewingOpMap.put(productionPart.getProductionPartCode() + craft.getCraftCode(), op);
            } else {
                preArrayOps.add(op);
            }
        }

        for (int i = 0; i < sewingArrayOps.size() - 1; i++) {
            sewingArrayOps.get(i).setHxgxdm(sewingArrayOps.get(i + 1).getGxdm());
        }
        for (int i = 0; i < preArrayOps.size() - 1; i++) {
            preArrayOps.get(i).setHxgxdm(preArrayOps.get(i + 1).getGxdm());
        }

        //??????????????????
        sewingArrayOps = insertVirtualCraft(sewingArrayOps, workTypeMap, makeTypeMap, sectionMap, partMiddleConfigMap);


        Map<String, Long> partCodeNumMap = routes.stream().collect(groupingBy(CraftMainFrameRoute::getNextProductionPartCode, counting()));

        //????????????????????????
        for (CraftMainFrameRoute route : routes) {
            String currCraftCode = productionMaxCraftMap.getOrDefault(route.getProductionPartCode(), null);
            String nextCraftCode = productionMinCraftMap.getOrDefault(route.getNextProductionPartCode(), null);
            if (currCraftCode == null || nextCraftCode == null) {
                continue;
            }
            SewingArrayOp op = craftSewingOpMap.getOrDefault(route.getProductionPartCode() + currCraftCode, null);
            if (op == null) {
                continue;
            }
            op.setHxgxdm(nextCraftCode);

            //????????????????????????????????????????????????
            long num = partCodeNumMap.get(route.getNextProductionPartCode());
            if (num > 1) {
                op = craftSewingOpMap.getOrDefault(route.getNextProductionPartCode() + nextCraftCode, null);
                if (op != null) {
                    op.setZhxh("1");
                }
            }
        }


        //??????
        List<CuttingArrayOp> cuttingArrayOps = GetBigCutPath();
        String[] xhNameArr = {"???", "???", "???", "???", "???", "???", "???", "???", "???", "???"};
        int idx = 0;
        for (DesignPart designPart : designPartMap.values()) {
            //????????? ????????????????????????
            if ("CP".equals(designPart.getPatternMode())) {
                CuttingArrayOp cuttingOp = new CuttingArrayOp();
                cuttingOp.setXh(Integer.toString(idx + 1));
                cuttingOp.setGxdm("XH" + String.valueOf(idx + 1));
                cuttingOp.setHxgxdm("");
                cuttingOp.setGxmc("????????????" + xhNameArr[idx]);
                cuttingOp.setBjdm("MB");
                cuttingOp.setBjmc("??????");
                cuttingOp.setSmv("0.0000");
                cuttingOp.setDj("0.0000");
                cuttingOp.setPzsm("");
                cuttingOp.setGzsm(designPart.getDesignName() + "-" + designPart.getRemark() + " " + designPart.getPatternTechnology());
                cuttingOp.setSgzdm("XH" + String.valueOf(idx + 1));
                cuttingOp.setPattDesc(designPart.getPatternTechnology() + " " + designPart.getGongYiExplain() + " " + designPart.getPatternMsg());
                if (StringUtils.isNotBlank(designPart.getImageDurlMtm())) {
                    cuttingOp.setImageURL(designPart.getImageDurlMtm());
                } else {
                    cuttingOp.setImageURL(designPart.getDesignImage());
                }
                cuttingOp.setBjdm1(designPart.getDesignCode());
                cuttingOp.setBjmc1(designPart.getDesignName() + "-" + designPart.getRemark());
                cuttingOp.setFjPath("");
                cuttingOp.setVideoURL("");
                cuttingOp.setGroupnext("");
                cuttingOp.setGroupchild("");
                cuttingArrayOps.add(cuttingOp);
            }
        }

        //?????????????????????
        for (int i = 0; i < cuttingArrayOps.size() - 1; i++) {
            CuttingArrayOp cuttingOp = cuttingArrayOps.get(i);
            if (StringUtils.isBlank(cuttingOp.getHxgxdm())) {
                cuttingOp.setHxgxdm(cuttingArrayOps.get(i + 1).getGxdm());
            }
        }

        CraftOperationPath operPath = new CraftOperationPath();
        operPath.setDdbh("");
        operPath.setXh("1");
        operPath.setSite("8081");
        operPath.setKsbh(style.getCtStyleCode());
        operPath.setPreArrayOp(preArrayOps);
        operPath.setCuttingArrayOp(cuttingArrayOps);
        operPath.setSewingArrayOp(sewingArrayOps);

//        DataParent<CraftOperationPath> parent = new DataParent<>();
//        parent.setInterfaceType("pi.custStyle");
//        parent.setSynTime(new Date());
//        parent.setCount("1");
//        List<DataChild<CraftOperationPath>> items = new ArrayList<>();
//        DataChild<CraftOperationPath> item = new DataChild<CraftOperationPath>();
//        item.setItemId("0");
//        item.setItem(operPath);
//        items.add(item);
//        parent.setItems(items);

        if (isSend) {
            sendPiService.postObject(CUSTOM_STYLE_OPERATION_PATH_URL, operPath);
            return "";
        } else {
            return JSON.toJSONString(operPath,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullNumberAsZero);
        }
    }


    private List<SewingArrayOp> insertVirtualCraft(List<SewingArrayOp> opList,
                                                   Map<String, WorkType> workTypeMap,
                                                   Map<String, MakeType> makeTypeMap,
                                                   Map<String, Dictionary> sectionMap,
                                                   Map<String, PartPartMiddleConfig> partMiddleConfigMap
    ) {
        List<String> codeList = new ArrayList<>();
        codeList.add("TQTED010");
        List<SewingCraftWarehouse> crafts = sewingCraftWarehouseDao.getDataByCraftCodeList(codeList);
        if (ObjectUtils.isEmptyList(crafts)) {
            return opList;
        }
        SewingCraftWarehouse craft = crafts.get(0);
        List<SewingCraftWarehouseWorkplace> workplaces = sewingCraftWarehouseWorkplaceDao.getDataBySewingCraftRandomCode(craft.getRandomCode());
        if (ObjectUtils.isEmptyList(workplaces)) {
            return opList;
        }
        SewingCraftWarehouseWorkplace workplace = workplaces.get(0);


        SewingArrayOp op = new SewingArrayOp();
        op.setScbj(workplace.getProductionPartCode());
        op.setScbjdm(workplace.getProductionPartCode());
        op.setScbjmc(workplace.getProductionPartName());

        op.setXh(workplace.getCraftFlowNum().toString());
        op.setGxdm(craft.getCraftCode());
        op.setZhxh("0");
        op.setZgxh("0");
        op.setScbjxh("0");
        op.setBmxh("0");
        op.setBmdm(craft.getWorkTypeCode());
        if (StringUtils.isNotBlank(craft.getWorkTypeCode())) {
            WorkType workType = workTypeMap.getOrDefault(craft.getWorkTypeCode(), null);
            if (workType != null) {
                op.setBmmc(workType.getWorkTypeName());
                op.setGddm(workType.getSectionCode());
                op.setGdmc(workType.getSectionName());
            }
        }
        op.setZglb(craft.getMakeTypeCode());
        if (StringUtils.isNotBlank(craft.getMakeTypeCode())) {
            MakeType makeType = makeTypeMap.getOrDefault(craft.getMakeTypeCode(), null);
            if (makeType != null) {
                op.setZgmc(makeType.getMakeTypeName());
            }
        }

        op.setGxmc(craft.getCraftName());
        op.setGxms(craft.getDescription());

        if (StringUtils.isNotBlank(craft.getCraftPartCode())) {
            PartPartMiddleConfig partMiddleConfig = partMiddleConfigMap.getOrDefault(craft.getCraftPartCode(), null);
            if (partMiddleConfig != null) {
                op.setBjdm(partMiddleConfig.getPartMiddleCode());
            }
        }
        op.setBjmc(craft.getCraftPartName());
        op.setSMV(craft.getStandardTime().multiply(new BigDecimal(60)).toString());
        op.setDj(craft.getStandardPrice().toString());
        op.setMtmbjdm(workplace.getWorkplaceCraftCode());
        op.setSgzdm(craft.getCraftCode());
        op.setSgzmc(craft.getCraftName());
        op.setPattDesc("");
        op.setMtmpart(workplace.getProductionPartCode());
//        op.setImageURL(designPart.getDesignImage());
//        op.setBjdm1(designPart.getDesignCode());
//        op.setBjmc1(designPart.getDesignName());
//        op.setPzsm(std.getRequireQuality());
//        op.setGzsm(std.getMakeDesc());
//        op.setFjPath(a.getFileUrl());
//        op.setVideoURL(a.getFileUrl());

        int lastIdx = -1;
        for (int i = 0; i < opList.size(); i++) {
            SewingArrayOp itm = opList.get(i);
            if ("QC".equals(itm.getZglb()) && itm.getXh() != null && itm.getHxgxdm() != null) {
                if (lastIdx == -1) {
                    lastIdx = i;
                } else {
                    int lastXh = Integer.parseInt(opList.get(lastIdx).getXh());
                    int curXh = Integer.parseInt(itm.getXh());
                    if (curXh > lastXh) {
                        lastIdx = i;
                    }
                }
            }
        }

        if (lastIdx != -1) {
            SewingArrayOp qcOp = opList.get(lastIdx);
            op.setHxgxdm(qcOp.getHxgxdm());
            qcOp.setHxgxdm(op.getGxdm());
            Integer xh = Integer.parseInt(qcOp.getXh());
            xh = xh + 1;
            op.setXh(xh.toString());
            opList.add(lastIdx + 1, op);
        }

        return opList;
    }

    @Override
    public String sendBigOrderPath(String productionTicketNo, boolean isSend) {

        Map<String, Object> param = new HashMap<>();
        param.put("productionTicketNo", productionTicketNo);
        try {
            //??????????????????
            orderProcessingStatusService.addOrUpdate(productionTicketNo, OderProcessingStatusConstants.BigStyleStatusName.BIG_STYLE_NAME_1270, OderProcessingStatusConstants.BigStyleStatus.BIG_STYLE_1270, "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<BigStyleAnalyseMaster> styles = bigOrderMasterDao.searchFromBigStyleAnalyse(param);
        if (ObjectUtils.isEmptyList(styles)) {
            param.clear();
            param.put("ctStyleCode", productionTicketNo);
            styles = bigOrderMasterDao.searchFromBigStyleAnalyse(param);
            if (ObjectUtils.isEmptyList(styles)) {
                return null;
            } else {
                //???????????????
                styles.stream().sorted(Comparator.comparing(BigStyleAnalyseMaster::getReleaseTime));
            }
        }

        BigStyleAnalyseMaster style = null;
        for(BigStyleAnalyseMaster sty: styles) {
            if (BusinessConstants.Status.PUBLISHED_STATUS.equals(sty.getStatus())) {
                //????????????
                style = sty;
            }
        }

        if(null == style) {
            return null;
        }


        //??????????????????
        Map<String, DesignPart> designPartMap = bigOrderPartCraftDao.getBigOrderDesignPart(style.getRandomCode());


        //????????????
        List<BigStyleAnalysePartCraftDetail> craftSorts = bigOrderPartCraftDetailDao.getBigOrderAnalysePartCraftDetail(style.getRandomCode());
        if (ObjectUtils.isEmptyList(craftSorts)) {
            return null;
        }

        //????????????
        List<SewingCraftWarehouseWorkplace> sewingWorkplaces = bigOrderSewingCraftWarehouseWorkplaceDao.getSewingCraftWarehouseWorkplace(style.getRandomCode());
        Map<String, SewingCraftWarehouseWorkplace> sewingWorkplaceMap = new HashMap<>();
        for (SewingCraftWarehouseWorkplace itm : sewingWorkplaces) {
            sewingWorkplaceMap.put(itm.getPartCraftMainCode() + itm.getCraftCode(), itm);
        }

        //????????????
        List<SewingCraftWarehouse> craftWarehouses = bigOrderSewingCraftWarehouseDao.getSewingCraftWarehouse(style.getRandomCode());
        Map<String, SewingCraftWarehouse> sewingCraftMap = new HashMap<>();
        for (SewingCraftWarehouse itm : craftWarehouses) {
            sewingCraftMap.put(itm.getPartCraftMainCode() + itm.getCraftCode(), itm);
        }


        //????????????????????????
        Map<String, ProductionPart> productionPartMap = productionPartDao.getMapByMainFrameCode(style.getMainFrameCode());

        //????????????????????????
        List<CraftMainFrameRoute> routes = craftMainFrameRouteDao.getByMainFrameCode(style.getMainFrameCode());

        //??????????????????
        List<CraftFile> craftFiles = bigOrderMasterDao.getCraftFileByRandomCode(style.getRandomCode());
        Map<String, List<CraftFile>> craftFileMap = craftFiles.stream().collect(Collectors.groupingBy(CraftFile::getKeyStr));
        Map<String, CraftStd> craftStdMap = bigOrderSewingCraftStdDao.getBigOrderCraftStdMap(style.getRandomCode(), null);
        //????????????
        Map<String, WorkType> workTypeMap = workTypeDao.getWorkTypeMap();
        //????????????
        Map<String, MakeType> makeTypeMap = makeTypeDao.getMakeTypeMap();
        //??????
        Map<String, Dictionary> sectionMap = dictionaryDao.getDictionaryMap("Section");

        //??????????????????
        Map<String, PartPartMiddleConfig> partMiddleConfigMap = partPartMiddleConfigDao.getPartPartMiddleConfigMap(null, null);


        //??????????????????
        Map<String, String> productionMinCraftMap = new HashMap<>();
        Map<String, String> productionMaxCraftMap = new HashMap<>();
        Map<String, Integer> productionPartMaxFlowMap = new HashMap<>();
        Map<String, Integer> productionPartMinFlowMap = new HashMap<>();
        Map<String, SewingArrayOp> craftSewingOpMap = new HashMap<>();
        List<FlowNumConfig> flowNumConfigs = productionPartService.getFlowNumConfigAll();


        List<SewingArrayOp> sewingArrayOps = new ArrayList<>();
        List<SewingArrayOp> preArrayOps = new ArrayList<>();

        for (int i = 0; i < craftSorts.size(); i++) {
            BigStyleAnalysePartCraftDetail craft = craftSorts.get(i);
            SewingCraftWarehouseWorkplace workplace = sewingWorkplaceMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);
            SewingCraftWarehouse sewingCraft = sewingCraftMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);
            if (sewingCraft == null) {
                continue;
            }

            //????????????????????????????????????
            FlowNumConfig flowNumConfig = flowNumConfigs.stream().filter(x->craft.getCraftNo() != null
                    && craft.getCraftNo().toString().startsWith(x.getFlowNum())).findFirst().orElse(null);
            if(flowNumConfig == null) {
                continue;
            }
            String productionPartCode = flowNumConfig.getProductionPartCode();
            //ProductionPart productionPart = productionPartMap.getOrDefault(workplace.getProductionPartCode(), null);
            ProductionPart productionPart = productionPartMap.getOrDefault(productionPartCode, null);
            if (productionPart == null) {
                continue;
            }


            boolean isXW = false;
            if (productionPart.getProductionPartCode() != null) {
                isXW = productionPart.getProductionPartCode().endsWith("XW");
            }

            SewingArrayOp op = new SewingArrayOp();
            op.setScbj(productionPart.getProductionPartCode());
            op.setScbjdm(productionPart.getProductionPartCode());
            op.setScbjmc(productionPart.getProductionPartName());
            //op.setScbj(productionPart.getCustomProductionAreaCode());
            //op.setScbjdm(productionPart.getCustomProductionAreaCode());
            //op.setScbjmc(productionPart.getCustomProductionAreaName());

            op.setXh(craft.getCraftNo());
            op.setGxdm(craft.getCraftCode());
            op.setZhxh("0");
            op.setZgxh("0");
            op.setScbjxh("0");
            op.setBmxh("0");
            op.setBmdm(sewingCraft.getWorkTypeCode());
            if (StringUtils.isNotBlank(sewingCraft.getWorkTypeCode())) {
                WorkType workType = workTypeMap.getOrDefault(sewingCraft.getWorkTypeCode(), null);
                if (workType != null) {
                    op.setBmmc(workType.getWorkTypeName());
                    op.setGddm(workType.getSectionCode());
                    op.setGdmc(workType.getSectionName());
                }
            }
            op.setZglb(sewingCraft.getMakeTypeCode());
            if (StringUtils.isNotBlank(sewingCraft.getMakeTypeCode())) {
                MakeType makeType = makeTypeMap.getOrDefault(sewingCraft.getMakeTypeCode(), null);
                if (makeType != null) {
                    op.setZgmc(makeType.getMakeTypeName());
                }
            }

            op.setGxmc(craft.getCraftName());
            op.setGxms(craft.getCraftRemark());

            if (StringUtils.isNotBlank(sewingCraft.getCraftPartCode())) {
                PartPartMiddleConfig partMiddleConfig = partMiddleConfigMap.getOrDefault(sewingCraft.getCraftPartCode(), null);
                if (partMiddleConfig != null) {
                    op.setBjdm(partMiddleConfig.getPartMiddleCode());
                }
            }
            op.setBjmc(sewingCraft.getCraftPartName());
            op.setSMV(sewingCraft.getStandardTime().multiply(new BigDecimal(60)).toString());
            op.setDj(sewingCraft.getStandardPrice().toString());
            if(workplace != null) {
                op.setMtmbjdm(workplace.getWorkplaceCraftCode());
            }
            op.setSgzdm(craft.getCraftCode());
            op.setSgzmc(craft.getCraftName());
            op.setPattDesc("");
            DesignPart designPart = designPartMap.getOrDefault(craft.getPartCraftMainCode(), null);
            if (designPart != null) {
                op.setImageURL(designPart.getDesignImage());
                op.setBjdm1(designPart.getDesignCode());
                op.setBjmc1(designPart.getDesignName());
            }
            op.setMtmpart(productionPartCode);


            if (sewingCraft.getIsCancelSend() == null || !sewingCraft.getIsCancelSend()) {
                CraftStd std = craftStdMap.getOrDefault(craft.getPartCraftMainCode() + craft.getCraftCode(), null);
                if (std != null) {
                    String bb = std.getRequireQuality();
                    if (StringUtils.isNotEmpty(bb)) {
                        bb = bb.replace("\"", " ");
                    }
                    op.setPzsm(bb);
                    String aa = std.getMakeDesc();
                    if (StringUtils.isNotEmpty(aa)) {
                        aa = aa.replace("\"", " ");
                    }
                    op.setGzsm(aa);
                } else {
                    String bb = sewingCraft.getQualitySpec();
                    if (StringUtils.isNotEmpty(bb)) {
                        bb = bb.replace("\"", " ");
                    }
                    op.setPzsm(bb);
                    String aa = sewingCraft.getMakeDescription();
                    if (StringUtils.isNotEmpty(aa)) {
                        aa = aa.replace("\"", " ");
                    }
                    op.setGzsm(aa);
                }

                List<CraftFile> files = craftFileMap.getOrDefault(craft.getCraftCode(), null);
                if (ObjectUtils.isNotEmptyList(files)) {
                    List<String> handPics = files.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_HAND_IMG))
                            .map(CraftFile::getFileUrl).collect(Collectors.toList());
                    if (ObjectUtils.isNotEmptyList(handPics)) {
                        op.setFjPath(StringUtils.join(handPics, ","));
                    }

                    List<String> videos = files.stream().filter(x -> x.getResourceType().equals(Const.RES_TYPE_STD_VIDEO))
                            .map(CraftFile::getFileUrl).collect(Collectors.toList());
                    if (ObjectUtils.isNotEmptyList(videos)) {
                        op.setVideoURL(StringUtils.join(videos, ","));
                    }
                }

                //???????????????
                if (StringUtils.isBlank(op.getFjPath()) || StringUtils.isBlank(op.getVideoURL())) {
                    String urlGst = StaticDataCache.getInstance().getValue(BusinessConstants.PiReceiveUrlConfig.GST_RESOURCE_URL, "");
                    if (StringUtils.isNotBlank(urlGst)) {
                        if (StringUtils.isBlank(op.getFjPath())) {
                            op.setFjPath(urlGst + op.getGxdm() + "-1.jpeg");
                        }
                        if (StringUtils.isBlank(op.getVideoURL())) {
                            op.setVideoURL(urlGst + op.getGxdm() + ".mp4");
                        }
                    }
                }
            }

            if (!isXW) {
                sewingArrayOps.add(op);
                int lastMax = productionPartMaxFlowMap.getOrDefault(productionPart.getProductionPartCode(), -1);
                int lastMin = productionPartMinFlowMap.getOrDefault(productionPart.getProductionPartCode(), -1);
                int currFlowNum = Integer.parseInt(craft.getCraftNo());

                if (currFlowNum > lastMax || lastMax == -1) {
                    productionPartMaxFlowMap.put(productionPart.getProductionPartCode(), currFlowNum);
                    productionMaxCraftMap.put(productionPart.getProductionPartCode(), craft.getCraftCode());
                }
                if (currFlowNum < lastMin || lastMin == -1) {
                    productionPartMinFlowMap.put(productionPart.getProductionPartCode(), currFlowNum);
                    productionMinCraftMap.put(productionPart.getProductionPartCode(), craft.getCraftCode());
                }

                craftSewingOpMap.put(productionPart.getProductionPartCode() + craft.getCraftCode(), op);
            } else {
                preArrayOps.add(op);
            }
        }

        for (int i = 0; i < sewingArrayOps.size() - 1; i++) {
            sewingArrayOps.get(i).setHxgxdm(sewingArrayOps.get(i + 1).getGxdm());
        }
        for (int i = 0; i < preArrayOps.size() - 1; i++) {
            preArrayOps.get(i).setHxgxdm(preArrayOps.get(i + 1).getGxdm());
        }

        //??????????????????
        sewingArrayOps = insertVirtualCraft(sewingArrayOps, workTypeMap, makeTypeMap, sectionMap, partMiddleConfigMap);

        Map<String, Long> partCodeNumMap = routes.stream().collect(groupingBy(CraftMainFrameRoute::getNextProductionPartCode, counting()));

        //????????????????????????
        for (CraftMainFrameRoute route : routes) {
            String currCraftCode = productionMaxCraftMap.getOrDefault(route.getProductionPartCode(), null);
            String nextCraftCode = productionMinCraftMap.getOrDefault(route.getNextProductionPartCode(), null);
            if (currCraftCode == null || nextCraftCode == null) {
                continue;
            }
            SewingArrayOp op = craftSewingOpMap.getOrDefault(route.getProductionPartCode() + currCraftCode, null);
            if (op == null) {
                continue;
            }
            op.setHxgxdm(nextCraftCode);

            //????????????????????????????????????????????????
            long num = partCodeNumMap.get(route.getNextProductionPartCode());
            if (num > 1) {
                op = craftSewingOpMap.getOrDefault(route.getNextProductionPartCode() + nextCraftCode, null);
                if (op != null) {
                    op.setZhxh("1");
                }
            }
        }


        //??????
        List<CuttingArrayOp> cuttingArrayOps = GetBigCutPath();
        String[] xhNameArr = {"???", "???", "???", "???", "???", "???", "???", "???", "???", "???"};
        int idx = 0;
        for (DesignPart designPart : designPartMap.values()) {
            //????????? ????????????????????????
            if ("CP".equals(designPart.getPatternMode())) {
                CuttingArrayOp cuttingOp = new CuttingArrayOp();
                cuttingOp.setXh(Integer.toString(idx + 1));
                cuttingOp.setGxdm("XH" + String.valueOf(idx + 1));
                cuttingOp.setHxgxdm("");
                cuttingOp.setGxmc("????????????" + xhNameArr[idx]);
                cuttingOp.setBjdm("MB");
                cuttingOp.setBjmc("??????");
                cuttingOp.setSmv("0.0000");
                cuttingOp.setDj("0.0000");
                cuttingOp.setPzsm("");
                cuttingOp.setGzsm(designPart.getDesignName() + "-" + designPart.getRemark() + " " + designPart.getPatternTechnology());
                cuttingOp.setSgzdm("XH" + String.valueOf(idx + 1));
                cuttingOp.setPattDesc(designPart.getPatternTechnology() + " " + designPart.getGongYiExplain() + " " + designPart.getPatternMsg());
                if (StringUtils.isNotBlank(designPart.getImageDurlMtm())) {
                    cuttingOp.setImageURL(designPart.getImageDurlMtm());
                } else {
                    cuttingOp.setImageURL(designPart.getDesignImage());
                }
                cuttingOp.setBjdm1(designPart.getDesignCode());
                cuttingOp.setBjmc1(designPart.getDesignName() + "-" + designPart.getRemark());
                cuttingOp.setFjPath("");
                cuttingOp.setVideoURL("");
                cuttingOp.setGroupnext("");
                cuttingOp.setGroupchild("");
                cuttingArrayOps.add(cuttingOp);
            }
        }

        //?????????????????????
        for (int i = 0; i < cuttingArrayOps.size() - 1; i++) {
            CuttingArrayOp cuttingOp = cuttingArrayOps.get(i);
            if (StringUtils.isBlank(cuttingOp.getHxgxdm())) {
                cuttingOp.setHxgxdm(cuttingArrayOps.get(i + 1).getGxdm());
            }
        }

        CraftOperationPath operPath = new CraftOperationPath();
        operPath.setDdbh("");
        operPath.setXh("1");
        operPath.setSite("8081");
        operPath.setOrderId(style.getProductionTicketNo());
        operPath.setKsbh(style.getCtStyleCode());
        operPath.setPreArrayOp(preArrayOps);
        operPath.setCuttingArrayOp(cuttingArrayOps);
        operPath.setSewingArrayOp(sewingArrayOps);


        //DataParent<CraftOperationPath> parent = new DataParent<>();
        //parent.setInterfaceType("pi.custStyle");
        //parent.setSynTime(new Date());
        //parent.setCount("1");
        //List<DataChild<CraftOperationPath>> items = new ArrayList<>();
        //DataChild<CraftOperationPath> item = new DataChild<CraftOperationPath>();
        //item.setItemId("0");
        //item.setItem(operPath);
        //items.add(item);
        //parent.setItems(items);

        if (isSend) {

            sendPiService.postObject(CUSTOM_STYLE_OPERATION_PATH_URL, operPath);
            return "";
        } else {

            String dataStr =
                    JSON.toJSONString(operPath,
                            SerializerFeature.WriteMapNullValue,
                            SerializerFeature.WriteNullStringAsEmpty,
                            SerializerFeature.WriteNullNumberAsZero);
            if (StringUtils.isNotEmpty(dataStr)) {
                dataStr = dataStr.replace("\\t", "");
                //dataStr = dataStr.replace("\"", " ");
                dataStr = dataStr.replace("\\r", "");
                dataStr = dataStr.replace("\\n", "");
                dataStr = dataStr.replace("<", "");
                dataStr = dataStr.replace(">", "");
                dataStr = dataStr.replace("\\u0000", "");
                dataStr = dataStr.replace("\\u0000d", "");
                dataStr = dataStr.replace("\\u0016", "");
            }
            return dataStr;
        }
    }
}
