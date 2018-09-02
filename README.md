# Planshboard

[![Build Status](https://travis-ci.com/grudus/Planshboard.svg?token=8nz31z85vuxVtzp5khqC&branch=master)](https://travis-ci.com/grudus/Planshboard)

## Development

1. Download project
```bash
 git clone git@github.com:grudus/Planshboard.git 
```

2. Create 2 PostgreSQL databases - `planshboard` and `planshboard_test`

3. Change some of app properties:
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
6. Additionally, you can fire setup.sh script to get some basic data
```bash
./src/main/resources/scripts/setup.sh
```

Voilà! Your app is working now.
