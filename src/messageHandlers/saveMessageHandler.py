from telegram import Update, ParseMode
import json
from lib import getMessageFromUpdate
from loggers.conversations import logger
from db import db


def saveMessageHandler(update, context):
    message = getMessageFromUpdate(update)
    callbackQuery = None
    user = None
    chat = None

    if update.callback_query is not None:
        callbackQuery = update.callback_query.to_dict()
        message = callbackQuery.message
        user = callbackQuery.from_user.to_dict()
        chat = message.chat.to_dict()
        message = message.to_dict()
    elif message is not None:
        user = message.from_user.to_dict()
        chat = message.chat.to_dict()
        message = message.to_dict()

    db.save_message(message, chat, callbackQuery, user)
