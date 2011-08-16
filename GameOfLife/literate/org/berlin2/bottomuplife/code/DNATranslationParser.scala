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
 * TranslationParser.scala
 * Feb 7, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 * 
 * triplets, attributes.
 */
package org.berlin2.bottomuplife.code

import org.berlin2.bottomuplife.life.BaseLifeComponents._
import org.berlin2.bottomuplife.life.BaseLifeComponents.DNABase1._
import org.berlin2.bottomuplife.life.BaseLifeComponents.DNABase1
import org.berlin2.bottomuplife.life.DNA
import org.berlin2.bottomuplife.life.Genes
import org.berlin2.bottomuplife.life.Genes._
import org.berlin2.bottomuplife.life.TraitToSystem._ 

trait DNAParser {
    def parse() : Unit
}

/**
 * Template DNA operations
 * 
 * @author bbrown
 *
 */
class DNATranslationParser(val dna:DNA) extends DNAParser {
    
    var currentDNASeqIndex = 0
    /**
     * Parse the dna strands and set the dna properties.
     * 
     */
    def parse() : Unit = {               
        var countStartSeq = 0
        currentDNASeqIndex = 0
        while (currentDNASeqIndex < dna.dnaSequenceStrand1.length) {
            val dnaStrand:DNAWord = dna.dnaSequenceStrand1(currentDNASeqIndex)            
            if (dnaStrand.equals(SEQSTART)) { countStartSeq = countStartSeq + 1 }                      
            if (countStartSeq == 3) {                
                parseAfterStart
                countStartSeq = 0
            } 
            currentDNASeqIndex = currentDNASeqIndex + 1            
        }
    }
        
    /**
     * Parse command after start correct sequence detected.
     */
    private def parseAfterStart() {
        // A = 1, G = 2, C = 3, T = 4
        if (currentDNASeqIndex >= (dna.dnaSequenceStrand1.length-3)) {
            return
        }
        // Memory location (last)
        val nextInstr1 = dna.dnaSequenceStrand1(currentDNASeqIndex+1)
        val nextInstr2 = dna.dnaSequenceStrand1(currentDNASeqIndex+2)
        // Instruction
        val nextInstr3 = dna.dnaSequenceStrand1(currentDNASeqIndex+3)
                
        if (nextInstr1.equals(SEQLOAD)) {                       
            dna.dnaMemory.dataMemory(wordToInt(nextInstr3)) = wordToInt(nextInstr2)
        } else if (nextInstr1.equals(SEQSET)) {              
            // We cannot set memory if memory already set
            // unless unload called.
            // Write once operation
            
            if (dna.dnaMemory.proteinMemory(wordToInt(nextInstr2)) == 0) {
                dna.dnaMemory.proteinMemory(wordToInt(nextInstr2)) = dna.dnaMemory.dataMemory(wordToInt(nextInstr3))
            }
        }                              
        currentDNASeqIndex = currentDNASeqIndex + 3        
    }
    
    /*
     * AAAA AAAA AAAA (must have full seq, but possible duplicates)
     * -------------------
     * Rules for parsing, you can call SET operation and can only be undone by UNSET.
     * For example, multiple SET calls cannot undo the first SET.  Write only operation.
     *  
     * AGCA - LOAD         OPERATION  ==> NUMBER, MEMORY LOCATION  -- GGCA AGCA    
     * AGCG - ADD          OPERATION  ==> NUMBER, MEMORY LOCATION, STORE BACK IN MEMORY LOCATION
     * AGCC - SUB          OPERATION  ==> NUMBER, MEMORY LOCATION, STORE BACK IN MEMORY LOCATION
     * AGCT - SET          OPERATION  ==> FORPROTEIN --- MEMORY LOCATION  - SET protein trait/value
     * 
     * AGAA - UNLOAD       OPERATION  ==> FORPROTEIN --- MEMORY LOCATION
     * AGAG - SCALEUP
     * AGAC - SCALEDOWN 
     * AGAT - CLEAR        OPERATION  ==> FROM MEMORY MEMORY
     */
    
} // End of the class //