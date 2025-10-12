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
   >> <img width="427" height="239" alt="projectsettings" src="https://github.com/user-attachments/assets/0f25b607-cb43-4160-9885-bd36a5afef7f" />

      
   >>  iii) Click on Service Accounts tab
   >> <img width="750" height="145" alt="serviceaccounts" src="https://github.com/user-attachments/assets/7598102a-2bec-40e1-8474-59c69dea1627" />


   >>   iv) Click "Generate new private key" - this will download a private key for you to use.

   >>   v) Rename the file to "firebase-admin.json"

9) Move/Copy the "firebase-admin.json" file to the following directory in the project:
         
         ```
         backend/src/main/resources
         ```

10) In IntelliJ, Click the "Run->Edit Configurations" and in the Run/Debug Configurations, under "Environment Variables" enter the following environment variable:

GOOGLE_APPLICATION_CREDENTIALS=*enter your absolute path to the firebase-admin.json file here*

https://github.com/user-attachments/assets/b3f54cc6-95ac-450b-8c32-04437eeebb50

Then click "Apply", then "Ok".

11) Run the application by executing BackendApplication.java by clicking the "Run Appplication" button in IntelliJ:
    <img width="482" height="77" alt="build-run" src="https://github.com/user-attachments/assets/dbaad99a-ee1b-4703-aac5-a25df9590b5d" />


# Sources

Some portions of this project, specifically guidance on implementing Firestore CRUD operations for Owner objects in Spring Boot, were informed by OpenAI’s ChatGPT (GPT-5) on multiple occasions between October 10 and October 12 2025.

Inline comments indicate where AI guidance was referenced or code was used. 
For reference, OpenAI ChatGPT is available at: https://chat.openai.com 
