package com.ylzs.entity.auth.resp;

import com.ylzs.entity.auth.CappUserBrand;
import com.ylzs.entity.auth.CappUserClothingCategory;
import com.ylzs.entity.auth.CappUserFactory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author weikang
 * @Description 用户数据权限返回数据
 * @Date 2020/5/5
 */
@Data
public class CappUserDataAuthResp implements Serializable {

    private static final long serialVersionUID = 1672129341383998206L;

    /**
     * 品牌
     */
    private List<CappUserBrand> userBrands;

    /**
     * 工厂
     */
    private List<CappUserFactory> userFactories;

    /**
     * 服装大类
     */
    private List<CappUserClothingCategory> userClothingCategories;
}
