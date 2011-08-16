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
 * SunLight.scala
 * Feb 5, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 * keywords: alife, langton, gameoflife, cellularautomata, ca, scala
 */
package org.berlin2.bottomuplife.life

import org.berlin2.bottomuplife.ui.BaseUIComponents._
import BaseLifeComponents._
import java.awt.{ Image, Color, Dimension, Graphics, Graphics2D, Point, geom }
import LifeSimulation._

import org.berlin2.bottomuplife.ui.RenderSimulation
import BaseLifeComponents._
import org.berlin2.bottomuplife.code.DNATranslationParser
import org.berlin2.bottomuplife.life.TraitToSystem._

/**
 * Living bacteria cell, single celled organism.
 * 
 * @author bbrown
 *
 */
class Bacteria(size:Int, pos:GridPos) extends BaseLifeComponents.LifeEntity(size, pos) with BaseLifeComponents.SingleCellLivingEntity {
    
    def deadSize = 6
    
    def randInitialChemicalWeight = 310        
    def minElementWeightBacteria = 900.0        
    def minColorChangeThreshold = 0.10
    
    def maxWeightLossPerc = -95.0
    
    /**
     * Point during life to give birth.
     * @return
     */
    def energyLevelLifeSpanForBirth = 0.4
    
    /**
     * Energy level required for rebuilding chemical bonds.
     * @return
     */
    def energyLevelForRebuild = 1.4
    
    /**
     * Chemical weight added on rebuild.
     * @return
     */
    def addedChemicalWeightOnRebuild = 5.3
    
    private val randInstance = new java.util.Random(System.nanoTime + 330)
    
    /**
     * Example value: (0.00001 / 100.0)
     */
    def decayFactorForBacteriaFromSun   = (0.000000001 / 100.0)
    def decayFactorForBacteriaFromWater = (0.3 / 100.0)
           
    private val dna = initDNA     
    private var bactericaAliveState = true
    private var mutableSize = size
    
    private var cellularRespirationEnergyLevel = 1000.0
    private var initCellularRespirationEnergyLevel = cellularRespirationEnergyLevel
    
    private var totalDefaultElementWeightBacteria:Double = 0.0
    private var currentElementWeightBacteria:Double = 0.0                                 
    
    private var hasChildAvailableThroughReplication = false;
    private var numChildAvailableThroughReplication = 1
    
    private var chanceMutationForRed   = 0.12
    private var chanceMutationForBlue  = 0.20
    private var mutatationsOnBirth = 0
    private var passingOnMutationFromParentToChild = false
    private var mutatationsPassedOnThruGenerations = 0
    
    private var personalCellName = "parent-no-name-for-cell"
    
    /**
     * A negative value of -10.0 represents 10% loss. 
     */
    private var lossInWeight:Double = 0.0
    
    /**
     * Percent energy level 0 - 1
     * @param x$1
     */
    private var percEnergyLevelFromStart = 1.0
    
    /**
     * Metabolic reaction and process to convert energy.
     * In our system, amount of energy for this cell.
     */
    def getCellularRespirationEnergyLevel : Double = { cellularRespirationEnergyLevel }
    
    def getChanceMutationForRedSunBurn : Double =  { chanceMutationForRed }
    def getChanceMutationForBlueWaterEnergy : Double =  { chanceMutationForBlue }
    
    def getName : String = { personalCellName } 
    
    override def toString() : String = {
        personalCellName
    }
    
    /**
     * Process chemical reaction with sun.
     */
    def chemicalReactionWithSun(sun:SunLight) : Unit  = {
        
        // Increase chances of mutation towards red if close to edge        
        val percEdgeEffect = RenderSimulation.calculateXYEdgeEffect(pos)
        val percEdgeEffectWithMod = percEdgeEffect * (if (percEdgeEffect < 0.29) 0.34 else 1.0) * (if (percEdgeEffect > 0.7) 1.6 else 1.0)   
        val increaseFromBeingCloseToEdge = 1.0 + (percEdgeEffectWithMod * 0.0017)
        this.chanceMutationForRed = this.chanceMutationForRed * increaseFromBeingCloseToEdge
        
        // Fatal energy death on moving outside of the grid
        if ((pos._1 < 8) || (pos._2 < 8) || (pos._1 > 104) || (pos._1 > 104)) {
            this.cellularRespirationEnergyLevel = -1000                       
        }
    } 
    
    
    /**
     * Process chemical reactions with water.
     * @param water
     */
    def chemicalReactionWithWater(water:Water) : Unit  = {
        
        // Increase the chances of mutation just based on time in water        
        chanceMutationForBlue = chanceMutationForBlue * 1.0007        
        for (element <- elementUnits) {
            element.elementWeightLevel = element.elementWeightLevel * (1.0 - decayFactorForBacteriaFromWater)
        }
    }
    
    
    /**
     * Process all chemical reactions.
     * @param sim
     */
    def chemicalReactions(sim:SimulationObjects) : Unit = {
        
        // If the organism is dead, then
        // we don't need to process chemical reaction
        if (!this.alive) {
            return
        }
        this.chemicalReactionWithSun(null)
        this.chemicalReactionWithWater(null)
        
        for (element <- elementUnits) {
            val checkForHalfLife = (element.elementWeightLevel / element.initElementLevel)
            if (checkForHalfLife > minColorChangeThreshold) {
                val levelIntensity1 = this.color.getRed * (element.elementWeightLevel / element.initElementLevel) 
                val levelIntensity2 = this.color.getGreen * (element.elementWeightLevel / element.initElementLevel)
                val levelIntensity3 = this.color.getBlue  * (element.elementWeightLevel / element.initElementLevel)
                element.color = new Color(math.min(255, levelIntensity1.toInt), math.min(255, levelIntensity2.toInt), math.min(255, levelIntensity3.toInt))                               
            }
        } // End of for
    }
    
