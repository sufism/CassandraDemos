/* Before you execute the program, Launch `cqlsh` and execute:
create keyspace example with replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
create table example.tweet(timeline text, id UUID, text text, PRIMARY KEY(id));
create index on example.tweet(timeline);
*/
package main

import (
	"fmt"
	"log"

	"github.com/gocql/gocql"
)


type AliLocationAwarePwdAuthenticator struct {
	Username string
	Password string
}

func (p AliLocationAwarePwdAuthenticator) Challenge(req []byte) ([]byte, gocql.Authenticator, error) {
	resp := make([]byte, 2+len(p.Username)+len(p.Password))
	resp[0] = 0
	copy(resp[1:], p.Username)
	resp[len(p.Username)+1] = 0
	copy(resp[2+len(p.Username):], p.Password)
	return resp, nil, nil
}

func (p AliLocationAwarePwdAuthenticator) Success(data []byte) error {
	return nil
}

func f(h *gocql.HostInfo) (gocql.Authenticator, error) {
	return AliLocationAwarePwdAuthenticator{Username:"cassandra@public",
		Password:"Demo123456"} ,nil
}

func main() {
	// connect to the cluster
	cluster := gocql.NewCluster("cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-002.cassandra.rds.aliyuncs.com",
		"cds-proxy-pub-2ze0l8v1dly1c1l4-1-core-003.cassandra.rds.aliyuncs.com")
	cluster.Keyspace = "system_auth"
	/*cluster.Authenticator = &gocql.PasswordAuthenticator{Username:"cassandra@public",
		Password:"Demo123456"}*/
	cluster.AuthProvider = f
	cluster.Consistency = gocql.Quorum
	session, err := cluster.CreateSession()
	if err != nil {
		log.Fatal(err)
	}
	defer session.Close()

	var version string
	// list all tweets
	iter := session.Query(`SELECT release_version FROM system.local`).Iter()
	for iter.Scan(&version) {
		fmt.Println(version)
	}
	if err := iter.Close(); err != nil {
		log.Fatal(err)
	}
}