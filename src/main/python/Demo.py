#!/usr/bin/env python
# -*- coding: UTF-8 -*-
import logging
import sys
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
logging.basicConfig(stream=sys.stdout, level=logging.INFO)
cluster = Cluster(
    # 此处填写数据库连接点地址（公网或者内网的），控制台有几个就填几个。
    # 实际上SDK最终只会连上第一个可连接的连接点并建立控制连接，填写多个是为了防止单个节点挂掉导致无法连接数据库。
    # 此处无需关心连接点的顺序，因为SDK内部会先打乱连接点顺序避免不同客户端的控制连接总是连一个点。
    # 千万不要把公网和内网的地址一起填入。
    contact_points=["cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-002.cassandra.rds.aliyuncs.com",
                    "cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-003.cassandra.rds.aliyuncs.com"],
    # 填写账户名密码（如果忘记可以在 帐号管理 处重置）
    auth_provider=PlainTextAuthProvider("cassandra@public", "Demo123456"))
# 果进行的是公网访问，需要在帐号名后面带上 @public 以切换至完全的公网链路。
# 否则无法在公网连上所有内部节点，会看到异常或者卡顿，影响本地开发调试。
# 后续会支持网络链路自动识别（即无需手动添加 @public）具体可以关注官网Changelog。
# auth_provider=PlainTextAuthProvider("cassandra@public", "123456"))
# 连接集群，会对每个Cassandra节点建立长连接池。
# 所以这个操作非常重，不能每个请求创建一个Session。合理的应该是每个进程预先创建若干个。
# 通常来说一个够用了，你也可以根据自己业务测试情况适当调整，比如把读写的Session分开管理等。
session = cluster.connect()
# 查询，此处我们查一下权限相关的表，看看我们创建了几个角色。
# 这是系统表，默认只有cassandra这个superuser账户有SELECT权限。
# 如果你使用其他帐号测试，可以换一个表或者授权一下。
rows = session.execute('SELECT release_version FROM system.local')
# 打印每行信息到控制台
for row in rows:
    print("# row: {}".format(row))
# 关闭Session
session.shutdown()
# 关闭Cluster
cluster.shutdown()