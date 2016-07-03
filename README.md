fleet-manager: 
=========================================================================================================
作者: Gong Yi


本机开发环境搭建
-----------

fleet manager 使用wildfly和mysql开发。使用maven进行项目管理。

1. Maven, 下载maven 3.x.y 最新版  <https://maven.apache.org>
2. Wildfly, 下载wildfly 9.x.y 最新版 <http://wildfly.org/downloads/>
3. Mysql server,下载mysql 最新版 <http://dev.mysql.com/downloads/mysql/>
4. 创建数据库.用户名fleetmgr,密码fleetmgr,数据库fleet-manager  
5. 初始化Wildfly
   将wildfly目录下的文件拷贝到wildfly/bin   
   启动wildfly/bin/standalone.sh 或者 wildfly\bin\standalone.bat.
   启动完成后,init.bat


程序部署
-------------------------
首先启动wildfly
 
运行 bin/standalone.bat
  
编译部署
    mvn package wildfly:deploy

打开 <http://localhost:8080/fleet-manager/>.

卸载已部署的程序
    mvn wildfly:undeploy



 
运行 Arquillian 测试
============================
1. 首先启动wildfly    
   运行 bin/standalone.bat
2. mvn clean test -Parq-wildfly-remote
