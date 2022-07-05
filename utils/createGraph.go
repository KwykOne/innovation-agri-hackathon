package utils

import (
	"fmt"
	log "github.com/sirupsen/logrus"
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
)

//TODO update this to take information from geoJson

// AddNode adds a node to the graph
func (g *ItemGraph) AddNode(n *entities.Node) {
	g.Lock.Lock()
	g.Nodes = append(g.Nodes, n)
	g.Lock.Unlock()
}

// AddEdge adds an edge to the graph
func (g *ItemGraph) AddEdge(n1, n2 *entities.Node, weight int) {
	g.Lock.Lock()
	if g.Edges == nil {
		g.Edges = make(map[entities.Node][]*Edge)
	}
	ed1 := Edge{
		Node:            n2,
		Weight:          weight,
		DestinationMeta: n2.Value,
	}

	ed2 := Edge{
		Node:            n1,
		Weight:          weight,
		DestinationMeta: n2.Value,
	}
	g.Edges[*n1] = append(g.Edges[*n1], &ed1)
	g.Edges[*n2] = append(g.Edges[*n2], &ed2)
	g.Lock.Unlock()
}

//UpdateWeightOfEdge updates the weight of 2 edges based on the weightIncrement provided
func (g *ItemGraph) UpdateWeightOfEdge(n1, n2 *entities.Node, weightIncrement int) bool {
	g.Lock.Lock()
	log.Infof("updating weight of edge %s to %s by %d", n1.Value, n2.Value, weightIncrement)
	log.Infof("length of edges from node n1 is %v", len(g.Edges[*n1]))
	for _, val := range g.Edges[*n1] {
		log.Infof("node edge value is %v", val)
		log.Infof("value of current node is %v", n1.Value)
		if val.DestinationMeta == n2.Value {
			val.Weight += weightIncrement
		}
	}

	for _, val := range g.Edges[*n1] {
		if val.DestinationMeta == n1.Value {
			val.Weight += weightIncrement
		}
	}
	log.Infof("new weight of edge %s to %s is %v", n1.Value, n2.Value, g.Edges[*n1])
	g.Lock.Unlock()
	return true
}

func printEdgesList(input []*Edge) {
	for _, val := range input {
		fmt.Println(val.Node, val.Weight, val.DestinationMeta)
	}
}

func CreateGraph(data InputGraph) *ItemGraph {
	var g ItemGraph
	nodes := make(map[string]*entities.Node)
	for _, v := range data.Graph {
		if _, found := nodes[v.Source]; !found {
			nA := entities.Node{Value: v.Source}
			nodes[v.Source] = &nA
			g.AddNode(&nA)
		}
		if _, found := nodes[v.Destination]; !found {
			nA := entities.Node{Value: v.Destination}
			nodes[v.Destination] = &nA
			g.AddNode(&nA)
		}
		g.AddEdge(nodes[v.Source], nodes[v.Destination], v.Weight)
	}
	return &g
}
