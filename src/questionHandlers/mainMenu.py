from telegram import InlineKeyboardButton, ReplyKeyboardRemove, InlineKeyboardMarkup
from db import db
from cfg import application as config
from lib import sendTypingAction, getMessageFromUpdate
from loggers.conversations import logger


def askLanguageSelection(update, context):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    reply_keyboard = []
    languages = db.getLanguages()
    logger.debug(languages)

    for language in languages:
        reply_keyboard.append([
            InlineKeyboardButton(
                language['name'],
                callback_data=language['code']
            )
        ])

    # Language code is not set for the user
    promptText = db.getMessageContent(config['defaultLanguage'], 'ask_language_selection')
    # logger.debug("Message to be sent = " + promptText)
    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askMainMenu(update, context, languageCode, sendHelpImage=True):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'main_menu_selection', 'manage_shop_str', 'manage_orders_str'
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['manage_shop_str'], callback_data='manage_shop')],
        [InlineKeyboardButton(contentObj['manage_orders_str'], callback_data='manage_orders')],
    ]

    filePath = config['uploads']['assets_dir'] + '/' + languageCode + '_intro1.mp4'
    message.reply_animation(animation=open(filePath, 'rb'), reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=False,
    ))


def askManageOrders(update, context, userProfile):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)
    languageCode = userProfile['languageCode'] if 'languageCode' in userProfile else config['defaultLanguage']

    contentObj = db.getMessageContents(languageCode, [
        'manage_orders_prompt', 'view_pending_orders_str', 'view_order_history_str',
        'back',
    ])

    pendingBtnLabel = contentObj['view_pending_orders_str']
    historyBtnLabel = contentObj['view_order_history_str']

    excludeStatus = ['pending', 'accepted']
    completedOrderCount = db.getOrdersWithoutStatusCount(userProfile['_id'], excludeStatus)

    excludeStatus = ['completed', 'cancelled']
    pendingOrderCount = db.getOrdersWithoutStatusCount(userProfile['_id'], excludeStatus)

    if pendingOrderCount > 0:
        pendingBtnLabel += ' (' + str(pendingOrderCount) + ')'

    if completedOrderCount > 0:
        historyBtnLabel += ' (' + str(completedOrderCount) + ')'

    reply_keyboard = [
        [InlineKeyboardButton(pendingBtnLabel, callback_data='view_pending_orders')],
        [InlineKeyboardButton(historyBtnLabel, callback_data='view_order_history')],
        [InlineKeyboardButton(contentObj['back'], callback_data='back')],
    ]

    filePath = config['uploads']['assets_dir'] + '/' + userProfile['languageCode'] + '_orders-help.mp4'
    message.reply_animation(animation=open(filePath, 'rb'), reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))
