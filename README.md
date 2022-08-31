本项目采用neo4j作为数据源，实现树形结构数据的读取、CRUD操作  

这里选择neo4j 3.5.28版本，高版本需要使用jdk11

#初始化mysql中的数据到neo4j
这里只选取了关键字段，实际中可以全量导入  

导入csv文件里的数据到neo4j数据库中（默认从neo4j的安装目录下import中读取csv文件）

LOAD CSV WITH HEADERS FROM "file:///节点.csv" AS line   
MERGE (u:USER{uid:line.uid,nickname:line.nickname,active:line.active})  
或  
load csv with headers  
from 'file:///节点.csv' as line  
fieldterminator ','  
create (  
	u:User{  
    	uid: toInteger(line.uid),  
    	nickname: line.nickname,  
		active: toInteger(line.active)  
    }  
)  
貌似3.5.28老版本neo4jz只认后面一种导入语法，merge相对于create可以防止重复  

创建节点之间关系  
LOAD CSV FROM "file:///relationship.csv" AS line  
fieldterminator ','  
match (from:User{uid:toInteger(line[0])}),(to:User{uid:toInteger(line[1])})  
merge (from)-[r:son]->(to)  

#UserNeoService
操作neo4j的主要几个接口

