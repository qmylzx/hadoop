Ubuntu12   hadoop2.6.5

#stop-all.sh    start-all.sh      找不到命令刷新配置文件  source /etc/profile
----------------------
vi /etc/apt/sources.list

deb http://tw.archive.ubuntu.com/ubuntu/ precise main universe restricted multiverse 
deb-src http://tw.archive.ubuntu.com/ubuntu/ precise main universe restricted multiverse 
deb http://tw.archive.ubuntu.com/ubuntu/ precise-security universe main multiverse restricted 
deb-src http://tw.archive.ubuntu.com/ubuntu/ precise-security universe main multiverse restricted 
deb http://tw.archive.ubuntu.com/ubuntu/ precise-updates universe main multiverse restricted 
deb-src http://tw.archive.ubuntu.com/ubuntu/ precise-updates universe main multiverse restricted
-----------------------
#备份
mv /etc/apt/sources.list.bak /etc/apt/sources.list              

#设置root密码
	sudo passwd root
	sudo passwd -u root
#安装不上ssh需要更新sourcelist，操作如下，先修改sources.list
	 sudo apt-get update		
	sudo apt-get upgrade
安装OPENssh

sudo apt-get install openssh-client 

sudo apt-get install openssh-server    //   sudo /etc/init.d/ssh start   启动ssh

ps -e |grep ssh  有sshd则说明SSH服务端已经启动


-----------------------------------
原因是openssh-client与openssh-server所依赖的版本不同，解决方法是下载对应版本的openssh-client后再下载openssh-server。

终端输入： sudo apt-get install openssh-client=1:5.9p1-5ubuntu1 下载openssh-client。


#都要开启

-------------------------------------------------------------------------

sudo ufw disable    或者直接关闭防火墙

master   192.168.88.2
slave1  192.168.88.3
slave2	192.168.88.4

dns  8.8.8.8

连接  ssh [options] [-l login_name][user@]hostname [command]
	ssh alen@192.168.88.2
其中，”-l login_name”选项用于指定用户名，表示以哪一个用户身份登录到远程系统
如果不提供用户名，则以当前 用户的身份登录到远程系统
------------------------------------------------------------------------------
bakup   cp -avx /etc/hosts /etc/hosts.bak              /home/slave2/Desktop
vi /etc/hosts         修改三台主机的hosts文件 
127.0.0.1 localhost
192.168.88.2 master
192.168.88.3 slave1
192.168.88.4 slave2
---------------------------------------------------------------------
修改主机名 vi /etc/hostname   各自改为master,slave1,slave2
--------------------------------------------------------------------
在master上生成密钥   ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa   在slave1  2 上执行   ssh-keygen
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
---
cd ~/.ssh
---
scp authorized_keys slave1:~/.ssh/
scp authorized_keys slave2:~/.ssh/
-----------------------------------------------------------------------

装jdk

cd Desktop
tar -zxvf jdk-8u181-linux-x64.tar.gz -C /usr/lib/

配置classpath
sudo vi  /etc/profile    
   jdk1.8.0_181


#set java environment
export JAVA_HOME=/usr/lib/jdk1.8.0_181
export JRE_HOME=/usr/lib/jdk1.8.0_181/jre
export HADOOP_HOME=/home/hadoop/hadoop-2.6.5     
export CLASSPATH=.:$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH:$HADOOP_HOME/lib
export PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin

#-
若使用不了jps  刷新path source /etc/profile
#-
----------------------------------------------------------------

设置我们安装的jdk
sudo update-alternatives --install /usr/bin/java java /usr/lib/jdk1.8.0_181/bin/java 300

sudo update-alternatives --config java

 

安装 hadoop   /home/hadoop/hadoop-2.6.5
tar -zxvf hadoop-2.6.5.tar.gz -C /home/hadoop

-----------------------------------------------------

1,文件 slaves

