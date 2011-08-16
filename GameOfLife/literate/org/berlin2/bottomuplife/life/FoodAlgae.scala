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
 * FoodAlgae.scala
 * Feb 5, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin2.bottomuplife.life

import org.berlin2.bottomuplife.ui.BaseUIComponents._
import BaseLifeComponents._
import java.awt.{ Image, Color, Dimension, Graphics, Graphics2D, Point, geom }
import LifeSimulation._

/**
 * All forms of algae are composed of eukaryotic cells but for our demo, we are treating 
 * Food/Algae as a type of non organic entity.  But the cells in our system feed on this non organic form of algae.
 * 
 * @author bbrown
 *
 */
class FoodAlgae(size:Int, pos:GridPos) extends BaseLifeComponents.LifeEntity(size, pos) with NonLivingEntity {
    
    def averageElementWeightAlgae = 1000.0
    def minElementWeightAlgae = 400.0
    
    /**
     * Example value: (0.00001 / 100.0)
     */
    def decayFactorForAlgaeFromSun   = (0.0001 / 100.0)
    def decayFactorForAlgaeFromWater = (0.0004 / 100.0)
        
    def chemicalReactionWithSun(sun:SunLight) : Unit  = {        
        for (element <- elementUnits) {
            element.elementWeightLevel = element.elementWeightLevel * (1.0 - decayFactorForAlgaeFromSun)
        }        
    }
    def chemicalReactionWithWater(water:Water) : Unit  = {
        for (element <- elementUnits) {
            element.elementWeightLevel = element.elementWeightLevel * (1.0 - decayFactorForAlgaeFromWater)
        }
    }
    
    /**
     * Interact with the universe.
     */
    def chemicalReactions(sim:SimulationObjects) : Unit = {
        chemicalReactionWithSun(null)
        chemicalReactionWithWater(null)        
        // Set color based on element weight level
        for (element <- elementUnits) {
            val checkForHalfLife = (element.elementWeightLevel / averageElementWeightAlgae)
            if (checkForHalfLife > 0.10) {
                val levelIntensity1 = 212.0 * (element.elementWeightLevel / averageElementWeightAlgae) 
                val levelIntensity2 = 73.0  * (element.elementWeightLevel / averageElementWeightAlgae)                
                element.color = new Color(32, math.min(255, levelIntensity1.toInt), math.min(255, levelIntensity2.toInt))                
            }
        } // End of for
    }
    
    /**
     * Initialize the elements and positions.
     */
    def setElementPositions() : Unit = {
        
        val rand = new java.util.Random(System.nanoTime + 400)
        // We expect an even number for the size
        // We will split the size in half and set the width and height positions.
        val halfSizeForWidthHeight = (size / 2).toInt        
        for (j <- 0 until size) {
            for (i <- 0 until size) {                
                val additionalWeight = rand.nextInt(500)
                val x = pos._1 - halfSizeForWidthHeight + i
                val y = pos._2 - halfSizeForWidthHeight + j
                val element = new ChemicalElementUnit(minElementWeightAlgae + additionalWeight, Element.AlgaeFoodElement, (x, y))
                
                val levelIntensity1 = 212.0 * (element.elementWeightLevel / averageElementWeightAlgae) 
                val levelIntensity2 = 73.0  * (element.elementWeightLevel / averageElementWeightAlgae)
                
                element.color = new Color(32, levelIntensity1.toInt, levelIntensity2.toInt)               
                elementUnits = element :: elementUnits
            }
        }
    }
    
    def sizeElements : Int = {
        return elementUnits.length
    }
    
    /**
     * Initialize the food.
     */
    def onInitialize : Unit = {
        setElementPositions
        this.color = new Color(32, 208, 73)
    }
    
} // End of class //