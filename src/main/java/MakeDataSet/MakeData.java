package MakeDataSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class MakeData {
    static File result = new File("C:\\Users\\Alen\\hadoop\\rec\\rec.txt");
    static List<String> list = new LinkedList();
    static Random r = new Random();

    public static void removeToList(File p) throws Exception {
        InputStream in = new FileInputStream(p);
        InputStreamReader sr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(sr);
        String temp = null, str;  //str.replaceAll("\\d+","")
        String[] store;
        while ((temp = br.readLine()) != null) {
            str = temp.replaceAll("\\d+", "");
            if (!str.trim().equals("")) { //去掉数字
                list.add(str);
            }
        }
        br.close();
    }

    public static void removeToResult(String filname ) throws Exception {
        File f = new File(filname);
        if (!f.exists()){
            f.createNewFile();
        }
        OutputStream out = new FileOutputStream(f, true);
        OutputStreamWriter sw = new OutputStreamWriter(out);
        BufferedWriter bw = new BufferedWriter(sw);
        for (int i = 0; i < 1200; i++) {
            bw.write(list.get(r.nextInt(list.size())));
            bw.write(" ");
            if(r.nextInt(150)<10){
                bw.newLine();
            }
        }
        bw.close();
    }
    public static void mkData()throws Exception{
        removeToList(result);
        for(int i=0;i<10050;i++){
            String filename = "C:\\Users\\Alen\\hadoop\\input\\rec"+i+".txt";
            r.setSeed(System.currentTimeMillis());
            removeToResult(filename);
        }
    }
    public static void main(String[] args) throws Exception{
        mkData();
    }
}
