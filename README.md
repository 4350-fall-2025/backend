# Backend REST API Server

## Onboarding Instructions
1) Install Intellij IDEA
2) Install [Java 25](https://www.oracle.com/ca-en/java/technologies/downloads/#java25) on Oracle website or through Intellij
3) Install [Postman](https://www.postman.com/downloads/)
4) Clone this repository
    ```bash
    git clone <repository_url>
    ```
5) Open the project in Intellij
6) [Set up the project SDK](https://www.baeldung.com/intellij-change-java-version) to Java 25
7) Enable Annotation Processing through the menu in IntelliJ: 
*IntelliJ Menu -> Build, Execution, Deploymnet -> Compiler -> 
Annotation Processors -> Enable annotation processing*
8) Install [firebase CLI](https://firebase.google.com/docs/cli#install_the_firebase_cli)
9) Install/setup the firebase emulators by following these steps:

    Login to firebase google account (this will take you to browser to give firebase access, you will need to allow firebase to have access):
    1. Navigate to the correct directory in your terminal:
    ```bash
    cd backend/src/main/java/com/softeng/backend
    ```
    2. Login to firebase with your google account:
    ``` bash
    firebase login
    ```
    Initialize firebase locally: 
    ``` bash
    firebase init
    ```
   * Choose emulators for the service, then firestore as the emulator to install*
   * Install the emulator UI and emulators 
   * Note: you will need to either create your own firebase project or be given access to our project's instance.
    Then run the emulators in terminal:
    ``` bash
    cd backend/src/main/java/com/softeng/backend
    firebase emulators:start --import ../../../../../test/data 
    ```
10) Run the application by executing BackendApplication.java

*Note: If you want to save the data you've added/update for future use and for us to test with, 
run the following before you stop the firestore emulator. Otherwise, the changes will not be saved.*

```bash
cd backend/src/main/java/com/softeng/backend
firebase emulators:export ../../../../../test/data 
```