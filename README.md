# Debezium with Quarkus

The project for testing Debezium with three different databases: MongoDB, Postgresql and MySql.
Debezium is an open source distributed platform for change data capture (CDC).  
For more information visit the [link](https://debezium.io/).

## Setup MongoDB
The MongoDB connector uses MongoDB’s oplog to capture the changes, so the connector works only with MongoDB replica sets
or with sharded clusters where each shard is a separate replica set. The Debezium MongoDB connector can capture changes from a single MongoDB replica set.

1. ```docker-compose-mongo up```
2. ```docker exec -it mongo1 bash```
3. ```mongo```
4. Initiate replica set:

```shell
config = {
    _id: "rp0",
    members: [
        { _id: 0, host: "mongo1:27017", priority : 2 },
        { _id: 1, host: "mongo2:27017", priority : 0 },
        { _id: 2, host: "mongo3:27017", priority : 0 },
    ],
}

rs.initiate(config)

rs.conf()
```

5. ```use chemistry```
6. ```db.atoms.insert({name:"Deuterium", mass: 2})```

## Setup Postgresql
There are a number of configuration parameters that need to be modified enable logical replication in Postgresql.
The easiest way is to use already configured docker image from debezium hub: ```debezium/postgres:14```

```docker-compose-postgres up```

## Setup MySql
MySQL has a binary log (binlog) that records all operations in the order in which they are committed to the database. 
This includes changes to table schemas and the data within tables. MySQL uses the binlog for replication and recovery.

No special settings for MySql database is needed, only data in application.properties
Run docker container with command:
```shell
docker run --name mysql-deuterium -e MYSQL_ROOT_PASSWORD=ForTheEmperor -v /home/milan/volumes/mysql-deuterium:/var/lib/mysql -p 3306:3306 -d mysql:8.0.30
```

---

Links:
* [Debezium examples](https://github.com/debezium/debezium-examples)
* [Redhat user guide](https://access.redhat.com/documentation/zh-cn/red_hat_integration/2020.q1/html/debezium_user_guide/debezium-connector-for-postgresql#overview)
* [Debezium docs](https://debezium.io/documentation/reference/stable/connectors/postgresql.html#setting-up-postgresql)



## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```
