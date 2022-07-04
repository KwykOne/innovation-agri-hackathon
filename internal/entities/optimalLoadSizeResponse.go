package entities

type OptimalLotSizeResponse struct {
	RequestId             string `json:"request_id"`
	NumberOfItems         int    `json:"number_of_items"`
	TotalWeightOfItems    int    `json:"total_weight_of_items"`
	TotalVolumetricWeight int    `json:"total_volumetric_weight"`
	ItemsToBeAdded        []Item `json:"items_to_be_added"`
}
