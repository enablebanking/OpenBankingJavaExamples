# Open Banking Java Examples

This repository contains sample Java code showing how to use Open banking APIs (PSD2 AISP &amp; PISP) using enable:Banking Java library.

## Quickstart

In order to compile and run the example code you need to have Java compiler and Maven toolset installed, as well as enablebanking.jar available in CLASSPATH.

It is also necessary to insert actual API credentials into Java files where they are supposed to be. Please check [settings](#settings) section for more information.

To run examples from your command line:

1. Compile source code

   ```
   mvn clean compile
   ```

2. Run compiled class

   ```
   mvn exec:java -Dexec.mainClass="com.eb.demo.EnableBankingDemo" -Dexec.cleanupDaemonThreads=false
   ```

## Settings

Sample application contains several files with settings for connecting to different banks.

By default `SPankkiSettings` class is imported and `ApiClient` is initialized with `"SPankki"` in the connector name parameter. If you wish to connect to another bank import another settings class (or define your own) and change connector name in `ApiClient` constructor call.

The classes containing connection settings for different banks are located inside [src/main/java/com/eb/demo/banks](src/main/java/com/eb/demo/banks) folder. By default certificates and possibly other assets required for connecting to banks are located in [src/main/resources](src/main/resources) folder; you should put your own files there.

This repository includes settings templates for the following banks:

- S-Pankki, Finland
  - Connector name: **SPankki**
  - Settings: [src/main/java/com/eb/demo/banks/SPankkiSettings.java](src/main/java/com/eb/demo/banks/SPankkiSettings.java)
  - Assets: [src/main/resources/SPankki](src/main/resources/SPankki)
  - Open banking homepage: [https://www.s-pankki.fi/fi/yhtiot/open-banking/](https://www.s-pankki.fi/fi/yhtiot/open-banking/)
- LHV Pank, Estonia
  - Connector name: **LHV**
  - Settings: [src/main/java/com/eb/demo/banks/LHVSettings.java](src/main/java/com/eb/demo/banks/LHVSettings.java)
  - Assets: [src/main/resources/LHV](src/main/resources/LHV)
  - Open banking homepage: [https://www.lhv.ee/en/open-banking](https://www.lhv.ee/en/open-banking)