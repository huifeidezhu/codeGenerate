package file;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

public class generateDao {
    private static final String PATH = System.getProperty("user.dir")+"/src/main/java/com/example/demo";
    private static String[] dirList = new String[]{"constants","entity","dto","repository"};

    public static void main(String[] args) {
        generateDaoFile();
    }

    private static void generateDaoFile(){
        InputStream in = null;
        OutputStreamWriter writer = null;
        OutputStreamWriter entity = null;
        OutputStreamWriter dto = null;
        OutputStreamWriter repository = null;
        try {
            writer = writer(dirList[0],"Dao.java");

            StringBuilder daoString = strBuilder(dirList[0],"Dao");

            String xlsPath = "./src/main/resources/dataStruct.xls";
            File file = new File(xlsPath);
            in = new FileInputStream(file);
            Workbook wb = new HSSFWorkbook(in);
            Sheet sheet = wb.getSheetAt(0);
            String tableName = "";
            StringBuilder entityString = null;
            StringBuilder dtoString = null;
            StringBuilder jpaString = null;
            for(Row row:sheet){
                if ("tablename".equals(row.getCell(0).getStringCellValue())){
                    tableName = row.getCell(1).getStringCellValue();
                    daoString.append("\npublic static final String "+tableName.toUpperCase()+"_TABLE_NAME = \""+tableName+"\";\n");

                    String name = tableName.substring(0,1).toUpperCase()+tableName.substring(1);
                    entity = writer(dirList[1], name+".java");
                    dto = writer(dirList[2],name+"Dto.java");
                    repository = writer(dirList[3],name+"Jpa.java");

                    entityString = entityBuilder(dirList[1],name,tableName.toUpperCase()+"_TABLE_NAME");
                    dtoString = strBuilder(dirList[2],name+"Dto");
                    jpaString = jpaBuilder(dirList[3],name);
                } else if("quit".equals(row.getCell(0).getStringCellValue())){
                    entityString.append("}");
                    jpaString.append("}");
                    dtoString.append("}");
                    entity.write(entityString.toString());
                    dto.write(dtoString.toString());
                    repository.write(jpaString.toString());
                    close(entity);
                    close(dto);
                    close(repository);
                }else{
                    String daoFieldName = tableName.toUpperCase()+"_"+row.getCell(0).getStringCellValue().toUpperCase();
                    String daoFieldValue = row.getCell(0).getStringCellValue();
                    daoString.append("public static final String "+ daoFieldName +"= \""+daoFieldValue+"\";");
                    daoString.append("//"+row.getCell(3).getStringCellValue()+"\n");

                    String[] field = daoFieldValue.split("_");
                    StringBuilder fieldBuider = new StringBuilder();
                    fieldBuider.append(field[0]);
                    for(int i=1;i<field.length;i++){
                        fieldBuider.append(field[i].substring(0,1).toUpperCase()+field[i].substring(1));
                    }
                    entityString.append("@Column(name = Dao."+daoFieldName+")\n");
                    entityString.append("private String "+fieldBuider+";\n");

                    dtoString.append("private String "+fieldBuider+";\n");
                }
            }
            daoString.append("}");
            writer.write(daoString.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(in !=null){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            close(writer);
            close(entity);
            close(dto);
            close(repository);
        }
    }

    private static  void close(OutputStreamWriter io){
        try {
            if(io!=null){
                io.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static OutputStreamWriter writer(String path,String name) throws IOException {
        File file = new File(PATH+"/"+path,name);
        if(file.exists()){
            file.delete();
        }else{
            file.createNewFile();
        }
        OutputStream daoOut = new FileOutputStream(file,true);
        return new OutputStreamWriter(daoOut);
    }

    private static StringBuilder strBuilder(String path,String name){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package com.example.demo."+path+";\n");
        stringBuilder.append("public class "+name+" {\n");
        return stringBuilder;
    }

    private static StringBuilder entityBuilder(String path,String name,String tableName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package com.example.demo."+path+";\n");
        stringBuilder.append("@Entity\n");
        stringBuilder.append("@Table(name = Dao."+tableName +")\n");
        stringBuilder.append("public class "+name+" {\n");
        return stringBuilder;
    }

    private static StringBuilder jpaBuilder(String path,String name){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("package com.example.demo."+path+";\n");
        stringBuilder.append("public interface "+name+"Jpa extends JpaRepository<"+name+", Long> {\n");
        return stringBuilder;
    }
}
