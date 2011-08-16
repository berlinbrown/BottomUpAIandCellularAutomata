/**
 * Copyright (c) 2006-2010 Berlin Brown and botnode.com  All Rights Reserved
 *
 * http://www.opensource.org/licenses/bsd-license.php

 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:

 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * * Neither the name of the Botnode.com (Berlin Brown) nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * SimulationObjects.scala
 * Feb 6, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin2.bottomuplife.life

import org.berlin2.bottomuplife.ui.BaseUIComponents._
import java.awt.{ Image, Color, Dimension, Graphics, Graphics2D, Point, geom }
import java.util.Random
import org.berlin2.bottomuplife.ui.RenderSimulation.SimulationState

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Cell Life Simulation objects.
 * 
 * @author bbrown
 *
 */
object LifeSimulation {
    
    def maxNumberFoodElementsOnCreate = 14
    def minOffsetNumberFoodElements = 6
    def foodInitSizeCreate = 12
    def foodMarginOffset = 16
    
    def maxNumberBacteria = 4
    def minOffsetNumberBacteria = 4
        
    def bactericaMarginOffset = 16    
    def maxGridOffsetForPos = 76
    
    def purgeCellsAtThreshold = 520
    
    /**
     * Simulation Type.
     */
    trait Simulation {        
        def onInitialize : Unit
        def onStepSimulation : Unit
    }
    
    /**
     * Container for all simulation objects.
     */
    class SimulationObjects extends Simulation {
        
        private val logger:Logger = LoggerFactory.getLogger(classOf[SimulationObjects])
        
        val simulationStatistics:SimulationLifeEntityStatistics = new SimulationLifeEntityStatistics 
        var simulationUIElementUnitList:List[ElementUnit] = List()               
        private var food:List[FoodAlgae] = List()
        private var bacteria:List[Bacteria] = List()
        var globalRenderSimulationState:SimulationState = null
        
        var nGlobalAliveCells = 0
        var nGlobalDeadCells = 0
        var mutationsGlobalPassedOn = 0
        
        /**
         * Set initial elements on the life simulation grid.
         */
        def initAllElements() : Unit = {
            
            logger.info("LifeSimulation [INIT SIMULATION] ********************** " + new java.util.Date)
            // Initialize food elements, create on the grid
            initFood
            initBacteria
            
            // Initialize elements
            food.foreach( e => e.onInitialize )
            bacteria.foreach( e => e.onInitialize )            
            simulationStatistics.runAllStatistics
            
            logger.info("LifeSimulation - initAll Elements - sizeOfBacteriaElements:" + bacteria.length)
            logger.info("LifeSimulation - initAll Elements - sizeOfFoodElements:" + food.length)
        }

        /**
         * Add food to the simulation.
         */
        def initFood() : Unit = {
                        
            val rand = new Random(System.nanoTime + 100)
            // Add initial food elements
            val posOffSet = foodMarginOffset
            val initSize = foodInitSizeCreate
            val maxGridWidthHeight = maxGridOffsetForPos            
            this.food = List()
            val numFoodElementsCreate = rand.nextInt(maxNumberFoodElementsOnCreate) + minOffsetNumberFoodElements            
            for (i <- 0 until numFoodElementsCreate) {
                val randposx = rand.nextInt(maxGridWidthHeight) + posOffSet
                val randposy = rand.nextInt(maxGridWidthHeight) + posOffSet
                val sizecreate = rand.nextInt(initSize) + 4                
                val foodNewObj = (new FoodAlgae(sizecreate, (randposx, randposy)))                
                food = foodNewObj :: food
            } // End for through food
        }
        
       /**
         * Add the organisms to the simulation.
         */
        def initBacteria() : Unit = {
                        
            val rand = new Random(System.nanoTime + 200)
            // Add initial food elements
            val posOffSet = bactericaMarginOffset
            // Size is set by dna
            val initSize = -1  
            val maxGridWidthHeight = maxGridOffsetForPos
            
            this.bacteria = List()
            val numElementsCreate = (if (maxNumberBacteria != 0) rand.nextInt(maxNumberBacteria) else 0) + minOffsetNumberBacteria            
            for (i <- 0 until numElementsCreate) {
                val randposx = rand.nextInt(maxGridWidthHeight) + posOffSet
                val randposy = rand.nextInt(maxGridWidthHeight) + posOffSet                               
                val bacteriaNewObj = (new Bacteria(initSize, (randposx, randposy)))                
                bacteria = bacteriaNewObj :: bacteria
            } // End for through food
        }
        
