/**
 * Created by mazhenlin on 2019/10/23.
 */
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PlainTextAuthProvider;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

public class Demo {

  public static void main(String[] args) {
    // 此处填写数据库连接点地址（公网或者内网的），控制台有几个就填几个。
    // 实际上SDK最终只会连上第一个可连接的连接点并建立控制连接，填写多个是为了防止单个节点挂掉导致无法连接数据库。
    // 此处无需关心连接点的顺序，因为SDK内部会先打乱连接点顺序避免不同客户端的控制连接总是连一个点。
    // 千万不要把公网和内网的地址一起填入。
    String[] contactPoints = new String[]{
        "cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-002.cassandra.rds.aliyuncs.com",
        "cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-003.cassandra.rds.aliyuncs.com"
    };
    Cluster cluster = Cluster.builder()
        .addContactPoints(contactPoints)
        // 填写账户名密码（如果忘记可以在 帐号管理 处重置）
        .withAuthProvider(new PlainTextAuthProvider("cassandra@public", "Demo123456"))
        // 如果进行的是公网访问，需要在帐号名后面带上 @public 以切换至完全的公网链路。
        // 否则无法在公网连上所有内部节点，会看到异常或者卡顿，影响本地开发调试。
        // 后续会支持网络链路自动识别（即无需手动添加@public）具体可以关注官网Changelog。
        //.withAuthProvider(new PlainTextAuthProvider("cassandra@public", "123456"))
        .build();
    // 初始化集群，此时会建立控制连接（这步可忽略，建立Session时候会自动调用）
    cluster.init();
    // 连接集群，会对每个Cassandra节点建立长连接池。
    // 所以这个操作非常重，不能每个请求创建一个Session。合理的应该是每个进程预先创建若干个。
    // 通常来说一个够用了，你也可以根据自己业务测试情况适当调整，比如把读写的Session分开管理等。
    Session session = cluster.connect();
    //查询
    ResultSet res = session.execute("SELECT release_version FROM system.local");
    // ResultSet 实现了 Iterable 接口，我们直接将每行信息打印到控制台
    res.forEach(System.out::println);
    // 关闭Session
    session.close();
    // 关闭Cluster
    cluster.close();
  }

}
