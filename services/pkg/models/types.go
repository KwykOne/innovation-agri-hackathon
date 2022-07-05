package models

type ProductEntity struct {
	ID          string            `json:"id" dynamodbav:"product_id,string"`
	Name        string            `json:"name" dynamodbav:"name,string"`
	Description string            `json:"description,omitempty" dynamodbav:"description,string"`
	Category    string            `json:"category" dynamodbav:"category,string"`
	Image       string            `json:"image" dynamodbav:"image,string"`
	Tags        map[string]string `json:"tags,omitempty" dynamodbav:"tags,string"`
}

type SellerEntity struct {
	ID      string `json:"id" dynamodbav:"seller_id,string"`
	Name    string `json:"name" dynamodbav:"name,string"`
	Address string `json:"address" dynamodbav:"address,string"`
	GSTN    string `json:"gstin" dynamodbav:"gstin,string"`
	Email   string `json:"email" dynamodbav:"email,string"`
	Phone   string `json:"phone" dynamodbav:"phone,string"`
}

type SellerProduct struct {
	ID          string  `json:"id"`
	ProductID   string  `json:"productId"`
	ProductName string  `json:"name"`
	SellerID    string  `json:"sellerId"`
	Quantity    int     `json:"quantity"`
	Price       float32 `json:"price"`
}
