package com.xuecheng.test;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class testFastDFS {
    //上传文件
    @Test
    public void testUpload() {
        try {
            //加载配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);
            //创建客户端
            TrackerClient tc = new TrackerClient();
            //连接tracker Server
            TrackerServer ts = tc.getConnection();
            if (ts == null) {
                System.out.println("getConnection return null");
                return;
            }
            //获取一个storage server
            StorageServer ss = tc.getStoreStorage(ts);
            if (ss == null) {
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 sc1 = new StorageClient1(ts, ss);
            NameValuePair[] meta_list = null; //new NameValuePair[0];
            String item = "C:\\Users\\Administrator\\Pictures\\images\\0\\1\\1.jpg";
            String fileId;
            fileId = sc1.upload_file1(item, "png", meta_list);
            System.out.println("Upload local file " + item + " ok, fileId=" + fileId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //查询文件
    @Test
    public void testQuery() throws IOException, MyException {
        //加载配置文件
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
        StorageClient storageClient = new StorageClient(trackerServer, storeStorage);
        FileInfo fileInfo = storageClient.query_file_info("group1", "M00/00/00/wKhlA19zOPaAKuIZAAMY93bgXqs050.png");
        System.out.println(fileInfo);
    }

    //下载文件
    @Test
    public void testDownload() throws IOException, MyException {
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
        StorageClient1 storageClient = new StorageClient1(trackerServer, storeStorage);
        byte[] bytes = storageClient.download_file1("group1/M00/00/00/wKj-gV9RdPSAOwK5AAIOzUeCwYo681.png");

        File file = new File("C:\\Users\\Administrator\\Desktop\\2.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }
}
