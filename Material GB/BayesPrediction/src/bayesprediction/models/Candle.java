/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.models;

import bayesprediction.controllers.ClusterCandle;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author EPJ
 */
public class Candle implements Comparable<Candle>, IfaceClusterInterval {
    protected double    low;
    protected double    high;
    protected double    open;
    protected double    close;
    protected long      volume;
    protected Calendar  timestamp;
    protected Candle    previous;
    protected Candle    next;

    public Candle(double high, double low, double open, double close, 
            long volume, Calendar timestamp) {
        init(high, low, open, close, volume, timestamp, null, null);
    }
    
    public Candle(double high, double low, double open, double close, 
            long volume, Calendar timestamp, Candle previous, Candle next) {
        init(high, low, open, close, volume, timestamp, previous, next);
    }

    private void init(double high, double low, double open, double close, 
            long volume, Calendar timestamp, Candle previous, Candle next)
    {
        setHigh(high);
        setLow(low);
        setOpen(open);
        setClose(close);
        setVolume(volume);
        setTimestamp(timestamp);
        setPrevious(previous);
        setNext(next);
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public String getTimeStampFormatted()
    {
        SimpleDateFormat df = new MT5DateFormat();
        
        return df.format(this.getTimestamp().getTime());
    }
    
    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public Candle getPrevious() {
        return previous;
    }

    public void setPrevious(Candle previous) {
        this.previous = previous;
    }

    public Candle getNext() {
        return next;
    }

    public void setNext(Candle next) {
        this.next = next;
    }

    public void recalc()
    {
    }
    
    @Override
    public String toString() {
        return "[" + this.getTimeStampFormatted() + "] " + 
            String.format("%.02f", high) + "|" + 
            String.format("%.02f", low) + "|" + 
            String.format("%.02f", open) + "|" + 
            String.format("%.02f", close) + "|" + 
            String.format("%07d", volume);
    }

    @Override
    public int compareTo(Candle o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }

    @Override
    public boolean belongsToCluster(ClusterCandle cluster) {
            Candle cMin = cluster.getMinValue();
            Candle cMax = cluster.getMaxValue();
            int mMin = this.compareTo(cMin);
            int mMax = this.compareTo(cMax);

            return cluster.checkValue(mMin, mMax);
    }
    
}
