@echo off

@echo building frontend...
cd frontend
call npm install
call npm run build:webcomponent

@echo building backend...
cd ..\backend
call mvn clean compile

@echo package frontend in backend...
xcopy /E /I /Y ..\frontend\dist target\classes\static

@echo install service...
call mvn install

@echo docker build...
cd ..
docker build --build-arg JAR_FILE=backend/target/*.jar --tag haukea/timesheet-approve .