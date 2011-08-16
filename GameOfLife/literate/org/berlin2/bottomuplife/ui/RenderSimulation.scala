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
 * RenderSimulation.scala
 * Feb 5, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 * keywords: alife, langton, gameoflife, cellularautomata, ca, scala, java, java2d
 */
package org.berlin2.bottomuplife.ui

import scala.swing.Swing._
import scala.swing.SimpleSwingApplication
import scala.swing.{ FlowPanel, MainFrame, Panel, SimpleGUIApplication }
import scala.swing.event._

import java.awt.{ Image, Color, Dimension, Graphics, Graphics2D, Point, geom }
import java.awt.EventQueue
import javax.swing.{ JPanel }

import java.lang.Runtime

import BaseUIComponents._

import org.berlin2.bottomuplife.ui.chart.BaseRenderUIChart

/**
 * Render components from off the grid.
 * 
 * @author bbrown
 *
 */
object RenderSimulation {

    def textHeaderConsoleYLine = 80
    
    def maxNumberGridUnitsX = 120
    def maxNumberGridUnitsY = 120
    
    def winCellSizeX = 6
    def winCellSizeY = 6
    
    def maxWinWidthForWater = 740
    def maxWinHeightForWater = 740
    
    /**
     * Return distance negative XY grid coordinates.
     */
    def calculateXYDistOffset(pos:GridPos) : GridPos = {
        val halfx = RenderSimulation.maxNumberGridUnitsX / 2
        val halfy = RenderSimulation.maxNumberGridUnitsY / 2
        return ((pos._1 - halfx), (halfy - pos._2)) 
    }
    
    def calculateXYDistance(pos:GridPos) : Double = {
        val offsets = calculateXYDistOffset(pos)
        math.sqrt((offsets._1 * offsets._1) + (offsets._2 * offsets._2))
    }
    
    /**
     * Higher value indicates we are closer to the edge of system,
     * 0 - 1.5?
     */
    def calculateXYEdgeEffect(pos:GridPos) : Double = {
      val halfx = RenderSimulation.maxNumberGridUnitsX / 2
      calculateXYDistance(pos) / halfx.toDouble 
    }
                
    /**
     * Simulation state statistics.
     */
    class SimulationState(val simStartTimeMS:Long) {
        var simulationCounter:Long = 0
        
        /**
         * Get difference from start time in milliseconds
         */
        def getDiffFromStartMillis() : Long = {
            return System.currentTimeMillis - simStartTimeMS
        }
        /**
         * Get difference from start time in milliseconds
         */
        def getDiffFromStartSeconds() : Long = {
            (getDiffFromStartMillis / 1000).toLong
        }
    }  // End of class
    
    /**
     * Main renderer, render all elements.
     */
    class Renderer {

        private val simulationState = new SimulationState(System.currentTimeMillis)
        private val mainGrid = new BaseUIComponents.MainGrid(0, textHeaderConsoleYLine, winCellSizeX, winCellSizeY);       
        private val renderCharts:BaseRenderUIChart = new BaseRenderUIChart
        
        def onInitialize : Unit = {
            mainGrid.globalRenderSimulationState = simulationState 
            mainGrid.initSimulationObjects
            mainGrid.buildUIElements
        }
        
        /**
         * Main utility to render the simulation.
         */
        def renderSimulationMain(g:Graphics) {
            
            // Step and build all UI elements
            mainGrid.buildUIElements
            
            // Continue with render
            renderBackgroundWater(g)
            renderAllElementUnits(g)
                                    
            renderTextHeaderConsole(g)
            renderMainCellGrid(g)            
            
            renderCharts(g)
            
            // Done with render
            simulationState.simulationCounter = simulationState.simulationCounter + 1
        }
        
        /**
         * Render water effect in background.
         * Water is composed of Hydrogen and Oxygen.  Water is essential to life and the properties of water allow organic compounds to interact and replicate.
         */
        def renderBackgroundWater(g:Graphics) {
            g.setColor(new Color(231, 230, 248))
            g.fillRect(BaseUIComponents.marginOffsetX, BaseUIComponents.marginOffsetY + textHeaderConsoleYLine, maxWinWidthForWater, maxWinHeightForWater)
            
            g.setColor(new Color(100, 100, 100))
            g.drawRect(BaseUIComponents.marginOffsetX, BaseUIComponents.marginOffsetY + textHeaderConsoleYLine, maxWinWidthForWater, maxWinHeightForWater)
        }
        
        def renderCharts(g:Graphics) {
            
            // Render only the outline and background of the chart
            // Allow the delegate class to handle the rest of the work.
            val lifech = mainGrid.defaultLifeChart
            g.setColor(lifech.backgroundColor)
            g.fillRect(lifech.winXPos, lifech.winYPos, lifech.winXWidth, lifech.winYHeight)
            g.setColor(new Color(100, 100, 100))
            g.drawRect(lifech.winXPos, lifech.winYPos, lifech.winXWidth, lifech.winYHeight)
            renderCharts.renderLifeChart(g, lifech)            
        }
        
