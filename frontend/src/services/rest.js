import axios from 'axios'
import fileDownload from 'js-file-download'

const SERVICES_URL_PREFIX = "https://b.medhaapps.com/services/v1"

async function getAllProducts() {
    var products = []
    try {
        const resp = await axios.get(`${SERVICES_URL_PREFIX}/products`)
        if(resp && resp.data && resp.data.success && resp.data.products) {
            products = resp.data.products
        }
    } catch(err) {
        console.log("Error getting all porducts", err)
    }
    return products
}

async function getAllSellers() {
    var sellers = []
    try {
        const resp = await axios.get(`${SERVICES_URL_PREFIX}/sellers`)
        if(resp && resp.data && resp.data.success && resp.data.sellers) {
            sellers = resp.data.sellers
        }
    } catch(err) {
        console.log("Error getting all porducts", err)
    }
    return sellers
}

function downloadSellerSheet({sellerId}) {
    try {
        axios({
            method: 'GET',
            url: `${SERVICES_URL_PREFIX}/sellers/${sellerId}/sheet`,            
            responseType: 'blob'
        }).then((resp) => {
            fileDownload(resp.data, `seller_${sellerId}.pdf`)
        })
    } catch(err) {
        console.log("Error downloading seller sheet", err)
    }    
}

const apiRegistry = {SERVICES_URL_PREFIX, getAllProducts, getAllSellers, downloadSellerSheet}
export {apiRegistry}
