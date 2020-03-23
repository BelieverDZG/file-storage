package com.dzg.controller;

import com.dzg.pojo.FastDfsFile;
import com.dzg.utils.FastDfsClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.dzg.utils.FastDfsClient.getStorageClient;



@RestController
@RequestMapping("/file")
@Api(tags = "半结构化与非结构化文件接口管理")
public class FileController {

    @PostMapping("/upload")
    @ApiOperation(value = "上传文件接口")
  //  @ApiImplicitParam(name = "form",value = "待上传文件",required = true)
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

    @GetMapping("/delete")
    @ApiOperation(value = "删除文件接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName",value = "组名",required = true,dataType = "String"),
            @ApiImplicitParam(name = "fileUrl",value = "存储url",required = true,dataType = "String")
    })
    public String deleteFile(String groupName,String fileUrl){
        int i = FastDfsClient.deleteFile(groupName, fileUrl);
        System.out.println(i);
        if(i==0){
            return "删除失败";
        }
        return "删除成功";
    }

    @GetMapping("/download")
    @ApiOperation(value = "下载文件接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName",value = "组名",required = true,dataType = "String"),
            @ApiImplicitParam(name = "fileUrl",value = "存储url",required = true,dataType = "String"),
            @ApiImplicitParam(name = "ext",value = "类型",required = true,dataType = "String")
    })
    public String downloadFile(String groupName,String fileUrl,String ext) throws IOException, MyException {
        String filePath = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
        //1 加载配置文件，配置文件中的内容是tracker服务的地址
        ClientGlobal.init(filePath);
        //2 创建一个TrackerClient对象
        TrackerClient client = new TrackerClient();
        //3 使用TrackerClient对象创建连接，获取一个TrackerServer对象
        TrackerServer trackerServer = client.getConnection();
        //4 创建一个StorageServer的引用，值为null
        StorageServer storageServer =null;
        //5 创建一个StorageClient对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //6 使用StorageClient对象上传图片
        byte[] bytes = storageClient.download_file(groupName, fileUrl);
        FastDfsFile fastDfsFile =new FastDfsFile();
        fastDfsFile.setExt(ext);
        if(bytes!=null){
            File file = new File("D:/download."+fastDfsFile.getExt());
            OutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
            if(file.exists()){
                return "下载成功";
            }
        }
        return "下载失败";
    }

    @PostMapping("/bachLoad")
    @ApiOperation(value = "批量上传文件接口")
    @ApiImplicitParam(name = "file",value = "文件",required = true)
    public String  bacheUpload(File file) throws IOException, MyException {
        List<String> directory = FastDfsClient.isDirectory(file);
        if(directory.size()!=0){
            return "上传成功"+directory;
        }
        return "上传失败";
    }

}

