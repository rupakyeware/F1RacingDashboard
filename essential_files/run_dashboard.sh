#!/bin/bash

# Check if MySQL JDBC driver exists, download if not
if [ ! -f "lib/mysql-connector-j.jar" ]; then
    echo "MySQL JDBC driver not found, downloading..."
    
    # Create lib directory if it doesn't exist
    mkdir -p lib
    
    # Download MySQL JDBC driver
    curl -L https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.tar.gz -o mysql-connector.tar.gz
    
    # Extract the driver
    tar -xzf mysql-connector.tar.gz
    
    # Copy the driver to the lib directory
    cp mysql-connector-*/mysql-connector-j-*.jar lib/mysql-connector-j.jar
    
    # Clean up
    rm -rf mysql-connector-j-*
    rm mysql-connector.tar.gz
    
    echo "MySQL JDBC driver downloaded successfully."
fi

# Make sure we have the JSON library
if [ ! -f "lib/json-20231013.jar" ]; then
    echo "JSON library not found, downloading..."
    mkdir -p lib
    curl -L https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar -o lib/json-20231013.jar
    echo "JSON library downloaded successfully."
fi

# Compile all Java files with MySQL JDBC driver in classpath
echo "Compiling Java files..."
javac -cp .:lib/mysql-connector-j.jar:lib/json-20231013.jar *.java

# Run the application with the MySQL JDBC driver in classpath
echo "Running F1 Dashboard application..."
java -cp .:lib/mysql-connector-j.jar:lib/json-20231013.jar F1SwingDashboardApp