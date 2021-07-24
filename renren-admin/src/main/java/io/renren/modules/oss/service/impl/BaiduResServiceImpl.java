package io.renren.modules.sys.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.sys.dao.BaiduResDao;
import io.renren.modules.sys.entity.BaiduResEntity;
import io.renren.modules.sys.service.BaiduResService;


@Service("baiduResService")
public class BaiduResServiceImpl extends ServiceImpl<BaiduResDao, BaiduResEntity> implements BaiduResService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BaiduResEntity> page = this.page(
                new Query<BaiduResEntity>().getPage(params),
                new QueryWrapper<BaiduResEntity>()
        );

        return new PageUtils(page);
    }

}
