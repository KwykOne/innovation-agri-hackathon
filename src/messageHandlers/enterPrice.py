from telegram import ReplyKeyboardRemove

from db import db
from lib import sendTypingAction
from loggers.conversations import logger
from conversationHandlers.stages import *
from questionHandlers.manageShop import askProductType, askQuantity, askPrice, askDiscount, askManageProduct


def enterPrice(update, context):
    message = update.message
    user = message.from_user
    txt = message.text
    chat = message.chat
    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    logger.info("User entered product price, %s: %s", user.first_name, txt)

    userProfile, _ = db.getUserProfile(user, message)

    try:
        txt = float(txt)
    except:
        # TODO Handle invalid quantity
        pass

    productData = {
        'price': txt,
    }

    db.saveTempProduct(userProfile['_id'], productData)

    selectedProductId = userProfile['selectedProductId']
    productId = db.saveProductFromTemp(userProfile, selectedProductId)

    if selectedProductId is None:
        # TODO New product added success message
        # message.reply_text('New product added')

        selectedProductId = productId
        db.updateUserProfile(userProfile, {
            'selectedProductId': productId,
        })

        # Inform that product is now added
        default1 = "Your product is listed. You will get notified when someone places an order."
        promptText = db.getMessageContent(userProfile['languageCode'], 'product_add_success', default=default1)
        sendTypingAction(update, context, timeout=1.3)
        message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())

        sendTypingAction(update, context, timeout=1.7)

    # Show Product Details and Ask Product manager Options
    product = db.getProduct(selectedProductId)
    askManageProduct(update, context, userProfile, product)
    return AFTER_PRODUCT_SELECTION
