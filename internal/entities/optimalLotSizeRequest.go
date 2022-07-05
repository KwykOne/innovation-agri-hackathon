package entities

type OptimalLotSizeRequest struct {
	RequestId     string  `json:"request_id"`
	Vehicle       Vehicle `json:"vehicle"`
	NumberOfItems int     `json:"number_of_items"`
	Type          int     `json:"type_of_good"`
	Items         []Item  `json:"item"`
}

type Item struct {
	Name          string `json:"name"`
	Weight        int    `json:"weight"`
	BoxLengthInCm int    `json:"box_length_in_cm"`
	BoxWidthInCm  int    `json:"box_width_in_cm"`
	BoxHeightInCm int    `json:"box_height_in_cm"`
	ShelfLifeDays int    `json:"shelf_life_days"`
	Cost          int    `json:"cost"`
}

type Vehicle struct {
	MaxLoadCapacity int `json:"max_load_capacity"`
	HeightInCm      int `json:"height_in_cm"`
	WidthInCm       int `json:"width_in_cm"`
	LengthInCm      int `json:"length_in_cm"`
}
