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

By default `SPankkiSettings` class is imported and `ApiClient` is initialized with `"SPankki"` in the connector name parameter. If you wish to connect to another bank import another settings class and change connector name in ApiClient constructor call.

### SPankki

The settings are located inside [src/main/java/com/eb/demo/banks/SPankkiSettings.java](src/main/java/com/eb/demo/banks/SPankkiSettings.java) file. You need to put your own client id and X-API-Key values there. By default certificates are located in [src/main/resources/SPankki](src/main/resources/SPankki) folder; you should overwrite files there with your own.

If you wish to sign up for S-Pankki sandbox account go to [https://www.s-pankki.fi/fi/yhtiot/open-banking/](https://www.s-pankki.fi/fi/yhtiot/open-banking/).