<br />
<p align="center">
  <h1 align="center">Asteroids</h1>

  <p align="center">
    The retro game is back with a brand new ... main menu!
  </p>
</p>

## Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Design Description](#design-description)
  * [View](#view)
  * [Model](#model)
  * [Controller](#controller)
  * [Game_Observer](#game_observer)
  * [Util](#util)
* [Evaluation](#evaluation)
* [Teamwork](#teamwork)
* [Extras](#extras)

## About The Project

This projects aims to recreate the classic "Asteroids" arcade game, whose objective is to shoot as many asteroids as possible without the player crashing his ship , while also adding a multiplayer aspect to it besides it's regular single-player features.

### Built With

* [Maven](https://maven.apache.org/)
* [MySQL](https://www.mysql.com/)
* [JDBC connector](https://dev.mysql.com/downloads/connector/j/)

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

The latest versions of the following:

* Java
* Maven 
* MySQL 
* JDBC connector

### Installation

1. Navigate to the Asteroids folder
2. Clean and build the project using:
```sh
mvn install
```
3. Run the `Main` method of Asteroids using:
```sh
mvn exec:java
```
4. Alternatively you can run the `main` method in `Asteroids.java` using an IDE of your choice (e.g. IntelliJ)

Should you want to run this program standalone, you can create a JAR file with the following maven command:

```sh
mvn clean package
```
The JAR file will appear in the `/target` directory.

If you are going to run the `main` method in `Asteroids.java` using Intellij IDE , then we recommend you do the following:

1. Download the `mysql-connector-java-8.0.22.zip` file using the provided hyperlink , then unzip the file .
2. Open up the project in Intellij and then click on the `File` button and then select `Project Structure` .
3. A new tab will open where in `Project Settings` you will find and click on `Module` .
4. Once you have done that , you will search for the `Dependencies` button and press it .
5. From there look for the `+` button on the right of the `Scope` panel and click it .
6. A small menu will appear and you shall look for the `JAR files and other directories` option .
7. That will open another menu from where you will select the `mysql-connector-java-8.0.22.jar` option .

Once you have done all of the above and exit the `Project Structure` tab , you should see the jar file in the `External libraries` folder . 


## Design Description
The project as a whole follows the MVC design pattern with some deviations in order to accommodate the different features of the game. Anything related to the game's model is found in the model package, view things such as Swing UI components are found in the view package, and you'll find controllers in the control package. The project also employs an Observer pattern so that when a game changes state, all registered observers are notified and updated automatically.

### View
When starting the game, an AsteroidsFrame is created, and it contains an AsteroidsPanel . This panel is responsible for drawing all the objects in the game each time the screen refreshes. However, the panel itself doesn't contain the code that draws each object. Instead, you'll find that in the view.view_models package. When the panel wants to draw the spaceship, for example, it will construct a new view model for the game's spaceship and call that view model's drawObject() method. Similarly, the panel draws other objects if necesarry, or the main menu if the game has such flags.

### Model
The main model that contains all of the information about the state of game is the Game class found in the model.game package. This model consists of a Spaceship, Bullet, Asteroid are inherited from GameObject abstract class that provides some basic attributes that all objects in the game should have, like position, velocity and determining whether or not a collision occured or objects have been destroyed or not. Meanwhile the model.online package contains all the information relating to the functionality of the multiplayer aspect of the game by utilizing UDP networking, as such every tick the host sends the game represented as an array of bytes to the Client who then sets it's game based on it. Then the Client send it's move to the server and the server updates the position of the Client's spaceship based on that. Class GameServer groups the game and the online functionality.

Regarding the topic of UDP uncertanties, we tackled the problem of packets not arriving in the correct order, we made the game accept only those packets that are newer than itself. We achieved by allocating an ID for each packet corresponding to the ID of the game tick. The other problem was not knowing if the initial packet arrived. We solved this by simulating hand-shake for the initial packet. If the hand shake is not completed, a new initial packet is send. For the problem of not knowing when the client has disconnected, we made it so that the host needs to get resonse at least every X ticks, otherwise it stops sending packets to the client and the client needs to establish the connection again.

An important class in the model.game package is ByteModel, which transforms all relevant aspects of model into a byte array so that it can be in the paket. This was done to cut down the size of an packets as much as possible. Bytemodel stores an array of bytes, to which objects can be added. There are 2 pointers read and write, which point to the position form which read and write of the byte array should start. In this way all relevant aspects of the game can be stored as byte array, from which the game can be reconstruted by the client.

### Controller
The controller responds to the user input and performs interactions on the data model objects. The controller receives the input, optionally validates it and then passes the input to the model. With this in mind the GameUpdater class which is a runnable object which, when started, runs the main game loop and periodically updates the game's model as time goes on can be considered the "Game Engine" since it is solely responsible for all changes made to the game model as a result of user input which is interpreted through the PayerKeyListener class. Another important aspect of the controller is the controller.menu package that handles main menu and menu bar. The actions of main menu and menu bar are implemented as command pattern. Each commands executes different action: starting a singleplayer game , joining a multiplayer game, hosting a multiplayer session, spectating a multiplayer game, viewing the highscores, quiting a game or returning the user to the main menu. All of these commands have their functionality defined in individual Java class stored in the controller.menu.menu_commands package. The complete command pattern consists of this package, MenuCommandHandler which maps commands to their titles, MenuItem which is an enum of menu items, MenuItemAction which executes the commands, and command pattern interface MenuItemCommand. In this control.menu package there is MenuMouseController as well. Thi class calls menu actions, if the user clicked on some menu item in the main menu. 

### Game_Observer 
The observer pattern utilized for the game can be found in the game_observer package which contains the GameUpdateListener which ensures that all classes that implement this interface should be notified when a game is updated, meanwhile the ObservableGame class indicates that an observable game is an object that game update listeners can register to, so that when the game updates, they will be able to react to it.

### Util 
In this package there are the classes are some utility classes: Pair groups 2 different generic objects into a pair and PolarCoordinate enables view to draw spaceship in the proper direction. In this package there is ScoreDB class as well, which is class for Database on using MySQL.

## Evaluation
The biggest strength of our project is main menu and menu bar. The code is clear, the view looks very pretty and its functionality works really good as well. On the other hand, we have not been able to implement several thing in a way we would like. Persistent highscore database is not integrated into the model due to technical issues with utilizing an MySQL server which seemingly does not allow for remote connection of other users to the database despite any attempts of granting server privileges, however, the classes for the database are still included in the package database. 
If we were to start agian, we would deal with multiple spaceships in a different way. In our current implementation game stores spaceships as ArrayList of all spaceships including the clients' and hosts. Initially this implementation seemed great, because of less code-consuming collision detection and drawing. However, later on when implementing multiplayer features it caused a lot of trouble of properly identifiyng which ship is client's and which one is hosts. Due to these problems, multiplayer has features as invisible spaceships for the client, but for the host everything is fine. If we had more time, we would add another command pattern for signals, that are defined in the Client class, so we could avoid many consequent if statements in the Server class.

## Teamwork
It was agreed upon before the beginning working on the project that each member would work to implement different parts of the program depending on their skills and experience with Java, as such Lubor would work on upgrading the given code for the assingment in order to implement the required multiplayer aspects, meanwhile Tudor would attempt to create a persistent high score database. Tudor established a connection to an MySQL server in order to store the names of players and how many points they would obtain in a game. From there he implemented several methods that would allow the creation and maniputation of a data table where the varibles are stored. The methods Tudor implemented allowed for updating specific rows based upon a provided identifier, selecting all the elements in the table to print or selecting all the items ordered in a descending manner based upon the points column and allowing the creation of a table High Scores if one does not already exist in the database.
Lubor implemented main menu both in terms of controller and view that allow the user to select the type of the action. Lubor modified the model itself in order to allow the presence of multiple players in a game as well as he implemented the online functionality to allow the project to run either a singleplayer or multiplayer session. 

## Extras
The main menu looks really fancy!
