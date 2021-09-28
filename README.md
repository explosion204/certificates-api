Gift Certificates REST API

**Local deployment via Docker**
1. Required JDK version: ***16***
2. Clone the repository.
3. Build project: `./gradlew build`
4. Run application: `docker-compose up -d --build`.
5. Stop application:
    1) Preserve all data: `docker-compose down`.
    2) Hard reset: `docker-compose down -v`.