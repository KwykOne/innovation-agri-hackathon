from telegram.ext import (Updater, CommandHandler, MessageHandler, Filters,
                          ConversationHandler, CallbackQueryHandler, PicklePersistence)

from callbackHandlers.cancelProductManagement import cancelProductManagement
from callbackHandlers.chooseProductType import chooseProductType
from callbackHandlers.manageProductDetails import manageProductDetails
from callbackHandlers.updatePendingOrder import updatePendingOrder
from callbackHandlers.chooseOrganic import chooseOrganic
from callbackHandlers.selectOldOrder import selectOldOrder
from callbackHandlers.selectPendingOrder import selectPendingOrder
from callbackHandlers.selectProduct import selectProduct
from cfg import application as config
from commandHandlers.createSampleOrder import createSampleOrder
from commandHandlers.setLanguage import setLanguage
from conversationHandlers.cancel import cancel
from callbackHandlers.languageSelection import languageSelection
from callbackHandlers.mainMenu import mainMenu
from callbackHandlers.manageOrders import manageOrders
from callbackHandlers.manageProducts import manageProducts
from loggers.conversations import logger
from commandHandlers.idle import idle
from commandHandlers.start import start
from conversationHandlers.stages import *
from messageHandlers.enterDiscount import enterDiscount
from messageHandlers.enterPrice import enterPrice
from messageHandlers.enterProductName import enterProductName
from messageHandlers.enterProductType import enterProductType
from messageHandlers.enterQuantity import enterQuantity
from messageHandlers.errorHandler import errorHandler
from messageHandlers.idleStage import idleStage
from messageHandlers.saveMessageHandler import saveMessageHandler
from messageHandlers.uploadProductImage import uploadProductImage


def main():
    pp = PicklePersistence(filename='logs/telegram-bot-conversations.dat')
    updater = Updater(token=config['telegram']['token'], use_context=True, persistence=pp)
    dp = updater.dispatcher
    bot = updater.bot

    conversation_handler = ConversationHandler(
        entry_points=[
            CommandHandler('start', start),
            CommandHandler('idle', idle),
        ],

        states={
            # When the bot is in idle stage
            IDLE_STAGE: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                # Here are commands that are available only in IDLE mode
                MessageHandler(Filters.location, idleStage),
                MessageHandler(Filters.text, idleStage),
                MessageHandler(Filters.all, idleStage),
            ],

            # When the bot has prompted for language selection of user
            LANGUAGE_SELECTION: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(languageSelection),
            ],

            # When user is seeing main menu
            MAIN_MENU: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(mainMenu),
            ],

            # Manage products options
            MANAGE_PRODUCTS: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(manageProducts),
            ],

            # Manage orders options
            MANAGE_ORDERS: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(manageOrders),
            ],

            # Stage to process product name
            ENTER_PRODUCT_NAME: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                MessageHandler(Filters.text, enterProductName),
                CallbackQueryHandler(cancelProductManagement),
            ],

            ENTER_PRODUCT_TYPE: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                MessageHandler(Filters.text, enterProductType),
                CallbackQueryHandler(chooseProductType),
                CallbackQueryHandler(cancelProductManagement),
            ],

            ENTER_QUANTITY: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                MessageHandler(Filters.text, enterQuantity),
                CallbackQueryHandler(cancelProductManagement),
            ],

            ENTER_PRICE: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                MessageHandler(Filters.text, enterPrice),
                CallbackQueryHandler(cancelProductManagement),
            ],

            ENTER_DISCOUNT: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                MessageHandler(Filters.text, enterDiscount),
                CallbackQueryHandler(cancelProductManagement),
            ],

            CHOOSE_ORGANIC: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(chooseOrganic),
                CallbackQueryHandler(cancelProductManagement),
            ],

            UPLOAD_PRODUCT_IMAGE: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                MessageHandler(Filters.photo, uploadProductImage),
                MessageHandler(Filters.video, uploadProductImage),
                MessageHandler(Filters.attachment, uploadProductImage),
                MessageHandler(Filters.document, uploadProductImage),
                CallbackQueryHandler(cancelProductManagement),
            ],

            # Not used until now
            SAVE_PRODUCT: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                # CallbackQueryHandler(saveProduct),
            ],

            SELECT_PRODUCT: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(selectProduct),
            ],

            AFTER_PRODUCT_SELECTION: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(manageProductDetails),
            ],

            SELECT_PENDING_ORDER: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(selectPendingOrder),
            ],

            SELECT_OLD_ORDER: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(selectOldOrder),
            ],

            MANAGE_PENDING_ORDER: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
                CommandHandler('order', createSampleOrder),
                CallbackQueryHandler(updatePendingOrder),
            ],

            # Not in use right now
            MANAGE_OLD_ORDER: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
            ],

            # Not in use right now
            ACCEPT_ORDER: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
            ],

            # Not in use right now
            REJECT_ORDER: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
            ],

            # Not in use right now
            INITIATE_ORDER_FULFILMENT: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
            ],

            # Not in use right now
            SET_ORDER_CANCELLED: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
            ],

            # Not in use right now
            SET_ORDER_FULFILLED: [
                CommandHandler('start', start),
                CommandHandler('idle', idle),
                CommandHandler('language', setLanguage),
                CommandHandler('cancel', cancel),
            ],
        },

        fallbacks=[
            CommandHandler('cancel', cancel),
        ],

        name="conversation_main",
        persistent=True,
    )

    dp.add_handler(conversation_handler)

    dp.add_handler(CommandHandler('start', start))
    dp.add_handler(CommandHandler('idle', idle))

    dp.add_handler(MessageHandler(Filters.location, saveMessageHandler))
    dp.add_handler(MessageHandler(Filters.text, saveMessageHandler))
    dp.add_handler(MessageHandler(Filters.all, saveMessageHandler))

    dp.add_handler(MessageHandler(Filters.text, idle))

    # log all errors
    dp.add_error_handler(errorHandler)

    logger.info("Starting Telegram bot..")

    # Start the Bot
    updater.start_polling()

    # Run the bot until you press Ctrl-C or the process receives SIGINT,
    # SIGTERM or SIGABRT. This should be used most of the time, since
    # start_polling() is non-blocking and will stop the bot gracefully.
    updater.idle()


if __name__ == '__main__':
    main()
