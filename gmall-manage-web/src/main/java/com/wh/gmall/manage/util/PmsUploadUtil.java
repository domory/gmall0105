package com.wh.gmall.manage.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author DOMORY
 */
public class PmsUploadUtil {


    public static String uploadImage(MultipartFile multipartFile) {
        String imgUrl="http://192.168.1.10";
        //
        String tracker=PmsUploadUtil.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient=new TrackerClient();
        TrackerServer trackerServer=null;
        try {
             trackerServer=trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageClient storageClient=new StorageClient(trackerServer,null);
        try {
            //获得上传对象的二进制
            byte[] bytes = multipartFile.getBytes();
            //获得文件后缀名
            String originalFilename = multipartFile.getOriginalFilename();
            int i = originalFilename.lastIndexOf(".");
            String exName = originalFilename.substring(i+1);
            String[] uploadInfos = storageClient.upload_file(bytes,exName, null);
            for (String uploadInfo : uploadInfos) {
                imgUrl +="/"+uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return imgUrl ;
    }
}
