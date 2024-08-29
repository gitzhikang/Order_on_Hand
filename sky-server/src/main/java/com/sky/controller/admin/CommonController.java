package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AwsS3Util;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    AwsS3Util awsS3Util;

    @Value("${sky.aws.s3.bucketName}")
    private String awsBucketName;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        try {
            return Result.success(awsS3Util.uploadFile(file,awsBucketName));
        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
        }
        return Result.error("文件上传失败");
    }

}
