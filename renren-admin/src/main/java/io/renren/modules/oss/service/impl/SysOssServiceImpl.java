/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.oss.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.oss.dao.SysOssDao;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import java.util.List;
import org.springframework.stereotype.Service;

import java.util.Map;
import org.springframework.util.CollectionUtils;


@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SysOssEntity> page = this.page(
			new Query<SysOssEntity>().getPage(params)
		);

		return new PageUtils(page);
	}

	@Override
	public SysOssEntity listWaiting() {
		QueryWrapper<SysOssEntity>qw =new QueryWrapper<>();
				qw.eq("state",0);
		qw.last(" limit 1");
		List<SysOssEntity>list = list(qw);
		return CollectionUtils.isEmpty(list)?null:list.get(0);
	}

}
