package com.ylzs.service.craftmainframe.impl;

import com.ylzs.common.constant.BusinessConstants;
import com.ylzs.dao.craftmainframe.CraftMainFrameDao;
import com.ylzs.dao.craftmainframe.CraftMainFrameRouteDao;
import com.ylzs.dao.craftmainframe.ProductionPartDao;
import com.ylzs.entity.craftmainframe.CraftMainFrame;
import com.ylzs.entity.craftmainframe.CraftMainFrameRoute;
import com.ylzs.entity.craftmainframe.ProductionPart;
import com.ylzs.service.craftmainframe.ICraftMainFrameService;
import com.ylzs.vo.craftmainframe.CraftMainFrameExportVo;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.ylzs.common.constant.BusinessConstants.Status.IN_VALID;

@Service
@Transactional(rollbackFor = Exception.class)
public class CraftMainFrameService implements ICraftMainFrameService {

    @Resource
    private CraftMainFrameDao craftMainFrameDao;

    @Resource
    private ProductionPartDao productionPartDao;

    @Resource
    private CraftMainFrameRouteDao craftMainFrameRouteDao;


    @Override
    public int deleteByPrimaryKey(Long randomCode) {
        return craftMainFrameDao.deleteByPrimaryKey(randomCode);
    }

    @Override
    public int insert(CraftMainFrame record) {
        return craftMainFrameDao.insert(record);
    }

    @Override
    public int insertSelective(CraftMainFrame record) {
        return craftMainFrameDao.insertSelective(record);
    }

    @Override
    public Map<String,CraftMainFrame> getAllMainFrameToMap(){
        return craftMainFrameDao.getAllMainFrameToMap();
    }

    @Override
    public CraftMainFrame selectMainFrameByClothesCategoryCode(String clothesCategoryCode) {
        return craftMainFrameDao.selectMainFrameByClothesCategoryCode(clothesCategoryCode);
    }

    @Override
    public List<CraftMainFrame> selectMainFrameListByClothesCategoryCode(String clothesCategoryCode) {
        return craftMainFrameDao.selectMainFrameListByClothesCategoryCode(clothesCategoryCode);
    }

    @Override
    public CraftMainFrame selectByPrimaryKey(Long randomCode) {
        return craftMainFrameDao.selectByPrimaryKey(randomCode);
    }



