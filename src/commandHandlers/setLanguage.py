from questionHandlers.mainMenu import askLanguageSelection
from db import db
from lib import sendTypingAction, getMessageFromUpdate
from loggers.conversations import logger
from conversationHandlers.stages import *


def setLanguage(update, context):
    message = getMessageFromUpdate(update)
    user = message.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    # TODO Save user in database

    logger.debug("User requested to change language: %s", user.first_name)
    sendTypingAction(update, context, timeout=1.5)

    # Store new user in db
    userProfile, isNewUser = db.getUserProfile(user, message)

    # if isNewUser:
    # logger.debug("Found new user: %s", user.first_name)
    db.updateUserProfile(userProfile, {
        'botStage': 'languageSelection',
    })
    askLanguageSelection(update, context)

    # message.reply_animation(animation=open('assets/111.gif', 'rb'))
    return LANGUAGE_SELECTION
