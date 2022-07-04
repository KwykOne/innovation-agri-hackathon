package service

import "github.com/vaibhavahuja/load-route-optimisation/utils"

type Application struct {
	graph *utils.ItemGraph
}

func NewApplication(itemGraph *utils.ItemGraph) *Application {
	return &Application{
		graph: itemGraph,
	}
}
