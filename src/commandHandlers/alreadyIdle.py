from telegram import ParseMode

from conversationHandlers.stages import IDLE_STAGE
from lib import getMessageFromUpdate, sendTypingAction
from loggers.conversations import logger


def alreadyIdle(update, context):
    message = getMessageFromUpdate(update)
    user = message.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    # TODO Save user in database

    logger.info("User is already in idle mode ^_^", user.first_name)
    sendTypingAction(update, context, timeout=1.5)
    message.reply_text('Haha, I\'m already in idle mode.', parse_mode=ParseMode.HTML)
    return IDLE_STAGE
