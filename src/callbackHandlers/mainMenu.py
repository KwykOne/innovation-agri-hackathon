from telegram import InlineKeyboardMarkup

from lib import sendTypingAction
from questionHandlers.mainMenu import askManageOrders
from conversationHandlers.stages import *
from db import db
from cfg import application as config
from loggers.conversations import logger
from questionHandlers.manageShop import askProductSelection


def mainMenu(update, context):
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

    languageCode = userProfile['languageCode']

    if selectedMenuItem == 'manage_shop':
        db.updateUserProfile(userProfile, {
            'botStage': 'manageShop',
        })

        askProductSelection(
            update, context, userProfile['languageCode'],
            userProfile, 1, config['paging']['selectProduct'],
        )
        return SELECT_PRODUCT

        # askManageShop(update, context, languageCode)

        # return MANAGE_PRODUCTS
    elif selectedMenuItem == 'manage_orders':
        db.updateUserProfile(userProfile, {
            'botStage': 'manageOrders',
        })

        askManageOrders(update, context, userProfile)

        return MANAGE_ORDERS
    else:
        # Invalid selection
        pass

    return IDLE_STAGE
