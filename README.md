# TravellingApp
How to install?
Create a file docker-compose.yml
Copy this into it:
```
version: '3'
services:
  data-engine-service:
    image: 23051999/travellingapp
    ports:
      - "8083:8083"
    links:
      - kafka
  zookeeper:
    image: zookeeper:3.7.0
    ports:
      - "2181:2181"
  kafka:
    image: wurstmeister/kafka
    ports:
      - "9092:9092"
    links:
      - zookeeper
    environment:
      KAFKA_ADVERTISED_HOST_NAME: kafka
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_CREATE_TOPICS: "GeoNodes:1:1,rawPlaces:1:1"
  frontend:
    image: 23051999/travelersfrontend
    ports:
      - "3000:3000"

```

Run Docker
In a terminal, in the folder where you created docker-compose.yml, execute the command: 'docker compose up'.
The website should be accesssible through port 3000. 
