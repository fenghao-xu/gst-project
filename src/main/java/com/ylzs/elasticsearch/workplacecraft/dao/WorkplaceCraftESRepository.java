package com.ylzs.elasticsearch.workplacecraft.dao;

import com.ylzs.elasticsearch.workplacecraft.esbo.WorkplaceCraftEsBO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author xuwei
 * @create 2020-02-29 14:46
 * 工位工序
 */
@Repository
public interface WorkplaceCraftESRepository extends ElasticsearchRepository<WorkplaceCraftEsBO,Long> {
}
