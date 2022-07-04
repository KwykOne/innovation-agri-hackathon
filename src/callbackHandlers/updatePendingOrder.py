from telegram import InlineKeyboardMarkup, ReplyKeyboardRemove

from conversationHandlers.stages import *
from db import db
from loggers.conversations import logger
from questionHandlers.mainMenu import askManageOrders


def updatePendingOrder(update, context):
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

    if selectedMenuItem == 'home':
        db.updateUserProfile(userProfile, {
            'botStage': 'manageOrders',
            'selectedOrderId': None,
            'ordersPage': 1,
        })

        askManageOrders(update, context, userProfile)
        return MANAGE_ORDERS

    elif selectedMenuItem == 'accept':
        orderId = userProfile['selectedOrderId'] if 'selectedOrderId' in userProfile else None
        order = None

        if orderId is not None:
            order = db.getOrder(orderId)

        if order is None:
            # TODO Handle error
            pass

        db.updateOrder(orderId, {
            'currentStatus': 'accepted',
        })

        db.updateUserProfile(userProfile, {
            'botStage': 'manageOrders',
            'selectedOrderId': None,
            'ordersPage': 1,
        })

        promptText = db.getMessageContent(languageCode, 'order_accepted_success', default='This order is ACCEPTED.')
        message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())

        askManageOrders(update, context, userProfile)
        return MANAGE_ORDERS

    elif selectedMenuItem == 'cancel':
        orderId = userProfile['selectedOrderId'] if 'selectedOrderId' in userProfile else None
        order = None

        if orderId is not None:
            order = db.getOrder(orderId)

        if order is None:
            # TODO Handle error
            pass

        db.updateOrder(orderId, {
            'currentStatus': 'cancelled',
        })

        db.updateUserProfile(userProfile, {
            'botStage': 'manageOrders',
            'selectedOrderId': None,
            'ordersPage': 1,
        })

        promptText = db.getMessageContent(languageCode, 'order_cancelled_success', default='This order is CANCELLED.')
        message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())

        askManageOrders(update, context, userProfile)
        return MANAGE_ORDERS
    elif selectedMenuItem == 'complete':
        orderId = userProfile['selectedOrderId'] if 'selectedOrderId' in userProfile else None
        order = None

        if orderId is not None:
            order = db.getOrder(orderId)

        if order is None:
            # TODO Handle error
            pass

        db.updateOrder(orderId, {
            'currentStatus': 'completed',
        })

        db.updateUserProfile(userProfile, {
            'botStage': 'manageOrders',
            'selectedOrderId': None,
            'ordersPage': 1,
        })

        promptText = db.getMessageContent(languageCode, 'order_completed_success', default='This order is COMPLETED.')
        message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())

        askManageOrders(update, context, userProfile)
        return MANAGE_ORDERS
