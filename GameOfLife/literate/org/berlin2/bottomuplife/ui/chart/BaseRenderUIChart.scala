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
 * BaseRenderUIChart.scala
 * Feb 21, 2011
 * bbrown
 * Contact: Berlin Brown <berlin dot brown at gmail.com>
 */
package org.berlin2.bottomuplife.ui.chart

import java.awt.{ Image, Color, Dimension, Graphics, Graphics2D, Point, geom }

/**
 * Render delegate for charts.
 * 
 * @author bbrown
 *
 */
class BaseRenderUIChart {

    def everyNumDataPoints = 24
    
    /**
     * Scale a lot to allow multiple values on chart
     */
    var scaleYHeightChart = 0.14
    
    /**
     * Render life chart data points.
     *     
     * @param g
     * @param lifeChart
     */
    def renderLifeChart(g:Graphics, lifeChart:BaseUILifeChart) : Unit = {
        val aliveColor:Color = new Color(0,255,0)
        val deadColor:Color = new Color(255,0,0)
        val mutColor:Color = new Color(220,190,0)
                
        lifeChart.lifeAliveData.synchronized {
            val startYHeightForPoint = lifeChart.winYPos + lifeChart.winYHeight - 2 
            val startX = lifeChart.winXPos+2
            var i = 0                       
            
            val data1 = rebuildAvgDataPointList(lifeChart.lifeDeadData.reverse)
            val data2 = rebuildAvgDataPointList(lifeChart.lifeMutationData.reverse)
            val data3 = rebuildAvgDataPointList(lifeChart.lifeAliveData.reverse)
            
            if ((data1.length == data2.length) && (data2.length == data3.length)) {
                                
                for (dataPoint <- data1) {          
                    val yposOffset1 = (startYHeightForPoint - dataPoint.toInt)                                        
                    val point2 = data2(i)
                    val point3 = data3(i)
                    
                    g.setColor(aliveColor)
                    g.drawLine(startX + i, startYHeightForPoint, startX + i, yposOffset1 - point2.toInt - point3.toInt)
                    
                    g.setColor(mutColor)
                    g.drawLine(startX + i, startYHeightForPoint, startX + i, yposOffset1 - point2.toInt)
                                        
                    g.setColor(deadColor)
                    g.drawLine(startX + i, startYHeightForPoint, startX + i, yposOffset1)
                                                                                                   
                    // Continue to next point
                    i = i + 1
                }
            }
        } // End of sync               
    }
    
    /**
     * 
     * @param list
     * @return
     */
    def rebuildAvgDataPointList(list:List[Double]) : List[Double] = {
        var mainDataPointList:List[Double] = List()        
        var sumVal = 0.0
        var numPointsCollect = 1
        for (dataPoint <- list.reverse) {
            sumVal = sumVal + dataPoint
            numPointsCollect = numPointsCollect + 1
            if (numPointsCollect >= everyNumDataPoints) {
                val findAvg = sumVal / numPointsCollect 
                sumVal = 0.0
                numPointsCollect = 1
                var dataValCommit = findAvg*scaleYHeightChart
                if (dataValCommit > 30) {
                    dataValCommit = 30
                }
                mainDataPointList = dataValCommit :: mainDataPointList
            }
        }
        mainDataPointList
    }
    
} // End of the class //