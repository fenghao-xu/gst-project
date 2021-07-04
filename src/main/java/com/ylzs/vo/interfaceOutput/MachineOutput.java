package com.ylzs.vo.interfaceOutput;

import lombok.Data;

import java.io.Serializable;

@Data
public class MachineOutput implements Serializable {
    private static final long serialVersionUID = 863669077170199840L;
    /**
     * 站点
     */
    private String site;

    /**
     * 机器代码
     */
    private String jqdm;

    /**
     * 机器名称
     */
    private String jqmc;


}
