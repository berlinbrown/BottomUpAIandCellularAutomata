/**
 * Copyright (c) 2006-2010 Berlin Brown All Rights Reserved
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
 * DNA.scala
 * Feb 7, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 * 
 * keywords: alife, langton, gameoflife, cellularautomata, ca, scala
 */
package org.berlin2.bottomuplife.life

import java.awt.Color

import org.berlin2.bottomuplife.life.BaseLifeComponents._
import DNABase1._
 
/**
 * @author bbrown
 *
 */
object Genes {    
   
    // A = 1, G = 2, C = 3, T = 4
    
    def SEQSTART:DNAWord  = (A,A,A,A)    
    def SEQLOAD:DNAWord   = (A,G,C,A)   // Load into memory
    def SEQLOAD2:DNAWord  = (G,G,C,A)   // Load into memory
    def SEQADD:DNAWord    = (A,G,C,G)
    def SEQSUB:DNAWord    = (A,G,C,C)   // Set protein value
    def SEQSET:DNAWord    = (A,G,C,T)      
    def SEQUNLOAD:DNAWord = (A,G,A,A) 
    def SEQSCALEUP:DNAWord   = (A,G,A,G) 
    def SEQSCALEDOWN:DNAWord = (A,G,A,C)  
    def SEQCLEAR:DNAWord  = (A,G,A,T)
       
    def ProteinBaseColor:DNAWord = (C,C,A,A) 
    def ProteinSize:DNAWord = (C,C,A,G)
    def ProteinSynthesize:DNAWord = (C,C,A,T)
    def ProteinEnergy:DNAWord = (C,C,A,C)
    
    def DominantRedColor:DNAWord  = (A,G,C,T)
    def DominantRedColor2:DNAWord = (A,G,C,C)
    def DominantTraitGreenColor:DNAWord = (A,G,C,A)
    def DominantTraitBlueColor:DNAWord  = (A,G,C,G)     
    def DominantTraitLightRedColor:DNAWord    = (A,G,A,T)
    def DominantTraitLightGreenColor:DNAWord  = (A,G,G,T)
    def DominantTraitLightBlueColor:DNAWord   = (A,G,C,T)   
    
    def RecessiveTraitDarkRedColor:DNAWord    = (A,A,C,T)
    def RecessiveTraitDarkGreenColor:DNAWord  = (A,G,C,T)
    def RecessiveTraitDarkBlueColor:DNAWord   = (A,C,C,T)     
    
    def DominantTraitSizeMedium:DNAWord= (G,C,C,A)
    def DominantTraitSizeTall:DNAWord   = (G,C,C,G)        
    def RecessiveTraitSizeLargeTall:DNAWord = (G,C,C,C)
    def RecessiveTraitSizeSmall:DNAWord = (G,C,C,T)         
    def DominantTraitProteinSynthesizeStrong:DNAWord  = (A,A,G,A)
    def DominantTraitProteinSynthesizeStrong2:DNAWord = (A,A,G,T)    
    def DominantTraitEnergyHigh:DNAWord = (C,A,G,A)
    def DominantTraitEnergyMed:DNAWord  = (C,A,G,T)    
    def RecessiveTraitEnergyLow:DNAWord = (C,A,G,G)
    def RecessiveTraitEnergyTooHigh:DNAWord = (C,A,G,C)
    
} // End of Object 

/**
 * Convert the trait into attributes understandable by the system 
 * @author bbrown
 *
 */
object TraitToSystem {
    
    /**
     * Size trait to system, set on init.  This will not 
     * change with mutations.
     */
    def sizeTraitToSystemImmutable(dnaTrait:Int) : Int = {        
        if (dnaTrait.equals(wordToInt(Genes.DominantTraitSizeMedium))) {
            return 4
        } else if (dnaTrait.equals(wordToInt(Genes.DominantTraitSizeTall))) {
            return 6
        } else {       
            return 0
        }        
    }
    
