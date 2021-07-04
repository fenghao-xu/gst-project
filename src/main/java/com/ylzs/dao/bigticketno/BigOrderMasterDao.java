package com.ylzs.dao.bigticketno;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.craftstd.CraftFile;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-19 15:47
 */
public interface BigOrderMasterDao extends BaseMapper<BigStyleAnalyseMaster> {

    public List<BigStyleAnalyseMaster> getDataByPager(Map<String, Object> param);

    public void updateAdaptCode(@Param("adaptCode")String adaptCode,@Param("id") Long id);

    /**
     * 添加
     */
    public void addBigStyleAnalyseMaster(Map<String, Object> param);

    public Integer deleteByID(@Param("id") Long id);

    /**
     * 更新
     */
    public void updateBigStyleAnalyseMaster(Map<String, Object> param);

    /**
     * 从款式工艺里面导入 部件工艺和工序数据
     */
    public List<BigStyleAnalyseMaster> searchFromBigStyleAnalyse(Map<String, Object> map);

    public void updateStatus(@Param("status") Integer status, @Param("randomCode") Long randomCode, @Param("userCode") String userCode, @Param("releaseTime") Date releaseTime);

    public BigStyleAnalyseMaster searchFromBigStyleAnalyseByRandomCode(Long randomCode);

    BigStyleAnalyseMaster getBigStyleAnalyseByStyleCode(@Param("styleCode") String styleCode);

    public void deleteByRandomCode(@Param("randomCode") Long randomCode);

    BigStyleAnalyseMaster getBigStyleAnalyseMasterByRandomCode(@Param("randomCode") Long randomCode);

    List<CraftFile> getCraftFileByRandomCode(@Param("styleRandomCode") Long styleRandomCode);

    public List<BigStyleAnalyseMaster> getPriceAndTime();

    public List<BigStyleAnalyseMaster> getPriceAndTimeSew();

    public List<BigStyleAnalyseMaster> searchBigOrderByCraftInfo(Map<String, Object> map);
}
