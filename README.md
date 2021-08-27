# Server
##### A socket based server for two flutter apps; *Savvy Meals!*
---

### Description
This is one repository of the four repositories created for the final project of the **Advanced Programming course**, and contains the java source code for the **socket based** server app. To run the app you can download the java archive file [here](https://github.com/MeysamBavi/ap-project-server/releases/download/v1.0.0/Server.jar) and run the commands 

```
java -jar Server.jar 8081 .
```

or clone the repository and run these commands in src folder:

```
javac Main.java
java Main 8081 .
```

**NOTE:** if you clone the repository, you need to add [gson](https://github.com/MeysamBavi/ap-project-server/blob/main/lib/gson-2.8.7.jar) to classpath, because the project depends on it.

#### CLI arguments

The first CLI argument is the port number server will start listening to and the second one is an existing or a new database directory. Optionally you can add a third argument which determines the number of threads used for processing.

---

### How it works

Using a command-based API, the flutter apps send their request to the server and the server responds back. The transferred data is usually JSON strings of the [model objects](https://github.com/MeysamBavi/ap-project-models). If needed, these JSON strings will be saved and written to a corresponding file in database. Updated JSON strings are usually created by client and are saved as they have been received.

#### Database

Depending on their type, these JSON files are stored in a directory and are named via a unique, permanent ID.

#### Lock system

To prevent different thread from writing simultaneously on a file, or reading from a file that is being written to, each thread has to acquire the lock of the file. These locks are java Semaphore objects, that are stored in a hashmap and their key is the absolute path of the file.

---

### Other repositories
You can visit the repositories of the flutter apps here:  
[Owner app (Savvy Meals - Managers)](https://github.com/sinatb/ap_project_RESTAURANT)  
[User app (Savvy Meals)](https://github.com/sinatb/ap_project_USER)  
and the project's object models here:  
[Models](https://github.com/MeysamBavi/ap-project-models)  
  
---

### About us
We are two CE students from SBU!  
[Meysam Bavi](https://github.com/MeysamBavi)  
[Sina Taheri](https://github.com/sinatb)  
