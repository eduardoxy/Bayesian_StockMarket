/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.controllers;

import bayesprediction.models.Candle;
import bayesprediction.models.CandleClosePercentDiff;
import bayesprediction.models.MT5DateFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author EPJ
 */
public class LoadMT5CSVFile {
    private static final int    EXPECTED_FIELDS = 7;
    private static final String SEPARATOR = ",";
    private static final int    COL_TIMESTAMP = 0;
    private static final int    COL_OPEN = 1;
    private static final int    COL_HIGH = 2;
    private static final int    COL_LOW = 3;
    private static final int    COL_CLOSE = 4;
    private static final int    COL_VOLUME = 6;
    
    private File f;

    public LoadMT5CSVFile(File f) {
        setFile(f);
    }

    private void setFile(File f) {
        this.f = f;
    }

    public CandleCollection readFile() throws IOException
    {
        FileInputStream     fis = new FileInputStream(f);
        InputStreamReader   isr = new InputStreamReader(fis, "UTF-16");
        BufferedReader      br = new BufferedReader(isr);
        String              line;
        Candle              c;
        CandleCollection    cCol = new CandleCollection();
        
        try
        {
            while ((line = br.readLine()) != null)
            {
                c = breakLine(line);
                if (c != null)
                    cCol.add(c);
            }
        } catch (IOException e)
        {
            // throw some exception if a fails occurs when reading the file
        } finally
        {
            br.close();
        }
        
        return cCol;
    }
    
    private Candle breakLine(String line)
    {
        String      fields[];
        int         fCount;
        double      open, close, high, low;
        long        volume;
        Calendar    timestamp;
        
        fields = line.split(SEPARATOR);
        fCount = fields.length;
        
        // If the fields are different thant the expected
        if (fCount != EXPECTED_FIELDS)
        {
            // Throw some exception
            System.out.println("Error on line -> " + line);
            return null;
        }
        
        open = Double.parseDouble(fields[COL_OPEN]);
        close = Double.parseDouble(fields[COL_CLOSE]);
        high = Double.parseDouble(fields[COL_HIGH]);
        low = Double.parseDouble(fields[COL_LOW]);
        volume = Long.parseLong(fields[COL_VOLUME]);
        timestamp = convertDate(fields[COL_TIMESTAMP]);
        
        return new CandleClosePercentDiff(high, low, open, close, volume, timestamp);
    }

    private Calendar convertDate(String field)
    {
        Calendar c = Calendar.getInstance();
        
        SimpleDateFormat dFormat = new MT5DateFormat();
        try
        {
            c.setTime(dFormat.parse(field));
        } catch (ParseException e)
        {
            e = e;
            return null;
        }
        
        return c;
    }
}
