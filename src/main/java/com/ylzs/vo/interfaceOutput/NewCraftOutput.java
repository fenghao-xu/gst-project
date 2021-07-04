package com.ylzs.vo.interfaceOutput;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NewCraftOutput implements Serializable {
    private static final long serialVersionUID = 7528323253364761479L;
    private String site;
    /**
     * 1或其他来自GST 2来自CAPP
     */
    private String fromSysFlag;
    private List<NewCraftItemOuput> operations;
}