cd /home/hadoop/hadoop-2.6.5/etc/hadoop
vim /home/hadoop/hadoop-2.6.5/etc/hadoop/slaves
将原来 localhost 删除，把所有Slave的主机名写上，每行一个。例如我只有一个 Slave节点，那么该文件中就只有一行内容： Slave1。

2, 文件 core-site.xml ，将原本的如下内容：

<property>
</property>
改为下面的配置。后面的配置文件的修改类似。

复制代码
<property>
    <name>fs.defaultFS</name>
    <value>hdfs://master:9000</value>
</property>
<property>
    <name>hadoop.tmp.dir</name>
    <value>file:/home/hadoop/hadoop-2.6.5/tmp</value>
    <description>Abase for other temporary directories.</description>
</property>
复制代码
3, 文件hdfs-site.xml，因为只有2个Slave，所以dfs.replication的值设为2。

复制代码
<property>
    <name>dfs.namenode.secondary.http-address</name>
    <value>master:50090</value>
</property>
<property>
    <name>dfs.namenode.name.dir</name>
    <value>file:/home/hadoop/hadoop-2.6.5/tmp/dfs/name</value>
</property>
<property>
    <name>dfs.datanode.data.dir</name>
    <value>file:/home/hadoop/hadoop-2.6.5/tmp/dfs/data</value>
</property>
<property>
    <name>dfs.replication</name>
    <value>2</value>
</property>
复制代码
4, 文件mapred-site.xml，这个文件不存在，首先需要从模板中复制一份：

cp mapred-site.xml.template mapred-site.xml
然后配置修改如下：

<property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
</property>
5, 文件yarn-site.xml：

复制代码
<property>
    <name>yarn.resourcemanager.hostname</name>
    <value>master</value>
</property>
<property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
</property>
复制代码
6,在hadoop-env修改JDK配置环境(前面粗体字有提及)，在hadoop/etc/hadoop/下面，将

export JAVA_HOME=${JAVA_HOME}
改成绝对路径
 export JAVA_HOME=/usr/lib/jdk1.8.0_181
---------------------------------------------------------
配置好后，将 master 上的 Hadoop 文件复制到各个节点上(虽然直接采用 scp 复制也可以正确运行，但会有所不同，如符号链接 scp 过去后就有点不一样了。所以先打包再复制比较稳妥)。因为之前有跑过伪分布式模式，建议在切换到集群模式前先删除之前的临时文件。
cd  /home
rm -r ./hadoop/hadoop-2.6.5/tmp  # 删除 Hadoop 临时文件
sudo tar -zcf ./hadoop.tar.gz ./hadoop
scp ./hadoop.tar.gz Slave1:/home/hadoop
解压
sudo tar -zxf hadoop.tar.gz
sudo chown -R root:root /home/hadoop
-----------------------------------------------
cd /home/hadoop/hadoop-2.6.5
bin/hadoop namenode  -format  --这里是格式化 一次即可
//   sbin/stop-all.sh 关闭   sbin/start-all.sh
#查看HDFS集群状态，访问如下地址：http://master:50070
--------------------------------------------------

Hello World-经典的wordcount程序

创建文件
echo "Hello World" > file1.txt
echo "Hello Hadoop" > file2.txt
cp README.txt /home/hadoop/input
------------------------------------------
cd /home/hadoop/hadoop-2.6.5
在HDFS上创建文件操作
	hadoop fs -mkdir input
	hadoop fs -ls
	hadoop fs -put /home/hadoop/input/* input
	hadoop fs -ls input

//调用
hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.5.jar wordcount input output

查看输出结果：
hadoop fs -ls output
hadoop fs -rm -r output  //删除文件
hadoop fs -rm -r input     //删除文件

                          本地文件目录的路径	           hdfs文件目录路径，即存储路径 
hdfs dfs -copyFromLocal /home/hadoop/input/*.txt input
--
                     hdfs文件目录    本地文件文件目录
hdfs dfs -copyToLocal output /home/hadoop
#
#
hadoop fs -cat /output/2018_10_15_20_39_00/part-r-00000
#
#
##
