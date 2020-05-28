package com.capstone;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class ImageManager {

    private static LinkedList<String> filePaths = new LinkedList<String>();

    public static void addImagePath(String path){
        filePaths.add(path);
        try {
            String imageFileName = "/AmenityDetectorImageFilePaths.txt";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File saveFile = new File(storageDir + imageFileName);
            String content = "";
            if(!saveFile.exists()){
                saveFile.createNewFile();
            } else {
                Scanner reader = new Scanner(saveFile);
                while(reader.hasNextLine()){
                    content += reader.nextLine() + "\n";
                }
                reader.close();
            }
            content += path;
            PrintWriter pw = new PrintWriter(saveFile);
            pw.println(content);
            pw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void readInImagePaths(){
        filePaths.clear();
        try {
            String imageFileName = "/AmenityDetectorImageFilePaths.txt";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File saveFile = new File(storageDir + imageFileName);
            if(!saveFile.exists()){
                saveFile.createNewFile();
            } else {
                Scanner reader = new Scanner(saveFile);
                while(reader.hasNextLine()){
                    filePaths.add(reader.nextLine());
                }
                reader.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void removeImagePath(String path){
        int index = -1;
        while(++index < filePaths.size()){
            if(filePaths.get(index).equals(path)){
                filePaths.remove(index);
            }
        }
    }

    public static LinkedList<String> getFilePaths(){
        return filePaths;
    }

}
