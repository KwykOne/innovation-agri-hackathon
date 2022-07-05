import shutil
from datetime import datetime

from telegram import ReplyKeyboardRemove

from db import db
from cfg import application as config
from lib import sendTypingAction, sha1File
from loggers.conversations import logger
from conversationHandlers.stages import *
from questionHandlers.mainMenu import askMainMenu
from questionHandlers.manageShop import askProductType, askManageProduct


def uploadProductImage(update, context):
    message = update.message
    user = message.from_user
    chat = message.chat
    # Ensuring this message is sent personally to bot
    if chat.title is not None:
        # Do nothing
        return IDLE_STAGE

    logger.info("User uploaded product image, %s", user.first_name)

    userProfile, _ = db.getUserProfile(user, message)

    file = None

    if update.message.document is not None:
        file = update.message.document.get_file()
    elif update.message.photo is not None and len(update.message.photo) > 0:
        file = update.message.photo[0].get_file()
    elif update.message.video is not None:
        file = update.message.video.get_file()
    elif update.message.attachment is not None:
        file = update.message.attachment.get_file()

    # TODO Any validation on uploaded image

    if file is None:
        # Try again file not uploaded
        return UPLOAD_PRODUCT_IMAGE

    # Save image data with unique hashed file names
    now = datetime.now().strftime('%Y-%m-%dT%H-%M-%SZ')
    tmpFileName = config['uploads']['temp_dir'] + '/upl-at-' + now + '.jpg'
    # logger.debug(tmpFileName)
    file.download(tmpFileName)
    sha1Hash = sha1File(tmpFileName)
    finalFileName = config['uploads']['temp_dir'] + '/f-' + sha1Hash + '.jpg'
    # logger.debug(finalFileName)
    shutil.move(tmpFileName, finalFileName)

    productData = {
        'imagePath': finalFileName,
    }

    db.saveTempProduct(userProfile['_id'], productData)

    logger.debug('Saving new product details')

    selectedProductId = userProfile['selectedProductId']

    # New product
    # Move temp product to Products collection
    # Or make changes from temp to products collection
    db.saveProductFromTemp(userProfile, selectedProductId)

    # TODO Go into Edit Product flow
    db.updateUserProfile(userProfile, {
        'botStage': 'manageProduct',
    })

    product = db.getProduct(selectedProductId)

    # Show Product Details and Ask Product manager Options
    askManageProduct(update, context, userProfile, product)
    return AFTER_PRODUCT_SELECTION

    # askMainMenu(update, context, userProfile['languageCode'])
    # return MAIN_MENU
