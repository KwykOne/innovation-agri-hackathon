import os

application = {
    # Default language of the bot for new users
    'defaultLanguage': 'en',

    # Mongo DB credentials
    'mongo': {
        # Connection URI including authentication
        'uri': os.getenv('MONGO_URI', ''),
        # Name of the database to use
        'dbname': os.getenv('MONGO_DB_NAME', ''),
    },

    # Pagination of products and orders
    'paging': {
        'selectProduct': 50,
        'viewOrders': 50,
    },

    'telegram': {
        # The username of your bot
        'username': os.getenv('TELEGRAM_USERNAME', ''),

        # API token obtained from @BotFather for using your bot
        'token': os.getenv('TELEGRAM_BOT_TOKEN', ''),

        # Administrator Chat ID is used to send out Run-time errors to admin user
        'admin_ids': [464073059],
    },

    'uploads': {
        # Path to the folder where pre-uploaded static assets are present
        'assets_dir': os.getenv('ASSETS_DIR', '<YOUR-PATH-HERE>/assets'),

        # Path to the folder where new images uploaded by users are to be stored
        # This folder must be writable for the user who runs the process
        'temp_dir': os.getenv('UPLOAD_DIR', '<YOUR-PATH-HERE>/uploads')
    },
}

# Use to view your config
# print(application)
