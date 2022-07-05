package utils

import (
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
	"math"
	"sync"
)

func GetShortestPath(startNode, endNode *entities.Node, g *ItemGraph) ([]string, int) {
	visited := make(map[string]bool)
	dist := make(map[string]int)
	prev := make(map[string]string)
	//pq := make(PriorityQueue, 1)
	//heap.Init(&pq)
	q := NodeQueue{}
	pq := q.NewQ()
	start := Vertex{
		Node:     startNode,
		Distance: 0,
	}
	for _, nval := range g.Nodes {
		dist[nval.Value] = math.MaxInt64
	}
	dist[startNode.Value] = start.Distance
	pq.Enqueue(start)
	//im := 0
	for !pq.IsEmpty() {
		v := pq.Dequeue()
		if visited[v.Node.Value] {
			continue
		}
		visited[v.Node.Value] = true
		near := g.Edges[*v.Node]

		for _, val := range near {
			if !visited[val.Node.Value] {
				if dist[v.Node.Value]+val.Weight < dist[val.Node.Value] {
					store := Vertex{
						Node:     val.Node,
						Distance: dist[v.Node.Value] + val.Weight,
					}
					dist[val.Node.Value] = dist[v.Node.Value] + val.Weight
					//prev[val.Node.Value] = fmt.Sprintf("->%s", v.Node.Value)
					prev[val.Node.Value] = v.Node.Value
					pq.Enqueue(store)
				}
				//visited[val.Node.value] = true
			}
		}
	}
	pathval := prev[endNode.Value]
	var finalArr []string
	finalArr = append(finalArr, endNode.Value)
	for pathval != startNode.Value {
		finalArr = append(finalArr, pathval)
		pathval = prev[pathval]
	}
	finalArr = append(finalArr, pathval)
	for i, j := 0, len(finalArr)-1; i < j; i, j = i+1, j-1 {
		finalArr[i], finalArr[j] = finalArr[j], finalArr[i]
	}
	//fmt.Println(finalArr)
	return finalArr, dist[endNode.Value]

}

type Edge struct {
	Node            *entities.Node
	Weight          int
	DestinationMeta string
}

type Vertex struct {
	Node     *entities.Node
	Distance int
}

type ItemGraph struct {
	Nodes []*entities.Node
	Edges map[entities.Node][]*Edge
	Lock  sync.RWMutex
}

type PriorityQueue []*Vertex

type NodeQueue struct {
	Items []Vertex
	Lock  sync.RWMutex
}

type InputGraph struct {
	Graph []InputData `json:"graph"`
}

type InputData struct {
	Source      string `json:"source"`
	Destination string `json:"destination"`
	Weight      int    `json:"weight"`
}