    /**
     * Process the dna cell at each step of the simulation.
     */
    def onStepSimulationProcessCell() : Unit = {
        if (this.alive) {            
            this.produceProteins
        } else {
            // Cell is dead, do no further processing [exit]
            this.deadChemicalUnits
            return
        } // End of the if
        
        this.rebuildChemicalBonds
        this.giveBirthSelfReplicate       
                             
        ///////////////////////////////////////////////////
        // Process chemical weight, calculate loss
        ///////////////////////////////////////////////////
        var sumWeightDefault:Double = 0.0
        var sumWeightCurrent:Double = 0.0
        for (element <- elementUnits) {
            sumWeightDefault = sumWeightDefault + element.initElementLevel
            sumWeightCurrent = sumWeightCurrent + element.elementWeightLevel
        } // End of for
        this.totalDefaultElementWeightBacteria = sumWeightDefault
        this.currentElementWeightBacteria = sumWeightCurrent        
        this.lossInWeight = -1.0 * (100.0 - ((this.currentElementWeightBacteria / this.totalDefaultElementWeightBacteria)*100.0))
        
        ///////////////////////////////////////////////////
        // Check for death state
        ///////////////////////////////////////////////////
        if (this.lossInWeight < maxWeightLossPerc) {
            // The cell has died because chemical weight/bonds are too low
            this.bactericaAliveState = false
        }               
    }
    
    private def giveBirthSelfReplicate() : Unit = {
        if (this.getCellularRespirationEnergyLevel < 0) {
            return
        }
        val energyForBirthWithOffsetAge = energyLevelLifeSpanForBirth + (randInstance.nextDouble * 0.15)
        if (!hasChildAvailableThroughReplication && this.percEnergyLevelFromStart < energyForBirthWithOffsetAge) {            
            hasChildAvailableThroughReplication = true
        }        
    }

    /**
     * If the cell has enough energy, rebuild bonds for cell walls.
     */
    private def rebuildChemicalBonds : Unit = {
         if (!this.alive) {            
            return
         }
         if (this.getCellularRespirationEnergyLevel < 0) {
            // Energy level set too low, can't rebuild cell chemical bonds
            return
         }         
         // Add chemical bonds because we have energy to rebuild
         // our bacterial cell walls
         val chemicalBondLevelRandShift = addedChemicalWeightOnRebuild * 1.2 * randInstance.nextDouble( )
         for (element <- elementUnits) {
            element.elementWeightLevel = element.elementWeightLevel + chemicalBondLevelRandShift
         } // End of for                 
         val energyLevelRandShift = energyLevelForRebuild * 1.2 * randInstance.nextDouble( )         
         // Reduce energy level after operation
         cellularRespirationEnergyLevel = cellularRespirationEnergyLevel - energyLevelForRebuild - energyLevelRandShift
         this.percEnergyLevelFromStart = cellularRespirationEnergyLevel / this.initCellularRespirationEnergyLevel
    }
    
    /**
     * Produce/synthesize proteins based on the DNA data.
     * Invokes process DNA.
     */
    def produceProteins() : Unit = {
        processDNA
        onStepSetSystemTraits
    }
    
    /**
     * Process DNA.
     */
    def processDNA() : Unit = {
        val parser = new DNATranslationParser(this.getDNA)
        parser.parse        
        // Validate the DNA, invalid values equal instant death
        bactericaAliveState = getDNA.validateDNA
            
    }   
    
    def hasConsumeForReplication : Boolean = {  hasChildAvailableThroughReplication && numChildAvailableThroughReplication > 0 }
    
