# Publish message

### Topic - user (via kafkacat)

* `echo '{"id": "'"$(uuidgen)"'", "type": "UserCreated", "userId": "1", "userName": "Michal Sadel", "occurrenceTime": "'"$(date -u --iso-8601=seconds)"'"}' | kafkacat -P -b localhost -t user`
* `echo '{"id": "'"$(uuidgen)"'", "type": "UserDeleted", "userId": "1", "occurrenceTime": "'"$(date -u --iso-8601=seconds)"'"}' | kafkacat -P -b localhost -t user`

### Topic - charm (via kafkacat)

* `echo '{"id": "'"$(uuidgen)"'", "type": "CharmReceived", "userId": "1", "occurrenceTime": "'"$(date -u --iso-8601=seconds)"'"}' | kafkacat -P -b localhost -t user`

### Topics - event type dependent (via rest controller)

* `echo '{"id": "'"$(uuidgen)"'", "type": "CharmReceived", "userId": "1", "occurrenceTime": "'"$(date -u --iso-8601=seconds)"'"}' | http :8080/publish`
* `echo '{"id": "'"$(uuidgen)"'", "type": "UserCreated", "userId": "1", "userName": "Michal Sadel", "occurrenceTime": "'"$(date -u --iso-8601=seconds)"'"}' | http :8080/publish`
* `echo '{"id": "'"$(uuidgen)"'", "type": "UserDeleted", "userId": "1", "occurrenceTime": "'"$(date -u --iso-8601=seconds)"'"}' | http :8080/publish`

# Controllers

### Charms

* `http -b :8080/charms`
* `http -b :8080/charms/{userId}`

### Events

* `http --stream :8080/events`

# Cluster

There are 3 zookeepers and 3 brokers. Log dir are mounted as tmpfs, after restart cluster is clear.

`docker-compose -f docker-compose-kafka-cluster.yml up`