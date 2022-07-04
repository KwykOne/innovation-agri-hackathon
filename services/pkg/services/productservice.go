package services

import (
	"bytes"
	"log"

	"github.com/google/uuid"
	"github.com/johnfercher/maroto/pkg/color"
	"github.com/johnfercher/maroto/pkg/consts"
	"github.com/johnfercher/maroto/pkg/pdf"
	"github.com/johnfercher/maroto/pkg/props"
	"github.com/xuri/excelize/v2"
	"gkishor.net/agrihack/services/pkg/models"
)

func PersistProducts(content []byte) ([]models.ProductEntity, error) {
	var products []models.ProductEntity

	xlsFile, err := excelize.OpenReader(bytes.NewReader(content))
	if err != nil {
		log.Println("Error opening xls content as file", err)
		return products, err
	}
	defer xlsFile.Close()
	rows, err := xlsFile.GetRows("Farm Produce")
	if err != nil {
		log.Println("Error reading product xls", err)
		return products, err
	}

	for _, row := range rows {
		if row[0] == "ID" {
			continue
		}
		if len(row) < 4 {
			row = append(row, "")
		}
		product := models.ProductEntity{
			ID:       row[0],
			Category: row[1],
			Name:     row[2],
			Image:    row[3],
		}
		products = append(products, product)
	}
	return models.PersistProducts(products)
}

func ProcessInventory(buf []byte) ([]models.SellerProduct, error) {
	return models.ParseInventoryTable(buf)
}

func AddSeller(seller *models.SellerEntity) (*models.SellerEntity, error) {
	if seller.ID == "" {
		seller.ID = uuid.NewString()
	}
	err := models.PersistSeller(seller)
	return seller, err
}

func GenerateSellerSheet(sellerID string) ([]byte, error) {
	buffer, err := generatePDF(sellerID)
	if err != nil {
		return nil, err
	}
	return buffer.Bytes(), nil
}

func GetAllProducts() ([]models.ProductEntity, error) {
	return models.GetAllproducts()
}

func GetAllSellers() ([]models.SellerEntity, error) {
	return models.GetAllSellers()
}

func generatePDF(sellerID string) (bytes.Buffer, error) {
	var content bytes.Buffer
	products, err := models.GetAllproducts()
	if err != nil {
		return content, err
	}
	m := pdf.NewMaroto(consts.Portrait, consts.A4)
	m.SetPageMargins(10, 15, 10)

	m.RegisterHeader(func() {
		m.Row(20, func() {
			m.ColSpace(2)
			m.Col(8, func() {
				m.Text("Daily Seller Inventory Management sheet", props.Text{
					Size:        15,
					Align:       consts.Center,
					Style:       consts.Bold,
					Extrapolate: false,
					Color:       color.NewBlack(),
				})
			})
			m.ColSpace(2)
		})
		m.Row(10, func() {
			m.Col(12, func() {
				m.Text("Seller: "+sellerID, props.Text{
					Top:   3,
					Style: consts.Bold,
					Align: consts.Left,
				})
			})
		})
	})

	var tableContents [][]string

	for _, product := range products {
		tableContents = append(tableContents, []string{product.ID, product.Name, "", ""})
	}
	tableHeaders := []string{"ID", "Name", "Price", "Quantity"}
	m.TableList(tableHeaders, tableContents, props.TableList{
		Line: true,
		ContentProp: props.TableListContent{
			GridSizes: []uint{5, 4, 2, 1},
		},
		HeaderProp: props.TableListContent{
			GridSizes: []uint{5, 4, 2, 1},
		},
	})

	return m.Output()
}
