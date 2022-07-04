package utils

import (
	"encoding/json"
	log "github.com/sirupsen/logrus"
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
	"io/ioutil"
	"os"
)

func GenerateGraphFromOpenMapsApiData() (graph *ItemGraph) {
	geoJson := fetchTestData("/test-data/mapsGeo2.json")
	var myGraphInputData []InputData
	for _, feature := range geoJson.Features {
		coordinatesList := feature.Geometry.Coordinates
		var inputData InputData
		for index := 0; index < len(coordinatesList)-1; index++ {
			startLocation := &entities.Location{
				Longitude: coordinatesList[index][0],
				Latitude:  coordinatesList[index][1],
			}
			endLocation := &entities.Location{
				Longitude: coordinatesList[index+1][0],
				Latitude:  coordinatesList[index+1][1],
			}
			inputData.Source = startLocation.GetNodeFromLocation().Value
			inputData.Destination = endLocation.GetNodeFromLocation().Value
			inputData.Weight = int(100 * FindHaversineDistanceInKm(*startLocation, *endLocation))
			myGraphInputData = append(myGraphInputData, inputData)
		}
	}
	inputGraph := InputGraph{Graph: myGraphInputData}
	graph = CreateGraph(inputGraph)

	//log.Info("trying to print the graph")
	//for _, val := range graph.Nodes {
	//	printEdgesList(graph.Edges[*val])
	//	fmt.Println()
	//}

	return
}

func fetchTestData(fileName string) (resp entities.GeoJson) {
	path, _ := os.Getwd()
	file, err := ioutil.ReadFile(path + fileName)
	if err != nil {
		log.Errorf("unable to read file")
	}
	err = json.Unmarshal(file, &resp)
	if err != nil {
		log.Errorf("Unable to unmarshall %s", err)
	}
	return
}
