package com.ylzs.dao.bigstylecraft;

import com.ylzs.entity.bigstylecraft.BigStyleOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author xuwei
 * @create 2020-08-05 8:46
 */
public interface BigStyleOperationLogDao {

    public void insertData(BigStyleOperationLog log);

    public List<BigStyleOperationLog> getAll();

    public void deleteById(@Param("id") Long id);

}
