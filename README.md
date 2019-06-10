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
   mvn exec:java -Dexec.mainClass="com.eb.demo.EnableBankingDemo"
   ```

## Settings

TBD
