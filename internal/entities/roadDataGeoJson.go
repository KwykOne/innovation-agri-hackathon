package entities

//GeoJson contains a sample struct. Used to unmarshal info received from open maps
type GeoJson struct {
	Type     string     `json:"type"`
	Features []Features `json:"features"`
}

type Features struct {
	Type       string      `json:"type"`
	Properties interface{} `json:"properties"`
	Geometry   Geometry    `json:"geometry"`
}

type Geometry struct {
	Type        string      `json:"type"`
	Coordinates [][]float64 `json:"coordinates"`
}
