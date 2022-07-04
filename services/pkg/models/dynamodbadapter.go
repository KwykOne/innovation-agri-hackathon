package models

import (
	"context"
	"log"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/feature/dynamodb/attributevalue"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb/types"
	"github.com/aws/aws-sdk-go/aws"
	"github.com/google/uuid"
)

var dynamoDBClient *dynamodb.Client

func init() {
	cfg, err := config.LoadDefaultConfig(context.TODO(), config.WithRegion("ap-south-1"))
	if err != nil {
		log.Println("Error creating aws config")
	}
	dynamoDBClient = dynamodb.NewFromConfig(cfg)
}

func PersistSeller(seller *SellerEntity) error {
	input := &dynamodb.PutItemInput{
		TableName: aws.String("agri_sellers"),
		Item: map[string]types.AttributeValue{
			"seller_id": &types.AttributeValueMemberS{
				Value: seller.ID,
			},
			"name": &types.AttributeValueMemberS{
				Value: seller.Name,
			},
			"address": &types.AttributeValueMemberS{
				Value: seller.Address,
			},
			"gstin": &types.AttributeValueMemberS{
				Value: seller.GSTN,
			},
			"email": &types.AttributeValueMemberS{
				Value: seller.Email,
			},
			"phone": &types.AttributeValueMemberS{
				Value: seller.Phone,
			},
		},
	}
	_, err := dynamoDBClient.PutItem(context.TODO(), input)
	if err != nil {
		log.Println("Error persisting seller to dymaamo db", err)
		return err
	}

	return nil

}

func GetAllproducts() ([]ProductEntity, error) {
	var allProducts []ProductEntity
	var err error

	input := &dynamodb.ScanInput{TableName: aws.String("agri_products"), Limit: aws.Int32(50)}
	for {
		result, err := dynamoDBClient.Scan(context.TODO(), input)
		if err != nil {
			log.Println("Error scanning dynamodb for products", err)
			return allProducts, err
		}
		var products []ProductEntity
		err = attributevalue.UnmarshalListOfMaps(result.Items, &products)
		if err != nil {
			log.Println("Error unmarshaling list of maps to list of products", err)
			break
		}
		allProducts = append(allProducts, products...)
		if result.ScannedCount == 0 || len(result.LastEvaluatedKey) == 0 {
			break
		}
		input.ExclusiveStartKey = result.LastEvaluatedKey
	}

	return allProducts, err
}

func GetAllSellers() ([]SellerEntity, error) {
	var allSellers []SellerEntity
	var err error

	input := &dynamodb.ScanInput{TableName: aws.String("agri_sellers"), Limit: aws.Int32(50)}
	for {
		result, err := dynamoDBClient.Scan(context.TODO(), input)
		if err != nil {
			log.Println("Error scanning dynamodb for products", err)
			return allSellers, err
		}
		var sellers []SellerEntity
		err = attributevalue.UnmarshalListOfMaps(result.Items, &sellers)
		if err != nil {
			log.Println("Error unmarshaling list of maps to list of products", err)
			break
		}
		allSellers = append(allSellers, sellers...)
		if result.ScannedCount == 0 || len(result.LastEvaluatedKey) == 0 {
			break
		}
		input.ExclusiveStartKey = result.LastEvaluatedKey
	}

	return allSellers, err
}

func PersistProducts(products []ProductEntity) ([]ProductEntity, error) {
	ctx := context.TODO()
	var uploadedProducts []ProductEntity
	for i := range products {
		product := &products[i]
		if product.ID == "" {
			product.ID = uuid.New().String()
		}
		item := &dynamodb.PutItemInput{
			TableName: aws.String("agri_products"),
			Item: map[string]types.AttributeValue{
				"product_id": &types.AttributeValueMemberS{
					Value: product.ID,
				},
				"name": &types.AttributeValueMemberS{
					Value: product.Name,
				},
				// "description": &types.AttributeValueMemberS{
				// 	Value: product.Description,
				// },
				"category": &types.AttributeValueMemberS{
					Value: product.Category,
				},
			},
		}
		if product.Image != "" {
			item.Item["image"] = &types.AttributeValueMemberS{
				Value: product.Image,
			}
		}
		_, err := dynamoDBClient.PutItem(ctx, item)
		if err != nil {
			log.Println("Error persisting product to dynamo db")
			//return products, err
			continue
		}
		uploadedProducts = append(uploadedProducts, *product)
	}
	return uploadedProducts, nil
}
