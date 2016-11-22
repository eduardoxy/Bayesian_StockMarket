/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.controllers;

import bayesprediction.models.Candle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author EPJ
 */
public class ClusterCandle {
    private Candle                  minValue;
    private Candle                  maxValue;
    private boolean                 minInclusive;
    private boolean                 maxInclusive;
    private final CandleCollection  cCol; //
    private ClusterCandleCollection clusters;

    public ClusterCandle(CandleCollection cCol, Candle minValue, Candle maxValue, 
        boolean minInclusive, boolean maxInclusive) {
        this.cCol = cCol;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public Candle getMinValue() {
        return minValue;
    }

    public void setMinValue(Candle minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(Candle maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinInclusive(boolean minInclusive) {
        this.minInclusive = minInclusive;
    }

    public void setMaxInclusive(boolean maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Candle getMaxValue() {
        return maxValue;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }
    
    public void setConditionalClusters(ClusterCandleCollection c)
    {
        this.clusters = c;
    }
    
    public boolean checkValue(int minCompare, int maxCompare)
    {
        boolean retVal;
        
        retVal = (minCompare > 0);
        if ((!retVal) && this.isMinInclusive())
            retVal = (minCompare == 0);
        if (retVal)
            retVal = (maxCompare < 0);
        if ((!retVal) && this.isMaxInclusive())
            retVal = (maxCompare == 0);
        
        return retVal;
    }

    @Override
    public String toString() {
        String msg;
        
        msg = "Cluster -> Min " + (isMinInclusive() ? "Inclusive" : "") + "-> <" + minValue + ">" + 
            "Max " + (isMaxInclusive() ? "Inclusive" : "") + "-> <" + maxValue + ">";
        
        return msg;
    }
    
    public List<Candle> calcMarginalProb()
    {
        List<Candle> matches = new ArrayList<>();
        Iterator<Candle> it = cCol.iterator();
        Candle c;
        
        while (it.hasNext())
        {
            c = it.next();
            if (c.belongsToCluster(this))
                matches.add(c);
        }
        
        //System.out.println(this + " Matches -> " + matches.size());
        return matches;
    }
    
    public List<List<Candle>> calcConditionalProb()
    {
        List<Candle>            margMatches = calcMarginalProb();
        List<List<Candle>>      matches = new ArrayList<>();
        ClusterCandle           cluster;
        Iterator<ClusterCandle> it = clusters.iterator();
        
        System.out.println("-> Conditional " + this);
        while (it.hasNext())
        {
            cluster = it.next();
            List<Candle> mMatches = new ArrayList<>();
            matches.add(mMatches);
            
            //System.out.println("P(" + cluster + "|" + this + ")");
            Iterator<Candle> itCandle = margMatches.iterator();
            while (itCandle.hasNext())
            {
                Candle c = itCandle.next().getNext();
                if ((c != null) && (c.belongsToCluster(cluster)))
                    mMatches.add(c);
            }
            double pCond = (((double) mMatches.size() / (double) margMatches.size()) * 100.0);
            System.out.println("P([" + cluster + "]|[" + this + "]) -> " + String.format("%02.05f", pCond) + "%");
        }
        
        return matches;
    }
}
