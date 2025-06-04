#!/bin/bash

# Create lib directory if it doesn't exist
mkdir -p lib

# Download MySQL JDBC driver
echo "Downloading MySQL JDBC driver..."
curl -L https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-j-8.0.33.tar.gz -o mysql-connector.tar.gz

# Extract the driver
echo "Extracting MySQL JDBC driver..."
tar -xzf mysql-connector.tar.gz

# Copy the driver to the lib directory
echo "Copying MySQL JDBC driver to lib directory..."
cp mysql-connector-*/mysql-connector-j-*.jar lib/mysql-connector-j.jar

# Clean up
echo "Cleaning up..."
rm -rf mysql-connector-j-*
rm mysql-connector.tar.gz

echo "MySQL JDBC driver downloaded successfully to lib/mysql-connector-j.jar"