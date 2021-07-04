package com.ylzs.controller.interfaceOutput;


import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.common.constant.OderProcessingStatusConstants;
import com.ylzs.common.util.Result;
import com.ylzs.common.util.StringUtils;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.controller.auth.Authentication;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.entity.system.WebConfig;
import com.ylzs.service.bigticketno.BigOrderMasterService;
import com.ylzs.service.interfaceOutput.IInterfaceOutputService;
import com.ylzs.service.interfaceOutput.INewCraftService;
import com.ylzs.service.interfaceOutput.IOperationPathService;
import com.ylzs.service.interfaceOutput.ISectionSMVService;
import com.ylzs.service.orderprocessing.OrderProcessingStatusService;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.service.pi.impl.HttpClientWithBasicAuth;
import com.ylzs.service.receivepilog.IReceivePiLogService;
import com.ylzs.service.system.IWebConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/output")
@Api(tags = "接口输出")
public class InterfaceOutputController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceOutputController.class);
    @Resource
    private IOperationPathService operationPathService;
    @Resource
    private ISectionSMVService sectionSMVService;
    @Resource
    private IInterfaceOutputService interfaceOutputService;
    @Resource
    private INewCraftService newCraftService;
    @Resource
    private ISendPiService sendPiService;
    @Resource
    private IReceivePiLogService receivePiLogService;
    @Resource
    private BigOrderMasterService bigOrderMasterService;

    @Resource
    private OrderProcessingStatusService orderProcessingStatusService;

    @Resource
    private IWebConfigService webConfigService;

    private RestTemplate restTemplate = new RestTemplate();




    @ApiOperation(value = "batchSendCraft", notes = "通过工序编码模糊查找批量发送工序给ME", httpMethod = "GET", response = Result.class)
    @RequestMapping(value = "/batchSendCraft", method = RequestMethod.GET)
    public String batchSendCraft(
            @RequestParam(name = "craftCode", required = true) String craftCode, HttpServletRequest request) throws Exception {
        Map<String, Object> param = new HashMap<>(0);
        param.put("status", BusinessConstants.Status.PUBLISHED_STATUS);
        param.put("code", craftCode);
        RestTemplate restTemplate = new RestTemplate();
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://"
                + request.getServerName() + ":" + request.getServerPort()
                + path + "/";
        String data = restTemplate.getForObject(basePath + "/sewingCraft/batchSendCraft?craftCode=" + craftCode, String.class);
        return data;
    }

    private void doneForPage() {
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        List<WebConfig> webConfigList = webConfigService.getConfigList();
    }

    @RequestMapping(value = "/outputOrderOpPath", method = RequestMethod.GET)
    @ApiOperation(value = "outputOrderOpPath", notes = "定制订单工序路径输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputOrderOpPath(@RequestParam(name = "orderNo", required = true) String orderNo,
                                    @RequestParam(name = "lineId", required = true) String lineId,
                                    @RequestParam(name = "version", required = false) String version,
                                    @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";
        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        try {
            orderProcessingStatusService.addOrUpdate(orderNo + "-" + lineId, OderProcessingStatusConstants.CusOrderStatusName.CUS_ORDER_NAME_1270,
                    OderProcessingStatusConstants.CusOrderStatus.CUS_ORDER_1270, "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String data = operationPathService.sendCustomStylePath(orderNo, lineId, version, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }

    @RequestMapping(value = "/outBigOrderThinkFlag", method = RequestMethod.GET)
    @ApiOperation(value = "outBigOrderThinkFlag", notes = "大货智化标识输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outBigOrderThinkFlag(@RequestParam @ApiParam(name = "productionTicketNo", value = "工单号", required = true) String productionTicketNo,
            @RequestParam @ApiParam(name = "isSend", value = "是否发送 1发送 0不发送", required = false) String isSend
                                       ) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";
        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        String data = bigOrderMasterService.sendBigOrderThinkFlag(productionTicketNo, needSend);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }

    @RequestMapping(value = "/outputBigStyleOpPath", method = RequestMethod.GET)
    @ApiOperation(value = "outputBigStyleOpPath", notes = "大货款工序路径输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputBigStyleOpPath(@RequestParam(name = "styleAnalyseCode", required = true) String styleAnalyseCode,
                                       @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";
        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();

        String data = operationPathService.sendBigStylePath(styleAnalyseCode, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }

    @RequestMapping(value = "/outputCustomOrderOpPath", method = RequestMethod.GET)
    @ApiOperation(value = "outputCustomOrderOpPath", notes = "定制订单工序路径输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputCustomOrderOpPath(@RequestParam(name = "orderNo", required = true) String orderNo,
                                          @RequestParam(name = "lineNo", required = true) String lineNo,
                                          @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";
        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();

        String data = operationPathService.sendCustomStylePath(orderNo, lineNo, null, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }


    @RequestMapping(value = "/outputBigOrderOpPath", method = RequestMethod.GET)
    @ApiOperation(value = "outputBigOrderOpPath", notes = "工单工序路径输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputBigOrderOpPath(@RequestParam(name = "productionTicketNo", required = true) String productionTicketNo,
                                       @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";
        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }

        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        String data = operationPathService.sendBigOrderPath(productionTicketNo, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            String[] split = productionTicketNo.split("-");
            if (split.length == 2) {
                //大货工单路线未找到，则取定制订单工艺路线
                data = operationPathService.sendCustomStylePath(split[0], split[1], "", needSend);
            }

            return emptyResult;
        } else {
            return data;
        }
    }

    @RequestMapping(value = "/outputBigStylePreSectionSMV", method = RequestMethod.GET)
    @ApiOperation(value = "outputBigStylePreSectionSMV", notes = "大货款预估工段工时输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputBigStylePreSectionSMV(@RequestParam(name = "styleCode", required = true) String styleCode,
                                              @RequestParam(name = "preSMV", required = true) String preSMV,
                                              @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";

        BigDecimal smv = new BigDecimal(preSMV);
        smv.setScale(3);
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }

        String data = sectionSMVService.sendBigStylePreSectionSMV(styleCode, smv, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }

    }


    @RequestMapping(value = "/outputBigStyleActualSectionSMV", method = RequestMethod.GET)
    @ApiOperation(value = "outputBigStyleActualSectionSMV", notes = "大货款实际工段工时输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputBigStyleActualSectionSMV(@RequestParam(name = "bigStyleAnalyseCode", required = true) String bigStyleAnalyseCode,
                                                 @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";

        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        String data = sectionSMVService.sendBigStyleActualSectionSMV(bigStyleAnalyseCode, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }

    @RequestMapping(value = "/outputBigOrderActualSectionSMV", method = RequestMethod.GET)
    @ApiOperation(value = "outputBigOrderActualSectionSMV", notes = "大货工单实际工段工时输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputBigOrderActualSectionSMV(@RequestParam(name = "productionTicketNo", required = true) String productionTicketNo,
                                                 @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";

        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        String data = sectionSMVService.sendBigOrderActualSectionSMV(productionTicketNo, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }


    @RequestMapping(value = "/outputCustomStyleActualSectionSMV", method = RequestMethod.GET)
    @ApiOperation(value = "outputCustomStyleActualSectionSMV", notes = "定制款实际工段工时输出")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputCustomStyleActualSectionSMV(@RequestParam(name = "orderNo", required = true) String orderNo,
                                                    @RequestParam(name = "orderLineId", required = true) String orderLineId,
                                                    @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";

        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        String data = sectionSMVService.sendCustomStyleActualSectionSMV(orderNo, orderLineId, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }


    @RequestMapping(value = "/outputMachines", method = RequestMethod.GET)
    @ApiOperation(value = "outputMachines", notes = "发送机器")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputMachines(@RequestParam(name = "machineCodes", required = false) String machineCodes,
                                 @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";

        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }

        String data = interfaceOutputService.sendMachines(machineCodes, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }

    @RequestMapping(value = "/outputBigStyleNewCraft", method = RequestMethod.GET)
    @ApiOperation(value = "outputBigStyleNewCraft", notes = "发送大货款新工序")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputBigStyleNewCraft(@RequestParam(name = "styleAnalyseCode", required = false) String styleAnalyseCode,
                                         @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";

        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        String data = newCraftService.sendBigStyleNewCraft(styleAnalyseCode, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }


    @RequestMapping(value = "/outputBigOrderNewCraft", method = RequestMethod.GET)
    @ApiOperation(value = "outputBigOrderNewCraft", notes = "发送大货工单新工序")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputBigOrderNewCraft(@RequestParam(name = "productionTicketNo", required = false) String productionTicketNo,
                                         @RequestParam(name = "isSend", required = false) String isSend) {
        String emptyResult = "{\"interfaceType\": \"No Data\"}";

        boolean needSend = false;
        if ("1".equals(isSend) || "true".equals(isSend) || "True".equals(isSend)) {
            needSend = true;
        }
        //防止分页插件bug 偶发性影响,因为分页插件只对service里面第一条语句有影响，所以这里放一条
        doneForPage();
        String data = newCraftService.sendBigOrderNewCraft(productionTicketNo, needSend);
        data = StringUtils.replaceSpecial(data);
        if (data == null) {
            return emptyResult;
        } else {
            return data;
        }
    }


    @RequestMapping(value = "/outputSectionSMV", method = RequestMethod.GET)
    @ApiOperation(value = "outputSectionSMV", notes = "发送自定义工段工时")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputSectionSMV(@RequestParam(name = "dataStr", required = true) String dataStr
    ) {
        return sectionSMVService.sendSectionSMV(dataStr);
    }

    @RequestMapping(value = "/outputDataStr", method = RequestMethod.POST)
    @ApiOperation(value = "outputDataStr", notes = "发送自定义报文")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public String outputDataStr(
            @RequestParam(name = "url", required = true) String url,
            @RequestParam(name = "appKey", required = true) String appKey,
            @RequestParam(name = "secretKey", required = true) String secretKey,
            @RequestParam(name = "dataStr", required = true) String dataStr
    ) {
        return sendPiService.sendToPiRaw(url, appKey, secretKey, dataStr);
    }

    @RequestMapping(value = "/send2MeByLog", method = RequestMethod.POST)
    @ApiOperation(value = "send2MeByLog", notes = "给自己发接口数据（开发用）")
    @Authentication(auth = Authentication.AuthType.QUERY, required = false)
    public void send2MeByLog() {
        Map<String, String> param = new HashMap<>();
        param.put("receiveType", "智库款主数据");
        param.put("returnDescribe", "传入数据条数与头部总条数不一致");
        param.put("count", "0");
        try {
            List<ReceivePiLog> logs = receivePiLogService.getList(param);
            if (ObjectUtils.isEmptyList(logs)) {
                return;
            }
            String url = "http://localhost:8080/gst-project/productModelMasterData/getProductModelData";
            for (ReceivePiLog itm : logs) {
                String dataStr = itm.getData();
                if (StringUtils.isNotBlank(dataStr)) {
                    HttpClientWithBasicAuth.SendData(url, dataStr);
                    receivePiLogService.updateCount(itm.getId(), -1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
