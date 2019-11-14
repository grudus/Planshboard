# Planshboard

![Build Status](https://travis-ci.org/grudus/Planshboard.svg?branch=master)

## Development

1. Download project
```bash
 git clone git@github.com:grudus/Planshboard.git 
```

2. Create database in docker container. From root directory run:
```bash
docker-compose up -d
```

Alternatively - if you don't want to use `Docker` - you can create database manually:
 
* Use your custom PostgreSQL client
* Create 2 PostgreSQL databases - `planshboard` and `planshboard_test`
* If you use different credentials than configured in the `docker-compose.yml`, change some of the app properties:
    * Configure build - in `pom.xml` change `db.url`, `db.username` and `db.password` 
    * Configure app - in `src/main/resources/application.properties` change `spring.datasource.url`, `spring.datasource.username` and `spring.datasource.password` 
    * Configure tests - in `src/test/resources/test.properties` change `spring.datasource.url`, `spring.datasource.username` and `spring.datasource.password` 

4. Go to downloaded directory and install maven project 
```bash
mvn clean install
```

5. Run it via your IDE or from command line
```bash
mvn spring-boot:run
```
6. Additionally, you can fire setup.sh script to get some initial data locally
```bash
./scripts/setup.sh
```

Voilà! Your app is working now.
