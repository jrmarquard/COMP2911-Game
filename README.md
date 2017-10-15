# COMP2911 Project (2016)

## Game Features:
- 3 Game modes
    - Adventure: Endless maze generation, find more enemies the deeper you go)
    - Race: Race the AI or a friend through the same maze design
    - Battle: Fight each other in the maze
- Lighting engine
- Adjustable maze size
- Maze elements: enemies, lock and key, coins
- Up to 4 player multiplayer (with people or AI)
- 3 Graphic Sets: Castle, Desert, Space
- Life sound effects and music    

### Screenshots:
![](https://raw.githubusercontent.com/jrmarquard/COMP2911-Game/master/screenshots/screenshot1-adventure-mode.PNG)
 
![](https://raw.githubusercontent.com/jrmarquard/COMP2911-Game/master/screenshots/screenshot2-game-options.PNG)
 
![](https://raw.githubusercontent.com/jrmarquard/COMP2911-Game/master/screenshots/screenshot3-multiplayer.PNG)
 
![](https://raw.githubusercontent.com/jrmarquard/COMP2911-Game/master/screenshots/screenshot4-desert.PNG)
 
![](https://raw.githubusercontent.com/jrmarquard/COMP2911-Game/master/screenshots/screenshot5-space.PNG)

---

## Assignment Details

 **Due Date**: Week 12, Friday, May 27, 11:59 p.m.

**[Project Page](https://www.cse.unsw.edu.au/~cs2911/assignments/ass03.html)**

#### Assignment Goals: 
In this assignment, the aim is to construct an interactive maze puzzle game that provides an interesting and challenging experience. The primary requirement is not to develop an efficient problem solver, but to understand and anticipate user requirements, and to design a graphical user interface so that users of varying degrees of expertise can interact effectively with the system.

## Project Development

#### Scrum Roles:
- John: Developer/Scrum Master/Project Owner
- Joshua: Developer
- Tim: Developer
- Tyler: Developer

#### Sprints:
- **Week 9**: Have a working application to present to the tutorial by week 10. A working application means:
    - Application needs to be displayed on screen 
    - At least one maze design implemented
    - User needs to be able to play at least one game mode
- **Week 10**: Imrprove the application with functional features; appearance is secondary.
    - Implement a menu system
    - Add a second game mode
    - Improve display of maze
- **Week 11**: Finish a maze which meets all the critea of the assignment.
    - Assignment criteria:
        - No bugs seen during demo/testing
        - Smooth, responsive, intuitive, well designed user interface
        - Clear design and diagrams fully adhering to design principles and conforming to code
        - Algorithm that generates interesting and challenging games
        - More than one type of extension (e.g. animation, multi-player, etc.)
    - Finalise menus, this includes their appearance and their functionality
    - Finalise design
        - Create design documents (UML, 'design diaries' maybe?, anything else?)
        - Implement threading
        - Tidy and refactor code
    - Generate mazes that distinguish ours from the rest
        - make it feature rich (e.g. door/keys, different routes, buttons)
        - demonstrate why our generation is 'hard', give it difficulties. Maybe compare different algos in a document
    - Extension suggestions:
        - Polish multiplayer
        - Add other game modes
        - Animation
        - Add sprites
        - Add textures
        - Add enemies
        - Add countdown/timer
        - Add highscores
    - Personal goals!
        - Have a complete diary, showing your contribution week to week (or just something)
        - Be able to explain your code, don't just copy paste of SO without knowing what it does

#### Goals (bastardised user stories):
- User needs to be able to start game, change settings, exit application
- User needs to be able to change AI difficulty, and other options
- User needs to be able to play against different mazes
- User needs to have different game modes, such as solving it individually, against a timer, with enemies, against another player, ... etc

#### Week 10 Discussion of medium range game:
- Easy, Medium, Hard, Custom (sizes)
- AI which doesnt update
- Viewing limitations (dark everywhere else)
- Step counter
- My first enemy
- Fix start and finish locations (on the maze)
- Change default size (it changes after a new maze)
- More randomisation for start and finish locations
- Investigate threading and the communication between threads (What the hell is a thread?)


#### TBF Goals and their developers
- Unassigned
    - Working on graphical designs and ideas
    - 'Design doc' outlining design ideas, could cover graphics+audio
    - Secondary objectives/challenges
    - different game types (multiple exits, teleporting tiles that transfer players from one spot to another etc.)
    - multiplayer
    - different difficulties
    - hints for users when they are stuck (ie a maze solving algorithm)
    - enemies in the maze to avoid
- John
    - Implementing a graphical front end 
    - backend and how it interfaces with each component
- Joshua
    - maze generation
    - multiplayer game mode
- Tim
    - maze implementation
    - Sound
- Tyler
    - AI implementation
    - Main menu

#### Completed Goals
- Basic GUI
- Basic Game Mode
- Maze genration with different sizes
- Menu (functional but not pretty)
- Ratio restriction (with columns to adjust window size)
