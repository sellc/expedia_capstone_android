package com.capstone;

public class FileInfo {

    private String name;
    private long fileSize;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public long getFIleSize(){
        return fileSize;
    }

    public void setFileSize(long fileSize){
        this.fileSize = fileSize;
    }
}
