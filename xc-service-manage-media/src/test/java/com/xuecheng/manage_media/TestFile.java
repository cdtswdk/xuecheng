package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class TestFile {

    @Test
    public void testChunk() throws Exception {
        File sourceFile = new File("D:\\Code\\21-xuecheng\\XcEduUI03\\video\\lucene.avi");

        String chunkPath = "D:\\Code\\21-xuecheng\\XcEduUI03\\video\\chunk\\";
        File chunkFolder = new File(chunkPath);

        if(!chunkFolder.exists()){
            chunkFolder.mkdirs();
        }
        //分块大小
        long chunkSize = 1*1024*1024;
        //分块数量
        long chunkSum = (long) Math.ceil(sourceFile.length()*1.0/chunkSize);
        if(chunkSum<1){
            chunkSum = 1;
        }
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");

        for(int i=0;i<chunkSum;i++){
            File file = new File(chunkPath + i);
            boolean newFile = file.createNewFile();
            if(newFile){
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b))!=-1){
                    raf_write.write(b,0,len);
                    if(file.length()>=chunkSize){
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }

    @Test
    public void testMerge() throws Exception {
        File chunkFolder = new File("D:\\Code\\21-xuecheng\\XcEduUI03\\video\\chunk\\");

        //要合并的最后文件
        File mergeFile = new File("D:\\Code\\21-xuecheng\\XcEduUI03\\video\\lucene1.avi");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        mergeFile.createNewFile();
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];

        //分块列表
        File[] files = chunkFolder.listFiles();

        List<File> fileList = new ArrayList<>(Arrays.asList(files));
        //按名字从小到大排序
        Collections.sort(fileList, (o1, o2) -> {
            if(Integer.parseInt(o1.getName())<Integer.parseInt(o2.getName())){
                return -1;
            }
            return 1;
        });

        for (File chunkFile : fileList) {
            int len = -1;
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
            while ((len = raf_read.read(b))!=-1){
                raf_write.write(b,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
