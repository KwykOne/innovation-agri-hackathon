from telegram import InlineKeyboardMarkup, ReplyKeyboardRemove

from questionHandlers.mainMenu import askMainMenu
from conversationHandlers.stages import *
from cfg import application as config
from db import db
from loggers.conversations import logger


def languageSelection(update, context):
    query = update.callback_query
    message = query.message
    user = query.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    languageCode = query.data
    logger.info("User selected language, %s: %s", user.first_name, languageCode)

    query.edit_message_reply_markup(
        reply_markup=InlineKeyboardMarkup([])
    )

    userProfile, _ = db.getUserProfile(user, message)
    isNewUser = False

    if 'languageCode' not in userProfile or userProfile['languageCode'] is None or len(userProfile['languageCode']) == 0:
        # First time user
        # Send intro messages
        isNewUser = True

    db.updateUserProfile(userProfile, {
        'languageCode': languageCode,
        'botStage': 'mainMenu',
    })

    # TODO Success message of language selection
    promptText = db.getMessageContent(languageCode, 'language_set_successfully', "Language set successfully.")
    message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())

    askMainMenu(update, context, languageCode)

    return MAIN_MENU