        /**
         * Render HUD.
         */
        def renderTextHeaderConsole(g:Graphics) {
            
            val startLineY = 10
            val lineHeight = 16
                
            g.setColor(Color.black)
            g.drawLine(0, textHeaderConsoleYLine, BaseUIComponents.maxWinCanvasWidth, textHeaderConsoleYLine)                        
            g.drawString("[ALife] SimCounter:" + simulationState.simulationCounter, 5, startLineY)
            
            val runt = Runtime.getRuntime
            val totalmb = runt.totalMemory / (1024.0 * 1024.0)
            val maxmb = runt.maxMemory / (1024.0 * 1024.0)
            val freemb = runt.freeMemory / (1024.0 * 1024.0)
            val memoryMsg = "TotalMemLvl:" + totalmb + " MaxMem:" + maxmb + String.format(" FreeMem:%.3f", freemb.asInstanceOf[Object])
            g.drawString(memoryMsg, 5, startLineY + (lineHeight * 1))
            
            val universeWeightMsg = "UnivElemWeightAll:" + numFormat(mainGrid.simulationObjects.simulationStatistics.universeElementWeightAll)
            g.drawString(universeWeightMsg, 5, startLineY + (lineHeight * 2))
            
            val foodWeightMsg = "FoodElemWeightAll:" + numFormat(mainGrid.simulationObjects.simulationStatistics.foodElementWeightAll)
            g.drawString(foodWeightMsg, 5, startLineY + (lineHeight * 3))
            
            val bacteriaWeightMsg = "BacteriaElemWeightAll:" + numFormat(mainGrid.simulationObjects.simulationStatistics.bacteriaElementWeightAll)
            g.drawString(bacteriaWeightMsg, 5, startLineY + (lineHeight * 4))
            
            // Iterate through the rest of the messages
            // Column 2:
            val startXCol2 = 340
            var linesCtCol2 = 1
            
            g.drawString("Time:" + simulationState.getDiffFromStartSeconds +  "s", startXCol2, startLineY)
            
            // Iterate through second column messages
            for (lineForCol2 <- mainGrid.simulationObjects.simulationStatistics.statisticMessages) {
                g.drawString(lineForCol2, startXCol2, startLineY + (lineHeight * linesCtCol2))
                linesCtCol2 = linesCtCol2 + 1
            }            
        }
                
        /**
         * Render cell lines.
         */
        def renderMainCellGrid(g: Graphics) = {
            
            g.setColor(new Color(100, 100, 100))            
            val xfix = 166
            val yfix = 80
            
            val ox = BaseUIComponents.marginOffsetX
            val oy = BaseUIComponents.marginOffsetY + textHeaderConsoleYLine
            
            val maxx = BaseUIComponents.maxWinCanvasWidth
            val maxy = BaseUIComponents.maxWinCanvasHeight
            var xlinepos = 0
            var ylinepos = 0
            
            for (i <- 0 until (maxNumberGridUnitsX + 1)) {                    
                // Line travels along Y axis, moves X
                g.drawLine(xlinepos + ox + 2, oy+2, xlinepos + ox + 2, (maxy - BaseUIComponents.marginOffsetY - BaseUIComponents.marginOffsetY - yfix))
                xlinepos = xlinepos + mainGrid.winCellSizeX
            }
            for (j <- 0 until (maxNumberGridUnitsY + 1)) {
                // Line travels along X axis, moves y                
                g.drawLine(ox+2, oy + ylinepos + 2, 2+(maxx - BaseUIComponents.marginOffsetX - BaseUIComponents.marginOffsetX - xfix), oy + ylinepos + 2)                 
                ylinepos = ylinepos + mainGrid.winCellSizeY
            } // End of the for             
        }          
      
        /**
         * Render a elements in the universe.
         */
        def renderAllElementUnits(g:Graphics) {            
            mainGrid.elementUnitList.foreach( e => renderElementUnit(g, e) )            
        }    
        
        /**
         * Render a single element unit on the grid.
         * Render a chemical element.
         */
        def renderElementUnit(g:Graphics, elementUnit:BaseUIComponents.ElementUnit) {
             g.setColor(elementUnit.color)
             val x = (elementUnit.pos._1 * (mainGrid.winCellSizeX)) + BaseUIComponents.marginOffsetX + 2
             val y = (elementUnit.pos._2 * (mainGrid.winCellSizeY)) + (BaseUIComponents.marginOffsetY + textHeaderConsoleYLine) + 2
             g.fillRect(x, y, mainGrid.winCellSizeX, mainGrid.winCellSizeY) 
        }
                       
    } // End of the class //
    
} // End of Object