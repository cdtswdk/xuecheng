package com.xuecheng.filesystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileSystemServiceImpl implements FileSystemService {

    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers;
    @Value("${xuecheng.fastdfs.charset}")
    private String charset;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private int connect_timeout_in_seconds;

    @Autowired
    private FileSystemRepository fileSystemRepository;

    @Override
    public UploadFileResult upload(MultipartFile file, String fileTag, String businessKey, String metadata) {
        //上传文件为空
        if (file == null) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //上传文件，返回文件id
        String fileId = fdfs_upload(file);
        if (StringUtils.isBlank(fileId)) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        //创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        //文件id
        fileSystem.setFileId(fileId);
        //业务key
        fileSystem.setBusinesskey(businessKey);
        //文件名字
        fileSystem.setFileName(file.getOriginalFilename());
        //文件路径
        fileSystem.setFilePath(fileId);
        //标签
        fileSystem.setFiletag(fileTag);
        //文件类型
        fileSystem.setFileType(file.getContentType());
        //文件大小
        fileSystem.setFileSize(file.getSize());
        //元数据
        if (StringUtils.isNotBlank(metadata)) {
            try {

                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //保存到文件存储系统
        this.fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
    }

    //文件上传
    private String fdfs_upload(MultipartFile file) {
        try {
            //初始化文件配置
            initFdfsConfig();
            TrackerClient trackerClient = new TrackerClient();
            //获取tracker服务器
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage服务器
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
            //上传文件
            //获取文件字节
            byte[] bytes = file.getBytes();
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            return storageClient1.upload_file1(bytes, extName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //初始化文件配置
    private void initFdfsConfig() {
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
