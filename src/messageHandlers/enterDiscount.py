from db import db
from lib import sendTypingAction
from loggers.conversations import logger
from conversationHandlers.stages import *
from questionHandlers.manageShop import askProductType, askQuantity, askPrice, askDiscount, askOrganic, askManageProduct


def enterDiscount(update, context):
    message = update.message
    user = message.from_user
    txt = message.text
    chat = message.chat
    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    logger.info("User entered product discount, %s: %s", user.first_name, txt)

    userProfile, _ = db.getUserProfile(user, message)

    if txt[-1] == '%':
        txt = txt[:-1]

    # try:
        # txt = float(txt)
    # except:
    #     TODO Handle invalid quantity
    #     pass

    productData = {
        'discount': txt,
    }

    db.saveTempProduct(userProfile['_id'], productData)

    selectedProductId = userProfile['selectedProductId']
    db.saveProductFromTemp(userProfile, selectedProductId)

    # Show Product Details and Ask Product manager Options
    product = db.getProduct(selectedProductId)
    askManageProduct(update, context, userProfile, product)
    return AFTER_PRODUCT_SELECTION
