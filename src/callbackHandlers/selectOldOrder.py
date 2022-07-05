import math

import bson
from telegram import InlineKeyboardMarkup, ReplyKeyboardRemove, ParseMode

from cfg import application as config
from conversationHandlers.stages import *
from db import db
from lib import getOrderText
from loggers.conversations import logger
from questionHandlers.mainMenu import askMainMenu, askManageOrders
from questionHandlers.manageOrders import askOrderSelection
from questionHandlers.manageShop import askProductSelection, askProductName


def selectOldOrder(update, context):
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

        excludeStatus = ['pending', 'accepted']
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

        excludeStatus = ['pending', 'accepted']
        return askOrderSelection(update, context, userProfile, excludeStatus, currentPage)
    elif selectedMenuItem == 'cancel':
        db.updateUserProfile(userProfile, {
            'botStage': 'mainMenu',
            'selectedOrderId': None,
            'ordersPage': 1,
        })

        askMainMenu(update, context, userProfile['languageCode'])
        return MAIN_MENU
    elif selectedMenuItem.startswith('select-order'):
        orderId = selectedMenuItem.replace('select-order-', '')
        orderId = bson.ObjectId(orderId)

        db.updateUserProfile(userProfile, {
            'botStage': 'selectOldOrder',
            'selectedOrderId': orderId,
            'ordersPage': 1,
        })

        order = db.getOrder(orderId)

        # TODO Send order details
        contentObj = db.getMessageContents(userProfile['languageCode'], [
            'order_details_message', 'order_str',
            'customer_details_str', 'total_str', 'status_str',
            'amount_str', 'phone_str', 'items_str',
            'pending_str', 'accepted_str', 'cancelled_str', 'completed_str',
        ])
        orderText = getOrderText(contentObj, order)

        message.reply_text(orderText, reply_markup=ReplyKeyboardRemove(), parse_mode=ParseMode.HTML)

        askManageOrders(update, context, userProfile)
        return MANAGE_ORDERS

    return IDLE_STAGE
