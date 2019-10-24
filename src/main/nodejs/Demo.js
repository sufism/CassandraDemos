const cassandra = require('cassandra-driver');
const client = new cassandra.Client({ contactPoints: ['cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-002.cassandra.rds.aliyuncs.com', 'cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-003.cassandra.rds.aliyuncs.com'],
localDataCenter: 'cn-beijing-h',credentials: { username: 'cassandra@public', password: 'Demo123456' } });

const query = 'SELECT release_version FROM system.local';
client.execute(query)
  .then(result => console.log(result.rows));