import math

import bson
from telegram import InlineKeyboardMarkup, ReplyKeyboardRemove

from cfg import application as config
from conversationHandlers.stages import *
from db import db
from loggers.conversations import logger
from questionHandlers.mainMenu import askMainMenu, askManageOrders
from questionHandlers.manageOrders import askOrderSelection, askOrderManage
from questionHandlers.manageShop import askProductSelection, askProductName


def selectPendingOrder(update, context):
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
        currentPage = userProfile['ordersPage'] if 'ordersPage' in userProfile['ordersPage'] else 1
        currentPage -= 1
        if currentPage < 1:
            currentPage = 1

        db.updateUserProfile(userProfile, {
            'botStage': 'chooseOrder',
            'selectedOrderId': None,
            'ordersPage': currentPage,
        })

        excludeStatus = ['completed', 'cancelled']
        return askOrderSelection(update, context, userProfile, excludeStatus, currentPage)
    elif selectedMenuItem == 'next':
        currentPage = userProfile['productsPage'] if 'productsPage' in userProfile['productsPage'] else 1
        currentPage += 1

        allProductsCount = db.getProductsCount(userProfile['_id'])
        totalPages = math.ceil(allProductsCount / config['paging']['selectProduct'])

        if currentPage > totalPages:
            currentPage = totalPages

        db.updateUserProfile(userProfile, {
            'botStage': 'chooseOrder',
            'selectedProductId': None,
            'ordersPage': currentPage,
        })

        excludeStatus = ['completed', 'cancelled']
        return askOrderSelection(update, context, userProfile, excludeStatus, currentPage)
    elif selectedMenuItem == 'cancel':
        db.updateUserProfile(userProfile, {
            'botStage': 'mainMenu',
            'selectedOrderId': None,
            'ordersPage': 1,
        })

        askManageOrders(update, context, userProfile)
        return MANAGE_ORDERS
    elif selectedMenuItem.startswith('select-order'):
        orderId = selectedMenuItem.replace('select-order-', '')
        orderId = bson.ObjectId(orderId)

        db.updateUserProfile(userProfile, {
            'botStage': 'selectPendingOrder',
            'selectedOrderId': orderId,
            'ordersPage': 1,
        })

        # askProductName(update, context, userProfile['languageCode'])
        # return ENTER_PRODUCT_NAME
        # TODO Send order details
        # askManagePendingOrder()
        askOrderManage(update, context, userProfile, orderId)

        return MANAGE_PENDING_ORDER

    return IDLE_STAGE