    @Override
    public int updateByPrimaryKeySelective(CraftMainFrame record) {
        return craftMainFrameDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(CraftMainFrame record) {
        return craftMainFrameDao.updateByPrimaryKey(record);
    }

    @Override
    public int batchInsert(List<CraftMainFrame> list) {
        return craftMainFrameDao.batchInsert(list);
    }

    @Override
    public int insertAll(CraftMainFrame frame, List<ProductionPart> parts, List<CraftMainFrameRoute> routes) {
        int ret = craftMainFrameDao.insert(frame);
        if(ret == 1) {
            if (parts.size() > 0) {
                productionPartDao.batchInsert(parts);
            }

            if (routes.size() > 0) {
                craftMainFrameRouteDao.batchInsert(routes);
            }
        }
        return ret;
    }

    @Override
    public List<CraftMainFrame> getByCraftCategoryAndType(String craftCategoryCode, String frameType) {
        return craftMainFrameDao.getByCraftCategoryAndType(craftCategoryCode,frameType);
    }

    @Override
    @Async
    public void updateRelateMainFrameInfo(String mainFrameCode) {
        craftMainFrameDao.updateRelateMainFrameInfo(mainFrameCode);
    }

    @Override
    public CraftMainFrame selectByCode(String code) {
        return craftMainFrameDao.selectByCode(code);
    }

    @Override
    public int updateAll(CraftMainFrame frame, List<ProductionPart> parts, List<CraftMainFrameRoute> routes) {
        List<ProductionPart> oldParts = productionPartDao.getByMainFrameRandomCode(frame.getRandomCode());
        List<CraftMainFrameRoute> oldRoutes = craftMainFrameRouteDao.getByMainFrameRandomCode(frame.getRandomCode());


        int ret = craftMainFrameDao.updateByPrimaryKeySelective(frame);
        if(ret == 1) {
            List<ProductionPart> addParts = new ArrayList<ProductionPart>();
            List<ProductionPart> updateParts = new ArrayList<ProductionPart>();
            List<ProductionPart> delParts = new ArrayList<ProductionPart>();

            //生产部件有的修改没有就新增
            for(ProductionPart part: parts) {
                Optional<ProductionPart> finded = oldParts.stream().filter(a -> part.getRandomCode().equals(a.getRandomCode())).findFirst();
                if(finded.isPresent()) {
                    updateParts.add(part);
                } else {
                    addParts.add(part);
                }
            }

            //生产部件新的没有就作废
            for (ProductionPart part: oldParts) {
                Optional<ProductionPart> finded = parts.stream().filter(a ->part.getRandomCode().equals(a.getRandomCode())).findFirst();
                if(!finded.isPresent()) {
                    part.setIsInvalid(true);
                    part.setStatus(IN_VALID);
                    part.setUpdateTime(new Date());
                    part.setUpdateUser(frame.getUpdateUser());
                    delParts.add(part);
                }
            }

            if(addParts.size() > 0) {
                productionPartDao.batchInsert(addParts);
            }
            if(updateParts.size() > 0) {
                updateParts.forEach(a -> {productionPartDao.updateByPrimaryKeySelective(a);});

            }
            if (delParts.size() > 0) {
                delParts.forEach(a -> {productionPartDao.updateByPrimaryKeySelective(a);});
            }

            List<CraftMainFrameRoute> addRoutes = new ArrayList<CraftMainFrameRoute>();
            List<CraftMainFrameRoute> updateRoutes = new ArrayList<CraftMainFrameRoute>();
            List<CraftMainFrameRoute> delRoutes = new ArrayList<CraftMainFrameRoute>();
            //生产部件有的修改没有就新增
            for(CraftMainFrameRoute route: routes) {
                Optional<CraftMainFrameRoute> finded = oldRoutes.stream().filter(a -> route.getRandomCode().equals(a.getRandomCode())).findFirst();
                if(finded.isPresent()) {
                    updateRoutes.add(route);
                } else {
                    addRoutes.add(route);
                }
            }

            //生产部件新的没有就作废
            for (CraftMainFrameRoute route: oldRoutes) {
                Optional<CraftMainFrameRoute> finded = routes.stream().filter(a ->route.getRandomCode().equals(a.getRandomCode())).findFirst();
                if(!finded.isPresent()) {
                    route.setIsInvalid(true);
                    route.setStatus(IN_VALID);
                    route.setUpdateTime(new Date());
                    route.setUpdateUser(frame.getUpdateUser());
                    delRoutes.add(route);
                }
            }


            if(addRoutes.size() > 0) {
                craftMainFrameRouteDao.batchInsert(addRoutes);
            }
            if(updateRoutes.size() > 0) {
                updateRoutes.forEach(a -> {craftMainFrameRouteDao.updateByPrimaryKeySelective(a);});
            }
            if (delRoutes.size() > 0) {
                delRoutes.forEach(a -> {craftMainFrameRouteDao.updateByPrimaryKeySelective(a);});
            }

        }


        return ret;
    }

    @Override
    public List<CraftMainFrame> getByCondition(String keywords, Date beginDate, Date endDate) {
        return craftMainFrameDao.getByCondition(keywords, beginDate, endDate);
    }

    @Override
    public boolean isDefaultMainFrameExists(Long craftCategoryRandomCode, Long excludeMainFrameRandomCode) {
        return craftMainFrameDao.isDefaultMainFrameExists(craftCategoryRandomCode, excludeMainFrameRandomCode);
    }

    @Override
    public List<CraftMainFrameExportVo> getCraftMainFrameExportVos(List<CraftMainFrame> craftMainFrames) {
        List<CraftMainFrameExportVo> craftMainFrameExportVos = new ArrayList<>();
        int i = 1;
        for(CraftMainFrame itm: craftMainFrames) {
            CraftMainFrameExportVo vo = getCraftMainFrameExportVo(itm);
            vo.setLineNo(i++);
            craftMainFrameExportVos.add(vo);
        }
        return craftMainFrameExportVos;
    }

    private CraftMainFrameExportVo getCraftMainFrameExportVo(CraftMainFrame craftMainFrame) {
        CraftMainFrameExportVo result = new CraftMainFrameExportVo();
        try {
            BeanUtils.copyProperties(craftMainFrame, result);
            if(craftMainFrame.getStatus() == null || craftMainFrame.getStatus().equals(BusinessConstants.Status.DRAFT_STATUS)) {
                result.setStatusName("草稿");
            } else if(craftMainFrame.getStatus().equals(BusinessConstants.Status.AUDITED_STATUS)) {
                result.setStatusName("已审核");
            } else if(craftMainFrame.getStatus().equals(BusinessConstants.Status.PUBLISHED_STATUS)) {
                result.setStatusName("已发布");
            } else if(craftMainFrame.getStatus().equals(IN_VALID)) {
                result.setStatusName("已删除");
            } else if(craftMainFrame.getStatus().equals(BusinessConstants.Status.INVALID_STATUS)) {
                result.setStatusName("已失效");
            } else {
                result.setStatusName("未知");
            }
        } catch (Exception e) {

        }
        return result;

    }
}
