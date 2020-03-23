package com.dzg.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzg.pojo.FastDfsFile;
import com.dzg.utils.FastDfsClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.parser.JSONParser;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageServer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@RestController
@RequestMapping("/file")
@Api(tags = "半结构化与非结构化文件接口管理")
public class FileController {

    @PostMapping(value = "/upload", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传文件接口")

//    @ApiImplicitParam(value = "待上传文件", required = true)
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
                return FastDfsClient.getTrackerUrl() + uploadResults[0] + "/" + uploadResults[1];
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "文件上传失败!";
    }


    @GetMapping(value = "/getFileInfo")
    @ApiOperation(value = "获取文件信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "文件所属组", required = true),
            @ApiImplicitParam(name = "remoteFileUrl", value = "远端文件路径", required = true)
    })
    public String fileInfo(String groupName, String remoteFileUrl) {
        if (groupName.trim().length() == 0 || groupName.trim().equals("")) {
            throw new RuntimeException("文件所属组名不能为空！");
        }
        if (remoteFileUrl.trim().length() == 0 || remoteFileUrl.trim().equals("")) {
            throw new RuntimeException("文件路径不能为空！");
        }

        FileInfo fileInfo = FastDfsClient.getFileInfo(groupName, remoteFileUrl);
        return fileInfo.toString();
    }

    @GetMapping(value = "/getStoreStorages")
    @ApiOperation(value = "获取某一组的存储服务器信息")
    @ApiImplicitParam(name = "groupName", value = "文件所属组", required = true)
    public String queryStoreStorages(String groupName) throws IOException {
        if (groupName.trim().length() == 0 || groupName.trim().equals("")) {
            throw new RuntimeException("文件所属组名不能为空！");
        }
        StorageServer[] storeStorages = FastDfsClient.getStoreStorages(groupName);
        Set<String> sets = new HashSet<>();
        for (StorageServer server : storeStorages) {
            sets.add(server.getSocket().toString());
        }
        return sets.toString();
    }

}
