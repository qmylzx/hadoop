package Util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import java.util.*;
import java.io.*;
import java.net.URI;
import java.text.NumberFormat;


public class MyFileUtils {
    static File result = new File("C:\\Users\\Alen\\hadoop\\result\\result.txt");
    static List<String> list = new LinkedList<String>();
    static long count = 0;

    public static void upLoadFiletoHDFS() throws Exception{
        Configuration conf = new Configuration();
        String localDir = "C:\\Users\\Alen\\hadoop\\input\\temp\\";
        String hdfsDir = "hdfs://master:9000/user/root/input";
        File file = new File(localDir);
        File temp[] = file.listFiles();
        if (temp.length <= 0) {
            System.out.println("目录下无文件！");
            return;
        }
        try {
            Path hdfsPath = new Path(hdfsDir);
            FileSystem hdfs = FileSystem.get(conf);
            if (!hdfs.exists(hdfsPath)) {
                hdfs.mkdirs(hdfsPath);
            }
            for (File f : temp) {
                System.out.print(f.getAbsolutePath() + " + ");
                hdfs.copyFromLocalFile(new Path(f.getAbsolutePath()), hdfsPath);
            }
        } catch (Exception e) {
            System.out.println("失败!!!");
            e.printStackTrace();
        }
    }

    //downLoadFiletoHDFS("hdfs://master:9000/user/root/output","C:\\Users\\Alen\\hadoop\\output\\");
    public static boolean downLoadFiletoHDFS(String hdfsSrc, String localDst) throws IOException {
        Configuration conf = new Configuration();
        Path dstpath = new Path(hdfsSrc);
        int i = 1;
        FileSystem fs = FileSystem.get(URI.create(hdfsSrc), conf);
        try {
            String subPath = "";
            FileStatus[] fList = fs.listStatus(dstpath);
            for (FileStatus f : fList) {
                if (null != f) {
                    subPath = new StringBuffer()
                            .append(f.getPath().getParent()).append("/")
                            .append(f.getPath().getName()).toString();
                    if (f.isDir()) {
                        downLoadFiletoHDFS(subPath, localDst);
                    } else {
                        System.out.println("/t/t" + subPath);// hdfs://54.0.88.53:8020/
                        Path dst = new Path(subPath);
                        i++;
                        try {
                            Path Src = new Path(subPath);
                            String Filename = Src.getName().toString();
                            String local = localDst + Filename;
                            Path Dst = new Path(local);
                            FileSystem localFS = FileSystem.getLocal(conf);
                            FileSystem hdfs = FileSystem.get(URI
                                    .create(subPath), conf);
                            FSDataInputStream in = hdfs.open(Src);
                            FSDataOutputStream output = localFS.create(Dst);
                            byte[] buf = new byte[1024];
                            int readbytes = 0;
                            while ((readbytes = in.read(buf)) > 0) {
                                output.write(buf, 0, readbytes);
                            }
                            in.close();
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.print(" download failed.");
                        } finally {
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            System.out.println("the number of files is :" + i);
        }
        return true;
    }

    public static void doFinalJob() throws Exception {
        File file = new File("C:\\Users\\Alen\\hadoop\\output\\");
        File[] files = file.listFiles();
        if (!result.exists()) {
            result.createNewFile();
        }
        for (File f : files) {
            if (f.getName().contains(".") || f.getName().contains("_")) {//删除无用文件
                System.out.println(f.getAbsolutePath());
                deleteFile(f.getAbsolutePath());
            } else {//有用文件
                removeToList(f);
            }
        }
        if (!list.isEmpty()) {
            removeToResult();
        } else {
            System.out.println("结果不存在");
        }
    }

    public static void removeToList(File part) throws Exception {
        InputStream in = new FileInputStream(part);
        InputStreamReader sr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(sr);
        String temp = null, str;  //str.replaceAll("\\d+","")
        String[] store;
        while ((temp = br.readLine()) != null) {
            str = temp.replaceAll("\\d+", "");
            if (!str.trim().equals("")) { //去掉数字
                store = temp.split("\\s");
                count += Integer.parseInt(store[1]);
                list.add(temp);
            }
        }
        System.out.println(count);
        br.close();
    }

    public static void removeToResult() throws Exception {
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {
                String[] store1,store2;
                store1 = o1.split("\\s");
                store2 = o2.split("\\s");
                return Integer.parseInt(store2[1])-Integer.parseInt(store1[1]);
            }
        });
        String[] store;
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(10);
        OutputStream out = new FileOutputStream(result);
        OutputStreamWriter sw = new OutputStreamWriter(out);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write("次数\t\t频率\t\t      单词");
        bw.newLine();
        for (String s : list) {
            store = s.split("\\s");
            bw.write(store[1] + "\t\t" + format.format(Double.parseDouble(store[1]) / count) + "\t\t" + store[0]);
            bw.newLine();
        }
        bw.close();
    }

    public static void doPrepareWork() throws Exception {
        File file = new File("C:\\Users\\Alen\\hadoop\\input\\");
        File dataSet = new File("C:\\Users\\Alen\\hadoop\\input\\temp\\dataSet.txt");
        File[] files = file.listFiles();
        if (!dataSet.exists()) {
            dataSet.createNewFile();
        }
        for (File f : files) {
            if (!f.isDirectory()) {//
                FileInputStream in = new FileInputStream(f);
                FileOutputStream out = new FileOutputStream(dataSet, true);
                byte[] buf = new byte[1024];
                int index = -1;
                while ((index = (in.read(buf))) != -1) {
                    out.write(buf, 0, index);
                }
                out.write(System.getProperty("line.separator").getBytes());
                in.close();
                out.close();
            }
        }
    }

    public static void deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }
}

