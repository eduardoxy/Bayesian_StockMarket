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
public class CandleCompareByTimeStamp extends Candle {

    public CandleCompareByTimeStamp(Calendar timestamp) {
        super(0, 0, 0, 0, 0, timestamp);
    }

    public CandleCompareByTimeStamp(Candle c) {
        super(c.getHigh(), c.getLow(), c.getOpen(), c.getClose(), 
            c.getVolume(), c.getTimestamp(), c.getPrevious(), c.getNext());
    }

    @Override
    public int compareTo(Candle o) {
        CandleCompareByTimeStamp other;
        Date dThis;
        Date dOther;
        
        if (o instanceof CandleCompareByTimeStamp)
            other = (CandleCompareByTimeStamp) o;
        else
            other = new CandleCompareByTimeStamp(o);
        
        dThis = this.getTimestamp().getTime();
        dOther = other.getTimestamp().getTime();
        
        if (dThis.compareTo(dOther) < 0)
            return -1;
        else if (dThis.compareTo(dOther) > 0)
            return 1;
        else
            return 0;
    }
}
