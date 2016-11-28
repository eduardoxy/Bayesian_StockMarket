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
import unbbayes.prs.Node;
import unbbayes.prs.bn.JunctionTreeAlgorithm;
import unbbayes.prs.bn.PotentialTable;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.prs.exception.InvalidParentException;
import unbbayes.util.extension.bn.inference.IInferenceAlgorithm;

/**
 *
 * @author eduardo
 */
public class UNBBayesEngineStock 
{
    private static final ResourceBundle resource = 
        unbbayes.util.ResourceController.newInstance().getBundle(
        unbbayes.example.resources.ExampleResources.class.getName());
    
    protected ProbabilisticNetwork  net;
    protected BayesAnalysis         bayesSupport;
    protected CandleCollection      cCol;
    protected final int             clustersNbr;
    
    public UNBBayesEngineStock(Calendar startTime, Calendar endTime, 
        CandleCollection cCol, int clusters) {
        this.clustersNbr = clusters;
        init(startTime, endTime, cCol, clusters);
        runTest(1);
    }
    
    private void init(Calendar startTime, Calendar endTime, CandleCollection cCol,
        int clusters)
    {
        this.cCol = cCol;
        bayesSupport = new BayesAnalysis(startTime, endTime, cCol, clusters);
        //buildMarkovChain();
    }
    
