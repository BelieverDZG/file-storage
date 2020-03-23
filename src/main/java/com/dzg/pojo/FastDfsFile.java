package com.dzg.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 对上传的文件的详细信息进行封装
 */
//@Data //lombok插件
//@AllArgsConstructor
//@Getter
//@Setter
//@ToString
//@EqualsAndHashCode
@ApiModel
public class FastDfsFile {

    @ApiModelProperty(value = "文件名")
    private String name;//文件名
    @ApiModelProperty(value = "文件内容")
    private byte[] contents;//文件内容
    @ApiModelProperty(value = "文件扩展名")
    private String ext;//文件扩展名
    @ApiModelProperty(value = "文件md5摘要")
    private String md5;//文件md5摘要
    @ApiModelProperty(value = "文件上传者")
    private String author;//文件上传者


    public FastDfsFile() {
    }

    public FastDfsFile(String name, byte[] contents, String ext) {
        super();
        this.name = name;
        this.contents = contents;
        this.ext = ext;
    }

    public FastDfsFile(String name, byte[] contents, String ext, String md5, String author) {
        super();
        this.name = name;
        this.contents = contents;
        this.ext = ext;
        this.md5 = md5;
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