    /**
     * Give birth, create new cell.
     * If child available, consume/replaceand add to core list.
     */
    def consumeChildCellReplication : LivingEntityCell = {
        if (numChildAvailableThroughReplication <= 0) {
            // Not available to consume
            return null
        }
        if (this.getCellularRespirationEnergyLevel < 0) {
            return null
        }
        
        // Replicate given a certain energy level
        // It takes about 25% of energy to replicate
        val unitsEnergyToRemoveBirth = 0.22 * this.getCellularRespirationEnergyLevel
        cellularRespirationEnergyLevel = cellularRespirationEnergyLevel - unitsEnergyToRemoveBirth
        
        val sz = this.getMutableSize - 1        
        val finder = new BirthLocationFinder(sz, this.pos)
        val resForLoc = finder.findOffsetForBirthLocation        
        val newPos = (this.pos._1 + resForLoc._1, this.pos._2 + resForLoc._2)        
        val newBacteriaChildReplicate = new Bacteria(this.getMutableSize, newPos)        
        this.onInitializeReplicate(newBacteriaChildReplicate)
        
        val randIdForChild = randInstance.nextInt(1000)
        newBacteriaChildReplicate.personalCellName = "child-gen-" + newBacteriaChildReplicate.mutatationsPassedOnThruGenerations + "-x" + randIdForChild + "-parentPos[" + this.pos + "]"  
        
        // Consume our available child create
        hasChildAvailableThroughReplication = false;
        numChildAvailableThroughReplication = 0
        newBacteriaChildReplicate
    }
    
    /**
     * Mutate DNA during the replication process.
     * We don't change the dna of the parent cell.
     * We only change the dna of the target child.
     *
     * @param dna
     * @return
     */
    def mutateDNAOnReplication(targetCell:LivingEntityCell) : DNA = {
        import Genes._
        import DNABase1._
        
        val bacteriaForReplicate = targetCell.asInstanceOf[Bacteria]
        bacteriaForReplicate.getDNA.dnaSequenceStrand1 = this.dna.copyStrand1 
        bacteriaForReplicate.getDNA.dnaSequenceStrand2 = this.dna.copyStrand2
        
        val chanceRed = randInstance.nextDouble
        val chanceBlue = randInstance.nextDouble
                
        // Blue has a lower chance, check it first
        var passingOnMutation = false
        if (chanceRed < chanceMutationForRed) {
            passingOnMutation = true
            this.mutatationsOnBirth = this.mutatationsOnBirth + 1            
            bacteriaForReplicate.getDNA.dnaSequenceStrand1 = bacteriaForReplicate.getDNA.dnaSequenceStrand1.updated(16, DominantRedColor)
            bacteriaForReplicate.cellularRespirationEnergyLevel = (this.getCellularRespirationEnergyLevel * 2.1) 
        } else if (chanceBlue < chanceMutationForBlue) {
            passingOnMutation = true
            this.mutatationsOnBirth = this.mutatationsOnBirth + 1
            bacteriaForReplicate.getDNA.dnaSequenceStrand1 = bacteriaForReplicate.getDNA.dnaSequenceStrand1.updated(16, DominantTraitBlueColor)
            bacteriaForReplicate.cellularRespirationEnergyLevel = (this.getCellularRespirationEnergyLevel * 5.1)            
        }   
                                       
        // Pass on mutation information
        bacteriaForReplicate.passingOnMutationFromParentToChild = passingOnMutation
        bacteriaForReplicate.mutatationsPassedOnThruGenerations = this.mutatationsPassedOnThruGenerations + 1
        return bacteriaForReplicate.getDNA
    }
    
    /**
     * Set immutable system traits on startup.  Must be called after DNA is processed.
     */
    def setImmutableSystemTraits : Unit = {
        // Set the size, but the size will not change in the future //
        this.mutableSize = sizeTraitToSystemImmutable(this.getDNA.dnaMemory.proteinMemory(wordToInt(Genes.ProteinSize)))        
        this.color = colorBaseTraitToSystemMutable(this.getDNA.dnaMemory.proteinMemory(wordToInt(Genes.ProteinBaseColor)))        
        // Additional system traits
        this.initCellularRespirationEnergyLevel = cellularRespirationEnergyLevel               
    }
    
    /**
     * On step simulation, set system traits, mutable traits.
     * This may also be called at startup.  Must be called after DNA is processed.
     */
    def onStepSetSystemTraits : Unit = {
                  
    }
    
