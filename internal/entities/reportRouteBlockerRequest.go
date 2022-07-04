package entities

type ReportRouteBlocker struct {
	BlockerType         string   `json:"blocker_type"`
	RoadStartCoordinate Location `json:"road_start_coordinate"`
	RoadEndCoordinate   Location `json:"road_end_coordinate"`
}
