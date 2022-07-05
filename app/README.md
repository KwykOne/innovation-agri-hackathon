# rainbow-catalog-app
App for farmers for managing inventory. 

## Original repo: 
https://github.com/abhisekpadhi/rainbow-catalog-app

## demo of product
[Video](https://vimeo.com/726786155)

## Technology
- React native

## How to run this
- Find backend API server here - https://github.com/abhisekpadhi/rainbow-catalog-api
- Modify the [constants](constants.js) and add LAN ip of the host running api server
  - If you are using mac, use this command to get the ip
  ```shell
  ifconfig | grep "inet " | grep -Fv 127.0.0.1 | awk '{print $2}'
    ```
- Run the [api server](https://github.com/abhisekpadhi/rainbow-catalog-api)
    ```shell
    yarn dev
    ```
- Connect your android phone or fire up an android emulator. I've only tested on an android phone. Issue this command to run the app on your phone.
  ```shell
    yarn android
  ```

