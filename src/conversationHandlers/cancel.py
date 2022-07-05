from telegram import ReplyKeyboardRemove
from conversationHandlers.stages import IDLE_STAGE
from lib import sendTypingAction
from loggers.conversations import logger

# TODO Reply Keyboard Remove is required in all steps
# Otherwise keyboard remains as it was in chat
# Whenever reply_text happens without a keyboard, reply_markup=ReplyKeyboardRemove() is needed


def cancel(update, context):
    message = update.message
    user = message.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    sendTypingAction(update, context)
    logger.info("User %s canceled the conversation.", user.first_name)
    message.reply_text(text="Last operation was cancelled.", reply_markup=ReplyKeyboardRemove())

    return IDLE_STAGE