        /**
         * Step through the simulation and address all entities.         
         */
        def onStepSimulation : Unit = {
            
            // Process the living cells simulation routines
            bacteria.foreach( e => e.onStepSimulationProcessCell() )
            
            // If child available, add to list
            var forAddCell:Bacteria = null
            for (curBacteriaCell <- bacteria) {
                if (curBacteriaCell.hasConsumeForReplication) {
                    val childCellBacteria = curBacteriaCell.consumeChildCellReplication
                    if (childCellBacteria != null) {
                        forAddCell = childCellBacteria.asInstanceOf[Bacteria]
                    }
                }
            }      
            var simulationCounter:Long = -1
            var timeSeconds:Long = -1
            if (globalRenderSimulationState != null) {
                simulationCounter = globalRenderSimulationState.simulationCounter
                timeSeconds = globalRenderSimulationState.getDiffFromStartSeconds
                
                if (simulationCounter % 100 == 0) {
                    ////////////////////
                    // Log information
                    ///////////////////                    
                    var mutationsPassedOn = 0
                    for (bactElement <- bacteria) {
                        if (bactElement.getPassingOnMutationFromParentToChild) {
                            mutationsPassedOn = mutationsPassedOn + 1
                        }
                    }                                       
                    val nAliveCells = bacteria.filter { e => e.alive }.size
                    val nDeadCells = bacteria.filter { e => !e.alive }.size                               
                    val msg = ("LogUpdate [ctr:" + simulationCounter + " t:" + timeSeconds + "] NumberOfCells:" + bacteria.size + " LiveCells:" + nAliveCells + " DeadCells:" + nDeadCells + " MutationsAcrossSystem:" + mutationsPassedOn)
                    logger.info(msg)
                }
            } // End of if logging
            
            // Add to list of bacteria cells
            if (forAddCell != null) {
                bacteria.synchronized {                
                    bacteria = forAddCell :: bacteria                                         
                    logger.info("LifeSimulation:[ctr:" + simulationCounter + " t:" + timeSeconds + "] CREATE_ bacteria cell, add to list")                    
                    logger.debug("LifeSimulation:info on bacteria, dna=" + forAddCell.getDNA)
                    logger.info("LifeSimulation:info on bacteria, name=" + forAddCell.toString)
                    logger.info("LifeSimulation:info on bacteria, energyLevelAtCreate=" + forAddCell.getCellularRespirationEnergyLevel + " sunBurnRed:" + forAddCell.getChanceMutationForRedSunBurn + " waterBlueEnergy:" + forAddCell.getChanceMutationForBlueWaterEnergy)
                    logger.info("LifeSimulation:COUNT_ number elements list=" + bacteria.size)                    
                }
            }
            
            // Purge the bacteria list when it becomes too large
            if (bacteria.size > purgeCellsAtThreshold) {
                bacteria.synchronized {
                    bacteria = bacteria.filter { e => e.alive }
                }
            }
                                                                                
            // Chemical reaction not called at init
            food.foreach( e => e.chemicalReactions(SimulationObjects.this) )
            bacteria.foreach( e => e.chemicalReactions(SimulationObjects.this) )
            
            /////////////////////////////////////
            // REBUILD OBJECTS:
            // Create a new list, load all element units:
            /////////////////////////////////////
            simulationUIElementUnitList = List()
            var indexForFood = 0
            for (foodElem <- food) {                
                indexForFood = indexForFood + 1
                for (chemicalElemOfFood <- foodElem.elementUnits) {                    
                    val uiElementUnit = new ElementUnit((chemicalElemOfFood.gridPos._1, chemicalElemOfFood.gridPos._2), chemicalElemOfFood.color)
                    uiElementUnit.renderOrderWeight = indexForFood
                    simulationUIElementUnitList = uiElementUnit :: simulationUIElementUnitList
                     
                }                
            } // End of for, food elem
            var numElementsForBacteria = 0
            var indexForBacteria = 0
            for (bacteriaElem <- bacteria) {
                indexForBacteria = indexForBacteria + 1
                for (chemicalElemOfBacteria <- bacteriaElem.elementUnits) {                    
                    val uiElementUnit = new ElementUnit((chemicalElemOfBacteria.gridPos._1, chemicalElemOfBacteria.gridPos._2), chemicalElemOfBacteria.color)
                    uiElementUnit.renderOrderWeight = indexForBacteria + (if (bacteriaElem.alive) 60 else -1000) 
                    simulationUIElementUnitList = uiElementUnit :: simulationUIElementUnitList
                    numElementsForBacteria = numElementsForBacteria + 1
                }                
            } // End of for, food elem
                                    
            // Collect statistics
            simulationStatistics.runAllStatistics
        }
        
