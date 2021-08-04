package io.renren.modules.oss.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.oss.dao.BaiduResDao;
import io.renren.modules.oss.entity.BaiduResEntity;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.BaiduResService;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.controller.vo.BaiduRes;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import org.springframework.util.CollectionUtils;


@Service("baiduResService")
public class BaiduResServiceImpl extends ServiceImpl<BaiduResDao, BaiduResEntity> implements BaiduResService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BaiduResEntity> page = this.page(
                new Query<BaiduResEntity>().getPage(params),
                new QueryWrapper<BaiduResEntity>().orderByAsc("file_id").orderByAsc("seq")
        );

        IPage<BaiduRes> page2 =new Page<>();
       List<BaiduRes>res= setup(page.getRecords());

        return new PageUtils(page,res);
    }

  @Override
  public BaiduRes getByIdExt(Integer id) {
      if(id==null){
        return new BaiduRes();
      }
    BaiduResEntity en =getById(id);
    SysOssEntity oe =ossService.getById(en.getFileId());
    BaiduRes res =new BaiduRes();
    BeanUtils.copyProperties(en,res);
    String url =oe.getUrl().replace("http://corona.sigma-stat.com/images",
        "http://corona.sigma-stat.com:9000/");
    res.setUrl(url);
    return res;
  }

  @Override
  public BaiduRes getByIdNextExt(Integer id) {
      Integer nId =getBaseMapper().getNextId(id);
    return getByIdExt(nId);
  }

  @Override
  public int saveAndOss(BaiduResEntity baiduRes) {
      if(baiduRes.getId()==null){
        save(baiduRes);
      }else {
        updateById(baiduRes);
      }

      int count =getBaseMapper().countRemainUncheckd(baiduRes.getFileId());
      if(count ==0){
        SysOssEntity entity =ossService.getById(baiduRes.getFileId());
        entity.setState(SysOssEntity.ST_AUDIT_OK);
        ossService.updateById(entity);
      }
      return baiduRes.getId().intValue();
  }

  @Autowired
    SysOssService ossService;

    private List<BaiduRes> setup(List<BaiduResEntity> records) {

        return CollectionUtils.isEmpty(records)? Collections.emptyList():
            records.stream().map(x->{
                BaiduRes res =new BaiduRes();
                BeanUtils.copyProperties(x,res);
                SysOssEntity entity =ossService.getById(x.getFileId());
                if(entity!=null && entity.getUrl()!=null){
                  String url =entity.getUrl().replace("http://corona.sigma-stat.com/images",
                      "http://corona.sigma-stat.com:9000/");
                  res.setUrl(url);
                }

                return res;
            }).collect(Collectors.toList());
    }

}
