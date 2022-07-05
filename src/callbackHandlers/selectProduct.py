import math

import bson
from telegram import InlineKeyboardMarkup

from cfg import application as config
from conversationHandlers.stages import *
from db import db
from loggers.conversations import logger
from questionHandlers.mainMenu import askMainMenu
from questionHandlers.manageShop import askProductSelection, askProductName, askManageProduct


def selectProduct(update, context):
    query = update.callback_query
    message = query.message
    user = query.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    selectedMenuItem = query.data
    logger.info("User selected menu item, %s: %s", user.first_name, selectedMenuItem)

    userProfile, _ = db.getUserProfile(user, message)
    query.edit_message_reply_markup(
        reply_markup=InlineKeyboardMarkup([])
    )

    if selectedMenuItem == 'prev':
        currentPage = userProfile['productsPage'] if 'productsPage' in userProfile['productsPage'] else 1
        currentPage -= 1
        if currentPage < 1:
            currentPage = 1

        db.updateUserProfile(userProfile, {
            'botStage': 'chooseProductToEdit',
            'selectedProductId': None,
            'productsPage': currentPage,
        })

        askProductSelection(
            update, context, userProfile['languageCode'],
            userProfile, currentPage, config['paging']['selectProduct'],
        )

        return SELECT_PRODUCT
    elif selectedMenuItem == 'next':
        currentPage = userProfile['productsPage'] if 'productsPage' in userProfile['productsPage'] else 1
        currentPage += 1

        allProductsCount = db.getProductsCount(userProfile['_id'])
        totalPages = math.ceil(allProductsCount / config['paging']['selectProduct'])

        if currentPage > totalPages:
            currentPage = totalPages

        db.updateUserProfile(userProfile, {
            'botStage': 'chooseProductToEdit',
            'selectedProductId': None,
            'productsPage': currentPage,
        })

        askProductSelection(
            update, context, userProfile['languageCode'],
            userProfile, currentPage, config['paging']['selectProduct'],
        )

        return SELECT_PRODUCT
    elif selectedMenuItem == 'cancel':
        db.updateUserProfile(userProfile, {
            'botStage': 'mainMenu',
            'selectedProductId': None,
        })

        askMainMenu(update, context, userProfile['languageCode'])
        return MAIN_MENU
    elif selectedMenuItem == 'add_product':
        db.updateUserProfile(userProfile, {
            'botStage': 'addProduct',
            'selectedProductId': None,
        })

        db.clearTempProduct(userProfile['_id'])

        askProductName(update, context, userProfile['languageCode'])
        return ENTER_PRODUCT_NAME
    elif selectedMenuItem.startswith('select-product'):
        productId = selectedMenuItem.replace('select-product-', '')
        productId = bson.ObjectId(productId)

        db.updateUserProfile(userProfile, {
            'botStage': 'chooseProductToEdit',
            'selectedProductId': productId,
            'productsPage': 1,
        })

        product = db.getProduct(productId)
        db.saveTempProduct(userProfile['_id'], product)

        askManageProduct(update, context, userProfile, product)
        return AFTER_PRODUCT_SELECTION

    return IDLE_STAGE
