package com.ylzs.service.interfaceOutput.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ylzs.common.utils.object.ObjectUtils;
import com.ylzs.dao.craftstd.MachineDao;
import com.ylzs.dao.receivepilog.ReceivePiLogDao;
import com.ylzs.entity.craftstd.Machine;
import com.ylzs.entity.receivepilog.ReceivePiLog;
import com.ylzs.service.interfaceOutput.IInterfaceOutputService;
import com.ylzs.service.pi.ISendPiService;
import com.ylzs.vo.interfaceOutput.MachineOutput;
import com.ylzs.vo.system.DataChild;
import com.ylzs.vo.system.DataParent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.ylzs.common.constant.BusinessConstants.PiReceiveUrlConfig.MACHINE_URL;

@Service
public class IntefaceOuputService implements IInterfaceOutputService {
    @Resource
    private MachineDao machineDao;
    @Resource
    private ISendPiService sendPiService;
    @Resource
    private ReceivePiLogDao receivePiLogDao;




    @Override
    public String sendMachines(String machineCodes, boolean isSend) {
        List<Machine> machines = machineDao.getAllMachine();
        if(ObjectUtils.isEmptyList(machines)) {
            return null;
        }
        if(!StringUtils.isBlank(machineCodes)) {
            String[] split = machineCodes.split(",");
            for(int i = machines.size() - 1; i >= 0; i--) {
                Machine machine = machines.get(i);
                if(machineCodes.indexOf(machine.getMachineCode()) == -1) {
                    machines.remove(i);
                }
            }
        }

        DataParent<MachineOutput> parent = new DataParent<>();
        parent.setInterfaceType("pi.Machine");
        parent.setSynTime(new Date());
        parent.setCount("1");



        List<DataChild<MachineOutput>> items = new ArrayList<>();
        for(int i = 0; i < machines.size(); i++) {
            Machine machine = machines.get(i);
            MachineOutput machineOutput = new MachineOutput();
            machineOutput.setSite("8081");
            machineOutput.setJqdm(machine.getMachineCode());
            machineOutput.setJqmc(machine.getMachineNameCn());

            DataChild<MachineOutput> item = new DataChild<MachineOutput>();
            item.setItemId(String.valueOf(i));
            item.setItem(machineOutput);
            items.add(item);
        }
        parent.setItems(items);

        if(isSend) {
            Triple<String, String, String> ret = sendPiService.postObject(MACHINE_URL, parent);
            return ret.getLeft()!=null?ret.getLeft():ret.getMiddle();
        } else {
            return JSON.toJSONString(parent,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullNumberAsZero);
        }
    }



}
