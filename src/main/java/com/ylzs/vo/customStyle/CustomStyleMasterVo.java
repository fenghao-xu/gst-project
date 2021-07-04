package com.ylzs.vo.customStyle;

import com.ylzs.vo.bigstylecraft.StyleCraftVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className CustomStyleMasterVo
 * @Description
 * @Author sky
 * @create 2020-05-13 14:27:20
 */
@Data
public class CustomStyleMasterVo implements Serializable {

    private String orderNo;
    private String orderLineId;
    private String releaseVersion;
    private List<String> craftCodeList;
    private String searchType;
    private String bigStyleAnalyseCode;
    private List<StyleCraftVO> codeList;
}
