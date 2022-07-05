from telegram import Update, ParseMode
from cfg import application as config
from loggers.conversations import logger
import traceback
import html
import json


def errorHandler(update, context):
    """Log Errors caused by Updates."""
    logger.warning('Error: "%s"', context.error)
    logger.error('Update "%s" caused error "%s"', update, context.error)

    # Only send error description if an admin is configured
    if len(config['telegram']['admin_ids']) == 0:
        return

    # traceback.format_exception returns the usual python message about an exception, but as a
    # list of strings rather than a single string, so we have to join them together.
    tb_list = traceback.format_exception(None, context.error, context.error.__traceback__)
    tb_string = "".join(tb_list)

    # Build the message with some markup and additional information about what happened.
    # You might need to add some logic to deal with messages longer than the 4096 character limit.
    # update_str = update.to_dict() if isinstance(update, Update) else str(update)
    message = (
        f"An exception was raised while handling an update\n"
        # f"<pre>update = {html.escape(json.dumps(update_str, indent=2, ensure_ascii=False))}"
        # "</pre>\n\n"
        f"<pre>context.chat_data = {html.escape(str(context.chat_data))}</pre>\n\n"
        f"<pre>context.user_data = {html.escape(str(context.user_data))}</pre>\n\n"
        f"<pre>{html.escape(tb_string)}</pre>"
    )

    # Finally, send the message
    adminId = config['telegram']['admin_ids'][0]
    context.bot.send_message(
        chat_id=adminId, text=message, parse_mode=ParseMode.HTML,
    )
