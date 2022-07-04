from telegram import ParseMode

from conversationHandlers.stages import IDLE_STAGE
from lib import getMessageFromUpdate, sendTypingAction
from loggers.conversations import logger


def idle(update, context):
    message = getMessageFromUpdate(update)
    user = message.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    # TODO Save user in database

    logger.info("User is not in idle mode?? %s", user.first_name)
    sendTypingAction(update, context, timeout=1.5)
    message.reply_text(
        'This bot has hit its limits. The feature you were trying to access was not found.'
        '\n\nUse the <b>/start</b> command to start from main menu.',
        parse_mode=ParseMode.HTML
    )
    return IDLE_STAGE

