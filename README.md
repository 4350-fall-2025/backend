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
8) Set up Firestore authentication:

   a) For graders/Lauren, you will be provided the auth file as communicated. Refer to step 9

   b) For devs: 

   >>  i) Ask Victoria to be added to the Firebase project, send her the gmail account that you wish to use.

   >> ii) Navigate to project settings icon on the top left, click on Project Settings
      
   >>  iii) Click on Service Accounts tab

   >>   iv) Click "Generate new private key" - this will download a private key for you to use.

   >>   v) Rename the file to "firebase-admin.json"

9) Move/Copy the "firebase-admin.json" file to the following directory in the project:
         
         ```
         backend/src/main/resources
         ```

10) Update or create environment properties file, with filename ".properties" in ```src/main/resources```. If you don't have a .properties file, create one in the aforementioned directory.

   *Add these environment variables*:

   - GOOGLE_APPLICATION_CREDENTIALS=the absolute path to the firebase-admin.json file

   - APP_ENV=local (only option currently)
   
11) Run the application by executing BackendApplication.java by clicking the "Run Appplication" button in IntelliJ.