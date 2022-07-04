from telegram import InlineKeyboardMarkup
from cfg import application as config
from conversationHandlers.stages import *
from db import db
from loggers.conversations import logger
from questionHandlers.manageShop import askProductSelection, askQuantity, askManageProduct


def chooseProductType(update, context):
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
    else:
        productData = {
            'type': selectedMenuItem,
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
