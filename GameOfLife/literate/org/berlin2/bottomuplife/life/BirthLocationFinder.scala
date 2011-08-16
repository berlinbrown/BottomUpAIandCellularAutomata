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
 * BirthLocationFinder.scala
 * Feb 20, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin2.bottomuplife.life

import org.berlin2.bottomuplife.ui.RenderSimulation

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Helper class for finding a prime birth location from the parent.
 * 
 * @author bbrown
 *
 */
class BirthLocationFinder(val size:Int, val pos:(Int, Int)) {

    private val logger:Logger = LoggerFactory.getLogger(classOf[BirthLocationFinder])
    
    private val randInstance = new java.util.Random(System.nanoTime + 336)
    
    /**
     * Find position for birth.
     * 
     * @param pos
     * @return
     */
    def findOffsetForBirthLocation() : (Int, Int) = {
        val resForDir = findPrimeDirectionRandEquation(findPrimeDirectionForBirth)              
        var offsetx = size
        var offsety = size
        var directionToGiveBirth = 0        
        var maxVal = -1.0        
        var i = 0
        for (chkForHighVal <- resForDir) {
            if (chkForHighVal > maxVal) {
                maxVal = chkForHighVal
                directionToGiveBirth = i 
            }
            i = i + 1            
        }
        // Nullify selected direction with random val
        // to avoid clustering in one area
        if (maxVal < 1.5) {
            directionToGiveBirth = randInstance.nextInt(8)
        }
        directionToGiveBirth match {
            case 0 => { offsetx = size  ; offsety = -size  }
            case 1 => { offsetx = size  ; offsety = 0     }
            case 2 => { offsetx = size  ; offsety = size }
            case 3 => { offsetx = 0     ; offsety = size }
            case 4 => { offsetx = -size ; offsety = size }
            case 5 => { offsetx = -size ; offsety = 0     }
            case 6 => { offsetx = -size ; offsety = size }
            case 7 => { offsetx = 0     ; offsety = -size  }
            case _ => { offsetx = size  ; offsety = -size  }          
        }
        
        // Shift the result some ore
        val randOffsetMore1 = if (randInstance.nextDouble < 0.25) -1 else 0
        val randOffsetMore2 = if (randInstance.nextDouble < 0.25) -1 else 0       
        val randOffsetMore3 = if (randInstance.nextDouble < 0.35) 1 else 0
        val randOffsetMore4 = if (randInstance.nextDouble < 0.25) 1 else 0
        
        val res = (offsetx + randOffsetMore1 + randOffsetMore3, offsety + randOffsetMore2 + randOffsetMore4)        
        res
    }
    
    private def findPrimeDirectionRandEquation(shiftDirection:List[Double]) : List[Double] = {
        val randVal = randInstance.nextDouble
        var NE = shiftDirection(0) * randVal 
        var E  = shiftDirection(1) * randVal
        var SE = shiftDirection(2) * randVal
        var S  = shiftDirection(3) * randVal
        var SW = shiftDirection(4) * randVal
        var W  = shiftDirection(5) * randVal
        var NW = shiftDirection(6) * randVal
        var N  = shiftDirection(7) * randVal
        return (NE :: E :: SE :: S :: SW :: W :: NW :: N :: List())        
    }
    
    /**
     * Based on distance from edge, try to find a direction to give birth in
     * 1:NE, 2: E, 3:SE, 4:S, 5:SW, 6:W, 7:NW, 8:N
     * 
     * Create an equation to favor a particular direction.
     * 
     * TODO - refactor
     * @return
     */
    private def findPrimeDirectionForBirth() : List[Double] = {
        
        val halfx = RenderSimulation.maxNumberGridUnitsX / 2
        val halfy = RenderSimulation.maxNumberGridUnitsY / 2
        val equationOffsetXAXIS:Double = (this.pos._1 - halfx) / RenderSimulation.maxNumberGridUnitsX.toDouble
        val equationOffsetYAXIS:Double = (halfy - this.pos._2) / RenderSimulation.maxNumberGridUnitsY.toDouble        
        val equationDirection = new FavorDirectionForEquation
                
        val moreOffsetX = (math.abs(equationOffsetXAXIS) / 0.5) * 2.0
        val moreOffsetY = (math.abs(equationOffsetYAXIS) / 0.5) * 2.0
                       
        if (equationOffsetXAXIS < 0 && equationOffsetYAXIS < 0) {
            
            equationDirection.favorEast(moreOffsetX)
            equationDirection.favorNorth(moreOffsetY)
            
        } else if (equationOffsetXAXIS < 0 && equationOffsetYAXIS > 0) {
            equationDirection.favorEast(moreOffsetX)
            equationDirection.favorSouth(moreOffsetY)
            
        } else if (equationOffsetXAXIS > 0 && equationOffsetYAXIS < 0) {
            equationDirection.favorWest(moreOffsetX)
            equationDirection.favorNorth(moreOffsetY)
            
        } else if (equationOffsetXAXIS > 0 && equationOffsetYAXIS > 0) {
            equationDirection.favorWest(moreOffsetX)
            equationDirection.favorSouth(moreOffsetY)
        }               
        return equationDirection.toList
    }    
    
    /**
     * Modify equation such that we favor a particular direction to give birth.
     * @author bbrown
     *
     */
    private class FavorDirectionForEquation {
        
        var NE = 1.0
        var E  = 1.0
        var SE = 1.0
        var S  = 1.0
        var SW = 1.0
        var W  = 1.0
        var NW = 1.0
        var N  = 1.0
        
        def favorNorth(shift:Double) = {
            N  = N  * (2.2 + shift)
            S  = S  * 0.3            
            
            NE = NE * (1.4 + shift)
            SE = SE * 0.5
            
            NW = NW * (1.4 + shift)
            SW = SW * 0.5                                   
        }
        
        def favorSouth(shift:Double) = {
            // Favor south
            S  = S  * (2.2 + shift)
            N  = N  * 0.3
            
            SE = SE * (1.4 + shift)
            NE = NE * 0.5
                                
            SW = SW * (1.4 + shift)
            NW = NW * 0.5
        }
        
        def favorWest(shift:Double) = {
            // Favor West
            W  = W  * (2.2 + shift)
            E  = E  * 0.3            
                    
            SW = SW * (1.4 + shift)
            NW = NW * (1.4 + shift)
            
            NE = NE * 0.5                    
            SE = SE * 0.5                      
        }
        
        def favorEast(shift:Double) = {
            E  = E  * (2.2 + shift)
            W  = W  * 0.3
            
            NE = NE * (1.4 + shift)           
            SE = SE * (1.4 + shift)
                    
            SW = SW * 0.5            
            NW = NW * 0.5
        }
                      
        def toList() : List[Double] = {
            return (NE :: E :: SE :: S :: SW :: W :: NW :: N :: List())                
        }
        
        def listToStr() : String = {
            val lst = this.toList
            "[Favor Direction Equation: NE:" + lst(0) + " E :: " + lst(1) + " SE :: " + lst(2) + " S :: " + lst(3) + " SW :: " + lst(4) + " W :: " + lst(5) + " NW :: " + lst(6) + " N ::"  + lst(7) + "]"
        }
        
        override def toString : String = {
            listToStr               
        }
        
    } // End of Class
    
} // End of the Class