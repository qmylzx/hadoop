package MyWordStatistics;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountRunner {
    boolean doCompute()throws Exception{
        Configuration config = new Configuration();
        config.set("mapreduce.framework.name", "yarn");//集群的方式运行，非本地运行
        config.set("mapreduce.app-submission.cross-platform", "true");//意思是跨平台提交，在windows下如果没有这句代码会报错 "/bin/bash: line 0: fg: no job control"，去网上搜答案很多都说是linux和windows环境不同导致的一般都是修改YarnRunner.java，但是其实添加了这行代码就可以了。
        //配置生成当前项目的jar，上传到服务器
        config.set("mapreduce.job.jar","C:\\Users\\Alen\\hadoop\\out\\artifacts\\hadoop_jar\\hadoop.jar");
        Job job = Job.getInstance(config);
        job.setJarByClass(WordCountRunner.class);
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //要处理的数据输入与输出地址
        FileInputFormat.setInputPaths(job,"hdfs://master:9000/user/root/input/dataSet.txt");
        FileOutputFormat.setOutputPath(job,new Path("hdfs://master:9000/user/root/output"));
        return job.waitForCompletion(true);
    }
}
