# 0. Table of Contents
**1. Introduction** ... 2

**1.1 Overview** ... 2

**1.2 Glossary** ... 2

**2. System Architecture** ... 2

**2.1 Client** ... 3

**2.2 Web server** ... 3

**2.3 Database** ... 4

**2.4 File system** ... 4

**3. High-Level Design** ... 4

**3.1 Context Diagram** ... 4

**3.2 Logical-Flow Diagram** ... 4

**3.3 Data Flow Diagram** ... 5

**3.4 Client-Server communications** ... 5

**3.5 Saving Answers** ... 7

**3.6 Offline Mode** ... 7

**3.7 Caching System** ... 7

**3.8 Managing Image Files** ... 8

**4. Problems and Resolutions** ... 12

**4.1 Activities vs Fragments** ... 12

**4.2 Implementing Fragments** ... 13

**4.3 HTTP Requests** ... 13

**4.4 Offline Mode** ... 14

**5. Installation Guide** ... 14

**6. Javadocs** ... 14

# 1. Introduction

## 1.1 Overview

We have developed an application on the Android platform which allows secondary school students to share their solutions to past exam paper questions. They can judge and critique others for their solutions, while simultaneously receiving feedback on their own submissions.

To use the app, students must first create an account, with which they can then sign in and freely browse the applications content. Students can vote solutions up or down, and gain reputation as a result. This reputation can be viewed as an indicator of the users contribution to the platform. Students may also comment on other solutions, allowing them to share their opinions and suggest improvements.

Although our application relies heavily on an active internet connection, students can save any solution they want for offline use with a single click. Of course, the majority of features available to the user will be disabled until internet access is restored.

Our application communicates with one external system - our remote server. This is essentially a linux web server running a MySQL database, with an API developed in PHP which encapsulates the underlying database and file management system.

## 2.2 Glossary
SQLite3 - Self-contained, serverless, zero-configuration, transactional SQL database engine
MySQL - Open-source relational database management system

# 2. System Architecture

