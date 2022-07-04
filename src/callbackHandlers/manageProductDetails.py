from telegram import InlineKeyboardMarkup, ReplyKeyboardRemove

from conversationHandlers.stages import *
from db import db
from cfg import application as config
from loggers.conversations import logger
from questionHandlers.manageShop import askProductSelection, askProductName, askProductType, askQuantity, askPrice, \
    askDiscount, askOrganic, askProductImage, askManageProduct


def manageProductDetails(update, context):
    query = update.callback_query
    message = query.message
    user = query.from_user
    chat = message.chat

    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    selectedMenuItem = query.data
    logger.info("User selected menu item, %s: %s", user.first_name, selectedMenuItem)

    userProfile, _ = db.getUserProfile(user, message)
    query.edit_message_reply_markup(
        reply_markup=InlineKeyboardMarkup([])
    )

    languageCode = userProfile['languageCode']

    if selectedMenuItem == 'cancel':
        db.updateUserProfile(userProfile, {
            'botStage': 'manageShop',
            'selectedProductId': None,
        })

        askProductSelection(
            update, context, userProfile['languageCode'],
            userProfile, 1, config['paging']['selectProduct'],
        )

        return SELECT_PRODUCT
    elif selectedMenuItem == 'set_name':
        db.updateUserProfile(userProfile, {
            'botStage': 'updateProductName',
        })

        askProductName(update, context, userProfile['languageCode'])
        return ENTER_PRODUCT_NAME
    elif selectedMenuItem == 'set_type':
        db.updateUserProfile(userProfile, {
            'botStage': 'updateProductType',
        })

        selectedProductId = userProfile['selectedProductId']
        productName = ''
        if selectedProductId is None:
            t = db.getTempProduct(userProfile['_id'])
            productName = t['name'] if t is not None else ''
        else:
            product = db.getProduct(userProfile['selectedProductId'])
            productName = product['name'] if product is not None else ''

        refProduct = db.findReferenceProduct(productName)
        askProductType(update, context, userProfile['languageCode'], refProduct)
        return ENTER_PRODUCT_TYPE
    elif selectedMenuItem == 'set_price':
        db.updateUserProfile(userProfile, {
            'botStage': 'updatePrice',
        })
        askPrice(update, context, userProfile['languageCode'])
        return ENTER_PRICE
    elif selectedMenuItem == 'set_quantity':
        db.updateUserProfile(userProfile, {
            'botStage': 'updateQty',
        })
        askQuantity(update, context, userProfile['languageCode'])
        return ENTER_QUANTITY
    elif selectedMenuItem == 'set_discount':
        db.updateUserProfile(userProfile, {
            'botStage': 'updateProductDiscount',
        })
        askDiscount(update, context, userProfile['languageCode'])
        return ENTER_DISCOUNT
    elif selectedMenuItem == 'set_organic':
        db.updateUserProfile(userProfile, {
            'botStage': 'updateProductOrganic',
        })
        askOrganic(update, context, userProfile['languageCode'])
        return CHOOSE_ORGANIC
    elif selectedMenuItem == 'set_picture':
        db.updateUserProfile(userProfile, {
            'botStage': 'updateProductImage',
        })
        askProductImage(update, context, userProfile['languageCode'])
        return UPLOAD_PRODUCT_IMAGE
    elif selectedMenuItem == 'delete':
        db.updateUserProfile(userProfile, {
            'botStage': 'deleteProduct',
        })

        promptText = db.getMessageContent(languageCode, 'product_deleted_str',
                                          "Product delete functionality is not available.")
        message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())

        product = db.getProduct(userProfile['selectedProductId'])
        askManageProduct(update, context, userProfile, product)
        return AFTER_PRODUCT_SELECTION