        /**
         * Main initialization for simulation objects.
         * Initialize and convert to UI elements.
         */
        def onInitialize : Unit = {
            
            initAllElements
            
            /////////////////////////////////////
            // Create a new list, load all element units:
            /////////////////////////////////////
            simulationUIElementUnitList = List()            
            // Load all food.
            // Set grid position and color.
            for (foodElem <- food) {                
                for (chemicalElemOfFood <- foodElem.elementUnits) {                    
                    val uiElementUnit = new ElementUnit((chemicalElemOfFood.gridPos._1, chemicalElemOfFood.gridPos._2), chemicalElemOfFood.color)
                    simulationUIElementUnitList = uiElementUnit :: simulationUIElementUnitList
                }                
            } // End of for, food elem                       
            
            for (bacteriaElem <- bacteria) {                
                for (bacelem <- bacteriaElem.elementUnits) {                    
                    val uiElementUnit = new ElementUnit((bacelem.gridPos._1, bacelem.gridPos._2), bacelem.color)
                    simulationUIElementUnitList = uiElementUnit :: simulationUIElementUnitList
                }                
            } // End of for, food elem
        }       
        
        /**
         * SimulationLifeEntity Statistics, collect data on the simulation objects.
         */
        class SimulationLifeEntityStatistics {
            
            /**
             * Weight of all elements in the universe.
             */
            var universeElementWeightAll:Double = 0            
            var foodElementWeightAll:Double = 0
            var bacteriaElementWeightAll:Double = 0
            var statisticMessages:List[String] = List()
            
            def runAllStatistics() {                
                // Reset                
                resetTotals                
                // Analyze the food.
                for (foodElement <- food) {
                    for (chemicalElementForFood <- foodElement.elementUnits) {
                        universeElementWeightAll = universeElementWeightAll + chemicalElementForFood.elementWeightLevel
                        foodElementWeightAll = foodElementWeightAll + chemicalElementForFood.elementWeightLevel
                    }
                } // End of the for //
                var mutationsPassedOn = 0
                for (bactElement <- bacteria) {
                    if (bactElement.getPassingOnMutationFromParentToChild) {
                        mutationsPassedOn = mutationsPassedOn + 1
                    }
                    for (chemicalElement <- bactElement.elementUnits) {
                        universeElementWeightAll = universeElementWeightAll + chemicalElement.elementWeightLevel
                        bacteriaElementWeightAll = bacteriaElementWeightAll + chemicalElement.elementWeightLevel                        
                    }
                } // End of the for //
                                
                // Basic count number of alive cells
                val nAliveCells = bacteria.filter { e => e.alive }.size
                val nDeadCells = bacteria.filter { e => !e.alive }.size
                
                nGlobalAliveCells = nAliveCells 
                nGlobalDeadCells = nDeadCells
                mutationsGlobalPassedOn = mutationsPassedOn
                
                val weightBactAvg = bacteriaElementWeightAll / bacteria.length
                val weightFoodAvg = foodElementWeightAll / food.length
                statisticMessages = ("BacteriaAvgWeight:" + numFormat(weightBactAvg)) :: statisticMessages
                statisticMessages = ("FoodAvgWeight:" + numFormat(weightFoodAvg)) :: statisticMessages
                statisticMessages = ("NmCells:" + bacteria.size + " Liv:" + nAliveCells + " Dea:" + nDeadCells + " Mut:" + mutationsPassedOn) :: statisticMessages                
            }
            
            def resetTotals() = {
                universeElementWeightAll = 0
                foodElementWeightAll = 0
                bacteriaElementWeightAll = 0
                statisticMessages = List()
            }
            
        } // End of the class - statistics
        
        def getPublicBacteria() : List[Bacteria ]= {
            return bacteria
        }
        
    } // End of Class

} // End of Object