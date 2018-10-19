package MyWordStatistics;

import Util.MyFileUtils;

public class Main {
    public static void main(String[] args) throws Exception{
        MyFileUtils.doPrepareWork();
        System.out.println("准备完成，等待开始上传数据到服务器...");
        Thread.currentThread().sleep(2000);
        MyFileUtils.upLoadFiletoHDFS();
        System.out.println("上传成功！");
        Thread.currentThread().sleep(5000);
        WordCountRunner runner = new WordCountRunner();
        runner.doCompute();
        System.out.println("计算完成");
        Thread.currentThread().sleep(5000);
        MyFileUtils.downLoadFiletoHDFS("hdfs://master:9000/user/root/output","C:\\Users\\Alen\\hadoop\\output\\");
        System.out.println("下载成功！");
        MyFileUtils.doFinalJob();
    }
}
