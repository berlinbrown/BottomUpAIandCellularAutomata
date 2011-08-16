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
 * BaseLifeComponents.scala
 * Feb 5, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 * 
 * keywords: alife, langton, gameoflife, cellularautomata, ca
 */
package org.berlin2.bottomuplife.life

import org.berlin2.bottomuplife.ui.BaseUIComponents._ 
import LifeSimulation._
import scala.math._ 

/** 
 * Base Life simulation classes and objects.
 * 
 * @author bbrown
 *
 */
object BaseLifeComponents {

    /**
     * The most basic units in the life simulation consist of
     * Chemical Life Elements.  This enumeration contain the element types.
     * The element types are closely tied to the proteins on the grid
     * but in reality they are synonymous with chemical life elements like Carbon, Oxygen, etc.
     */
    object Element extends Enumeration {
        type Element = Value
        val BacteriaWallProteinElement, WaterElement, SunLightEnergy, AlgaeFoodElement = Value
    } // End of object
    import Element._    
    type ElementType = Element

    /**
     * Interface living and non-living types.
     */
    trait NonLivingEntity
    trait LivingEntityCell {
        def getName : String
        def getDNA : DNA                
        def alive : Boolean
        def processDNA() : Unit
        def produceProteins() : Unit
        def onStepSimulationProcessCell() : Unit
        def getMutableSize : Int
        def setImmutableSystemTraits : Unit
        def onStepSetSystemTraits : Unit
        /**
         * Metabolic reaction and process to convert energy.
         * In our system, amount of energy for this cell.
         */
        def getCellularRespirationEnergyLevel : Double
        def hasConsumeForReplication : Boolean
        /**
         * If child available, consume and add to core list.
         */
        def consumeChildCellReplication : LivingEntityCell
        def onInitializeReplicate(cell:LivingEntityCell) : Unit
        def mutateDNAOnReplication(targetCell:LivingEntityCell) : DNA
    }
    trait SingleCellLivingEntity extends LivingEntityCell
    trait MultiCellLivingEntity extends LivingEntityCell
           
    /**     
     * Basic properties for a life agent.
     * All entities for the life simulation are life agent entities.
     */
    trait LifeAgent {
        def setElementPositions : Unit
        def sizeElements : Int
        def onInitialize : Unit
        def chemicalReactionWithSun(sun:SunLight) : Unit
        def chemicalReactionWithWater(water:Water) : Unit
        def chemicalReactions(sim:SimulationObjects) : Unit
    }
        
    /**
     * Each grid in the life simulation consists of a chemical element unit.
     * The element unit contains an element level or weight.  The elements
     * react with other elements.
     */
    class ChemicalElementUnit(val initElementLevel:Double, val elementType:ElementType, val gridPos:GridPos) {               
        var elementWeightLevel = initElementLevel
        var color:java.awt.Color = java.awt.Color.gray        
    }    
       
    abstract class LifeEntity(val size:Int, val pos:GridPos) extends LifeAgent {        
        var name = "life-entity"
        var color:java.awt.Color = java.awt.Color.gray
        var elementUnits:List[ChemicalElementUnit] = List()
    }
    
    /**
     * Model DNA bases, Adenine, Cytosine, Guanine, Thymine. 
     */
    object DNABase1 {
        def A = 1
        def G = 2
        def C = 3
        def T = 4
        
        def a = 1
        def g = 2
        def c = 3
        def t = 4
    }
    type DNABase = Int
    
    /**
     * The DNA Word tuple consists of 4 ints/digits.
     * The highest value of the left most outer digit could be represented by: 4 * pow(4, 3) or 4 * (4 ^ 3).
     */
    type DNAWord = (DNABase, DNABase, DNABase, DNABase)
    
    /**
     * A Gene4 sequence is equivalent to a DNA Word. 
     */
    type Gene4 = DNAWord 
    
    /**
     * Convert base to character, on invalid base default to 'A'.
     */
    def baseToChar (dnaBase:DNABase) : Char = dnaBase match {
            case 1 => 'A'
            case 2 => 'G'
            case 3 => 'C'                     
            case _ => 'T'          
    }
    
    /**
     * Convert DNA word to string.
     */
    def wordToStr(dnaWord:DNAWord) : String = {
        return (baseToChar(dnaWord._1) :: baseToChar(dnaWord._2) :: baseToChar(dnaWord._3) :: baseToChar(dnaWord._4) :: List()).mkString 
    }    
        
    /**
     * Convert DNA word to int.
     */
    def wordToInt(dnaWord:DNAWord) : Int = {        
        val c = (dnaWord._3 * scala.math.pow(4,1).toInt)
        val b = (dnaWord._2 * scala.math.pow(4,2).toInt)
        val a = (dnaWord._1 * scala.math.pow(4,3).toInt)
        return a + b + c + dnaWord._4 
    }
    
} // End of Object
