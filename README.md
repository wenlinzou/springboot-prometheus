
1 打包maven
```jshelllanguage
mvn clean install -Dmaven.test.skip=true
```

2 运行jar文件
```jshelllanguage
nohup java -jar {xx}.jar &
```



### docker搭建Prometheus监控



#### 一、安装docker

- 1 删除原`docker`

```shell
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```

- 2 按照依赖包

```shell
sudo yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2
```

- 3 添加`yum`源

```
sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

- 4 安装`DOCKER ENGINE - COMMUNITY`

```shell
sudo yum install docker-ce docker-ce-cli containerd.io
```

- 5 查看所有仓库中所有docker版本，并选择特定版本安装

```shell
yum list docker-ce --showduplicates | sort -r
```

- 6 启动`docker`

```shell
sudo systemctl start docker
```

- 7 验证安装是否成功(有client和service两部分表示docker安装启动都成功了)

```shell
docker version
```

- 8 启动并加入开机启动

```shell
sudo systemctl start docker
sudo systemctl enable docker
```



服务启动关闭

```
service docker start
service docker stop
```



#### 二、安装node监控

- 1 下载镜像包

```shell
docker pull prom/node-exporter
```

- 2 启动`node-exporter`

```shell
docker run -d -p 9100:9100 \
  -v "/proc:/host/proc:ro" \
  -v "/sys:/host/sys:ro" \
  -v "/:/rootfs:ro" \
  --net="host" \
  prom/node-exporter
```

- 3 查看端口

```shell
netstat -anpt
```

- 4 访问url

```
http://10.1.171.197:9100/metrics
```



#### 三、安装prometheus

- 1 下载镜像包

```
docker pull prom/prometheus
```

- 2 新建目录`prometheus`，编辑配置文件`prometheus.yml`

```shell
mkdir /prometheus
vim prometheus.yml
```

- 3 内容如下

```shell
global:
  scrape_interval:     60s
  evaluation_interval: 60s
 
scrape_configs:
  - job_name: prometheus
    static_configs:
      - targets: ['localhost:9090']
        labels:
          instance: prometheus
 
  - job_name: linux
    static_configs:
      - targets: ['192.168.91.132:9100']
        labels:
          instance: localhost
```

注意：修改IP地址，这里的`192.168.91.132`就是本机地址

- 4 在目录`/data/prometheus`启动

```shell
docker run  -d \
  -p 9090:9090 \
  -v /data/docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml  \
  prom/prometheus
```

- 5 查看端口

```
netstat -anpt
```

- 6 访问url

```
http://10.1.171.197:9090/graph
```

- 7 访问`targets`，url如下

```shell
http://10.1.171.197:9090/targets
```



`prometheus.yml`内容如下：

```yml
global:
  scrape_interval:     60s
  evaluation_interval: 60s
 
scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
        labels:
          instance: prometheus
 
  - job_name: 'linux-node'
    static_configs:
      - targets: ['10.1.171.197:9100']
        labels:
          instance: localhost

  - job_name: 'prometheus_demo'
    scrape_interval: 5s
#    scrape_timeout: 5s
    metrics_path: '/actuator/prometheus'
#    scheme: http
#    basic_auth:
#      username: admin
#      password: 123456
    static_configs:
      - targets: ['10.1.171.197:8089']
        labels:
          instance: prometheus_demo
       # - 127.0.0.1:8089  #此处填写 Spring Boot 应用的 IP + 端口号  
```



#### 四、安装grafana

- 1 拉取镜像

```shell
docker pull grafana/grafana
```

- 2 新建文件夹`grafana-storage`，用于存储数据

```
mkdir grafana-storage
```

- 3 设置权限

```
chmod 777 -R /data/docker/grafana-storage
```

- 4 启动，到目录`/data/docker/grafana-storage`下启动

```shell
docker run -d \
  -p 3000:3000 \
  --name=grafana \
  -v /data/docker/grafana-storage:/var/lib/grafana \
  grafana/grafana
```

- 5 查看端口

```
netstat -anpt
```

- 6 访问url（用户名，密码都是`admin`）

```
http://10.1.171.197:3000
```



在 [Dashboard 市场](https://grafana.com/grafana/dashboards) 中，提供了两个适合 Spring Boot 应用的 Dashboard

- Grafana 导入 [Spring Boot 2.1 Statistics](https://grafana.com/grafana/dashboards/10280)
- Grafana 导入 [JVM (Micrometer)](https://grafana.com/grafana/dashboards/4701)

参考`https://www.cnblogs.com/xiao987334176/p/9930517.html`