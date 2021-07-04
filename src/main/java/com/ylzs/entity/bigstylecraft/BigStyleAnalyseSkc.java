package com.ylzs.entity.bigstylecraft;

import com.ylzs.core.model.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-03-24 9:25
 * 大货款式工艺 的 部件工艺
 */
@Data
public class BigStyleAnalyseSkc {
    private Long id;
    /**
     * 款的随机码
     */
    private Long styleRandomCode;

    /**
     * 款的码
     */
    private String ctStyleCode;
    /**
     *
     */
    private String styleSKCcode;
    /**
     *
     */
    private String skcColorName;
    /**
     *
     */
    private String skcColorCode;


}
