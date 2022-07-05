from conversationHandlers.stages import IDLE_STAGE
from lib import getMessageFromUpdate
from loggers.messages import logger


def idleStage(update, context):
    if update.message is None:
        return IDLE_STAGE

    message = getMessageFromUpdate(update)
    user = message.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    # Reply if necessary
    # chat_obj, is_new_user = db.get_user_chat(user, message)

    # chat_obj, is_new_user = {}, False
    message.reply_text('Hello there')

    # To save this message into database
    # message = json.loads(update.message.to_json())
    # db.save_message(message, chat=None)

    return IDLE_STAGE
