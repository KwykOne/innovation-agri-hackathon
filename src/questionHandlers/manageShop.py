import math

from telegram import InlineKeyboardButton, InlineKeyboardMarkup, ReplyKeyboardRemove, ParseMode
from cfg import application as config
from db import db
from lib import getMessageFromUpdate, sendTypingAction


def askManageShop(update, context, languageCode):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'manage_products_prompt', 'add_product_str', 'edit_product_str', 'back',
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['add_product_str'], callback_data='add_product')],
        [InlineKeyboardButton(contentObj['edit_product_str'], callback_data='edit_product')],
        [InlineKeyboardButton(contentObj['back'], callback_data='back')],
    ]

    message.reply_text(text=contentObj['manage_products_prompt'], reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askProductName(update, context, languageCode):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'cancel_str',
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['cancel_str'], callback_data='cancel')],
    ]

    filePath = config['uploads']['assets_dir'] + '/' + languageCode + '_enter-name.mp4'
    # promptText = db.getMessageContent(languageCode, 'ask_product_name_prompt', default='Enter Product Name')
    message.reply_animation(animation=open(filePath, 'rb'), reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askProductType(update, context, languageCode, refProduct):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'cancel_str',
    ])

    reply_keyboard = []
    productTypes = []

    if refProduct is not None:
        if 'typesLocal' in refProduct and languageCode in refProduct['typesLocal']:
            productTypes = refProduct['typesLocal'][languageCode]
        elif 'types' in refProduct and len(refProduct['types']) > 0:
            productTypes = refProduct['types']

        for tp in productTypes:
            reply_keyboard.append(
                [InlineKeyboardButton(tp, callback_data=tp)],
            )

    reply_keyboard.append(
        [InlineKeyboardButton(contentObj['cancel_str'], callback_data='cancel')],
    )

    promptText = db.getMessageContent(languageCode, 'ask_product_type_prompt', default='Enter Product Type')
    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askQuantity(update, context, languageCode):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'cancel_str',
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['cancel_str'], callback_data='cancel')],
    ]

    promptText = db.getMessageContent(languageCode, 'ask_product_qty_prompt',
                                      default='How many kgs do you want to sell?')
    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askPrice(update, context, languageCode):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'cancel_str',
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['cancel_str'], callback_data='cancel')],
    ]

    promptText = db.getMessageContent(languageCode, 'ask_product_price_prompt',
                                      default='How much do you want to charge per kg')
    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askDiscount(update, context, languageCode):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'cancel_str',
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['cancel_str'], callback_data='cancel')],
    ]

    promptText = db.getMessageContent(languageCode, 'ask_product_discount_prompt',
                                      default='How much discount do you want to give?')
    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askOrganic(update, context, languageCode):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'ask_product_organic_prompt', 'yes', 'no', 'back',
        'cancel_str',
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['yes'], callback_data='yes')],
        [InlineKeyboardButton(contentObj['no'], callback_data='no')],
        # [InlineKeyboardButton(contentObj['back'], callback_data='back')],
        [InlineKeyboardButton(contentObj['cancel_str'], callback_data='cancel')],
    ]

    message.reply_text(text=contentObj['ask_product_organic_prompt'], reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askProductImage(update, context, languageCode):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    contentObj = db.getMessageContents(languageCode, [
        'cancel_str',
    ])

    reply_keyboard = [
        [InlineKeyboardButton(contentObj['cancel_str'], callback_data='cancel')],
    ]

    promptText = db.getMessageContent(languageCode, 'ask_product_image_prompt',
                                      default='Upload one image for your product')
    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askProductSelection(update, context, languageCode, userProfile, currentPage, limit, sendHelpImage=True):
    message = getMessageFromUpdate(update)
    sendTypingAction(update, context, timeout=1.3)

    products = db.getProducts(userProfile['_id'], currentPage, limit)
    allProductsCount = db.getProductsCount(userProfile['_id'])
    totalPages = math.ceil(allProductsCount / limit)

    # If no products send a different image
    if len(products) == 0:
        filePath = config['uploads']['assets_dir'] + '/' + userProfile['languageCode'] + '_store-empty.mp4'
    else:
        filePath = config['uploads']['assets_dir'] + '/' + userProfile['languageCode'] + '_catalog-help.mp4'

    # promptText = db.getMessageContent(languageCode, 'ask_select_product', default='Choose a Product')
    contentObj2 = db.getMessageContents(languageCode, [
        'prev', 'next', 'add_product_str', 'back_to_main_menu_str',
    ])

    reply_keyboard = []
    for product in products:
        buttonLabel = product['name'] + ' (' + product['type'] + \
                      ') ' + ' - ' + str(product['qty']) + ' kg' + ' @ ' + \
                      '₹' + str(product['price'])
        cbData = 'select-product-' + str(product['_id'])
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
        InlineKeyboardButton(contentObj2['add_product_str'], callback_data='add_product'),
    ])

    reply_keyboard.append([
        InlineKeyboardButton(contentObj2['back_to_main_menu_str'], callback_data='cancel')
    ])

    message.reply_animation(animation=open(filePath, 'rb'), reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ))


