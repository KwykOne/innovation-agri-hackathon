package service

import (
	log "github.com/sirupsen/logrus"
	"github.com/vaibhavahuja/load-route-optimisation/constants"
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
	"github.com/vaibhavahuja/load-route-optimisation/utils"
	"time"
)

func (app *Application) ReportRouteBlocker(request entities.ReportRouteBlocker) (response bool, err error) {
	log.Infof("received the following request %v", request)

	//modifies the graph
	//demonstrating for one road blocker type, this approach can be extended to other road blocker types as well
	switch request.BlockerType {
	case constants.AccidentRoadBlockerType:
		timeOut := time.After(constants.AccidentRoadBlockerExpiryTime)
		app.graph.UpdateWeightOfEdge(request.RoadStartCoordinate.GetNodeFromLocation(), request.RoadEndCoordinate.GetNodeFromLocation(), constants.AccidentRoadBlockerWeightIncrement)

		log.Infof("Incrementing the weight by %d of edge on the basis or road blocker event received", constants.AccidentRoadBlockerWeightIncrement)
		log.Infof("Weight of edge will expire in %v seconds", constants.AccidentRoadBlockerExpiryTime)

		go func(graph *utils.ItemGraph, startNode, endNode *entities.Node) {
			select {
			case <-timeOut:
				log.Info("Great news! the blocker got removed")
				graph.UpdateWeightOfEdge(startNode, endNode, -1*constants.AccidentRoadBlockerWeightIncrement)
			}
		}(app.graph, request.RoadStartCoordinate.GetNodeFromLocation(), request.RoadEndCoordinate.GetNodeFromLocation())
	}
	response = true
	return
}