    private void buildMarkovChain()
    {
        // Initialize the network
        ClusterCandleCollection clusters = bayesSupport.getClusters();
        net = new ProbabilisticNetwork("Stock Price Prediction Network");
        
        // Add the node t0
        ProbabilisticNode n_t0 = createNode("t0", "t0 instant");
        // For each node, append each cluster available
        appendStates(n_t0, clusters);
        // Set the probabilistic table
        setMarginalTable(n_t0);
        net.addNode(n_t0);
        
        //Add the node evidence 0
        ProbabilisticNode e_t0 = createNode("e0", "e0 evidence");
        // For each node, append each cluster available
        appendStates(e_t0, clusters);
        setEvidenceTable(e_t0, n_t0);
        net.addNode(e_t0);
        
        // Add the node t1
        ProbabilisticNode n_t1 = createNode("t1", "t1 instant");
        // For each node, append each cluster available
        appendStates(n_t1, clusters);
        setConditionalTable(n_t1, n_t0);
        net.addNode(n_t1);
        
        //Add the node evidence 1
        ProbabilisticNode e_t1 = createNode("e1", "e1 evidence");
        // For each node, append each cluster available
        appendStates(e_t1, clusters);
        setEvidenceTable(e_t1, n_t1);
        net.addNode(e_t1);
        
        // Add the node t2
        ProbabilisticNode n_t2 = createNode("t2", "t2 instant");
        // For each node, append each cluster available
        appendStates(n_t2, clusters);
        setConditionalTable(n_t2, n_t1);
        net.addNode(n_t2);
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
    
    private void setMarginalTable(ProbabilisticNode node)
    {
        Iterator<ClusterCandle> it = bayesSupport.getClusters().iterator();
        ClusterCandle           cluster;
        
        // Set the probabilistic table
        PotentialTable pt = node.getProbabilityFunction();
        pt.addVariable(node);
        //pt.setTableSize(bayesSupport.getClusters().size());
        while (it.hasNext())
        {
            cluster = it.next();
            System.out.println(cluster.getId() + " " + (float) cluster.getMarginalProbability());
            pt.setValue(cluster.getId(), (float) cluster.getMarginalProbability());
        }
        
        printNode(node);
    }
    
    private void setEvidenceTable(ProbabilisticNode evidence, 
            ProbabilisticNode parent)
    {
        try
        {
            Edge newEdge = new Edge(parent, evidence);
            net.addEdge(newEdge);
            
            PotentialTable pt = evidence.getProbabilityFunction();
            pt.addVariable(parent);
            
            int tSize = evidence.getStatesSize() * parent.getStatesSize();
            pt.setTableSize(tSize);
            for (int i = 0; i < tSize; i++)
            {
                float val = ((i % (evidence.getStatesSize() + 1)) == 0) ? 1.0f : 0.0f;
                pt.addValueAt(i, val);
            }
        } catch (InvalidParentException e)
        {
            System.out.println("Error on setEvidenceTable");
        }
    }
    
    private void setConditionalTable(ProbabilisticNode child, 
        ProbabilisticNode parent)
    {
        try
        {
            ClusterCandleCollection clusters = bayesSupport.getClusters();
            Edge newEdge = new Edge(parent, child);
            net.addEdge(newEdge);
            
            PotentialTable pt = child.getProbabilityFunction();
            pt.addVariable(parent);

            int tSize = child.getStatesSize() * child.getStatesSize();
            pt.setTableSize(tSize);

            for (int iCluster = 0; iCluster < clusters.size(); iCluster++)
            {
                ClusterCandle c = clusters.get(iCluster);
                List<Float> cCond = c.getConditionalProbability();
                for (int iCond = 0; iCond < cCond.size(); iCond++)
                {
                    int p = (iCluster * child.getStatesSize()) + iCond;
                    pt.addValueAt(p, cCond.get(iCond));
                }
            }
        } catch (InvalidParentException e)
        {
            System.out.println("Error on setConditionalTable");
        }
    }
    
    private void runTest(int times)
    {
        int refStart;
        int refEnd;
        int target = 0;
        CandleCollection fCol; //filtered collection
        
        refStart = bayesSupport.getIndexStart();
        refEnd = bayesSupport.getIndexEnd();
        
        for (int i = 0; (i < times) && (refEnd + i + 1 < cCol.size()); i++)
        {
            Candle cStart = cCol.get(refStart + i);
            Candle cEnd = cCol.get(refEnd + i);
            
            // do the bayes analysis
            bayesSupport = new BayesAnalysis(cStart.getTimestamp(), 
                cEnd.getTimestamp(), cCol, clustersNbr);
            // build the network
            buildMarkovChain();
            try {
   
                fCol = bayesSupport.getCandleCollection();
                Candle cEv_0    = fCol.getLast().getPrevious();
                Candle cEv_1    = fCol.getLast();
                Candle cFuture = fCol.getLast().getNext();

                // Fit the cFuture
                cFuture.setPrevious(cEv_1);
                cFuture.recalc();

                System.out.println(cFuture);

                JunctionTreeAlgorithm alg = new JunctionTreeAlgorithm();
                alg.setNetwork(net);
                alg.run();
                
                // print node's initial states
                /*for (Node node : net.getNodes()) {
                    System.out.println(node.getDescription());
                    for (int j = 0; j < node.getStatesSize(); j++) {
                        System.out.println(node.getStateAt(j) + " : " 
                         + ((ProbabilisticNode)node).getMarginalAt(j));
                   }
                }*/
                
                // Propagate evidences
                /*ProbabilisticNode e0 = (ProbabilisticNode) net.getNode("e0");
                e0.addFinding(cEv_0.pickFirstCluster(bayesSupport.clusters).getId());
                /*net.updateEvidences();
                algorithm.propagate();
                
                ProbabilisticNode e1 = (ProbabilisticNode) net.getNode("e1");
                e1.addFinding(cEv_1.pickFirstCluster(bayesSupport.clusters).getId());
                /*net.updateEvidences();
                algorithm.propagate();
            
                ProbabilisticNode t2 = (ProbabilisticNode) net.getNode("t2");
                if (checkPrediction(t2, cFuture))
                    target++;*/
            } catch (Exception exc) {
                System.out.println(exc.getMessage());               	
            }
        }
        System.out.println("In " + times + " times " + target + " were right!");
    }
    
    private boolean checkPrediction(ProbabilisticNode prediction, Candle candle)
    {
        ClusterCandle cluster = candle.pickFirstCluster(bayesSupport.getClusters());
        if (cluster == null)
            return false;
        
        int best = pickTheBest(prediction);
        ClusterCandle cPrect = bayesSupport.getClusters().get(best);
        
        return (cPrect == cluster);
    }
    
    private int pickTheBest(ProbabilisticNode node)
    {
        float   best = 0.0f;
        int     id = -1;
        
        //printNode(node);
        for (int i = 0; i < node.getStatesSize(); i++)
        {
            if (node.getMarginalAt(i) > best)
            {
                best = node.getMarginalAt(i);
                id = i;
            }
        }
        
        return id;
    }
    
    private void printNode(ProbabilisticNode node)
    {
        for (int i = 0; i < node.getStatesSize(); i++)
            System.out.println("Node [" + node.getName() + 
                "] -> State(" + node.getStateAt(i) + ") = " + 
                String.format("%.05f", node.getMarginalAt(i)));
    }
}
