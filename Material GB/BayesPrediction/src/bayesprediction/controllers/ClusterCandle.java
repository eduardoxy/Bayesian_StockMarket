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
    private static int              id = -1;
    private int                     mId;
    private Candle                  minValue;
    private Candle                  maxValue;
    private boolean                 minInclusive;
    private boolean                 maxInclusive;
    private final CandleCollection  cCol; //
    private ClusterCandleCollection clusters;
    private List<Candle>            margiMatches;
    private List<List<Candle>>      condMatches;

    public ClusterCandle(CandleCollection cCol, Candle minValue, Candle maxValue, 
        boolean minInclusive, boolean maxInclusive) {
        this.mId = ++id;
        this.cCol = cCol;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        this.margiMatches = null;
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
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
        
        /*msg = "Cluster -> Min " + (isMinInclusive() ? "Inclusive" : "") + "-> <" + minValue + ">" + 
            "Max " + (isMaxInclusive() ? "Inclusive" : "") + "-> <" + maxValue + ">";*/
        msg = minValue + 
                (isMinInclusive() ? " <=" : " <") + " val" + 
                (isMaxInclusive() ? " <=" : " <") + 
                maxValue;
        
        return msg;
    }
    
    public double getMarginalProbability()
    {
        return ((calcMarginalProb(false).size() * 1.0f) / cCol.size());
    }
    
    public List<Float> getConditionalProbability()
    {
        List<Float> c = new ArrayList<>();
        
        List<Candle>            mMatches = calcMarginalProb(true);
        List<List<Candle>>      cMatches = calcConditionalProb();
        Iterator<List<Candle>>  it = cMatches.iterator();
        
        //System.out.println("-> Conditional " + this);
        while (it.hasNext())
        {
            List<Candle> matches = it.next();
            float val = ((matches.size() * 1.0f) / mMatches.size());
            c.add(val);
            //System.out.println(String.format("%.05f", val));
        }
        
        return c;
    }
    
    private List<Candle> calcMarginalProb(boolean toConditional)
    {
        margiMatches = new ArrayList<>();
        Iterator<Candle> it = cCol.iterator();
        Candle c;
        
        while (it.hasNext())
        {
            c = it.next();

            if ((c.belongsToCluster(this)) && ((cCol.getLast() != c) || !toConditional))
                margiMatches.add(c);
        }
        //System.out.println(this + "" + " -> " + margiMatches.size() + "/" + cCol.size());
        
        return margiMatches;
    }
    
    public List<List<Candle>> calcConditionalProb()
    {
        if (condMatches != null)
            return condMatches;
        
        List<Candle>            margMatches = calcMarginalProb(true);
        ClusterCandle           cluster;
        Iterator<ClusterCandle> it = clusters.iterator();
        
        condMatches = new ArrayList<>();
        
        //System.out.println("-> Conditional " + this);
        while (it.hasNext())
        {
            cluster = it.next();
            List<Candle> mMatches = new ArrayList<>();
            condMatches.add(mMatches);
            
            //System.out.println("P(" + cluster + "|" + this + ")");
            Iterator<Candle> itCandle = margMatches.iterator();
            while (itCandle.hasNext())
            {
                Candle c = itCandle.next().getNext();
                if ((c != null) && (c.belongsToCluster(cluster)))
                    mMatches.add(c);
            }
            //double pCond = (((double) mMatches.size() / (double) margMatches.size()) * 100.0);
            //System.out.println("P([" + cluster + "]|[" + this + "]) -> " + mMatches.size() + "/" + margMatches.size());
            //System.out.println("P([" + cluster + "]|[" + this + "]) -> " + String.format("%02.05f", pCond) + "%");
        }
        
        return condMatches;
    }
}
