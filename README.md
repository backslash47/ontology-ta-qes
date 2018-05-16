# Ontology Full name verification based on European QES Trusted anchor example

This is an example showing how to program a basic Trusted Anchor software. 

## Overview

Main objective of this example is to issue a Verifiable claim to an user, declaring his full name and citizenship based on European Qualified electronic signature (QES).

QES is compliant with EU Regulation No 910/2014 (eIDAS Regulation), which means that it is created with qualified certificate. Qualified certificate contains the name and address of the holder and therefore by creating QES on document with TA challenge embedded, one is proving his identity (full name and country from the qualified certificate).

Because this is only an example, the signing process is solely in the hands of the user (no signing applet/plugin is provided). User can use any viable means to create valid eIDAS conformant PADES signature. One of the possible way, how to create such a signature is to use http://dss.nowina.lu/signature .

Working copy of this TA is deployed to http://ontdetective.org.

## Process

* User will first submit a Request for Verifiable claim based on OEP-2 https://github.com/ontio/OEPs/blob/master/OEP-2/OEP-2.1.mediawiki

* Trusted anchor will generate a challenge pdf to the user for signing (the pdf contains signed JWT challenge)

* User will sign this pdf with his Qualified certificate (e.g.: estonian/slovak eID, ...)

* User will upload signed pdf to Trusted anchor

* Trusted anchor will issue and attest a Verifiable claim if received 

## Getting started

This TA example consist of two parts: 
* TypeScript TA part responsible for integration to Ontology network
* Java Server responsible for PDF generation and validation (so https://github.com/esig/dss can be used for validation against European Trusted list)

#### Install yarn
For faster building process and development experience install Yarn

```
npm install --global yarn
```

#### Install maven
Java server is build with maven

#### Download
```
git clone 'https://github.com/backslash47/ontology-ta-qes.git'
```

#### Build and start development server
````
yarn build
yarn start
````

#### Build and start Java server
````
mvn package
java -jar target/bundle.jar
````

## Built With

* [TypeScript](https://www.typescriptlang.org/) - Used language
* [Node.js](https://nodejs.org) - JavaScript runtime for building and ingest
* [Ontology TypeScript SDK](https://github.com/ontio/ontology-ts-sdk) - The framework used
* [Spring Boot](https://projects.spring.io/spring-boot/) - The framework used

## Authors

* **Matus Zamborsky** - *Initial work* - [Backslash47](https://github.com/backslash47)

## License

This project is licensed under the LGPL License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

Many thanks to the whole Ontology team, who done a great job bringing Ontology to life.
