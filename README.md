# BankApp-Java
Project created using Intellij IDEA, MySQL Workbench 8.0 CE, Apache Tomcat
1. Create project using spring initializer, check maven, packaging Jar, java 17.
2. Copy pom.xml file from repository and paste it to created project.
3. Download dependecies then copy files from src/java/mvcsecuritytest and paste it to where your main class exists, in my case src/main/java/com.example.demo
4. Copy resources file and paste it to your resources file, change db connection to match yours in application.properties.
5. Create db from MySQL create statement in repository.
6. Create account on https://exchangeratesapi.io/ to get your api key and paste it in CurrencyService.java at the accessKey field.
7. Run application and check localhost in your browser.
