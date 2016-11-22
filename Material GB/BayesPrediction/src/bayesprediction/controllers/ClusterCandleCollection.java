/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.controllers;

import java.util.ArrayList;

/**
 *
 * @author EPJ
 */
public class ClusterCandleCollection extends ArrayList<ClusterCandle> {

    public ClusterCandleCollection() {
        super();
    }

    public void inverse()
    {
        ClusterCandle temp;
        
        for (int i = 0; i < (size() / 2); i++)
        {
            temp = get(i);
            set(i, get(size() - 1 - i));
            set(size() - i - 1, temp);
        }
    }
}
