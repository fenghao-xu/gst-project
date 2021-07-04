package com.ylzs.service.designPart;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.vo.designpart.DesignPartExportVo;
import com.ylzs.vo.designpart.DesignPartSourceVo;
import com.ylzs.vo.designpart.DesignPartVo;

import java.util.HashMap;
import java.util.List;

/**
 * 设计部件
 *
 * @author sky
 * @email liangweijie@eeka.cn
 * @date 2020-02-29 11:53:47
 */
public interface IDesignPartService extends IService<DesignPart> {

    /**
     * 查询设计部件信息
     * 不传参默认查询状态等于0的所有数据
     * categoryCode 服装品类code
     * partMiddleCode 部件中类code
     * designCode 设计部件code
     * designName 设计部件名称
     * @param map
     * @return
     */
    List<DesignPartVo> getDesignDataList(HashMap map);

    List<DesignPartExportVo> getDesignDataListExport(HashMap map);


    int addOrUpdatePartData(DesignPart designPart);//增加或修改部件主数据主表

    List<DesignPartSourceVo> getDesignPartSourceVos(String designPartCode, Integer useIn);
}

