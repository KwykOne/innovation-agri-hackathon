from questionHandlers.mainMenu import askLanguageSelection, askMainMenu
from db import db
from lib import sendTypingAction, getMessageFromUpdate
from loggers.conversations import logger
from conversationHandlers.stages import *


def start(update, context):
    message = getMessageFromUpdate(update)
    user = message.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    # TODO Save user in database

    logger.debug("User connected: %s", user.first_name)
    sendTypingAction(update, context, timeout=1.5)

    # Store new user in db
    userProfile, isNewUser = db.getUserProfile(user, message)

    if isNewUser:
        logger.debug("Found new user: %s", user.first_name)
        db.updateUserProfile(userProfile, {
            'botStage': 'languageSelection',
        })

        # Create sample order for new users
        db.insertSampleOrder(userProfile['_id'])

        askLanguageSelection(update, context)
        return LANGUAGE_SELECTION

    if 'languageCode' not in userProfile or userProfile['languageCode'] is None or len(userProfile['languageCode']) == 0:
        logger.debug("User preferred language is missing: %s", user.first_name)
        db.updateUserProfile(userProfile, {
            'botStage': 'languageSelection',
        })

        askLanguageSelection(update, context)
        return LANGUAGE_SELECTION

    logger.debug("Sending main menu to: %s", user.first_name)

    db.updateUserProfile(userProfile, {
        'botStage': 'mainMenu',
    })

    languageCode = userProfile['languageCode']
    askMainMenu(update, context, languageCode)
    return MAIN_MENU

    # return IDLE_STAGE