    /**
     * On initialize after replication
     */    
    def onInitializeReplicate(targetCellToReplication:LivingEntityCell) : Unit  = {        
        if (targetCellToReplication == null) {
            return 
        }     
        val bacteriaForReplicate = targetCellToReplication.asInstanceOf[Bacteria]
        // Copy the DNA
        mutateDNAOnReplication(bacteriaForReplicate)
        bacteriaForReplicate.processDNA
        bacteriaForReplicate.setImmutableSystemTraits
        bacteriaForReplicate.onStepSetSystemTraits
        bacteriaForReplicate.setElementPositions
    }
    
    /**
     * On initialize.
     */
    def onInitialize() : Unit = {
        // Process DNA initially
        processDNA
        setImmutableSystemTraits
        onStepSetSystemTraits
        setElementPositions                
    }
    
    /**
     * Set initial element positions.
     */
    def setElementPositions() : Unit = {        
        if (!this.alive) {
            deadChemicalUnits
            return
        }                 
        // Create initial chemical elementUnits, append to list.        
        val rand = new java.util.Random(System.nanoTime + 300)
        // We expect an even number for the size
        // We will split the size in half and set the width and height positions.
        val halfSizeForWidthHeight = (getMutableSize / 2).toInt  
        elementUnits = List()        
        for (j <- 0 until getMutableSize) {
            for (i <- 0 until getMutableSize) {
                val subWeight = rand.nextInt(randInitialChemicalWeight)
                val x = pos._1 - halfSizeForWidthHeight + i
                val y = pos._2 - halfSizeForWidthHeight + j
                val element = new ChemicalElementUnit(minElementWeightBacteria, Element.BacteriaWallProteinElement, (x, y))
                // Add loss of weight after creation
                element.elementWeightLevel = element.elementWeightLevel - subWeight                
                val levelIntensity1 = this.color.getRed * (element.elementWeightLevel / element.initElementLevel) 
                val levelIntensity2 = this.color.getGreen * (element.elementWeightLevel / element.initElementLevel)
                val levelIntensity3 = this.color.getBlue  * (element.elementWeightLevel / element.initElementLevel)
                element.color = new Color(Math.min(255, levelIntensity1.toInt), Math.min(255, levelIntensity2.toInt), Math.min(255, levelIntensity3.toInt))               
                elementUnits = element :: elementUnits
            }
        }        
    }
           
    def getDNA() : DNA = { dna }
    def sizeElements : Int = { return elementUnits.length }        
    def alive() : Boolean = { return bactericaAliveState }    
    def getMutableSize() : Int = { return mutableSize }    

    def getPassingOnMutationFromParentToChild = { passingOnMutationFromParentToChild }
    
    /**
     * Set chemical unit state if dead
     */
    private def deadChemicalUnits() : Unit = {
        if (alive) {
            return
        }
        elementUnits = List()
        val halfSizeForWidthHeight = (deadSize / 2).toInt               
        for (j <- 0 until deadSize) {
            for (i <- 0 until deadSize) {                    
                val x = pos._1 - halfSizeForWidthHeight + i
                val y = pos._2 - halfSizeForWidthHeight + j
                val element = new ChemicalElementUnit(1, Element.BacteriaWallProteinElement, (x, y))                                    
                element.color = new Color(210, 210, 210)               
                elementUnits = element :: elementUnits
            }
        }
    }
    
    /////////////////////////////////////////////////////////////////
    // Init DNA sequence:
    /////////////////////////////////////////////////////////////////
        
    /**
     * Create the DNA object and DNA sequence .
     * 
     * @return
     */
    def initDNA() : DNA = {
        import Genes._
        import DNABase1._
        
        val dna = new DNA
        dna.dnaSequenceStrand1 = List()
        
        val memloc2 = (G,A,A,G)
        val memloc3 = (G,A,A,T)
                        
        /////////////////////////////////////////
        // Begin set memory for trait size [1]
        /////////////////////////////////////////
        
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1

        dna.dnaSequenceStrand1 = SEQLOAD :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = DominantTraitSizeTall :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = memloc2 :: dna.dnaSequenceStrand1 
                        
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
                
        dna.dnaSequenceStrand1 = SEQSET :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = ProteinSize :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = memloc2 :: dna.dnaSequenceStrand1        
        
        /////////////////////////////////////////
        // Begin set memory for trait color [2]
        /////////////////////////////////////////
        
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        
        dna.dnaSequenceStrand1 = SEQLOAD :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = DominantTraitBlueColor :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = memloc3 :: dna.dnaSequenceStrand1 
                        
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
                
        dna.dnaSequenceStrand1 = SEQSET :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = ProteinBaseColor :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = memloc3 :: dna.dnaSequenceStrand1
        
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1       
        dna.dnaSequenceStrand1 = SEQSTART :: dna.dnaSequenceStrand1
                               
        dna.dnaSequenceStrand1 = dna.dnaSequenceStrand1.reverse        
        return dna
    }
    
} // End of the Class //