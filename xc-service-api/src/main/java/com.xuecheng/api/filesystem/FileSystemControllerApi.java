package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "文件上传服务", description = "文件上传服务", tags = {"文件上传服务"})
public interface FileSystemControllerApi {

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param fileTag       文件标签
     * @param businessKey   业务key
     * @param metadata      元信息，json格式
     * @return
     */
    @ApiOperation("上传文件")
    public UploadFileResult upload(MultipartFile multipartFile,
                                   String fileTag,
                                   String businessKey,
                                   String metadata);
}
