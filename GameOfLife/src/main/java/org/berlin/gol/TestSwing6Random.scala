/**
 * Copyright (c) 2006-2011 Berlin Brown.  All Rights Reserved
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
 * Date: 12/15/2009 
 * Description: Extend this customizable Swing wrapper library.
 * 
 * Home Page: http://code.google.com/u/berlin.brown/
 * Doing it wrong example, no refactoring
 * 
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin.gol
import java.awt.{ Color, Graphics, Graphics2D }
import scala.swing.{ MainFrame, Panel, SimpleSwingApplication }

import scala.swing.Swing._
import scala.swing.event._

import java.security.SecureRandom

/**
 * Test swing3, render grid.
 * 
 * @author berlinbrown
 *
 */
object TestSwing6Random extends SimpleSwingApplication {

  def maxWidth  = 400
  def maxHeight = 400 
    
  lazy val ui = new Panel {
      
    background = Color.white
    preferredSize = (maxWidth, maxHeight)
    focusable = true
    
    /**
     * Render the cell grid.
     */
    def renderLine(g: Graphics2D) = {
        val r = new java.util.Random()
        val r2 = SecureRandom.getInstance("SHA1PRNG")
        g.setColor(Color.black)
        for (i <- 0 until 30000) {
            val x = r2.nextInt(400)
            val y = r2.nextInt(200)
            g.drawLine(x, y, x, y)            
        }                                      
        g.setColor(Color.green)
        for (i <- 0 until 30000) {
            val x = r.nextInt(400)
            val y = r.nextInt(200)
            g.drawLine(x, 200 + y, x, 200 + y)            
        }
    }
    
   
    /**
     * Paint component.
     */
    override def paintComponent(g: Graphics2D) = {
      super.paintComponent(g)         
      g.setColor(Color.black)
      renderLine(g)
    }
    
  } // End of Object

  /**
   * Main Frame, entry point.
   */
  def top = new MainFrame {
      peer.setLocation(200, 200)
      title = "Game Of Life"          
      contents = ui     
  }
} // End of Class //
