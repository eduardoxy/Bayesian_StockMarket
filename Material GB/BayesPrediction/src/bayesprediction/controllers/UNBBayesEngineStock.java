/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bayesprediction.controllers;

import bayesprediction.models.Candle;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import unbbayes.prs.Edge;
import unbbayes.prs.bn.PotentialTable;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.prs.exception.InvalidParentException;

/**
 *
 * @author eduardo
 */
public class UNBBayesEngineStock extends BayesAnalysis 
{
    private static final ResourceBundle resource = 
        unbbayes.util.ResourceController.newInstance().getBundle(
        unbbayes.example.resources.ExampleResources.class.getName());
    
    protected ProbabilisticNetwork net;
    
    public UNBBayesEngineStock(Calendar startTime, Calendar endTime, 
        CandleCollection cCol, int clusters) {
        super(startTime, endTime, cCol, clusters);
        init();
    }
    
    private void init()
    {
        ClusterCandle cluster;
        Iterator<ClusterCandle> itCluster;
        
        // Initialize the network
        net = new ProbabilisticNetwork("Stock Price Prediction Network");
        // Add the node t0
        ProbabilisticNode n_t0 = createNode("t0", "t0 instant");
        // For each node, append each cluster available
        appendStates(n_t0, this.clusters);
        
        // Set the probabilistic table
        PotentialTable cpt = n_t0.getProbabilityFunction();
        cpt.addVariable(n_t0);
        itCluster = this.clusters.iterator();
        while (itCluster.hasNext())
        {
            cluster = itCluster.next();
            cpt.addValueAt(cluster.getId(), (float) cluster.getMarginalProbability());
        }
        // Add the node to the net
        net.addNode(n_t0);
        
        //Add the node evidence 0
        ProbabilisticNode e_t0 = createNode("e0", "e0 evidence");
        // For each node, append each cluster available
        appendStates(e_t0, this.clusters);
        //Add the node to the net
        net.addNode(e_t0);
        //Add an edge from n_t0 to e_t0
        try
        {
            Edge newEdge = new Edge(n_t0, e_t0);
            net.addEdge(newEdge);
            
            cpt = e_t0.getProbabilityFunction();
        } catch (InvalidParentException e)
        {
            System.out.println("error!");
        }
        
    }
    
    private ProbabilisticNode createNode(String name, String description)
    {
        ProbabilisticNode n = new ProbabilisticNode();
        n.setName(name);
        n.setDescription(description);
        
        return n;
    }
    
    private void appendStates(ProbabilisticNode n, ClusterCandleCollection clusters)
    {
        Iterator<ClusterCandle> it = clusters.iterator();
        
        while (it.hasNext())
            n.appendState(it.next().toString());
    }
}
