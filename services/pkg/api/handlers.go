package api

import (
	"io/ioutil"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"gkishor.net/agrihack/services/pkg/models"
	"gkishor.net/agrihack/services/pkg/services"
)

type APIResp struct {
	Success      bool                   `json:"success"`
	ErrorMessage string                 `json:"errorMessage,omitempty"`
	Products     []models.ProductEntity `json:"products,omitempty"`
	Sellers      []models.SellerEntity  `json:"sellers,omitempty"`
	Seller       *models.SellerEntity   `json:"seller,omitempty"`
	Inventory    []models.SellerProduct `json:"inventory,omitempty"`
}

func HandleGetAllProducts(c *gin.Context) {
	var resp APIResp

	products, err := services.GetAllProducts()
	if err != nil {
		log.Println("Error getting products", err)
		resp.ErrorMessage = "Unable to get products from dynamo db"
		c.JSON(http.StatusInternalServerError, &resp)
		return
	}

	resp.Success = true
	resp.Products = products
	c.JSON(http.StatusOK, &resp)
}

func HandleGetAllSellers(c *gin.Context) {
	var resp APIResp

	sellers, err := services.GetAllSellers()
	if err != nil {
		log.Println("Error getting sellers", err)
		resp.ErrorMessage = "Unable to get sellers from dynamo db"
		c.JSON(http.StatusInternalServerError, &resp)
		return
	}

	resp.Success = true
	resp.Sellers = sellers
	c.JSON(http.StatusOK, &resp)
}

func HandleAddSeller(c *gin.Context) {
	var resp APIResp
	var seller models.SellerEntity
	err := c.ShouldBindJSON(&seller)
	if err != nil {
		log.Println("Unable to bind seller add request")
		resp.ErrorMessage = "bad seller add request"
		c.JSON(http.StatusBadRequest, &resp)
		return
	}
	newSeller, err := services.AddSeller(&seller)
	if err != nil {
		log.Println("Could not add seller", err)
		resp.ErrorMessage = "Could not add seller. Try again"
		c.JSON(http.StatusInternalServerError, &resp)
		return
	}

	resp.Success = true
	resp.Seller = newSeller
	c.JSON(http.StatusOK, &resp)
}

func HandleGetSeller(c *gin.Context) {

}

func HandleSellerSheetDownload(c *gin.Context) {
	sellerID := c.Param("sellerId")

	buf, err := services.GenerateSellerSheet(sellerID)
	if err != nil {
		return
	}
	//c.Header("Content-Type", "application/octet-stream")
	c.Header("Content-Disposition", `attachment; filename="seller_`+sellerID+`.pdf"`)
	c.Data(200, "application/octet-stream", buf)
}

func HandleInventoryScan(c *gin.Context) {
	var imageContent []byte
	var err error
	var resp APIResp

	uploadMode := c.Query("uploadMode")
	if uploadMode == "file" {
		file, _, err := c.Request.FormFile("image")
		if err != nil {
			log.Println("Error getting image file upload", err)
			resp.ErrorMessage = "Unable to get image file upload"
			c.JSON(http.StatusInternalServerError, &resp)
			return
		}

		imageContent, err = ioutil.ReadAll(file)
		if err != nil {
			log.Println("Error reading image content from file upload", err)
			resp.ErrorMessage = "Unable to read image from file upload"
			c.JSON(http.StatusInternalServerError, &resp)
			return
		}
	} else if uploadMode == "body" {
		imageContent, err = c.GetRawData()
		if err != nil {
			log.Println("Error reading image from body upload", err)
			resp.ErrorMessage = "Unable to read image from body upload"
			c.JSON(http.StatusInternalServerError, &resp)
			return
		}
	}
	if len(imageContent) == 0 {
		log.Println("Received empty image file")
		resp.ErrorMessage = "Received empty image file"
		c.JSON(http.StatusInternalServerError, &resp)
		return
	}

	//call service to load data into dynamodb
	//inventory, err :=
	inventory, err := services.ProcessInventory(imageContent)
	if err != nil {
		log.Println("Error processing image file upload", err)
		resp.ErrorMessage = "Could not process image file upload content"
		c.JSON(http.StatusInternalServerError, &resp)
		return
	}

	resp.Success = true
	resp.Inventory = inventory
	c.JSON(http.StatusOK, &resp)
}

func HandleProductUpload(c *gin.Context) {

	var xlsContent []byte
	var err error
	var resp APIResp

	uploadMode := c.Query("uploadMode")
	if uploadMode == "file" {
		file, _, err := c.Request.FormFile("xls")
		if err != nil {
			log.Println("Error getting xls file upload", err)
			resp.ErrorMessage = "Unable to get xls from file upload"
			c.JSON(http.StatusInternalServerError, &resp)
			return
		}

		xlsContent, err = ioutil.ReadAll(file)
		if err != nil {
			log.Println("Error reading xls content from file upload", err)
			resp.ErrorMessage = "Unable to read xls from file upload"
			c.JSON(http.StatusInternalServerError, &resp)
			return
		}
	} else if uploadMode == "body" {
		xlsContent, err = c.GetRawData()
		if err != nil {
			log.Println("Error reading xls from body upload", err)
			resp.ErrorMessage = "Unable to read xls from body upload"
			c.JSON(http.StatusInternalServerError, &resp)
			return
		}
	}
	if len(xlsContent) == 0 {
		log.Println("Received empty xls file")
		resp.ErrorMessage = "Received empty xls file"
		c.JSON(http.StatusInternalServerError, &resp)
		return
	}

	//call service to load data into dynamodb
	products, err := services.PersistProducts(xlsContent)
	if err != nil {
		log.Println("Error processing file upload", err)
		resp.ErrorMessage = "Could not process file upload content"
		c.JSON(http.StatusInternalServerError, &resp)
		return
	}

	resp.Success = true
	resp.Products = products
	c.JSON(http.StatusOK, &resp)

}
