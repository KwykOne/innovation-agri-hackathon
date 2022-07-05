package utils

import (
	"github.com/umahmood/haversine"
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
)

func FindHaversineDistanceInKm(start, end entities.Location) (distance float64) {
	startCoordinate := haversine.Coord{
		Lat: start.Latitude,
		Lon: start.Longitude,
	}
	endCoordinate := haversine.Coord{
		Lat: end.Latitude,
		Lon: end.Longitude,
	}
	_, distance = haversine.Distance(startCoordinate, endCoordinate)
	return
}
