package com.capstone;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

public class ImageManager {

    private static LinkedList<String> filePaths = new LinkedList<String>();
    private static Hashtable<String, String> imageClassifications = new Hashtable<>();
    private static final String imageFileName = "/AmenityDetectorImageFilePaths.txt";

    public static void addClassifiedImage(String path, String classificationList){
        filePaths.add(path);
        try {
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
            content += path + classificationList;
            PrintWriter pw = new PrintWriter(saveFile);
            pw.println(content);
            pw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void readInClassifiedImages(){
        filePaths.clear();
        try {
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File saveFile = new File(storageDir + imageFileName);
            if(!saveFile.exists()){
                saveFile.createNewFile();
            } else {
                Scanner lineReader = new Scanner(saveFile);
                String filePath = "";
                String classifications = "";
                while(lineReader.hasNextLine()){
                    Scanner lineParser = new Scanner(lineReader.nextLine());
                    lineParser.useDelimiter(",");
                    filePath = lineParser.next();
                    while(lineParser.hasNext()){
                        if (lineParser.hasNextDouble()) {
                            classifications += "..." + lineParser.next() + "%\n";
                        } else {
                            classifications += lineParser.next();
                            if(lineParser.hasNext() && !lineParser.hasNextDouble()){
                                classifications += ", ";
                            }
                        }
                    }
                    filePaths.add(filePath);
                    imageClassifications.put(filePath, classifications);
                    classifications = "";
                }
                lineReader.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static String getClassifications(String path){
        return imageClassifications.get(path);
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
