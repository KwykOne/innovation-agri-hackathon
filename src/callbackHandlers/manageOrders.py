from telegram import ReplyKeyboardRemove, InlineKeyboardMarkup
from cfg import application as config
from conversationHandlers.stages import *
from db import db
from lib import sendTypingAction
from loggers.conversations import logger
from questionHandlers.mainMenu import askMainMenu, askManageOrders
from questionHandlers.manageOrders import askOrderSelection


def manageOrders(update, context):
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

    if selectedMenuItem == 'view_pending_orders':
        currentPage = 1

        db.updateUserProfile(userProfile, {
            'botStage': 'pendingOrders',
            'ordersPage': currentPage,
            'selectedOrderId': None,
        })

        excludeStatus = ['completed', 'cancelled']
        return askOrderSelection(update, context, userProfile, excludeStatus, currentPage)
    elif selectedMenuItem == 'view_order_history':
        currentPage = 1

        db.updateUserProfile(userProfile, {
            'botStage': 'orderHistory',
            'ordersPage': currentPage,
            'selectedOrderId': None,
        })

        excludeStatus = ['pending', 'accepted']
        return askOrderSelection(update, context, userProfile, excludeStatus, currentPage)

        # TODO Send old orders with page-wise data: kind of report functionality
    elif selectedMenuItem == 'back':
        db.updateUserProfile(userProfile, {
            'botStage': 'mainMenu',
        })

        askMainMenu(update, context, userProfile['languageCode'])

        return MAIN_MENU
    else:
        # Invalid selection
        pass

    return IDLE_STAGE
