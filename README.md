# Hesa Submission for ONDC-NABARD Hackathon
#### July 2022

### Overview

This is our submission for Challenge 4 - Easy Farmer Experience.

The solution is built as a Telegram bot, that allows any user to manage their product catalog and buyer orders.

### Demo

Use link https://t.me/HesaMandiBot or find `@HesaMandiBot` in Telegram app
to access demo. Check https://youtu.be/pOX-NQE-7yA for video demo.

### Structure of Repository
- `src` folder contains python code
- `setup_data` folder contains mongodump of collections and seed data required
- `docs` folder contains some additional useful documents

### Prerequisites

- Python 3.8 or above installed
- A running MongoDB instance
- Telegram user account

### Running this Bot

1. Import initial data dump (provided in `setup_data` folder) into your Mongo database using `mongorestore`.
```commandline
cd setup_data
tar -xvzf seed-data.tar.gz
mongorestore --host 127.0.0.1 --port 27017 --db dbname --username username --password password --authenticationDatabase admin hesa_ondc_db1
```
2. Create a Bot on Telegram by talking to `@BotFather`. Save the Username and API Token for the newly created bot.
3. Open `src/cfg.py` file and modify the following values as per your environment:
- `mongo.uri`
- `mongo.dbname` 
- `telegram.username`
- `telegram.token`
- `uploads.assets_dir`
- `uploads.temp_dir` (Temp dir must be writable)
4. Install dependencies

```commandline
cd src
pip install -r requirements.txt
```

5. Run `main.py` to start the bot
```commandline
cd src
python main.py
```

You can use [python-telegram-bot](https://docs.python-telegram-bot.org/en/v20.0a2/) and [Telegram Bot API](https://core.telegram.org/bots/api) documentation for further development on this bot.

### Testing

Once you run the bot, it will start responding to incoming chats. Start a conversation with your bot to test it.
You can use `/language` command to change your preferred language, and `/order` command to simulate a new order.

### Deployment

Follow the same instructions above on a VM, and use a process manager like `systemd` or `pm2` to keep the bot running in background.
If you are using `pm2`, check out the sample configuration file `src/ecosystem.config.js`.
In case any further information is required, contact [ananth@hesa.co](ananth@hesa.co).

https://hesa.co
