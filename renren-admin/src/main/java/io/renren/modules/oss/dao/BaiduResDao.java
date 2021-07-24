package io.renren.modules.oss.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.oss.entity.BaiduResEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 
 * 
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2021-07-24 20:09:59
 */
@Mapper
public interface BaiduResDao extends BaseMapper<BaiduResEntity> {

  @Select(" select a.id from baidu_res a ,sys_oss b  where "
      + "a.file_id =b.id and b.state in(2,3) and  a.id >#{id}  and a.checked=0  order by a.id asc limit 1")
  Integer getNextId(@Param("id") Integer id);

  @Select("select count(*) from baidu_res where file_id=#{fileId} and checked=0")
  int countRemainUncheckd(@Param("fileId")  Integer fileId);
}
