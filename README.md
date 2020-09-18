# Timesheet Approve Service 
## Getting Started
### Backend:
Im timesheet-approve Verzeichnis **mvn clean install** und dann unter src/main/java TimesheetApproveServiceApplication ausführen.
Auf `localhost:8080/api/checklist` können die Daten eingesehen werden.

### Frontend:
Ins timesheet-approve/timesheet-approve-service Directory wechseln und da **npm install** ausführen.
Anschließend kann im gleichen Verzeichnis mit **ng serve** die Oberfläche geladen werden.

## Oberfläche
Mit Download Excel wird das Timesheet für den jeweiligen Studierenden heruntergeladen. 
Dazu muss man sich in Jira anmelden. Es wird immer das Timesheet vom letzten Monat heruntergeladen.

![](./Images/TimesheetApproveService%20Frontend.png)



