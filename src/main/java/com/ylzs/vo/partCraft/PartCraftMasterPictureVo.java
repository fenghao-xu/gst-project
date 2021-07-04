package com.ylzs.vo.partCraft;

import com.ylzs.core.model.SuperEntityVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @className PartCraftMasterPictureVo
 * @Description
 * @Author sky
 * @create 2020-03-02 17:15:11
 */
@Data
public class PartCraftMasterPictureVo extends SuperEntityVo {

    private static final long serialVersionUID = -8804901796650266421L;
    private String pictureUrl;
    private Long partCraftMainRandomCode;
}
