#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

if [ ! -f .env ]; then
  echo "[ERROR] .env 不存在，请先在 deploy/ 下创建 .env"
  exit 1
fi

# 加载 .env
set -a
. ./.env
set +a

echo "[INFO] 构建并启动容器..."
docker compose up -d --build

echo "[INFO] 等待 MySQL 就绪..."
for i in {1..60}; do
  if docker exec rblog-mysql sh -lc 'mysqladmin ping -h 127.0.0.1 -p"$MYSQL_ROOT_PASSWORD" --silent' >/dev/null 2>&1; then
    echo "[INFO] MySQL 已就绪"
    break
  fi
  sleep 2
  if [ "$i" -eq 60 ]; then
    echo "[ERROR] MySQL 等待超时，请检查 docker logs rblog-mysql"
    exit 1
  fi
done

echo "[INFO] 确保数据库存在: $MYSQL_DATABASE"
docker exec rblog-mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS \`'$MYSQL_DATABASE'\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"'

SQL_FILE="../sql_data/r_blog.sql"
if [ -f "$SQL_FILE" ]; then
  echo "[INFO] 导入 SQL: $SQL_FILE"
  docker exec -i rblog-mysql sh -lc 'mysql -uroot -p"$MYSQL_ROOT_PASSWORD" "'$MYSQL_DATABASE'"' < "$SQL_FILE"
  echo "[INFO] SQL 导入完成"
else
  echo "[WARN] 未找到 $SQL_FILE，跳过导入"
fi

echo "[INFO] 所有服务状态："
docker compose ps

echo "[SUCCESS] 启动完成。前台: http://<host>:${VIEW_PORT} 后台: http://<host>:${CMS_PORT} API: http://<host>:${API_PORT}/RBlog/"


