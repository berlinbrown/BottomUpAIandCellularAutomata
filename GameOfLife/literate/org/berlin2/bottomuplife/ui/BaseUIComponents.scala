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
 * BaseUIComponents.scala
 * Feb 5, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 * keywords: alife, langton, gameoflife, cellularautomata, ca, scala, java, java2d
 */
package org.berlin2.bottomuplife.ui

import java.awt.{ Image, Color, Dimension, Graphics, Graphics2D, Point, geom }
import org.berlin2.bottomuplife.life.LifeSimulation._
import org.berlin2.bottomuplife.ui.RenderSimulation.SimulationState

import org.berlin2.bottomuplife.ui.chart.BaseUILifeChart

/**
 * Base Swing User Interface Components.
 *  
 * @author bbrown
 *
 */
object BaseUIComponents {
    
    type GridPos = (Int, Int)
    
    /**
     * Pixel width for canvas, should match the window size.
     */
    def maxWinCanvasWidth   = 920
    
    /**
     * Pixel height for canvas, should match the window size.
     */
    def maxWinCanvasHeight  = 920
   
    def marginOffsetX = 12
    def marginOffsetY = 12
            
    def estimateGridSize(numberOfCellsX:Int, numberOfCellsY:Int) : (Int, Int, Int) =  {
        val sizex = (maxWinCanvasWidth - marginOffsetX - marginOffsetX) / numberOfCellsX
        val sizey = (maxWinCanvasHeight - marginOffsetY - marginOffsetY) / numberOfCellsY
        val tot = numberOfCellsX * numberOfCellsY
        return (sizex, sizey, tot)
    }
    
    /**
     * Simple UI element for rendering to grid.
     */
    class ElementUnit(val pos:GridPos, val color:Color) {
        /**
         * Use render order weight to render non-living entities in the background.
         */
        var renderOrderWeight = 0
        var chemicalWeight = 0
    }
    
    /**
     * @param winCellSizeX  Size in pixels for a cell, used with the win canvas 
     * @param winCellSizeY  Size in pixels for a cell, used with the win canvas
     */
    class MainGrid(val winStartXOffset:Int, val winStartYOffset:Int, val winCellSizeX:Int, val winCellSizeY:Int)  {
        
        var elementUnitList:List[BaseUIComponents.ElementUnit] = List() 
        val simulationObjects:SimulationObjects = new SimulationObjects 
        var globalRenderSimulationState:SimulationState = null        
        val defaultLifeChart:BaseUILifeChart = new BaseUILifeChart(marginOffsetX, 840, 500, 70) 
        
        /**
         * Initialize simulation objects.
         */
        def initSimulationObjects = {
            simulationObjects.globalRenderSimulationState = globalRenderSimulationState
            simulationObjects.onInitialize
        }
        
        /**
         * Transfer the UI elements.
         */
        def buildUIElements() {            
            // Step through the simulation
            simulationObjects.onStepSimulation
            
            // Copy all elements
            elementUnitList = List()
            for (uiElement <- simulationObjects.simulationUIElementUnitList) {
                elementUnitList = uiElement :: elementUnitList
            }
            // Sort based on weight
            elementUnitList = elementUnitList.sortWith((e1, e2) => (e1.renderOrderWeight < e2.renderOrderWeight))
            
            addDataPointsCharts
        }       
        
        /**
         * Add data points to the chart
         */
        def addDataPointsCharts() {            
            defaultLifeChart.lifeAliveData.synchronized {
                defaultLifeChart.lifeAliveData = simulationObjects.nGlobalAliveCells :: defaultLifeChart.lifeAliveData
            } 
            defaultLifeChart.lifeDeadData.synchronized {
                defaultLifeChart.lifeDeadData = simulationObjects.nGlobalDeadCells :: defaultLifeChart.lifeDeadData
            }
            defaultLifeChart.lifeMutationData.synchronized {
                defaultLifeChart.lifeMutationData = simulationObjects.mutationsGlobalPassedOn :: defaultLifeChart.lifeMutationData
            }
        }
        
    } // End of Class
    
    def numFormat(valForFormat:Double) : String = {
        val formatter = new java.text.DecimalFormat("###,###,###,###.###")
        return formatter.format(valForFormat)
    }
    
} // End of Object