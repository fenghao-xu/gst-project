package com.ylzs.service.craftstd;


import com.ylzs.entity.craftstd.Machine;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 说明：机器服务接口
 *
 * @author Administrator
 * 2019-09-30 10:36
 */
public interface IMachineService {
    Integer addMachine(Machine machine);

    Integer deleteMachine(Integer machineId, String userCode);

    Integer updateMachine(Machine machine);

    List<Machine> getMachineByCode(String[] machineCodes);
    List<Machine> getMachineById(Integer[] machineIds);

    List<Machine> getMachineByPage(String keywords, Date beginDate, Date endDate, Integer workTypeId, Integer deviceId);

    Long getStdCountByMachineCode(String machineCode);

    List<Machine> getAllMachine();

    Machine getByCodeAndMakeTypeCode(String code,String makeTypeCode);

    String addResource(Machine machine,  MultipartFile file, String userCode);
    Integer deleteResource(Machine machine,  String fileUrl, String userCode);

    Machine getMachindCodeData(String code,String workTypeCode);
}
