package com.company;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by candy on 2015/11/1.
 */
public class XLS {
    private String fileName;
    public XLS(String fileName){
        this.fileName = fileName;
    }


    private static TreeSet StringToSet(String s){
        TreeSet<String> set = new TreeSet<>();
        String a[] = s.split(";");
        String[] b;
        for (int i = 0; i < a.length; i++) {
            b = a[i].split(":");
            set.add(b[0]);
        }
        return set;
    }

    private static List StringToList(String s){
        List<String> list = new ArrayList<>();
        String a[] = s.split(";");
        String b[];
        for (int i = 0; i < a.length; i++) {
            b = a[i].split(":");
            list.add(b[0]);
        }
        return list;
    }

    private Map dataPreProcess(Map<String,String> data){
        Map<String,TreeSet<String >> keySet = new HashMap<>();
        for(Map.Entry<String,String> entry:data.entrySet()){
            keySet.put(entry.getKey(), StringToSet(entry.getValue()));
        }
        return keySet;
    }

    private List dataPreProcess2(Map<String,String> data){
        List<List<String>> records = new ArrayList<>();
        for(Map.Entry<String,String> entry:data.entrySet()){
            records.add(StringToList(entry.getValue()));
        }
        return records;
    }

    public Map getData() throws IOException {
        Map<String,String> data  = new HashMap<>();
        if(fileName!=null) {
            File file = new File(this.fileName);
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            int rowstart = hssfSheet.getFirstRowNum()+1;
            int rowEnd = hssfSheet.getLastRowNum();
            for (int i = rowstart; i <= rowEnd; i++) {
                HSSFRow row = hssfSheet.getRow(i);
                if (null == row) continue;
                data.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
            }
        }
        return dataPreProcess(data);
    }

    public List getData2() throws IOException {
        Map<String,String> data  = new HashMap<>();
        if(fileName!=null) {
            File file = new File(this.fileName);
            POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);

            int rowstart = hssfSheet.getFirstRowNum()+1;
            int rowEnd = hssfSheet.getLastRowNum();
            for (int i = rowstart; i <= rowEnd; i++) {
                HSSFRow row = hssfSheet.getRow(i);
                if (null == row) continue;
                data.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
            }
        }
        return dataPreProcess2(data);
    }
}
