package com.ylzs.service.designPart.impl;

import com.ylzs.dao.designPart.DesignPartDao;
import com.ylzs.entity.designPart.DesignPart;
import com.ylzs.service.designPart.IDesignPartService;
import com.ylzs.service.impl.OriginServiceImpl;
import com.ylzs.vo.designpart.DesignPartExportVo;
import com.ylzs.vo.designpart.DesignPartSourceVo;
import com.ylzs.vo.designpart.DesignPartVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service()
public class DesignPartServiceImpl extends OriginServiceImpl<DesignPartDao, DesignPart> implements IDesignPartService {

    @Resource
    private DesignPartDao designPartDao;

    @Override
    public List<DesignPartVo> getDesignDataList(HashMap map) {
        return designPartDao.getDesignDataList(map);
    }

    @Override
    public List<DesignPartExportVo> getDesignDataListExport(HashMap map) {
        List<DesignPartVo> lst = designPartDao.getDesignDataList(map);
        List<DesignPartExportVo> exports = new ArrayList<>();
        for (DesignPartVo itm : lst) {
            try {
                DesignPartExportVo result = new DesignPartExportVo();
                BeanUtils.copyProperties(itm, result);
                exports.add(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return exports;

    }

    @Override
    public int addOrUpdatePartData(DesignPart designPart) {
        return designPartDao.addOrUpdatePartData(designPart);
    }

    @Override
    public List<DesignPartSourceVo> getDesignPartSourceVos(String designPartCode, Integer useIn) {
        List<DesignPartSourceVo> lst = designPartDao.getDesignPartSourceVos(designPartCode, useIn);
        for(int i=0; i<lst.size(); i++) {
            DesignPartSourceVo part = lst.get(i);
            part.setLineNo(i+1);

        }
        return lst;
    }


}