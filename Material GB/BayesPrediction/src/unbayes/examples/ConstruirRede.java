package unbayes.examples;

import bayesprediction.controllers.BayesAnalysis;
import unbbayes.prs.Edge;
import unbbayes.prs.Node;
import unbbayes.prs.bn.JunctionTreeAlgorithm;
import unbbayes.prs.bn.PotentialTable;
import unbbayes.prs.bn.ProbabilisticNetwork;
import unbbayes.prs.bn.ProbabilisticNode;
import unbbayes.prs.bn.TreeVariable;
import unbbayes.prs.exception.InvalidParentException;
import unbbayes.prs.id.DecisionNode;

public class ConstruirRede 
{
    private BayesAnalysis analise;
    private int numberOfClusters;
    private ProbabilisticNetwork rede;
    private ProbabilisticNode nodeFirst;
    private ProbabilisticNode nodeProb;
    private PotentialTable tabela;
    private String NODE_DECISAO = "decisionNode";
    private String NODE_PROBABILIDADE = "probNode1";
    
    private int nEvidencia;
    
    public ConstruirRede(BayesAnalysis analise)
    {        
        this.rede = new ProbabilisticNetwork("Rede");
        this.analise = analise;
        this.numberOfClusters = this.analise.getNumberOfClusters();
    }
    
    public void Construir()
    {
        this.ConstruirFirstNode();
        this.ConstruirProbabilisticNode();
        this.AdicionarRelacionamento();
        this.MontarTabelaProbabilidade();
        this.ConstruirEngine();
    }
    
    public void ImprimirNodeDecisao()
    {
		DecisionNode nodeDecisao = (DecisionNode) this.rede.getNode(this.NODE_DECISAO);
		this.imprimirNode(nodeDecisao);
    }
    
    public void ImprimirNodeProbabilidade()
    {
        ProbabilisticNode nodeProbabilidade = (ProbabilisticNode) this.rede.getNode(this.NODE_PROBABILIDADE);
        this.imprimirNode(nodeProbabilidade);
    }
    
    public void DefinirEvidencia(int numeroEvidencia) throws Exception
    {
        this.nEvidencia = numeroEvidencia;
        
        if(numeroEvidencia > this.numberOfClusters)
        {
            throw new Exception("Escolha deve ficar entre o número de estados...");
        }
        
        ((TreeVariable) this.rede.getNode(this.NODE_DECISAO)).addFinding(0) ;
        this.rede.updateEvidences();
    }
    
    private void ConstruirFirstNode()
    {
        // Add decision node
        this.nodeFirst = new ProbabilisticNode();
		this.nodeFirst.setName(this.NODE_DECISAO);
		this.nodeFirst.setDescription("Nó de decisão");
		
        for(int index=1; index<= numberOfClusters; index++)
        {
            this.nodeFirst.appendState(String.format("Faixa %d", index));
        }
		
		rede.addNode(this.nodeFirst);    
    }

    private void ConstruirProbabilisticNode()
    {
        // Add probabilistic node 1
        this.nodeProb = new ProbabilisticNode();
		this.nodeProb.setName(this.NODE_PROBABILIDADE);
		this.nodeProb.setDescription("Nó de probabilidade 1");
        
        for(int index=1; index<= this.numberOfClusters; index++)
        {
            this.nodeProb.appendState(String.format("Faixa %d", index));
        }
        
        for(int index=0; index< this.numberOfClusters; index++)
        {
            this.nodeProb.setMarginalAt(index, this.analise.getMarginalAt(index));
        }
        
		this.rede.addNode(this.nodeProb);
    }
    
    private void AdicionarRelacionamento() throws InvalidParentException
    {
        Edge node = new Edge(this.nodeFirst, this.nodeProb);
        rede.addEdge(node);
    }
    
    private void MontarTabelaProbabilidade()
    {
        this.tabela = this.nodeProb.getProbabilityFunction();
		this.tabela.addVariable(this.nodeProb);
		
        int qtdeIteracao = this.numberOfClusters * this.numberOfClusters;
        
        for(int index = 0; index < qtdeIteracao; index++)
        {
            this.tabela.addValueAt(index, this.ObterValorPosicao(index)); 
        }
    }
    
    private float ObterValorPosicao(int index)
    {
        // Numero do cluster que vai adicionar na arvore
        int nCluster = index / this.numberOfClusters;
        
        // Posição da condicional para adicionar
        int posicao = index % this.numberOfClusters;
        
        // Deve procurar na analise, qual é o valor da condicional que representa posicao
        return analise.ObterValorCondicional(nCluster, posicao);
    }
    
    private void ConstruirEngine()
    {
        JunctionTreeAlgorithm alg = new JunctionTreeAlgorithm();
		alg.setNetwork(this.rede);
		alg.run();
    }
    
    private void imprimirNode(Node node) 
    {
		System.out.println("Node: " + node.getName());
        
		for (int index = 0; index < node.getStatesSize(); index++) 
        {
			System.out.print(node.getStateAt(index));

			if (node instanceof DecisionNode) 
            {
				DecisionNode dn = (DecisionNode) node;
				System.out.println(" = " + dn.getMarginalAt(index));
			} 
            else if (node instanceof ProbabilisticNode) 
            {
				ProbabilisticNode dn = (ProbabilisticNode) node;
				System.out.println(" = " + dn.getMarginalAt(index));
			}
		}
	}
}
