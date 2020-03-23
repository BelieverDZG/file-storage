package com.dzg.utils;

import com.dzg.pojo.FastDfsFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

public class FastDfsClient {

    private static final Logger logger = LoggerFactory.getLogger(FastDfsClient.class);

    /**
     * 加载客户端配置资源
     */
    static {

        try {
            String conf_file_path = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
            ClientGlobal.init(conf_file_path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param file
     * @return 返回文件文件上传的位置信息：组-文件路径
     * @throws IOException
     */
    public static String[] upload(FastDfsFile file) throws IOException {

        byte[] contents = file.getContents();
        //获取文件上传的作者
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", file.getAuthor());
        //接收返回的数据
        String[] uploadResults = null;
        StorageClient storageClient = null;

        try {
            storageClient = getStorageClient();
            uploadResults = storageClient.upload_file(contents, file.getExt(), meta_list);
        } catch (MyException e) {
            logger.error("Exception occurred when upload file: ", file.getName(), e);
        }
        //文件上传失败
        if (uploadResults == null && storageClient != null) {
            logger.error("upload file failed ,error code ", storageClient.getErrorCode());
        }
        //若返回内容为空，表明文件上传失败
//        String group = uploadResults[0]; 文件所属组
//        String fileUrl = uploadResults[1]; 文件存储路径
        return uploadResults;
    }

    /**
     * 删除文件,删除成功返回一个正数22，否则返回0 ？
     *
     * @param groupName
     * @param fileUrl
     * @return
     */
    public static int deleteFile(String groupName, String fileUrl) {
        StorageClient storageClient = getStorageClient();
        int deleteFile = 0;
        try {
            deleteFile = storageClient.delete_file(groupName, fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return deleteFile;
    }

    /**
     * 文件下载
     *
     * @param groupName
     * @param fileUrl
     * @return
     */
    public static InputStream downloadFile(String groupName, String fileUrl) {

        try {
            StorageClient storageClient = getStorageClient();
            byte[] bytes = storageClient.download_file(groupName, fileUrl);
            InputStream is = null;
            if (bytes != null) {
                is = new ByteArrayInputStream(bytes);
            }
            return is;
        } catch (Exception e) {
            logger.error("fail to download file !", e);
        }
        return null;
    }

    /**
     * 获取文件的信息
     *
     * @param groupName
     * @param fileUrl
     * @return
     */
    public static FileInfo getFileInfo(String groupName, String fileUrl) {
        try {
            StorageClient storageClient = getStorageClient();
            FileInfo fileInfo = storageClient.get_file_info(groupName, fileUrl);
            return fileInfo;
        } catch (Exception e) {
            logger.error("fail to get the info of file !", e);
        }
        return null;
    }


    /**
     * 获取Storage组
     *
     * @param groupName
     * @return
     */
    public static StorageServer[] getStoreStorages(String groupName) {

        try {
            //创建一个TrackerClient
            TrackerClient trackerClient = new TrackerClient();
            //获取一个TrackerServer的连接
            TrackerServer trackerServer = trackerClient.getConnection();
            return trackerClient.getStoreStorages(trackerServer, groupName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new StorageServer[0];
    }

    /**
     * 获取storage信息:IP、端口号
     *
     * @param groupName
     * @return
     */
    public static StorageServer getStorageInfo(String groupName, String fileUrl) {
        try {
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            return trackerClient.getFetchStorage(trackerServer, groupName, fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取一个TrackerServer
     *
     * @return
     */
    public static TrackerServer getTrackerServer() {
        //创建一个客户端
        TrackerClient trackerClient = new TrackerClient();
        //客户端请求TrackerServer（TrackerServer会返回一个可用的StorageClient）
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trackerServer;
    }

    /**
     * 获取一个StorageClient，由TrackerServer请求查找到一个可用的，然后返回
     *
     * @return
     */
    public static StorageClient getStorageClient() {
        TrackerServer trackerServer = getTrackerServer();
        return new StorageClient(trackerServer, null);
    }


    /**
     * 获取Tracker服务器的地址
     *
     * @return
     */
    public static String getTrackerUrl() {
        TrackerServer trackerServer = getTrackerServer();
        String hostString = trackerServer.getInetSocketAddress().getHostString();
        return "http://" + hostString + ":" + ClientGlobal.getG_tracker_http_port() + "/";
    }

    public static void main(String[] args) throws IOException {
//        upload();
//        deleteFile("group1", "M00/00/04/CsRT5V5wjPaAF3AvAAOVh2NJNtY800.png");
        /*InputStream inputStream = downloadFile("group1", "M00/00/04/CsRT5V5wjPaAF3AvAAOVh2NJNtY800.png");

        OutputStream os = new FileOutputStream("D:\\down.png");
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) > 0) {
            os.write(buffer);
        }*/
        /*FileInfo fileInfo = getFileInfo("group1", "M00/00/04/CsRT5V5wkt6AFD3zAAOVh2NJNtY795.png");
        if (fileInfo != null){
            //source_ip_addr = 10.196.83.229, file_size = 234887, create_timestamp = 2020-03-17 17:05:34, crc32 = 1665742550
            System.out.println(fileInfo);
        }*/


        /*StorageServer[] storages = getStoreStorages("group1");
        for (int i = 0; i < storages.length; i++) {
            System.out.println(storages[i].getSocket());
            System.out.println(storages[i].getInetSocketAddress());
        }*/

        StorageServer storageServer = getStorageInfo("group1", "M00/00/04/CsRT5V5wkt6AFD3zAAOVh2NJNtY795.png");
        System.out.println(storageServer.getInetSocketAddress()); //    /10.196.83.229:23000
        System.out.println(storageServer.getSocket()); //Socket[addr=/10.196.83.229,port=23000,localport=13080]
        System.out.println(storageServer.getStorePathIndex());
    }

}
