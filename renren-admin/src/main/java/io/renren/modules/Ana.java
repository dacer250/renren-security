package io.renren.modules;


import cn.hutool.core.io.FileUtil;
import java.nio.charset.Charset;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class Ana {


  public Map<Integer,List<String>>   handle( ){


    Map<Integer,String> map =todo();
    Map<Integer,List<String>> write=new HashMap<>();
    for(Integer fileId :map.keySet()){
      String jpg =map.get(fileId);
      List<String> result =extract(jpg);
      if(!CollectionUtils.isEmpty(result)){
        write.put(fileId,result);
      }

    }
    return write;

  }

  public static Map<Integer,String> todo(){
    List<String> lines =FileUtil.readLines("d:/temp/todo.csv",Charset.defaultCharset());
    Map<Integer,String> ret =new HashMap<>();
    lines.forEach(x->{
      String[]arr =x.split(",");
      if(arr.length==2){
        Integer fileId =Integer.parseInt(arr[0]);
        String jpg =arr[1];
        ret.put(fileId,jpg);
      }
    });
    return ret;
  }


  public static List<String> extract(String jpg){
    String file=dir+jpg+".baidu.2.txt";
    List<String> arr = new ArrayList<>();
    List<String> origin =FileUtil.readLines(file, Charset.defaultCharset());
    try{
      JSONObject res =new JSONObject(origin.get(0));

      JSONArray array = res.getJSONArray("words_result");

      for (int i = 0; i < array.length(); i++) {
        JSONObject obj = array.getJSONObject(i);
        String result =obj.getString("words");
        if(!ommit(result)){
          arr.add(result);
        }
      }
    }catch (Exception e){
      return arr;
    }

    return arr;
  }


  static final  String dir="D:\\temp\\2.tar\\2\\";
 public static boolean ommit(String str){
    for(String key:filter){
      if(str.contains(key)){
        return true;
      }
    }
    return false;
 }

  static final Set<String> filter=new HashSet<>();
  static{
    filter.addAll(Arrays.asList(
        "采集地点,登记表,新冠病毒,采集时间,结果,送样人,联系电话,序号,条形码,姓名,联系方式,身份证,性别".split(",")));
  }
}
