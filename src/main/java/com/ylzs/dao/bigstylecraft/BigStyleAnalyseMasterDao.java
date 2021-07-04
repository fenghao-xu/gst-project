package com.ylzs.dao.bigstylecraft;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMaster;
import com.ylzs.entity.craftstd.CraftFile;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xuwei
 * @create 2020-03-19 15:47
 */
public interface BigStyleAnalyseMasterDao extends BaseMapper<BigStyleAnalyseMaster> {

    public Integer deleteByID(@Param("id") Long id);

    public List<BigStyleAnalyseMaster> getDataByPager(Map<String, Object> param);

    /**
     * 添加
     */
    public void addBigStyleAnalyseMaster(Map<String, Object> param);

    /**
     * 更新
     */
    public void updateBigStyleAnalyseMaster(Map<String, Object> param);

    public List<BigStyleAnalyseMaster> searchBigStyleAnalyseByCraftInfo(Map<String, Object> map);

    /**
     * 从款式工艺里面导入 部件工艺和工序数据
     */
    public List<BigStyleAnalyseMaster> searchFromBigStyleAnalyse(Map<String, Object> map);

    public void updateStatus(@Param("status") Integer status, @Param("randomCode") Long randomCode, @Param("userCode") String userCode, @Param("releaseTime") Date releaseTime);

    public BigStyleAnalyseMaster searchFromBigStyleAnalyseByRandomCode(Long randomCode);

    BigStyleAnalyseMaster getBigStyleAnalyseByCode(@Param("bigStyleAnalyseCode") String bigStyleAnalyseCode);

    @MapKey("ctStyleCode")
    Map<String, BigStyleAnalyseMaster> getStyleCodeMasterMap(@Param("styleCodes") List<String> styleCodes);

    List<CraftFile> getCraftFileByRandomCode(@Param("styleRandomCode") Long styleRandomCode);

    List<BigStyleAnalyseMaster> findAllReleaseUser();

    BigStyleAnalyseMaster getBigStyleAnalyseByStyleSKCCode(@Param("styleSKCCode") String styleSKCCode);

    public List<BigStyleAnalyseMaster> getPriceAndTime();

    public List<BigStyleAnalyseMaster> getPriceAndTimeSew();


    public Integer updatePriceAndTime(Map<String,Object>param);

    public BigStyleAnalyseMaster getBigStyleByCode(@Param("bigStyleAnalyseCode") String bigStyleAnalyseCode);

    int insertByHistoryBigStyle(Map<String,Object> param);

}
