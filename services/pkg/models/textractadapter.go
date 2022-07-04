package models

import (
	"context"
	"fmt"
	"log"
	"strconv"
	"strings"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/textract"
	"github.com/aws/aws-sdk-go-v2/service/textract/types"
	"github.com/google/uuid"
)

var parseClient *textract.Client

//var sampleResponse []byte

func init() {
	cfg, err := config.LoadDefaultConfig(context.TODO(), config.WithRegion("ap-south-1"))
	if err != nil {
		log.Println("Error creating aws config")
	}
	parseClient = textract.NewFromConfig(cfg)
	// file, err := os.Open("assets/textractsample.json")
	// if err != nil {
	// 	log.Println("Unable to open sample file", err)
	// 	return
	// }
	// defer file.Close()
	// sampleResponse, err = ioutil.ReadAll(file)
	// if err != nil {
	// 	log.Println("Error reading sample file content", err)
	// 	return
	// }
}

func ParseInventoryTable(imageContent []byte) ([]SellerProduct, error) {
	ctx := context.TODO()
	doc := types.Document{Bytes: imageContent}
	input := textract.AnalyzeDocumentInput{
		Document:     &doc,
		FeatureTypes: []types.FeatureType{types.FeatureTypeTables},
	}
	output, err := parseClient.AnalyzeDocument(ctx, &input)
	if err != nil {
		log.Println("Error parsing scanned doc", err)
		return nil, err
	}
	// buf, err := json.Marshal(output)
	// log.Println(string(buf), err)
	//var output textract.AnalyzeDocumentOutput
	//json.Unmarshal(sampleResponse, &output)

	blockMap := make(map[string]types.Block)
	var cellIds []string
	var sellerID string
	for _, block := range output.Blocks {
		blockMap[*block.Id] = block
		if block.BlockType == "TABLE" && len(block.Relationships) > 0 && len(block.Relationships[0].Ids) > 0 {
			cellIds = block.Relationships[0].Ids
		}
		if block.Text != nil && strings.Contains(*block.Text, "Seller:") {
			sellerID = strings.ReplaceAll(*block.Text, "Seller:", "")
		}
	}

	indexMap := make(map[string]string)
	var maxRow int32
	var maxCol int32
	for _, cellId := range cellIds {
		block, found := blockMap[cellId]
		if !found {
			break
		}
		childBlockText := getChildText(&block, blockMap)
		if block.RowIndex != nil && block.ColumnIndex != nil {
			key := fmt.Sprintf("%d_%d", *block.RowIndex, *block.ColumnIndex)
			if *block.RowIndex > maxRow {
				maxRow = *block.RowIndex
			}
			if *block.ColumnIndex > maxCol {
				maxCol = *block.ColumnIndex
			}
			indexMap[key] = childBlockText
		}
	}

	dataframe := make([][]string, maxRow+1)
	for i := 0; i <= int(maxRow); i++ {
		dataframe[i] = make([]string, maxCol+1)
	}
	for key, val := range indexMap {
		tokens := strings.Split(key, "_")
		row, _ := strconv.Atoi(tokens[0])
		col, _ := strconv.Atoi(tokens[1])
		dataframe[row][col] = val
	}
	var inv []SellerProduct
	for _, row := range dataframe {
		if len(row) != 5 {
			continue
		}
		if row[1] == "ID" || row[1] == "" || row[2] == "Name" {
			continue
		}
		row[4] = fixStr(row[4])
		row[3] = fixStr(row[3])
		price, _ := strconv.ParseFloat(row[3], 32)
		qty, _ := strconv.Atoi(row[4])
		sellerProduct := SellerProduct{
			ID:          uuid.New().String(),
			ProductID:   row[1],
			ProductName: row[2],
			SellerID:    sellerID,
			Price:       float32(price),
			Quantity:    qty,
		}
		inv = append(inv, sellerProduct)
	}
	return inv, nil
}

func fixStr(payload string) string {
	payload = strings.ReplaceAll(payload, "T", "7")
	payload = strings.ReplaceAll(payload, "O", "0")
	payload = strings.ReplaceAll(payload, "I", "1")
	payload = strings.ReplaceAll(payload, "i", "1")

	return payload
}

func getChildText(block *types.Block, blockMap map[string]types.Block) string {
	var text []string
	if len(block.Relationships) > 0 && len(block.Relationships[0].Ids) > 0 {
		for _, id := range block.Relationships[0].Ids {
			childBlock, found := blockMap[id]
			if found && childBlock.Text != nil {
				text = append(text, *childBlock.Text)
			}
		}
	}
	return strings.Join(text, " ")
}
