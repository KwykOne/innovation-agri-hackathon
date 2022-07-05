package service

import (
	log "github.com/sirupsen/logrus"
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
	"github.com/vaibhavahuja/load-route-optimisation/utils"
	"strconv"
	"strings"
)

func (app *Application) FindOptimalRoute(request entities.OptimalRouteRequest) (response entities.OptimalRouteResponse, err error) {
	log.Infof("received the following request %v", request)

	startNode := request.StartLocation.GetNodeFromLocation()
	endNode := request.EndLocation.GetNodeFromLocation()
	log.Infof("start node is %v and end node is %v", startNode, endNode)

	list, val := utils.GetShortestPath(startNode, endNode, app.graph)
	log.Infof("after the shortest path the val is %d", val)
	var route []entities.Location
	var distance float64
	for index := 0; index < len(list); index++ {
		//To evaluate (tc of regex extraction) : can use regex to extract instead?
		hashIndex := strings.Index(list[index], "#")
		log.Infof("before any conversion lat %s long %s", list[index][3:hashIndex], list[index][hashIndex+5:])
		latitude := convertStringToFloat(list[index][3:hashIndex])
		longitude := convertStringToFloat(list[index][hashIndex+5:])
		log.Info("the latitude is %v and longitude is %v", latitude, longitude)
		locationObject := entities.Location{
			Longitude: longitude,
			Latitude:  latitude,
		}
		route = append(route, locationObject)
		//log.Infof("got the latitude as %f and longitude as %f", latitude, longitude)
	}
	for index := 0; index < len(route)-1; index++ {
		distance += utils.FindHaversineDistanceInKm(route[index], route[index+1])
	}

	response.TotalDistanceInKm = distance
	response.Route = route
	return
}

func (app *Application) FindOptimalRouteAcrossMultiNodes(request entities.OptimalRouteMultipleNodesRequest) (response entities.OptimalRouteResponse, err error) {
	log.Infof("received the following request %v", request)
	for locationIndex := 0; locationIndex < len(request.LocationList)-1; locationIndex++ {
		startLocationNode := request.LocationList[locationIndex].GetNodeFromLocation()
		endLocationNode := request.LocationList[locationIndex+1].GetNodeFromLocation()

		log.Infof("start node is %v and end node is %v", startLocationNode, endLocationNode)
		optimalRouteRequest := entities.OptimalRouteRequest{
			StartLocation: request.LocationList[locationIndex],
			EndLocation:   request.LocationList[locationIndex+1],
		}
		optimalRoute, err := app.FindOptimalRoute(optimalRouteRequest)
		if err != nil {
			log.Errorf("error while finding optimal route %s", err)
		}
		response.TotalDistanceInKm += optimalRoute.TotalDistanceInKm
		for _, val := range optimalRoute.Route {
			response.Route = append(response.Route, val)
		}
	}
	return
}

func convertStringToFloat(input string) (f float64) {
	//log.Infof("received a string to convert %s", input)
	f, err := strconv.ParseFloat(input, 1)
	if err != nil {
		log.Errorf("Error while converting string to float %s", err)
	}
	return
}
