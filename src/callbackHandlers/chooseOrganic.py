from telegram import ReplyKeyboardRemove, InlineKeyboardMarkup

from conversationHandlers.stages import *
from db import db
from cfg import application as config
from loggers.conversations import logger
from questionHandlers.mainMenu import askMainMenu
from questionHandlers.manageShop import askProductSelection, askProductName, askProductImage, askManageProduct


def chooseOrganic(update, context):
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

    if selectedMenuItem == 'yes':
        productData = {
            'organic': True,
        }
        db.saveTempProduct(userProfile['_id'], productData)

        selectedProductId = userProfile['selectedProductId']
        db.saveProductFromTemp(userProfile, selectedProductId)

        # Show Product Details and Ask Product manager Options
        product = db.getProduct(selectedProductId)
        askManageProduct(update, context, userProfile, product)
        return AFTER_PRODUCT_SELECTION
    elif selectedMenuItem == 'no':
        productData = {
            'organic': False,
        }
        db.saveTempProduct(userProfile['_id'], productData)
        selectedProductId = userProfile['selectedProductId']
        db.saveProductFromTemp(userProfile, selectedProductId)

        # Show Product Details and Ask Product manager Options
        product = db.getProduct(selectedProductId)
        askManageProduct(update, context, userProfile, product)
        return AFTER_PRODUCT_SELECTION
    elif selectedMenuItem == 'cancel':
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
        # Invalid selection
        pass

    return IDLE_STAGE
