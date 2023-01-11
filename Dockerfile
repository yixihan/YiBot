FROM myos
EXPOSE 19999

MAINTAINER yixihan<3113788997@qq.com>

VOLUME /tmp
ENV LANG zh_CN.UTF-8
ADD target/yibot.jar  /app.jar
RUN bash -c 'touch /app.jar' cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ENTRYPOINT ["java","-jar","/app.jar"]
