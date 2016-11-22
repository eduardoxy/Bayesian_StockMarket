/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.models;

import java.text.SimpleDateFormat;

/**
 *
 * @author EPJ
 */
public class MT5DateFormat extends SimpleDateFormat{
    public MT5DateFormat() {
        super("yyyy.MM.dd kk:mm");
    }
}
