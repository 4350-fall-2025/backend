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
8) Install firebase CLI
    ```bash
    curl -sL firebase.tools | bash
    ```
   Other download options available on the [firebase website](https://firebase.google.com/docs/cli?authuser=2#install_the_firebase_cli) 

9) Set up Firestore authentication (to accesss hosted database):

   a) For graders/Lauren, you will be provided the auth file as communicated. Please note that due to security concerns, we require firebase emulators in order to use storage features, such as uploading images. Refer to step 10 to continue setup. 

   b) For devs: 

   >>  i) Ask Victoria to be added to the Firebase project, send her the gmail account that you wish to use.

   >> ii) Navigate to project settings icon on the top left, click on Project Settings
   >> <img width="427" height="239" alt="projectsettings" src="https://github.com/user-attachments/assets/0f25b607-cb43-4160-9885-bd36a5afef7f" />

      
   >>  iii) Click on Service Accounts tab
   >> <img width="750" height="145" alt="serviceaccounts" src="https://github.com/user-attachments/assets/7598102a-2bec-40e1-8474-59c69dea1627" />


   >>   iv) Click "Generate new private key" - this will download a private key for you to use.

   >>   v) Rename the file to "firebase-admin.json"

10) Move/Copy the "firebase-admin.json" file to the following directory in the project:
         
```
    backend/src/main/resources
```

11) Set the active profile for BackendApplication. In IntelliJ, Click the "Run->Edit Configurations" and in the Run/Debug Configurations, under "Active profiles", enter 1 of 2 profiles depending on your needs (`firebase`, `emulator`):

https://github.com/user-attachments/assets/fc647e81-b94e-4f0f-aff7-96be912b8445


Note*: If you do not see a section for profiles in edit configurations as outlined above, you just need to click "Modify Options->Active profiles" as seen below:


https://github.com/user-attachments/assets/54f69f0e-ac77-4339-8987-cf339f88b191

`firebase` profile:

- For using hosted database (Not recommended for running tests or profiling)
- Does not have auth or storage set up (you can't upload images)

`emulator` profile:
- With this profile you must run the emulator locally, this is used for local tests and profiling
- Also has auth/storage emulator, so you CAN upload images and other files.
- Follow the instructions below for setting up and running the emulators.

> Set up Environment variables if you want to use emulators for auth & storage:
> 
> Click on the "Run" menu in IntelliJ: Run->Edit Configurations
> For environment variables:
> 
> - FIREBASE_AUTH_EMULATOR_HOST=localhost:9099
> - FIREBASE_STORAGE_EMULATOR_HOST=localhost:9199
>

To run the emulators:

```bash
firebase emulators:start --only firestore,auth,storage --project qdog-6aca2
```

*Note:* before you STOP the emulators, save test data by running in a different terminal:
```bash
cd src/main/java/com/softeng/backend
firebase emulators:export ../../../../../test/data
```
You can run the emulators with `--import ../../../../../test/data` to import the data into the emulators.

11) Run the application by executing BackendApplication.java by clicking the "Run Appplication" button in IntelliJ:
    <img width="482" height="77" alt="build-run" src="https://github.com/user-attachments/assets/dbaad99a-ee1b-4703-aac5-a25df9590b5d" />

# Sources

Some portions of this project, specifically guidance on implementing Firestore CRUD operations for Owner objects in Spring Boot, were informed by OpenAI’s ChatGPT (GPT-5) on multiple occasions for each sprint.

Inline comments indicate where AI guidance was referenced or code was used. 
For reference, OpenAI ChatGPT is available at: https://chat.openai.com 