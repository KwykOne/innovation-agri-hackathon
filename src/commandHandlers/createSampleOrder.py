from telegram import ReplyKeyboardRemove

from conversationHandlers.stages import *
from db import db
from lib import sendTypingAction, getMessageFromUpdate
from loggers.conversations import logger
from questionHandlers.mainMenu import askManageOrders


def createSampleOrder(update, context):
    message = getMessageFromUpdate(update)
    user = message.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    logger.debug("User wants to create sample order: %s", user.first_name)
    sendTypingAction(update, context, timeout=1.5)

    userProfile, isNewUser = db.getUserProfile(user, message)

    db.insertSampleOrder(userProfile['_id'])

    languageCode = userProfile['languageCode']
    promptText = db.getMessageContent(languageCode, 'sample_order_success', default='Sample order created successfully.')
    message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())

    askManageOrders(update, context, userProfile)
    return MANAGE_ORDERS
