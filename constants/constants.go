package constants

import "time"

const (
	VolumeTypeGoods                    = 1
	WeightTypeGoods                    = 2
	ShelfLifeWeightage                 = 70
	CommodityPriceWeightage            = 30
	AccidentRoadBlockerType            = "Accident"
	AccidentRoadBlockerExpiryTime      = 20 * time.Second
	AccidentRoadBlockerWeightIncrement = 1000
)
