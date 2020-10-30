package com.xuecheng.filesystem.service;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileSystemService {

    /**
     * 文件上传
     *
     * @param file
     * @param fileTag
     * @param businessKey
     * @param metadata
     * @return
     */
    public UploadFileResult upload(MultipartFile file,
                                   String fileTag,
                                   String businessKey,
                                   String metadata);
}
