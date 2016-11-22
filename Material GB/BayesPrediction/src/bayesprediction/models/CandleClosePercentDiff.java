/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.models;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author EPJ
 */
public class CandleClosePercentDiff extends Candle {
    private final int   NOT_COMPUTED_YET = -1;
    protected Double    percDiff;
    
    public CandleClosePercentDiff(double high, double low, double open, 
            double close, long volume, Calendar timestamp) {
        super(high, low, open, close, volume, timestamp);
        init();
    }
    
    public CandleClosePercentDiff(double high, double low, double open, 
            double close, long volume, Calendar timestamp, Candle previous, Candle next) {
        super(high, low, open, close, volume, timestamp, previous, next);
        init();
    }
    
    public CandleClosePercentDiff(Candle c)
    {
        super(c.getHigh(), c.getLow(), c.getOpen(), c.getClose(), 
            c.getVolume(), c.getTimestamp(), c.getPrevious(), c.getNext());
        init();
    }

    public CandleClosePercentDiff(double percDiff) {
        super(0, 0, 0, 0, 0, null);
        this.percDiff = percDiff;
    }
    
    private void init()
    {
        if (this.getPrevious() == null)
            percDiff = (double) NOT_COMPUTED_YET;
        else
            percDiff = ((this.getClose() / this.getPrevious().getClose()) - 1) * 100;
    }

    @Override
    public void recalc() 
    {
        super.recalc(); //To change body of generated methods, choose Tools | Templates.
        init();
    }

    @Override
    public String toString() {
        String m = "";//super.toString();
        m += /*"|*/"Close % diff. = " + String.format("%.07f", percDiff);
        
        return m;
    }

    public Double getPercDiff() {
        return percDiff;
    }

    @Override
    public int compareTo(Candle o) {
        CandleClosePercentDiff other;
        double mPerDiff;
        double oPerDiff;

        if (o instanceof CandleClosePercentDiff)
            other = (CandleClosePercentDiff) o;
        else
            other = new CandleClosePercentDiff(o);
        
        /*if (!(this instanceof CandleClosePercentDiff))
        {
            CandleClosePercentDiff c = new CandleClosePercentDiff(this);
            mPerDiff = c.getPercDiff();
        }
        else*/
        mPerDiff = this.getPercDiff();
        
        oPerDiff = other.getPercDiff();
        if ((mPerDiff == NOT_COMPUTED_YET) || 
            (oPerDiff == NOT_COMPUTED_YET))
            return -1;

        if (mPerDiff > oPerDiff)
            return 1;
        else if (mPerDiff < oPerDiff)
            return -1;
        else
            return 0;
    }
    
}
