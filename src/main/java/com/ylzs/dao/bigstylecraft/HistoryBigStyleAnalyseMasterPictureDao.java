package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.bigstylecraft.BigStyleAnalyseMasterPicture;

public interface HistoryBigStyleAnalyseMasterPictureDao {
    int deleteByPrimaryKey(Long id);

    int insert(BigStyleAnalyseMasterPicture record);

    int insertSelective(BigStyleAnalyseMasterPicture record);

    BigStyleAnalyseMasterPicture selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(BigStyleAnalyseMasterPicture record);

    int updateByPrimaryKey(BigStyleAnalyseMasterPicture record);
}