package io.renren.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.oss.entity.BaiduResEntity;
import io.renren.modules.sys.controller.vo.BaiduRes;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2021-07-24 20:09:59
 */
public interface BaiduResService extends IService<BaiduResEntity> {

    PageUtils queryPage(Map<String, Object> params);

  BaiduRes getByIdExt(Integer id);

  BaiduRes getByIdNextExt(Integer id);

  void saveAndOss(BaiduResEntity baiduRes);
}

