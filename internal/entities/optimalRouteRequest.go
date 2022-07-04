package entities

import (
	"fmt"
)

type OptimalRouteRequest struct {
	RequestId     string   `json:"request_id"`
	StartLocation Location `json:"start_location"`
	EndLocation   Location `json:"end_location"`
}

type OptimalRouteMultipleNodesRequest struct {
	RequestId    string     `json:"request_id"`
	LocationList []Location `json:"location_list"`
}

type Location struct {
	Longitude float64 `json:"longitude"`
	Latitude  float64 `json:"latitude"`
}

type Node struct {
	Value string
}

func (loc *Location) GetNodeFromLocation() *Node {
	return &Node{Value: fmt.Sprintf("lat%f#long%f", loc.Latitude, loc.Longitude)}
}
