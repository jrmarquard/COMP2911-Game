# COMP2911 Project (2016)

## Assignment Details

 **Due Date**: Week 12, Friday, May 27, 11:59 p.m.

**[Project Page](https://www.cse.unsw.edu.au/~cs2911/assignments/ass03.html)**

#### Assignment Goals: 
In this assignment, the aim is to construct an interactive maze puzzle game that provides an interesting and challenging experience. The primary requirement is not to develop an efficient problem solver, but to understand and anticipate user requirements, and to design a graphical user interface so that users of varying degrees of expertise can interact effectively with the system.

## Project Development

#### Scrum Roles:
- John: Developer/Scrum Master/Project Owner
- Joshua: Developer
- Partrick: Developer
- Tim: Developer
- Tyler: Developer

#### Sprints:
- **Week 9**: Have a working application to present to the tutorial by week 10. A working application means:
    - Application needs to be displayed on screen 
    - At least one maze design implemented
    - User needs to be able to play at least one game mode 

#### Goals (bastardised user stories):
- User needs to be able to start game, change settings, exit application
- User needs to be able to change AI difficulty, and other options
- User needs to be able to play against different mazes
- User needs to have different game modes, such as solving it individually, against a timer, with enemies, against another player, ... etc

#### Week 10 Discussion of medium range game:
- Menu on startup (after a "splash screen")
- Easy, Medium, Hard, Custom (sizes)
- Ratio restriction (with columns to adjust window size)
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
    - Sound
    - hints for users when they are stuck (ie a maze solving algorithm)
    - enemies in the maze to avoid
- John
    - Implementing a graphical front end 
    - backend and how it interfaces with each component
- Joshua
    - maze generation
- Patrick
    - 
- Tim
    - maze implementation
- Tyler
    - AI implementation

#### Completed Goals
- Basic GUI
- Basic Game Mode
- Maze genration with different sizes
