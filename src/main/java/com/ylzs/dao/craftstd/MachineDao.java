package com.ylzs.dao.craftstd;

import com.ylzs.entity.craftstd.Machine;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 说明：
 *
 * @author lyq
 * 2019-09-25 16:38
 */
public interface MachineDao {
    Integer addMachine(Machine machine);

    Integer deleteMachine(String machineCode);

    Integer updateMachine(Machine machine);

    List<Machine> getMachineByCode(@Param("machineCodes") String[] machineCodes);

    List<Machine> getMachineById(@Param("machineIds") Integer[] machineIds);

    List<Machine> getMachineByPage(@Param("keywords") String keywords,
                                   @Param("beginDate") Date beginDate,
                                   @Param("endDate") Date endDate,
                                   @Param("workTypeId") Integer workTypeId,
                                   @Param("deviceId") Integer deviceId);

    Long getStdCountByMachineCode(String machineCode);

    public List<Machine> getAllMachine();

    public Machine getByCodeAndMakeTypeCode(@Param("code") String code, @Param("makeTypeCode") String makeTypeCode);

    public Machine getByCodeAndWorkTypeId(@Param("code") String code, @Param("workTypeid") Integer workTypeid);

    public Machine getMachindCodeData(@Param("machineCode")String machineCode,@Param("workTypeCode")String workTypeCode);
}