/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.controllers;

import bayesprediction.models.Candle;
import bayesprediction.models.IfaceClusterInterval;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author EPJ
 */
public class CandleCollection implements Iterable<Candle>, Iterator<Candle>{

    protected final int   MEMORY_BLOCK = 15;
    protected Candle[]    aData;
    protected int         mSize;
    protected int         curItItem;

    public CandleCollection() {
        mSize = 0;
        initObj();
    }
    
    public CandleCollection(Candle[] c, int size) {
        mSize = size;
        aData = new Candle[size];
        System.arraycopy(c, 0, aData, 0, size);
    }
    
    private void initObj()
    {
        mSize = 0;
        aData = new Candle[MEMORY_BLOCK];
    }
    
    private void resizeArray()
    {
        Candle[] n_aData = new Candle[aData.length + MEMORY_BLOCK];
        System.arraycopy(aData, 0, n_aData, 0, mSize);
        
        aData = n_aData;
    }
    
    public void add(Candle c)
    {
        if (mSize == aData.length)
            resizeArray();
        
        aData[mSize++] = c;
    }
    
    public int size()
    {
        return mSize;
    }
    
    public boolean isEmpty()
    {
        return mSize == 0;
    }
    
    private void checkIfHasData()
    {
        if (isEmpty())
            throw new IndexOutOfBoundsException("There is no item in the collection");
    }
    
    public Candle getFirst()
    {
        checkIfHasData();
        return aData[0];
    }
    
    public Candle getLast()
    {
        checkIfHasData();
        return aData[mSize - 1];
    }
    
    public int getLastLessEqualThan(Date dRef)
    {
        int high;
        int mid;
        int low;
        int p = -1;
        Candle c;
        
        low = 0;
        high = mSize - 1;
        mid = ((high - low) / 2) + low;
        
        while (low <= high)
        {
            c = aData[mid];
            if (c.getTimestamp().getTime().compareTo(dRef) <= 0)
            {
                p = mid;
                low = mid + 1;
            }
            else
                high = mid - 1;
            
            mid = ((high - low) / 2) + low;
        }
        
        return p;
    }
    
    public int getLastLessEqualThan(Candle target)
    {
        int high;
        int mid;
        int low;
        int p = -1;
        Candle c;
        
        low = 0;
        high = mSize - 1;
        mid = ((high - low) / 2) + low;
        
        while (low <= high)
        {
            c = aData[mid];
            if (c.compareTo(target) <= 0)
            {
                p = mid;
                low = mid + 1;
            }
            else
                high = mid - 1;
            
            mid = ((high - low) / 2) + low;
        }
        
        return p;
    }
    
    public int getFirstLargeEqualThan(Date dRef)
    {
        int high;
        int mid;
        int low;
        int p = -1;
        Candle c;
        
        low = 0;
        high = mSize - 1;
        mid = ((high - low) / 2) + low;
        
        while (low <= high)
        {
            c = aData[mid];
            if (c.getTimestamp().getTime().compareTo(dRef) >= 0)
            {
                p = mid;
                high = mid - 1;
            }
            else
                low = mid + 1;
            
            mid = ((high - low) / 2) + low;
        }
        
        return p;
    }
    
    public int getFirstLargeEqualThan(Candle target)
    {
        int high;
        int mid;
        int low;
        int p = -1;
        Candle c;
        
        low = 0;
        high = mSize - 1;
        mid = ((high - low) / 2) + low;
        
        while (low <= high)
        {
            c = aData[mid];
            if (c.compareTo(target) >= 0)
            {
                p = mid;
                high = mid - 1;
            }
            else
                low = mid + 1;
            
            mid = ((high - low) / 2) + low;
        }
        
        return p;
    }
    
    public CandleCollection subSet(int fIndex, int lIndex)
    {
        Candle      cur;
        Candle      prev;
        Candle      next;
        int         i;
        int         size = lIndex - fIndex + 1;
        Candle[]    subSet = new Candle[size];
        
        System.arraycopy(aData, fIndex, subSet, 0, size);
        
        if (fIndex - 1 < 0)
            prev = null;
        else
            prev = aData[fIndex - 1];
        
        cur = aData[fIndex];
        for (i = fIndex + 1; (i <= lIndex/* + 1*/) && (i < mSize); i++)
        {
            next = aData[i];
            cur.setPrevious(prev);
            cur.setNext(next);
            cur.recalc();
            
            prev = cur;
            cur = next;
        }
        cur.setPrevious(prev);
        cur.setNext(null);
        cur.recalc();
        
        return new CandleCollection(subSet, size);
    }
    
    public void quickSort()
    {
        quickSort(0, mSize-1);
    }
    
    private void quickSort(int low, int high)
    {
        Candle  pivot;
        Candle  tmp;
        int     tmpLow;
        int     tmpHigh;
        int     mid;
        
        tmpLow = low;
        tmpHigh = high;
        mid = (low + high) / 2;
        pivot = aData[mid];
        
        while (tmpLow <= tmpHigh)
        {
            while ((aData[tmpLow].compareTo(pivot) < 0) && (tmpLow < high))
                tmpLow++;
            
            while ((pivot.compareTo(aData[tmpHigh]) < 0) && (tmpHigh > low))
                tmpHigh--;
            
            if (tmpLow <= tmpHigh)
            {
                tmp = aData[tmpLow];
                aData[tmpLow] = aData[tmpHigh];
                aData[tmpHigh] = tmp;
                tmpLow++;
                tmpHigh--;
            }
        }
        
        if (low < tmpHigh)
            quickSort(low, tmpHigh);
        if (tmpLow < high)
            quickSort(tmpLow, high);
    }

    @Override
    public String toString() {
        String msg = "";
        int line = 0;
        
        for (Candle c : aData)
            msg += "[" + String.format("%05d", ++line) + "]" + c + "\n";
        
        return msg;
    }
    
    public Candle get(int p)
    {
        if ((p < 0) || (p >= mSize))
            throw new IndexOutOfBoundsException("Invalid index!");
        
        return aData[p];
    }
    
    public Candle[] toArray()
    {
        Candle[] n_aData = new Candle[mSize];
        System.arraycopy(aData, 0, n_aData, 0, mSize);
        
        return n_aData;
    }

    @Override
    protected Object clone(){
        try
        {
            super.clone();
        } catch (CloneNotSupportedException e)
        {
            //???
        }
        CandleCollection clone = new CandleCollection(aData, mSize);
        
        return clone;
    }

    @Override
    public Iterator<Candle> iterator() {
        curItItem = -1;
        return this;
    }

    @Override
    public boolean hasNext() {
        return (curItItem < mSize - 1);
    }

    @Override
    public Candle next() {
        return get(++curItItem);
    }
    
}
