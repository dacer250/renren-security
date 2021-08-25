/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.oss.cloud;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 云存储(支持七牛、阿里云、腾讯云、又拍云)
 *
 * @author Mark sunlightcs@gmail.com
 */
public  class LocalCloudStorageService extends CloudStorageService {
    /** 云存储配置信息 */
    CloudStorageConfig config;

    public LocalCloudStorageService(CloudStorageConfig config){
        this.config = config;


    }
    /**
     * 文件路径
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 返回上传路径
     */
    @Override
    public String getPath(String prefix, String suffix) {
        //生成uuid
//        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
//        //文件路径
//        String path = DateUtils.format(new Date(), "yyyyMMdd") + "/" + uuid;
//
//        if(StringUtils.isNotBlank(prefix)){
//            path = prefix + "/" + path;
//        }

        return config.getServerPath()+prefix + suffix;
    }

    /**
     * 文件上传
     * @param data    文件字节数组
      * @return        返回http地址
     */
    @Override
    public  String upload(byte[] data, String name){
        try {
        File f =new File(config.getServerPath()+name);
        if(f.exists()){
            name =getNewName(name);
            f =new File(config.getServerPath()+name);

        }else {
            f.createNewFile();
        }
            OutputStream os = new FileOutputStream(f,false);
            os.write(data);
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config.getServerUrl()+name;
    }


    private String getNewName(String name){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        String date= sdf.format(new Date());
        int idx =name.lastIndexOf(".");
        String f =name.substring(0,idx)+date+name.substring(idx);
        return f;
    }
    /**
     * 文件上传
     * @param data     文件字节数组
     * @param suffix   后缀
     * @return         返回http地址
     */
    @Override
    public   String uploadSuffix(byte[] data, String suffix){
        return null;
    }

    /**
     * 文件上传
     *
     * @param inputStream 字节流
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    @Override
    public String upload(InputStream inputStream, String path) {
        return null;
//        try {
//            File f =new File(path);
//            if(f.exists()){
//                return null;
//            }else {
//                f.createNewFile();
//            }
//
//            IOUtils.copy(inputStream,new FileOutputStream(f));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return path;
    }

    /**
     * 文件上传
     * @param inputStream  字节流
     * @param suffix       后缀
     * @return             返回http地址
     */
    @Override
    public   String uploadSuffix(InputStream inputStream, String suffix){
        return null;
    }

}
