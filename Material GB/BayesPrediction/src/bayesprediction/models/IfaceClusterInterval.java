/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.models;

import bayesprediction.controllers.ClusterCandle;
import bayesprediction.controllers.ClusterCandleCollection;

/**
 *
 * @author EPJ
 */
public interface IfaceClusterInterval {
    public boolean belongsToCluster(ClusterCandle cluster);
    
    public ClusterCandle pickFirstCluster(ClusterCandleCollection clusterCol);
}
