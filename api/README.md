# rainbow-catalog
API for smart cataloguing system for tech enablement of underserved farmers.

## Original repo: 
https://github.com/abhisekpadhi/rainbow-catalog-api

## tech
- api: nodejs & expressjs
- db: mysql

## db schema
- farmer
```shell
id
farmerName
```

- farm
```shell
id
farmerId
farmName
farmLocation
```

- farmPrefs
```shell
id
farmId
prefKey
prefValue
```

- productCatalog
```shell
id
productName
packSize
productDescription
imageUrl
grading
variant
perishability
logisticsNeed
coldChain
idealDelTat
```

- farmInventory
```shell
id
farmId
productId
priceInPaise
qty
```

- farmInventoryLedger
```shell
farmId
productId
qty
op
opening
createdAt
```

## How to run this
- Start a mysql server
- Create the schema of name `rainbow-catalog` 
- Import the [ddl](ddl.sql) file into the mysql repl
- Start a redis server
- Config of mysql and redis can be changed at [constants](src/CONSTANTS.ts) and [client](src/common/clients.ts)
- Install deps, fire this command
    ```shell
    yarn
    ```
- Start expressjs server, fire this command
    ```shell
    yarn dev
    ```
- Load [postman collection](Rainbow%20catalog%20-%20ONDC.postman_collection.json) to play around with the API's
- Entities and request/response schemas are in [model](src/models)
- Request flow: 
```shell
API -> Workflow -> Repository -> Database
                |-> Redis
``` 
- Master catalog excel sample, [download here](https://docs.google.com/spreadsheets/d/1rvJTNoKNvhWx_5yCntfI-rIxY7GELwy9CIEVRUsJrys/edit#gid=0) 