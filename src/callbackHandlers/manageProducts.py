from telegram import InlineKeyboardMarkup
from cfg import application as config
from conversationHandlers.stages import *
from db import db
from loggers.conversations import logger
from questionHandlers.mainMenu import askMainMenu
from questionHandlers.manageShop import askProductSelection, askProductName


def manageProducts(update, context):
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

    if selectedMenuItem == 'add_product':
        db.updateUserProfile(userProfile, {
            'botStage': 'addProduct',
            'selectedProductId': None,
        })

        askProductName(update, context, userProfile['languageCode'])
        return ENTER_PRODUCT_NAME
    elif selectedMenuItem == 'edit_product':
        currentPage = 1

        db.updateUserProfile(userProfile, {
            'botStage': 'chooseProductToEdit',
            'selectedProductId': None,
            'productsPage': currentPage,
        })

        askProductSelection(
            update, context, userProfile['languageCode'],
            userProfile, currentPage, config['paging']['selectProduct'],
        )
        return SELECT_PRODUCT
    elif selectedMenuItem == 'back':
        db.updateUserProfile(userProfile, {
            'botStage': 'mainMenu',
            'selectedProductId': None,
        })

        askMainMenu(update, context, userProfile['languageCode'])
        return MAIN_MENU
    else:
        # Invalid selection
        pass

    return IDLE_STAGE
