# Quick Start

## Create a Kafka server and create a topic named Messages
## Create a client.properties file with the following: 

bootstrap.servers=
security.protocol=
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username='' password='';
sasl.mechanism=PLAIN

# Required for correctness in Apache Kafka clients prior to 2.6
client.dns.lookup=use_all_dns_ips

# Best practice for higher availability in Apache Kafka clients prior to 3.0
session.timeout.ms=45000

# Best practice for Kafka producer to prevent data loss
acks=all

# Start multiple sessions of UserInterface

# You have now created a real time chat room using Kafka
