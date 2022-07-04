from db import db
from lib import sendTypingAction
from loggers.conversations import logger
from conversationHandlers.stages import *
from questionHandlers.manageShop import askProductType, askManageProduct


def enterProductName(update, context):
    message = update.message
    user = message.from_user
    txt = message.text
    chat = message.chat
    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    logger.info("User entered product name, %s: %s", user.first_name, txt)

    userProfile, _ = db.getUserProfile(user, message)

    txt = txt.title()
    productData = {
        'name': txt,
    }

    db.saveTempProduct(userProfile['_id'], productData)

    selectedProductId = userProfile['selectedProductId']

    if selectedProductId is None:
        refProduct = db.findReferenceProduct(txt)
        if refProduct is not None and 'imagePath' in refProduct and len(refProduct['imagePath']) > 0:
            # Copy image of reference product
            db.saveTempProduct(userProfile['_id'], {
                'imagePath': refProduct['imagePath'],
            })

            if 'imagePath' in refProduct and refProduct['imagePath'] is not None and len(refProduct['imagePath']) > 0:
                message.reply_photo(photo=open(refProduct['imagePath'], 'rb'))

        # Ask for product type
        askProductType(update, context, userProfile['languageCode'], refProduct)
        return ENTER_PRODUCT_TYPE
    else:
        db.saveProductFromTemp(userProfile, selectedProductId)

        # Show Product Details and Ask Product manager Options
        product = db.getProduct(selectedProductId)
        askManageProduct(update, context, userProfile, product)
        return AFTER_PRODUCT_SELECTION
