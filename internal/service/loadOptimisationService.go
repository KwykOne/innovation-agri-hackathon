package service

import (
	log "github.com/sirupsen/logrus"
	"github.com/vaibhavahuja/load-route-optimisation/constants"
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
)

func (app *Application) CalculateOptimalLotSize(request entities.OptimalLotSizeRequest) (response entities.OptimalLotSizeResponse, err error) {
	log.Infof("received the following request %v", request)
	//applying knapsack on type in items!!
	val, volumetricWeightMap := getKnapsackInputLists(request.Items, request.Type)
	vehicle := request.Vehicle
	optimisedListOfItems := knapsack(request.Items, val, volumetricWeightMap, calculateVolumetricWeight(vehicle.WidthInCm, vehicle.HeightInCm, vehicle.LengthInCm))
	log.Info("After applying knapsack algorithm the most optimised input list of items is %v", optimisedListOfItems)
	response.NumberOfItems = len(optimisedListOfItems)
	response.ItemsToBeAdded = optimisedListOfItems
	response.RequestId = request.RequestId
	response.TotalWeightOfItems, response.TotalVolumetricWeight = calculateTotalWeight(optimisedListOfItems)
	log.Info("Final response is %v", response)
	return
}

//calculateTotalWeight Takes in a list of items and returns the total weight and total volumetric weight of items
func calculateTotalWeight(items []entities.Item) (int, int) {
	totalWeightOfItems := 0
	totalVolumetricWeight := 0
	for _, item := range items {
		totalWeightOfItems += item.Weight
		totalVolumetricWeight += calculateVolumetricWeight(item.BoxHeightInCm, item.BoxLengthInCm, item.BoxWidthInCm)
	}
	return totalWeightOfItems, totalVolumetricWeight
}

//getKnapsackInputLists
//While trying to optimise the load, we try to maximise the value as much as we can
//Calculating the value map which will be used in the knapsack solution
func getKnapsackInputLists(request []entities.Item, typeOfGood int) ([]int, []int) {
	var valueMap, volumetricWeightMap []int
	for _, item := range request {
		valueMap = append(valueMap, generateValueOfItem(item))
		switch typeOfGood {
		case constants.VolumeTypeGoods:
			volumetricWeightMap = append(volumetricWeightMap, calculateVolumetricWeight(item.BoxHeightInCm, item.BoxLengthInCm, item.BoxWidthInCm))
		case constants.WeightTypeGoods:
			volumetricWeightMap = append(volumetricWeightMap, item.Weight)
		}
	}

	return valueMap, volumetricWeightMap
}

//generateValueOfItem
// Value is calculated by combining various parameters. This can be extended to more parameters.
// Giving weights to different parameters and calculating a final weighted value
func generateValueOfItem(item entities.Item) int {
	return (item.Cost*constants.CommodityPriceWeightage + (-1*item.ShelfLifeDays)*constants.ShelfLifeWeightage) / 100
}

//knapsack This method calculates the items which are to be included in the load
//Optimising it on the basis of values which is calculated within
//Capacity can be either volumetric or weight capacity of vehicle depending on the commodity type
func knapsack(items []entities.Item, value, volumetricWeight []int, capacity int) (output []entities.Item) {
	n := len(items)
	dpArray := make([][]int, n+1)
	for m := range dpArray {
		dpArray[m] = make([]int, capacity+1)
	}

	for i := 0; i <= n; i++ {
		for w := 0; w <= capacity; w++ {
			if i == 0 || w == 0 {
				dpArray[i][w] = 0
			} else if volumetricWeight[i-1] <= w {
				dpArray[i][w] = max(value[i-1]+dpArray[i-1][w-volumetricWeight[i-1]], dpArray[i-1][w])
			} else {
				dpArray[i][w] = dpArray[i-1][w]
			}
		}
	}

	totalValue := dpArray[n][capacity]
	log.Info("the total value generated is %d", totalValue)

	//tracing back in order to get all items which are included by our optimisation
	w := capacity
	for i := n; i > 0 && totalValue > 0; i-- {
		if totalValue == dpArray[i-1][w] {
			continue
		} else {
			output = append(output, items[i-1])
			totalValue = totalValue - value[i-1]
			w = w - volumetricWeight[i-1]
		}
	}
	return
}

func calculateVolumetricWeight(l, b, h int) int {
	return l * b * h / 5000
}
func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}
