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
package unbayes.examples;

import java.io.File;
import java.io.IOException;
import unbbayes.io.NetIO;
import unbbayes.io.exception.LoadException;
import unbbayes.prs.Node;
import unbbayes.prs.bn.JunctionTreeAlgorithm;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.prs.id.DecisionNode;

/**
 * Invesment example.
 * 
 * @author David Saldana
 */
public class InvesmentDecisionNetwork {

    public static void main(String[] args) throws LoadException, IOException, Exception {


        //Archivo de definición de la red bayesiana
        File file = new File("examples/buyHouseUNBB.net");
        ProbabilisticNetwork net = (ProbabilisticNetwork) new NetIO().load(file);

        // Correr el algoritmo Junction Tree sobre la red
        JunctionTreeAlgorithm alg = new JunctionTreeAlgorithm();
        alg.setNetwork(net);

        //Correr el algoritmo por primer vez
        alg.run();

        /// NO EVIDENCE ///////
        // imprimir el resultado sin agregar evicencia
        System.out.println("----SIN EVIDENCIA ----");
        DecisionNode nodeInspect = (DecisionNode) net.getNode("Inspect");
        imprimirNodo(nodeInspect);

        // Evidencia: Resultado de reporte malo //////

        System.out.println("----EVIDENCIA: REPORTE MALO----");
        // Agregar el resultado malo al reporte
        ProbabilisticNode nodeReport = (ProbabilisticNode) net.getNode("Report");
        nodeReport.addFinding(0);
        nodeReport.setMarginalAt(0, 0); // Good
        nodeReport.setMarginalAt(1, 1); // Bad
        nodeReport.setMarginalAt(2, 0); // Unknown

        // Tomar la decisión de inspeccionar
        nodeInspect.addFinding(0);
        nodeInspect.setMarginalAt(0, 1); // Inspect
        nodeInspect.setMarginalAt(1, 0); // NotInspect


        // Actualizar las evidencias y correr el algoritmo de arbol de nuevo
        net.updateEvidences();

        // Imprimir el nodo BuyHouse para tomar de dicisión de comprar la casa o no.
        DecisionNode nodeBuyhouse = (DecisionNode) net.getNode("BuyHouse");
        imprimirNodo(nodeBuyhouse);
    }

    private static void imprimirNodo(final Node node) {
        System.out.println("Nodo: " + node.getName());
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
}
