/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.controllers;

import bayesprediction.models.CandleClosePercentDiff;
import java.util.Calendar;
import java.util.Iterator;

/**
 *
 * @author EPJ
 */
public class BayesAnalysis {
    protected Calendar                    startTime;
    protected Calendar                    endTime;
    protected CandleCollection            cCol;
    protected CandleCollection            sCol;
    protected ClusterCandleCollection     clusters;
    protected int                         iStart;
    protected int                         iEnd;
    protected int                         nClusters;

    public BayesAnalysis(Calendar startTime, Calendar endTime, CandleCollection cCol, int clusters) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.cCol = cCol;
        this.clusters = new ClusterCandleCollection();
        this.nClusters = clusters;
        init();
    }

    private void init()
    {
        int p_s;
        int p_e;
        
        p_s = cCol.getFirstLargeEqualThan(startTime.getTime());
        p_e = cCol.getLastLessEqualThan(endTime.getTime());
        
        // If the start position is greater than the end position
        if (p_s > p_e)
        {
            // Throw some exception here...
            System.out.println("Start position must be greater than end position");
            return;
        }
        
        // Store the start and the end position of the analysed window.
        iStart = p_s;
        iEnd = p_e;
        
        cCol = cCol.subSet(p_s, p_e);
        sCol = (CandleCollection) cCol.clone();
        sCol.quickSort();
        
        createClusters();
        processClusters();
    }
    
    private void createClusters()
    {
        int zPos;
        int wCluster;
        int lastEnd;
        int iStart;
        int iEnd;
        int i;
        int wClusterCalc;
        ClusterCandle cInt;
        Iterator<ClusterCandle> it;

        clusters = new ClusterCandleCollection();
        zPos = sCol.getFirstLargeEqualThan(new CandleClosePercentDiff(0));
        
        // There is only high or low values, throw some exception
        if (zPos == -1)
        {
            System.out.println("Invalid data analysed.");
            return;
        }
        
        wClusterCalc = sCol.size() / nClusters;
        lastEnd = zPos;
        while (lastEnd > 0)
        {
            iEnd = lastEnd;
            iStart = iEnd - wClusterCalc;
            if (iStart < 0)
                iStart = 0;
            wCluster = iEnd - iStart + 1;
            
            /* If the cluster is small than 1, join it with the last cluster. */
            if (wCluster <= 1)
            {
                cInt = clusters.get(clusters.size() - 1);
                cInt.setMinValue(sCol.get(0));
                break;
            }
            
            cInt = new ClusterCandle(cCol, sCol.get(iStart), sCol.get(iEnd), true, false);
            clusters.add(cInt);
            
            lastEnd = iStart;
        }
        // Do an invertion to keep the ascending order.
        clusters.inverse();
        
        lastEnd = zPos;
        while (lastEnd < sCol.size() - 1)
        {
            iStart = lastEnd;
            iEnd = iStart + wClusterCalc;
            if (iEnd > sCol.size() -1)
                iEnd = sCol.size() - 1;

            wCluster = iEnd - iStart + 1;
                
            /* If the cluster is small than 1, join it with the last cluster. */
            if (wCluster <= 1)
            {
                cInt = clusters.get(clusters.size() - 1);
                cInt.setMaxValue(sCol.get(clusters.size()-1));
                cInt.setMaxInclusive(true);
                break;
            }
            
            cInt = new ClusterCandle(cCol, sCol.get(iStart), sCol.get(iEnd), true, 
                (iEnd == sCol.size() - 1));
            clusters.add(cInt);
            
            lastEnd = iEnd;
        }
        
        // Set the conditional clusters
        i = 0;
        it = clusters.iterator();
        while (it.hasNext())
        {
            cInt = it.next();
            cInt.setId(i++);
            cInt.setConditionalClusters(clusters);
        }
    }

    private void processClusters()
    {
        Iterator<ClusterCandle> it = clusters.iterator();
        ClusterCandle c;
        
        while (it.hasNext())
        {
            c = it.next();
            c.calcConditionalProb();
        }
    }
    
    public ClusterCandleCollection getClusters()
    {
        return clusters;
    }

    public int getIndexStart() {
        return iStart;
    }

    public int getIndexEnd() {
        return iEnd;
    }
    
    public CandleCollection getCandleCollection()
    {
        return cCol;
    }
}
