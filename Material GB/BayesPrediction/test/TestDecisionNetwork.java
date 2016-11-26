/*
 * Copyright (C) 2011 Free Software Foundation, Inc.
 *
 * This Project is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * The MobSim Project  is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with the MobSim Project ; see the file COPYING.LIB.
 * If not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA.
 */

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import unbbayes.io.NetIO;
import unbbayes.prs.Node;
import unbbayes.prs.bn.JunctionTreeAlgorithm;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.prs.id.DecisionNode;

/**
 * This is an example of a decision network mentioned in the book:
 * Bayesian Artificial Intelligence.Korb, K.B. and Nicholson, A.E. 2004.
 * 
 * This is the investment example.
 * 
 * @author David Saldana
 */
public class TestDecisionNetwork {

    ProbabilisticNetwork net;
    JunctionTreeAlgorithm alg = new JunctionTreeAlgorithm();

    @Before
    public void setUp() throws IOException {
        // This file defines the bayesian network and it has .net format.
        File file = new File("examples/buyHouseUNBB.net");
        net = (ProbabilisticNetwork) new NetIO().load(file);
        alg.setNetwork(net);

        // Run the Junction tree algorithm once.
        alg.run();
    }

    /**
     * Evaluate the result of the junction tree algorithm without evidence.
     */
    @Test
    public void runNoEvidence() {
        // get the Inspect node of the network.
        DecisionNode node = (DecisionNode) net.getNode("Inspect");        

        // Validate the Inspect node
        assertTrue(Math.round(node.getMarginalAt(0)) == 2635);
        assertTrue(Math.round(node.getMarginalAt(1)) == 2600);
    }

    /**
     * Evidence is "not inspect" and "report is unknown".
     */
    @Test
    public void testReportUnknown() throws Exception {
        //Add evidence for report=unknown
        ProbabilisticNode nodeReport = (ProbabilisticNode) net.getNode("Report");
        nodeReport.addFinding(0);

        float likelihood[] = new float[nodeReport.getStatesSize()];
        likelihood[0] = 0f; // good
        likelihood[1] = 0f; // bad
        likelihood[2] = 1; // unknown
        nodeReport.addLikeliHood(likelihood);

        printNode(nodeReport);

        System.out.println("--------");
        // Run juntion three algorithm with the new evidence
        net.updateEvidences();


        ValidateNode("BuyHouse", 2600, 0);
        System.out.println("--------------");
    }

    /**
     * Evidence is inspect and report is good.
     */
    @Test
    public void testIReportGood() throws Exception {
        //Add evidence for report=good
        ProbabilisticNode nodeReport = (ProbabilisticNode) net.getNode("Report");
        nodeReport.addFinding(0);
        nodeReport.setMarginalAt(0, 1); // Good
        nodeReport.setMarginalAt(1, 0); // Bad
        nodeReport.setMarginalAt(2, 0); // Unknown

        // Add evidence I=yes
        DecisionNode nodeInspect = (DecisionNode) net.getNode("Inspect");
        nodeInspect.addFinding(0);
        nodeInspect.setMarginalAt(0, 1); // Inspect
        nodeInspect.setMarginalAt(1, 0); // NotInspect



        System.out.println("--------");
        // Run juntion three algorithm with the new evidence
        net.updateEvidences();


        ValidateNode("BuyHouse", 4055, 0);

        System.out.println("--------------");

    }

    /**
     * Evidence is inspect and report is good.
     */
    @Test
    public void testIReportBad() throws Exception {
        //Add evidence for report=good
        ProbabilisticNode nodeReport = (ProbabilisticNode) net.getNode("Report");
        nodeReport.addFinding(0);
        nodeReport.setMarginalAt(0, 0); // Good
        nodeReport.setMarginalAt(1, 1); // Bad
        nodeReport.setMarginalAt(2, 0); // Unknown

        // Add evidence I=yes
        DecisionNode nodeInspect = (DecisionNode) net.getNode("Inspect");
        nodeInspect.addFinding(0);
        nodeInspect.setMarginalAt(0, 1); // Inspect
        nodeInspect.setMarginalAt(1, 0); // NotInspect




        System.out.println("--------");
        // Run juntion three algorithm with the new evidence
        net.updateEvidences();

        ValidateNode("BuyHouse", -2682, 0);
        ValidateNode("BuyHouse", -600, 1);
        System.out.println("--------------");

    }

    @After
    public void tearDown() {
    }

    /**
     * Print a node and its state values.
     * @param node
     */
    private void printNode(Node node) {
        System.out.println("Node: " + node.getName());
        for (int i = 0; i < node.getStatesSize(); i++) {
            System.out.print(node.getStateAt(i));

            if (node instanceof DecisionNode) {
                DecisionNode dn = (DecisionNode) node;
                System.out.println(" = " + dn.getMarginalAt(i));
            } else if (node instanceof ProbabilisticNode) {
                ProbabilisticNode dn = (ProbabilisticNode) node;
                System.out.println(" = " + dn.getMarginalAt(i));
            }
        }
    }

    private void ValidateNode(String nodeName, int expectedValue, int state) {

        DecisionNode node = (DecisionNode) net.getNode(nodeName);
        printNode(node);

        // Validar el valor calculado con el valor del libro.
        assertTrue(Math.round(node.getMarginalAt(state)) == expectedValue);


    }
}
