# Backend REST API Server

## Onboarding Instructions
1) Install Intellij IDEA
2) Install [Java 25](https://www.oracle.com/ca-en/java/technologies/downloads/#java25) on Oracle website or through Intellij
3) Install [Postman](https://www.postman.com/downloads/)
4) Clone this repository
    ```bash
    git clone git@github.com:4350-fall-2025/backend.git
    ```
5) Open the project in Intellij
6) [Set up the project SDK](https://www.baeldung.com/intellij-change-java-version) to Java 25
7) Enable Annotation Processing through the menu in IntelliJ: 
*IntelliJ Menu -> Build, Execution, Deployment -> Compiler -> 
Annotation Processors -> Enable annotation processing*
8) Install firebase CLI
    ```bash
    curl -sL firebase.tools | bash
    ```
   Other download options available on the [firebase website](https://firebase.google.com/docs/cli?authuser=2#install_the_firebase_cli).

9) Set up Firestore authentication (to access hosted database):

   1) For graders/Lauren, you will be provided a new auth file as communicated via email each sprint. Please note that due to security concerns, we require firebase emulators in order to use storage features, such as uploading images. Refer to step 10 to continue setup.
   2) For devs: 

      1) Ask Victoria to be added to the Firebase project, send her the gmail account that you wish to use.

      2) Navigate to project settings icon on the top left, click on Project Settings 
         <img width="427" height="239" alt="projectsettings" src="https://github.com/user-attachments/assets/0f25b607-cb43-4160-9885-bd36a5afef7f" />
 
      3) Click on Service Accounts tab
         <img width="750" height="145" alt="serviceaccounts" src="https://github.com/user-attachments/assets/7598102a-2bec-40e1-8474-59c69dea1627" />

      4) Click "Generate new private key" - this will download a private key for you to use.

      5) Rename the file to "firebase-admin.json". If you're using a prod environment, set "firebase-prod.json"

10) Move/Copy the "firebase-admin.json" file to the following directory in the project:
         
    ```
        backend/src/main/resources
    ```

11) Run the BackendApplication. See instructions below for setting up the active profile and running the app on different environments.

## Running the App Locally

### Setting the Active Profile/Environment

In IntelliJ, Click the "Run->Edit Configurations" and in the Run/Debug Configurations, under "Active profiles", enter 1 of 3 profiles depending on your needs (`emulator`, `firebase`, or `prod`):

https://github.com/user-attachments/assets/fc647e81-b94e-4f0f-aff7-96be912b8445

If you do not see a section for profiles in edit configurations as outlined above, you can:

Enable "Modify Options -> Active profiles" as seen below: 

https://github.com/user-attachments/assets/54f69f0e-ac77-4339-8987-cf339f88b191

Or enable "Modify Options -> VM Options" and manually enter: `-Dspring.profiles.active=<profile_name>`

`firebase` profile:
- For using hosted database (Not recommended for running tests or profiling)
- Does not have auth or storage set up (you can't upload images)

`emulator` profile:
- With this profile you must run the emulator locally, this is used for local tests and profiling
- Also has auth/storage emulator, so you CAN upload images and other files
- Follow the instructions below for setting up and running the emulators

`prod` profile:
- A real Firestore database (not recommended for development or testing)
- Has auth and storage set up (you can upload images)
- Follow instructions below for setting up environment variables

### Environment Variables
Click on the "Run" menu in IntelliJ: Run->Edit Configurations

You can enter environment variables in the "Environment variables" section, which may need to be enabled in the "Modify Options" menu.

If using `firebase` or `prod` profile, you will need to set the following environment variables to access projects:
- `GOOGLE_APPLICATION_CREDENTIALS=classpath:<key>` pointing to the service account key you received in step 9.
- `GOOGLE_CLOUD_DATABASE=<database_id>` set to the Firestore database ID

Prod specifically also requires an additional variable:
- `GOOGLE_CLOUD_PROJECT` set to the name of the project
An .env file can be sent to graders for ease of copy/paste.

If using `emulator` profile, you will need to set the following variables for auth & storage:
- `FIREBASE_AUTH_EMULATOR_HOST=localhost:9099`
- `FIREBASE_STORAGE_EMULATOR_HOST=localhost:9199`

#### Run & install the emulators

```bash
firebase emulators:start --only firestore,auth,storage --project qdog-6aca2
```

*Note:* before you STOP the emulators, save test data by running in a different terminal:
```bash
cd src/main/java/com/softeng/backend
firebase emulators:export ../../../../../test/data
```
You can run the emulators with `--import ../../../../../test/data` to import the data into the emulators.

Run the application by running the emulators (step 12), and then executing BackendApplication.java by clicking the "Run Appplication" button in IntelliJ:
<img width="482" height="77" alt="build-run" src="https://github.com/user-attachments/assets/dbaad99a-ee1b-4703-aac5-a25df9590b5d" />

## Sources

Some portions of this project, particularly containerization and connecting to Firebase, were informed by OpenAI’s ChatGPT (GPT-5) and Anthropic Claude 4.5.

Inline comments indicate where AI guidance was referenced or code was used. 
For reference, generative AI models can be found at: 
ChatGPT - https://chat.openai.com
Claude - https://claude.ai/
