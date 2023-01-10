FROM mysql:8-debian

ADD https://downloads.mysql.com/docs/sakila-db.tar.gz /tmp/sakila-db.tar.gz 
RUN cd /tmp && \
    tar xf sakila-db.tar.gz && \
    cp sakila-db/sakila-schema.sql /docker-entrypoint-initdb.d/001_sakila_schema.sql && \
    cp sakila-db/sakila-data.sql /docker-entrypoint-initdb.d/002_sakila_data.sql