def askManageProduct(update, context, userProfile, product):
    message = getMessageFromUpdate(update)
    languageCode = userProfile['languageCode']
    sendTypingAction(update, context, timeout=1.3)

    if 'imagePath' in product and product['imagePath'] is not None and len(product['imagePath']) > 0:
        message.reply_photo(photo=open(product['imagePath'], 'rb'))

    # promptText = db.getMessageContent(languageCode, 'product_details_text', default='Product details to be shown here')

    contentObj2 = db.getMessageContents(languageCode, [
        'set_name', 'set_type', 'set_price', 'set_quantity', 'set_discount',
        'set_organic', 'set_picture', 'delete', 'discount_str',
        'cancel_str', 'product_details_message', 'edit_product_prompt',
    ])

    promptText = contentObj2['product_details_message'] + '\n'
    promptText += '<b>' + product['name'] + '</b> (' + product['type'] + ')\n'
    promptText += '<b>' + str(product['qty']) + ' kg. @ ₹' + str(product['price']) + '/- per kg.</b>\n'

    if 'organic' in product:
        promptText += 'Organic: '
        if product['organic'] is True:
            promptText += 'Yes'
        else:
            promptText += 'No'
        promptText += '\n'

    if 'discount' in product:
        promptText += contentObj2['discount_str'] + ': ' + str(product['discount']) + '%\n'

    promptText += '\n' + contentObj2['edit_product_prompt']

    reply_keyboard = []

    # reply_keyboard.append([
    #     InlineKeyboardButton(contentObj2['set_name'], callback_data='set_name'),
    #     InlineKeyboardButton(contentObj2['set_type'], callback_data='set_type'),
    # ])

    reply_keyboard.append([
        InlineKeyboardButton(contentObj2['set_quantity'], callback_data='set_quantity'),
        InlineKeyboardButton(contentObj2['set_price'], callback_data='set_price'),
    ])

    reply_keyboard.append([
        InlineKeyboardButton(contentObj2['set_discount'], callback_data='set_discount'),
        InlineKeyboardButton(contentObj2['set_picture'], callback_data='set_picture'),
        # InlineKeyboardButton(contentObj2['set_organic'], callback_data='set_organic'),
    ])

    reply_keyboard.append([
        InlineKeyboardButton(contentObj2['cancel_str'], callback_data='cancel'),
    ])

    # InlineKeyboardButton(contentObj2['delete'], callback_data='delete'),

    message.reply_text(text=promptText, reply_markup=InlineKeyboardMarkup(
        reply_keyboard, one_time_keyboard=True, resize_keyboard=True,
    ), parse_mode=ParseMode.HTML)
