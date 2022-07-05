package main

import "github.com/vaibhavahuja/load-route-optimisation/utils"

//TODO delete this

func main() {
	utils.GenerateGraphFromTestData()
}

//func main() {
//	//will be writing script to convert geoJson to this value
//	myGraphInputData := []utils.InputData{
//		{
//			Source:      "A",
//			Destination: "B",
//			Weight:      100,
//		},
//		{
//			Source:      "A",
//			Destination: "C",
//			Weight:      50,
//		},
//		{
//			Source:      "C",
//			Destination: "D",
//			Weight:      30,
//		},
//		{
//			Source:      "D",
//			Destination: "B",
//			Weight:      10,
//		},
//	}
//
//	inputGraph := utils.InputGraph{
//		Graph: myGraphInputData,
//	}
//	graphCreated := utils.CreateGraph(inputGraph)
//
//	startNode := &entities.Node{Value: "A"}
//	endNode := &entities.Node{Value: "B"}
//	listString, _ := utils.GetShortestPath(startNode, endNode, graphCreated)
//	log.Info(listString)
//
//	graphCreated.UpdateWeightOfEdge(startNode, endNode, -90)
//	timeOut := time.After(2 * time.Second)
//	listString, _ = utils.GetShortestPath(startNode, endNode, graphCreated)
//	log.Info(listString)
//	go func(graph *utils.ItemGraph, startNode, endNode *entities.Node) {
//		select {
//		case <-timeOut:
//			log.Info("looks like the request timed out")
//			graphCreated.UpdateWeightOfEdge(startNode, endNode, 90)
//		}
//	}(graphCreated, startNode, endNode)
//
//	time.Sleep(5 * time.Second)
//	listString, _ = utils.GetShortestPath(startNode, endNode, graphCreated)
//	log.Info(listString)
//}

func updateValueOfEdge() {

}
