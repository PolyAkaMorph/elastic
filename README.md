# elastic
run elastic on 9200

```
docker run -d --name elastic-test -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" docker.elastic.co/elasticsearch/elasticsearch:8.8.2
```

Run application and verify it is working with curl
```
#create index abc
curl -v -X POST localhost:8080/index/abc

#upload document, ID will be in response
curl -v -X POST localhost:8080/document/abc -d '{"ad": "absda"}' -H "Content-Type:application/json"

#download document with ID from previous step
curl -v -X GET localhost:8080/document/abc/cdtfO4wBxVdr0j0BV_wx
```
