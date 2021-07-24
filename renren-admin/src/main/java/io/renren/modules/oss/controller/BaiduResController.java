package io.renren.modules.oss.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.oss.entity.BaiduResEntity;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.BaiduResService;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.controller.vo.BaiduRes;
import java.util.Arrays;
import java.util.Map;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * 
 *
 * @author Mark
 * @email sunlightcs@gmail.com
 * @date 2021-07-24 20:09:59
 */
@RestController
@RequestMapping("sys/baidures")
public class BaiduResController {
    @Autowired
    private BaiduResService baiduResService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:baidures:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = baiduResService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:baidures:info")
    public R info(@PathVariable("id") Integer id){
        BaiduRes baiduRes = baiduResService.getByIdExt(id);

        return R.ok().put("baiduRes", baiduRes);
    }
    @RequestMapping("/Nextinfo/{id}")
    @RequiresPermissions("sys:baidures:info")
    public R Nextinfo(@PathVariable("id") Integer id){
        BaiduRes baiduRes = baiduResService.getByIdNextExt(id);

        return R.ok().put("baiduRes", baiduRes);
    }

    @Autowired
    SysOssService ossService;
    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:baidures:save")
    public R save(@RequestBody BaiduResEntity baiduRes){
        SysOssEntity oss =ossService.getById(baiduRes.getFileId());
        oss.setState(SysOssEntity.ST_AUDITING);
        ossService.updateById(oss);
        baiduRes.setChecked(1);
        baiduResService.saveAndOss(baiduRes);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:baidures:update")
    public R update(@RequestBody BaiduResEntity baiduRes){
        ValidatorUtils.validateEntity(baiduRes);

        SysOssEntity oss =ossService.getById(baiduRes.getFileId());
        oss.setState(SysOssEntity.ST_AUDITING);
        ossService.updateById(oss);
        baiduRes.setChecked(1);
        baiduResService.saveAndOss(baiduRes);
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:baidures:delete")
    public R delete(@RequestBody Integer[] ids){
        baiduResService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
