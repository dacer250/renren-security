package io.renren.modules.job.task;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.renren.modules.Ana;
import io.renren.modules.oss.entity.BaiduResEntity;
import io.renren.modules.oss.service.BaiduResService;
import io.renren.modules.sys.controller.vo.BaiduRes;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("fixTask")
public class FixTask implements ITask {

  @Autowired
  Ana ana;
  @Autowired
  BaiduResService baiduResService;

  /**
   * 执行定时任务接口
   *
   * @param params 参数，多参数使用JSON数据
   */
  @Override
  public void run(String params) {

    Map<Integer, List<String>> map =ana.handle();
    for(Integer key:map.keySet()){
      List<String> v =map.get(key);
      UpdateWrapper<BaiduResEntity> up =new UpdateWrapper<>();
      up.eq("file_id",key);
      up.set("ext_info", Strings.join(v,'\r'));
      baiduResService.update(up);
     System.out.println(key);
    }
  }
}
