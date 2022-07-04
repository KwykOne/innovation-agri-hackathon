from db import db
from lib import sendTypingAction
from loggers.conversations import logger
from conversationHandlers.stages import *
from questionHandlers.manageShop import askProductType, askQuantity, askPrice, askManageProduct


def enterQuantity(update, context):
    message = update.message
    user = message.from_user
    txt = message.text
    chat = message.chat
    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    logger.info("User entered product quantity, %s: %s", user.first_name, txt)

    userProfile, _ = db.getUserProfile(user, message)

    try:
        txt = int(txt)
    except:
        # TODO Handle invalid quantity
        pass

    productData = {
        'qty': txt,
    }

    db.saveTempProduct(userProfile['_id'], productData)

    selectedProductId = userProfile['selectedProductId']

    if selectedProductId is None:
        # Ask for product type
        askPrice(update, context, userProfile['languageCode'])
        return ENTER_PRICE
    else:
        db.saveProductFromTemp(userProfile, selectedProductId)

        # Show Product Details and Ask Product manager Options
        product = db.getProduct(selectedProductId)
        askManageProduct(update, context, userProfile, product)
        return AFTER_PRODUCT_SELECTION


