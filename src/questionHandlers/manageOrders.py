import math

from telegram import InlineKeyboardButton, InlineKeyboardMarkup, ReplyKeyboardRemove, ParseMode
from conversationHandlers.stages import *
from cfg import application as config
from db import db
from lib import getMessageFromUpdate, sendTypingAction, getOrderStatusStr, getOrderText
from questionHandlers.mainMenu import askManageOrders


def askOrderSelection(update, context, userProfile, excludingStatus, currentPage):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    languageCode = userProfile['languageCode']

    orders = db.getOrdersWithoutStatus(
        userProfile['_id'], excludingStatus,
        currentPage, config['paging']['viewOrders']
    )

    totalCount = db.getOrdersWithoutStatusCount(userProfile['_id'], excludingStatus)

    if totalCount == 0:
        # No orders yet
        if 'completed' in excludingStatus:
            # Pending order
            sendTypingAction(update, context, timeout=1.3)
            promptText = db.getMessageContent(languageCode, 'no_pending_orders_str',
                                              default='You do not have any pending orders to display.')
            message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())
            askManageOrders(update, context, userProfile)
            return MANAGE_ORDERS
        else:
            # Completed orders
            sendTypingAction(update, context, timeout=1.3)
            promptText = db.getMessageContent(languageCode, 'no_completed_orders_str',
                                              default='You do not have any completed orders to display.')
            message.reply_text(text=promptText, reply_markup=ReplyKeyboardRemove())
            askManageOrders(update, context, userProfile)
            return MANAGE_ORDERS

    totalPages = math.ceil(totalCount / config['paging']['viewOrders'])

    promptText = db.getMessageContent(languageCode, 'ask_select_order', default='Choose an Order to Manage')
    contentObj2 = db.getMessageContents(languageCode, [
        'prev', 'next', 'cancel_order_selection_str',
        'pending_str', 'accepted_str', 'cancelled_str', 'completed_str',
    ])

    reply_keyboard = []
    for order in orders:
        statusStr = getOrderStatusStr(contentObj2, order['currentStatus'])
        buttonLabel = order['orderNo'] + ' - ' + order['customerDetails']['name'] + ' - ' + statusStr
        cbData = 'select-order-' + str(order['_id'])
        reply_keyboard.append([
            InlineKeyboardButton(buttonLabel, callback_data=cbData)
        ])

    if currentPage > 1:
        reply_keyboard.append([
            InlineKeyboardButton(contentObj2['prev'], callback_data='prev')
        ])

    if currentPage < totalPages - 1:
        reply_keyboard.append([
            InlineKeyboardButton(contentObj2['next'], callback_data='next')
        ])

    reply_keyboard.append([
        InlineKeyboardButton(contentObj2['cancel_order_selection_str'], callback_data='cancel')
    ])

    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))

    if 'completed' in excludingStatus:
        return SELECT_PENDING_ORDER
    else:
        return SELECT_OLD_ORDER


def askOrderManage(update, context, userProfile, orderId):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)
    languageCode = userProfile['languageCode']
    order = db.getOrder(orderId)
    if order is None:
        # TODO Handle error
        pass

    reply_keyboard = []
    contentObj = db.getMessageContents(languageCode, [
        'accept_order_str', 'cancel_order_str',
        'complete_order_str', 'back_to_main_menu_str',
        'order_details_message', 'order_str',
        'customer_details_str', 'total_str', 'status_str',
        'amount_str', 'phone_str', 'items_str',
        'pending_str', 'accepted_str', 'cancelled_str', 'completed_str',
    ])

    orderText = getOrderText(contentObj, order)

    if order['currentStatus'] == 'pending':
        reply_keyboard.append([
            InlineKeyboardButton(contentObj['accept_order_str'], callback_data='accept')
        ])

        reply_keyboard.append([
            InlineKeyboardButton(contentObj['cancel_order_str'], callback_data='cancel')
        ])

        reply_keyboard.append([
            InlineKeyboardButton(contentObj['back_to_main_menu_str'], callback_data='home')
        ])

    elif order['currentStatus'] == 'accepted':
        reply_keyboard.append([
            InlineKeyboardButton(contentObj['complete_order_str'], callback_data='complete')
        ])

        reply_keyboard.append([
            InlineKeyboardButton(contentObj['back_to_main_menu_str'], callback_data='home')
        ])

    elif order['currentStatus'] == 'cancelled':
        reply_keyboard.append([
            InlineKeyboardButton(contentObj['back_to_main_menu_str'], callback_data='home'),
        ])

    elif order['currentStatus'] == 'completed':
        reply_keyboard.append([
            InlineKeyboardButton(contentObj['back_to_main_menu_str'], callback_data='home'),
        ])

    message.reply_text(text=orderText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ), parse_mode=ParseMode.HTML)

