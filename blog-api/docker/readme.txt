自行更改dockerfile的jar包文件名称，和端口号，运行jar包的名称
自行更改docker-compose.yml的 ports:下面的port改成自己需要的端口号
数据库：    environment:
          MYSQL_ROOT_PASSWORD: 123456
          MYSQL_DATABASE: databaseName
          MYSQL_USER: myuser
          MYSQL_PASSWORD: 123456
root的密码，和需要的数据库名称都需要自行更改。

改好后：
docker compose up -d .
