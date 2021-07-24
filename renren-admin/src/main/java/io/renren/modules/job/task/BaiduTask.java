/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.job.task;

import cn.hutool.core.io.FileUtil;
import com.baidu.aip.ocr.AipOcr;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.entity.BaiduResEntity;
import io.renren.modules.sys.service.BaiduResService;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 测试定时任务(演示Demo，可删除)
 *
 * testTask为spring bean的名称
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component("baiduTask")
public class BaiduTask implements ITask {

  public static final String APP_ID = "24600154";
  public static final String API_KEY = "4W80ukpI0zZtTVXXZnhQCXZ5";
  public static final String SECRET_KEY = "8iIS3oa29UGcA2PfgBuVy3iaTl3pxFwG";
  AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
  HashMap<String, String> options = new HashMap<String, String>();
  @Autowired
  SysOssService ossService;
  @Autowired
  BaiduResService baiduResService;
  private Logger logger = LoggerFactory.getLogger(getClass());

  @PostConstruct
  public void init() {

    client.setConnectionTimeoutInMillis(4000);
    client.setSocketTimeoutInMillis(60000);
    options.put("recognize_granularity", "big");
    options.put("probability", "false");
    options.put("detect_direction", "true");
  }

  @Override
  public void run(String params) {
    logger.info("baidu启动，参数为：{}", params);

    while (true) {

      SysOssEntity entity = ossService.listWaiting();
      if (entity == null) {
        logger.info("没有待处理的识别");
        return;
      }

      entity.setState(SysOssEntity.ST_BAIDUING);
      ossService.updateById(entity);
      String url = entity.getUrl();
      try{
        String local = url.replace("http://corona.sigma-stat.com/images", "/home/apps/corona/images/");
        byte[] date = FileUtil.readBytes(new File(local));
        JSONObject res = client.handwriting(date, options);
        logger.info("image=" + local + " ,res=" + res);
        FileUtil.writeString(res.toString(),new File(entity.getUrl()+".baidu"), Charset.forName("utf-8"));

        JSONArray array = res.getJSONArray("words_result");
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
          JSONObject obj = array.getJSONObject(i);
          arr.add(obj.getString("words"));
        }
        logger.info("baiduOK:"+entity.getId()+",res="+Strings.join(arr,','));

        List<BaiduResEntity> baidu = patchUp(arr, entity.getId());
        logger.info("entityOK:"+entity.getId());
        if(!CollectionUtils.isEmpty(baidu)){
          baiduResService.saveBatch(baidu);
        }

        entity.setState(SysOssEntity.ST_BAIDU_OK);
        ossService.updateById(entity);
      }catch (Exception e){
        e.printStackTrace();
      }

    }


  }

  List<BaiduResEntity> patchUp(List<String> baidu, int fileId) {
    List<BaiduResEntity> res = new ArrayList<>();
    for (int i = 0; i < baidu.size(); i++) {
      String temp = baidu.get(i);
      if (hasNumberMoreThan(temp, 10, 8)) {
        BaiduResEntity entity = fixup(baidu, i, fileId);
        res.add(entity);
        i += 2;
      }

    }
    return res;
  }

  private BaiduResEntity fixup(List<String> baidu, int idNoIndex, int fileId) {
    String idNo = baidu.get(idNoIndex);
    int mobileIdx = idNoIndex + 1;
    String mobile = mobileIdx < baidu.size() ? baidu.get(mobileIdx) : "";
    int sexIdx = idNoIndex - 1;
    String sex = sexIdx > 0 ? baidu.get(sexIdx) : "";
    int nameIdx = sexIdx - 1;
    String name = nameIdx > 0 ? baidu.get(nameIdx) : "";
    int seqIdx = nameIdx - 1;
    String seq = seqIdx > 0 ? baidu.get(seqIdx) : "";
    Integer seqInt = null;
    try {
      seqInt = seq.length() < 3 ? Integer.parseInt(seq) : null;
    } catch (Exception e) {
    }
    BaiduResEntity res = new BaiduResEntity();
    res.setFileId(fileId);
    res.setSeq(seqInt);
    res.setName(name);
    res.setSex(sex);
    res.setIdNo(idNo);
    res.setMobile(mobile);
    res.setExtInfo(Strings.join(baidu.subList(seqIdx - 1, mobileIdx), ','));
    return res;
  }


  //前10个字符，>=8个是数字
  boolean hasNumberMoreThan(String input, Integer checkLen, int targetCount) {

    if (checkLen > input.length()) {
      checkLen = input.length();
    }
    int count = 0;
    for (int i = 0; i < checkLen; i++) {
      char x = input.charAt(i);
      if (x >= '0' && x <= '9') {
        count++;
      }
    }
    return count >= targetCount;

  }
}
