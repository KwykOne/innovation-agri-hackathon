from db import db
from lib import sendTypingAction
from loggers.conversations import logger
from conversationHandlers.stages import *
from questionHandlers.manageShop import askProductType, askQuantity, askManageProduct


def enterProductType(update, context):
    message = update.message
    user = message.from_user
    txt = message.text
    chat = message.chat
    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    logger.info("User entered product type, %s: %s", user.first_name, txt)

    userProfile, _ = db.getUserProfile(user, message)

    productData = {
        'type': txt,
    }

    db.saveTempProduct(userProfile['_id'], productData)

    selectedProductId = userProfile['selectedProductId']

    if selectedProductId is None:
        # Ask for product type
        askQuantity(update, context, userProfile['languageCode'])
        return ENTER_QUANTITY
    else:
        db.saveProductFromTemp(userProfile, selectedProductId)

        # Show Product Details and Ask Product manager Options
        product = db.getProduct(selectedProductId)
        askManageProduct(update, context, userProfile, product)
        return AFTER_PRODUCT_SELECTION
