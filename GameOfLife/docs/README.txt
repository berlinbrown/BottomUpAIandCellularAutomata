###########################################################
# README
# Created: 1/11/2011
###########################################################

    About: BottomUpCellLifeGame
    -----------------------------
    
    Also see:
    
    http://doingitwrongnotebook.googlecode.com/svn/trunk/doingitwrong_phase2/scala2/GameOfLife/src/main/scala/
    http://code.google.com/p/ainotebook/wiki/BottomUpArtificialLifeSimulation
    
    -----
    Notes:
    -----
    
    - The system is slightly interesting.  You can monitor the balance with the number of live cells. 
      You may notice a shift in mutations as cells grow away from the center.
    
    -  With only a few mutations, the color of cells tend to shift in color over time.
    
    - It takes many iterations for emergent behavior to emerge.
    
    - Code wise not really that interesting but already we can visualize the emergent behavior.
    
    - Cheating to make the simulation feasible 
    
    -----
    Building:
    -----
    Artificial Life Demo - launch the sbt.bat script and type compile at the prompt
    - sbt - at prompt compile
    - sbt - at prompt package
    - sbt - at prompt package-src 
    
    
    -----
    Now Using:
    -----  
    Scala Version: Scala 2.9.0 r24613 b20110328152330
    
    Previously Using scala: 2.8.0.r22118-b20106020
    
    
    sbt - simple build tool
    -----    
    
    Simple game of life in Scala and using scala.swing api.
    Doing it wrong version, no refactoring.
    
    Keywords: rule30, rule190, squaringrule, wolfram
    
    -----
    Running:
    -----
    Run the applet[1-4].html files in a modern browser.  The java applets will execute. 
    
    -----
    Adding svn propset on html files.
    svn propset svn:mime-type 'text/html' applet.html 
    -----
    
    Berlin Brown - berlin dot brown _at_ gmail dot com
    keywords: cells, dna, replication, gameoflife, scala, java, alife, artificiallife
    
---------------------------------------
    
The field of artificial intelligence in computer science focuses on many
different areas of computing from computer vision to natural language
processing. These top-down approaches typically concentrate on human behavior
or other animal functions. In this article we look at a bottom-up approach to
artificial life and how emergent cell behavior can produce interesting results.
With this bottom-up alife approach, we are not interested in solving any
particular task, but we are interested in observing the adaptive nature of the
entities in our simulation. We also wanted to introduce those more familiar with
software engineering to biological systems and evolutionary theory concepts.

Life is all around us. Even with inorganic material it is possible that
microscopic organisms are covering that surface. Moving forward if we want to
study, analyze and work with artificial agents, we might consider systems that
have evolved behavior over a series of steps. We should not necessarily build a
specific tool with a specific purpose but the creature that is built from the
system may produce interesting properties which are unlike the clean-room
created software that we create today. Most software and hardware today is
written to specification, line for line, most code written for today's systems
are created by man. That software is designed, coded and tested. It would be
interesting if we could start a biological like system and interesting behavior
from the system evolves over time.

Conway's Game of Life cellular automaton is one of the most prominent examples
of cellular automata theory. The one dimensional program consists of a cell grid
typically with several dozen or more rows and similar number of columns. Each
cell on the grid has an on or off Boolean state. Every cell on the grid survives
or dies to the next generation depending on the game of life rules. If there are
too many neighbors surrounding a cell then the cell dies due to overcrowding. If
there is only one neighbor cell, the base cell dies due to under-population.
Activity on a particular cell is not interesting but when you run the entire
system for many generations, a group of patterns begin to form.

You may notice some common patterns in the figure. After so many iterations
through the game of life rules, only a few cells tend to stay alive. We started
with a large random number of alive cells and over time those cells died off. In
a controlled environment you may begin with carefully placed live cells and
monitor the patterns that emerge to model some other natural phenomena.

Summary

Moving forward if we want to study, analyze and work with artificial agents, we
might consider systems that have evolved behavior over a series of steps. We
might not build a specific tool with a specific purpose but the creature that is
built from the system may produce interesting properties which are unlike the
clean-room created software that we create today.

With this artificial life approach, but we also want to study the simple life
forms first before moving too fast forward like human behavior.

-- Berlin Brown