    /**
     * Color trait to color, mutable can change during protein synthesis.
     */
    def colorBaseTraitToSystemMutable(dnaTrait:Int) : java.awt.Color = {                      
         if (dnaTrait.equals(wordToInt(Genes.DominantRedColor))) {
            return new Color(212, 73, 32)
        } else if (dnaTrait.equals(wordToInt(Genes.DominantRedColor2))) {
            return new Color(212, 73, 32)
        } else if (dnaTrait.equals(wordToInt(Genes.DominantTraitBlueColor))) {
           return new Color(73, 32, 212)
        } else if (dnaTrait.equals(wordToInt(Genes.DominantTraitGreenColor))) {
            return new Color(53, 212, 32)
        } else {       
            // Default to grey on error
            return new Color(50, 50, 50)
        } 
    }
    
} // End of object //

trait DNAType {
    def validateDNA : Boolean
}

/**
 * The DNA represents the CODE in this simulation.
 * We will decode the DNA code for protein synthesis.
 * 
 * For the bacteria cell, DNA can effect:
 * 
 * Size, weight, energy level, color, reproduction rate, 
 * food consumption rate, ability to handle water, sunlight/temperatures
 * 
 * @author bbrown
 *
 */
class DNA extends DNAType {       
    
    /**
     * DNA Memory. 
     */
    class DNAMemory {
        import scala.collection.mutable.Map
        import scala.collection.mutable.HashMap
        
        val proteinMemory:Map[Int, Int] = new HashMap[Int, Int]()                        
        val dataMemory:Map[Int, Int] = new HashMap[Int, Int]()
        
        {                           
            proteinMemory(wordToInt(Genes.ProteinBaseColor)) = 0 
            proteinMemory(wordToInt(Genes.ProteinSize)) = 0
            proteinMemory(wordToInt(Genes.ProteinSynthesize)) = 0
            proteinMemory(wordToInt(Genes.ProteinEnergy)) = 0
        }
                
    }       
    
    val dnaMemory:DNAMemory = new DNAMemory
    
    /**
     * The DNA sequence consists of a sequence of words.
     * @param x$1
     */
    var dnaSequenceStrand1:List[DNAWord] = List()
    var dnaSequenceStrand2:List[DNAWord] = List()
    
    /**
     * Simple validation on the DNA memory.
     * The DNA is invalid if the proteins don't have values.
     */
    def validateDNA() : Boolean = {
        val chk1 = this.dnaMemory.proteinMemory(wordToInt(Genes.ProteinBaseColor)) != 0 
        val chk2 = this.dnaMemory.proteinMemory(wordToInt(Genes.ProteinSize)) != 0
        val chk3 = this.dnaMemory.proteinMemory(wordToInt(Genes.ProteinSynthesize)) != 0
        val chk4 = this.dnaMemory.proteinMemory(wordToInt(Genes.ProteinEnergy)) != 0
        return chk1 || chk2 || chk3 || chk4
    }
    
    /**
     * Copy dna strand 1
     * @return
     */
    def copyStrand1() : List[DNAWord] = {
        var newList:List[DNAWord] = List()
        for (oldDnaWord <- this.dnaSequenceStrand1) {
            val copyWord = (oldDnaWord._1, oldDnaWord._2, oldDnaWord._3, oldDnaWord._4) 
            newList = copyWord :: newList            
        }
        return newList.reverse
    }
    
    /**
     * Copy dna strand 1
     * @return
     */
    def copyStrand2() : List[DNAWord] = {
        var newList:List[DNAWord] = List()
        for (oldDnaWord <- this.dnaSequenceStrand2) {
            newList = oldDnaWord :: newList
        }
        newList.reverse
    }
    
    /**
     * Convert the DNA sequence into a word.
     */    
    override def toString : String = {
        val buf = new StringBuilder
        for (base <- dnaSequenceStrand1) {
            buf.append(wordToStr(base)).append(" ")          
        }
        buf.toString
    }    
    
    /**
     * Convert the DNA sequence into a word.
     */    
    def toStringFormatFullVertical () : String = {
        val buf = new StringBuilder
        var i = 0
        buf.append("DNA Strand1 [FormatFullVertical)\n")
        for (base <- dnaSequenceStrand1) {
            buf.append(i).append(":")
            buf.append(wordToStr(base)).append("\n")
            i = i + 1
        }
        buf.toString
    }    
               
} // End of the class //