![System Architecture Diagram](http://i.imgur.com/6aqkcMN.png)

Our system architecture has (for the most part) stayed true to the initial design as described in our function specification many months ago, as it has throughout our entire development process. 

#### Client

The client is an Android application running on the end users smartphone. The physical type of device is irrelevant, as the underlying framework remains the same. Our app is targeted at Android API level 25 (7.1 - Nougat) however we have taken steps to ensure our app is backwards-compatible and will work on Android API level 19+ (4.4 - KitKat).

#### Web Server

During the running of the application, the client will communicate with our remote server via HTTP requests. These requests will be received by a local web server (Apache v2.4.7) and handled by an API written in PHP (v7.0.16). The web server will in turn communicate with the database server and file storage as appropriate. All responses to the client will again be channeled through the web server and sent back to the client over HTTP.

Before each API call is made however, a ping is sent to Google's web servers to ensure the client has an active internet connection. Although our server has maintained an uptime of 100% (so far), no service is as fast and reliable as Google. Once the client receives a response from Google, it will continue on and make the API call to our server.

#### Database

In order to store personal user data, file names, solutions etc. we decided to use the MySQL relational database management system. We chose to use MySQL as it is free, open-source and it is the environment we were most familiar with, having learned basic SQL syntax in second year.

#### File System

Here we are not referring to a file system in the conventional sense (ie Fat32 etc.), but instead referring to the scripts which store and manipulate the image files which have been uploaded by students. These files are stored in a folder accessible only by the web server's user.

# 3. High-Level Design

### Context Diagram

This diagram gives a high level view of the system and shows how it interacts with its external entities. Based on our functional spec we have completed all the large functionality tasks that we set out to do from the beginning, and this diagram at this scope displays that.

![Context Diagram](http://i.imgur.com/37pL7qT.png)

### Logical-flow diagram

This logical-flow diagram describes how the flow of our application from a very high level. We can see how the user navigates throughout the app in order to access each feature.

![Logical flow diagram](http://i.imgur.com/dFeqZ13.png)

### Data Flow Diagram

The data flow diagram is more low level than the context diagram, so you can analyse exactly where the data flow begins and follow it right through until it reaches its end destination. The data flow always begins with the user when they log in either by creating an account or by using an account that has already been created. The data flow always ends at the database on our very own web server which stores and updates data, while giving the admin access to analyse and query the database in order to make updates and identify reported content.

![Data Flow Diagram](http://i.imgur.com/tZxrHhX.png)

### Client-server communication

When the client needs to use the API, it makes an API call over HTTP as we have seen before. We decided to encapsulate this "request" in an abstract Request class. To make the call then, we simply instantiate whichever child class of the Request class which suits our needs. Let's look at a specific example. Let's say we want to sign in to the application. Once we gather the user's username and password, we pass these to the LoginRequest constructor, which in turn makes a call to the appropriate static Server method.

![Request class diagram](http://i.imgur.com/vzjJcM9.png)

Server is a singleton class. It contains only static methods, and should not be instantiated. It is responsible for making the various HTTP requests to the server, returning the JSON response as a String.

![Server class diagram](http://i.imgur.com/Y3eSW5X.png)

We then use Google's GSON library to deserialize the JSON response into an actual Java object. To accommodate this, we developed a set of Response classes, which represent the data returned by the server. These classes contain information about the request (such as whether the request was successful or not), and of course the data returned.

![Response class diagram](http://i.imgur.com/Ze4zSeu.png)

### Saving answers

As per our project specification, users must able to save solutions for offline use. To achieve this, we realized we would have to replicate parts of our remote database on the users device. A full-blown relational database would be overkill in this case, and would cause serious performance issues for a device such as smartphone, therefore we went with SQLite3.

When the user clicks the save button, the solution's information is stored in this local database. Now, whenever the user navigates to the saved answers tab, solution data will be pulled from this local database instead of the remote database.

### Offline mode

When an API call is made but the client fails to recieve a ping response from Google's servers, offline mode is activated. This essentially disables any features within the application which require interent access, such as browsing for new answers. The user will still be able to navigate to their saved answers, and access all of the content which they have stored for offline use. Offline mode is turned off when any API call is made and internet access if found to have been restored.

### Caching system

While we were developing offline mode, we decided to build a caching system. The problem we identified was that solutions with images were being downloaded every time the solution was opened. With modern smartphones, these images could be easily upwards of 5MB, posing a problem to low end devices, or whenever the internet connection is slow. We figured that if a user is going to download an image once, they may as well keep it stored on their device in case they open that same solution again in the future. Therefore we modified our DisplayAnswerFragment so that before it attempts to download an image, it first checks the image hash against its local database. If there is a matching entry, we attempt to retrieve it from the local file system.

### Managing image files

**Taking a picture**
When the user attempts to take a photo with their device's camera, we make a request to the device's default camera application and allow it do manage the process. We then retrieve the image data and metadata from the application and process it ourselves.

**Selecting images from the gallery**
If a user prefers to select one or more images from their gallery, we make a similar request to the device's default gallery application. We read through this data and store it in an ArrayList for further processing. Android has quite a strict set of security features, and so in order to allow our application to process these files, we must first request permission and declare this permission throughout the processing.

**Uploading images**
Performing POST HTTP requests with the Android framework is tricky. Sending binary data is even trickier. To overcome this, we prepare each file for uploading by encoding each image into Base64 byte by byte. We then send the data over to our server as a String, one FileUploadRequest per file.

**Storing images**
This process is much the same on both client and server side. Firstly the received image is decoded from Base64 and compressed into a Bitmap. The server extracts the file's extension type and generates a unique hash, storing both in the files table. The image is then stored in a folder only accessible by the Apache user.

### MySQL Database

The MySQL database used to store data on the server is very much integral to our whole application. It stores everything from our users' personal details, to uploaded image information, to our application content. All of the data in our database is stored in first normal form, meaning we have minimal unneccessary data replication. See our database's data dictionary and entity relationship model below for an overview of our schema:

![Data dictionary comments to reports](http://i.imgur.com/mvrZShO.png)
![Data dictionary solutions to subjects](http://i.imgur.com/D6lCLud.png)
![Data dictionary users to votes](http://i.imgur.com/TGB8Yn3.png)

![Entity relationship diagram](http://i.imgur.com/ATTwaQW.png)

# 4. Problems and Resolutions

### Activities vs Fragments

Arguably the biggest and most painful challenge we faced was using Fragments over Activities. Here's some context to the problem: An Activity represents a single, focused action that the user can perform. Almost all Activities interact with the user, and are presented as full-screen UIs. These are very easy to implement:

```
Intent intent = new Intent(this, MyNextActivity.class);
startActivity(intent);
```

The problem with this is that over-using Activities where they aren't appropriate for simplicity's sake is poor design, and quite simply, an easy way out. If at any stage of your app there is a single UI component which remains on the screen throughout various Activities, then you should be using Fragments. A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity. In its core, it represents a particular operation or interface that is running within a larger Activity.

Just about every app that exists uses a navigation bar of some part either at the top or the bottom of the screen. You use this nav bar to jump between different areas of your app, and **it remains in-place regardless of where in the app you navigate to**.

This might seem like a subtle feature to have for the amount of effort it requires to maintain, however the end result is a far snappier and more professional looking app.

### Implementing Fragments

The main difficulties we had with fragments was trying to keep track of them. Generally using fragments means using android back stack. A string called a tag is placed onto the stack when a fragment is created, so you can access that instance of that fragment some time again in the future and find it in the state you left it in. For us the android stack wasn't compatible with our bottom navigation bar design so we decided to create our own. We have three stacks, one for every tab on the bottom navigation bar and a different tag for each stack, which is used to identify the fragments associated with that stack. A HashMap was used to map the tags to the stacks.

### HTTP requests

Android's support for HTTP requests is limited. Well, technically it's pretty much unlimited and unrestricted in that Android provides a very powerful, but unfortunately a very low-level API for making HTTP requests, with the HTTPURLConnection class. It works well for GET requests, but is notoriously difficult for dealing with POST requests (and even more so when sending binary data). We stumbled upon a small, lightweight HTTP wrapper client on Github developed by David Webb, which enables easier sending of data. This, combined with the Base64 encoding of image files, enabled us to communicate with our server smoothly.

### Offline mode

We encountered several problems when developing our saved answer system. Firstly, we realized that MySQL would not be appropriate in this area, so we had to research an alternative. MySQLite3 was the solution, but it came with its own troubles. We had no real way to administrate the database as it does not provide any form of management system. We could not even access the files on our test device as the file the database is stored in is accessible only with a root user, which we did not have access to. Our solution was to build an emulated device on our computer and manually open a shell as root through the emulator.

Initially, we didn't even plan for an "offline mode", however we quickly realized the problem with that approach when every single API call crashed the app because of lack of internet access. To further the problem, Android doesn't actually provide any built-in APIs whatsoever to check for an internet connection. Therefore we came up with the idea of pinging Google's web servers on each API call.

# 5. Installation Guide

### Requirements

- Android smarthpone, KitKat or higher (Android v4.4+)
- app_release.apk, located in project root folder

### Installation steps

1. Allow unknown sources
    - As our app is not on the Play store, you need to ensure your device can install apps from unknown sources
    - Navigate to Settings > Security (and fingerprint) > Unknown sources > ON
2. Download the app_release.apk file in our project's root folder
3. Locate the .apk file on your device and install by opening it

### Uninstalling the app

1. Navigate to Settings > Apps > Answer Box > Dropdown in top-right > Uninstall for all users

# 6. Javadocs

You can find a copy of our generated Javadocs at [http://api.cathal.xyz/javadocs](http://api.cathal.xyz/javadocs)