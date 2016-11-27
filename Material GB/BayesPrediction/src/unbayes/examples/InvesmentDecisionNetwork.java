package unbayes.examples;

import java.io.File;
import java.io.IOException;
import unbbayes.io.NetIO;
import unbbayes.io.exception.LoadException;
import unbbayes.prs.Edge;
import unbbayes.prs.Node;
import unbbayes.prs.bn.JunctionTreeAlgorithm;
import unbbayes.prs.bn.PotentialTable;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.prs.id.DecisionNode;

public class InvesmentDecisionNetwork {

	public static void main(String[] args) throws LoadException, IOException, Exception {
		// Create Network
		ProbabilisticNetwork net = new ProbabilisticNetwork("StockNet");

		// Add decision node
		DecisionNode decision = new DecisionNode();
		decision.setName("decisionNode");
		decision.setDescription("Nó de decisão");
		decision.appendState("faixaA");
		decision.appendState("faixaB");
		decision.appendState("faixaC");
		decision.appendState("faixaD");
		decision.appendState("faixaE");
		net.addNode(decision);

		// Add probabilistic node 1
		ProbabilisticNode prob = new ProbabilisticNode();
		prob.setName("probNode1");
		prob.setDescription("Nó de probabilidade 1");
		prob.appendState("faixaA");
		prob.appendState("faixaB");
		prob.appendState("faixaC");
		prob.appendState("faixaD");
		prob.appendState("faixaE");
		PotentialTable auxCPT = prob.getProbabilityFunction();
		auxCPT.addVariable(prob);
		net.addNode(prob);
		net.addEdge(new Edge(decision, prob));

		// Set node 1 table's
		auxCPT.addValueAt(0,  0.2f); auxCPT.addValueAt(1,  0.2f); auxCPT.addValueAt(2,  0.2f); auxCPT.addValueAt(3,  0.2f); auxCPT.addValueAt(4,  0.2f);
		auxCPT.addValueAt(5,  0.2f); auxCPT.addValueAt(6,  0.2f); auxCPT.addValueAt(7,  0.2f); auxCPT.addValueAt(8,  0.2f); auxCPT.addValueAt(9,  0.2f);
		auxCPT.addValueAt(10, 0.2f); auxCPT.addValueAt(11, 0.2f); auxCPT.addValueAt(12, 0.2f); auxCPT.addValueAt(13, 0.2f); auxCPT.addValueAt(14, 0.2f);
		auxCPT.addValueAt(15, 0.2f); auxCPT.addValueAt(16, 0.2f); auxCPT.addValueAt(17, 0.2f); auxCPT.addValueAt(18, 0.2f); auxCPT.addValueAt(19, 0.2f);
		auxCPT.addValueAt(20, 0.2f); auxCPT.addValueAt(21, 0.2f); auxCPT.addValueAt(22, 0.2f); auxCPT.addValueAt(23, 0.2f); auxCPT.addValueAt(24, 0.2f);



		
		//Archivo de definición de la red bayesiana
		//File file = new File("examples/buyHouseUNBB.net");
		//ProbabilisticNetwork net = (ProbabilisticNetwork) new NetIO().load(file);

		// Correr el algoritmo Junction Tree sobre la red
		JunctionTreeAlgorithm alg = new JunctionTreeAlgorithm();
		alg.setNetwork(net);

		//Correr el algoritmo por primer vez
		alg.run();

		/// NO EVIDENCE ///////
		// imprimir el resultado sin agregar evicencia
		System.out.println("----SIN EVIDENCIA ----");
		DecisionNode nodeInspect = (DecisionNode) net.getNode("decisionNode");
		imprimirNodo(nodeInspect);

		// Evidencia: Resultado de reporte malo //////

		System.out.println("----EVIDENCIA: REPORTE MALO----");
		// Agregar el resultado malo al reporte
		ProbabilisticNode nodeReport = (ProbabilisticNode) net.getNode("probNode1");
		nodeReport.addFinding(0);
		nodeReport.setMarginalAt(0, 0);
		nodeReport.setMarginalAt(1, 1);
		nodeReport.setMarginalAt(2, 0);
		nodeReport.setMarginalAt(3, 0);
		nodeReport.setMarginalAt(4, 0);

		// Tomar la decisión de inspeccionar
		nodeInspect.addFinding(0);
		nodeInspect.setMarginalAt(0, 1);
		nodeInspect.setMarginalAt(1, 0);
		nodeInspect.setMarginalAt(2, 0);
		nodeInspect.setMarginalAt(3, 0);
		nodeInspect.setMarginalAt(4, 0);
		imprimirNodo(nodeInspect);

		// Actualizar las evidencias y correr el algoritmo de arbol de nuevo
		net.updateEvidences();

		// Imprimir el nodo BuyHouse para tomar de dicisión de comprar la casa o no.
		// DecisionNode nodeBuyhouse = (DecisionNode) net.getNode("BuyHouse");
		// imprimirNodo(nodeBuyhouse);
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
