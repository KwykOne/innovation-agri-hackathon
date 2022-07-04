package main

import (
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"gkishor.net/agrihack/services/pkg/api"
)

func main() {
	router := gin.Default()

	config := cors.DefaultConfig()
	config.AllowAllOrigins = true
	router.Use(cors.New(config))

	v1 := router.Group("/services/v1")
	{
		v1.POST("/sellers", api.HandleAddSeller)
		v1.GET("/sellers", api.HandleGetAllSellers)
		v1.GET("/sellers/:sellerId", api.HandleGetSeller)
		v1.GET("/sellers/:sellerId/sheet", api.HandleSellerSheetDownload)
		v1.POST("/products/upload", api.HandleProductUpload)
		v1.GET("/products", api.HandleGetAllProducts)
		v1.POST("/inventory/scan", api.HandleInventoryScan)
	}

	router.Run(":16000")
}
