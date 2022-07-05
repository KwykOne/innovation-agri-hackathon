package entities

type OptimalRouteResponse struct {
	TotalDistanceInKm float64    `json:"total_distance_in_km"`
	Route             []Location `json:"route"`
}
