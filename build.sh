#!/usr/bin/env bash

dir=${PWD}

echo "building frontend..."
cd frontend
npm install
npm run build --localize
cp -R -v dist/de/* dist/ && rm -rf dist/de

echo "building backend..."
cd ../backend
mvn clean compile -DskipTests

echo "package frontend in backend..."
cp -R -v ../frontend/dist target/classes/static/

echo "install service..."
mvn install -DskipTests

#echo "docker build..."
#cd ..
#docker build --build-arg JAR_FILE=backend/target/*.jar --tag docker.lej.eis.network/exxeta/haukea/timesheet-approve:latest .

cd ${dir}