<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.drakkarpress</groupId>
    <artifactId>drakkarpress-platform</artifactId>
    <version>2.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>DrakkarPress Platform</name>
    <description>Plataforma para la generación y gestión de contenido literario.</description>
    <url>http://www.drakkarpress.com</url>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <spring.boot.version>3.5.3</spring.boot.version>
        <spring.cloud.version>2024.0.0</spring.cloud.version>
    </properties>

    <dependencies>
        <!-- Dependencias del proyecto -->
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring.boot.version}</version>
                <configuration>
                    <mainClass>com.drakkarpress.DrakkarPressApplication</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
