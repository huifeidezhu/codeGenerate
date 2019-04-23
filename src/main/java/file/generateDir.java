package file;


import java.io.File;

public class generateDir {
    private static final String PATH = System.getProperty("user.dir")+"/src/main/java/com/example/demo";
    private static String[] dirList = new String[]{"constants","entity","dto","repository"};

    public static void main(String[] args) {
        makedir();
    }
    private static void makedir(){
        for(int i=0;i<dirList.length;i++){
            File file = new File(PATH,dirList[i]);
            if(file.exists()){
                file.delete();
            }else {
                file.mkdir();
            }
        }
    }
}
