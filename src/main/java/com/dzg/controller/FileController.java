package com.dzg.controller;

import com.dzg.pojo.FastDfsFile;
import com.dzg.utils.FastDfsClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/file")
@Api(tags = "半结构化与非结构化文件接口管理")
public class FileController {

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件接口")
    @ApiImplicitParam(name = "file",value = "待上传文件",required = true)
    public String uploadFile(MultipartFile file) {
        try {
            //文件上传前，判断文件是否存在
            if (file == null) {
                throw new RuntimeException("file does not exist");
            }
            //获取文件完整的姓名
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)) {
                throw new RuntimeException("file does not exist");
            }
            //获取文件扩展名
            String ext = originalFilename.substring(originalFilename.indexOf('.') + 1);
            //获取文件内容
            byte[] content = file.getBytes();
            //创建文件上传实体类
            FastDfsFile fastDfsFile = new FastDfsFile(originalFilename, content, ext);
            //调用工具类上传文件
            String[] uploadResults = FastDfsClient.upload(fastDfsFile);
            if (uploadResults.length > 0) {
                //返回文件上传后的路径
                return FastDfsClient.getTrackerUrl() + uploadResults[0] + "/" + uploadResults[1] ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "文件上传失败!";
    }

}
