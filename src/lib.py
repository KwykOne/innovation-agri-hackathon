import hashlib
import string
from telegram import ChatAction
from cfg import application as config
import random
import time


def randomSleep(timeout=1.0):
    timeout = random.uniform(timeout, timeout + 0.4)
    time.sleep(timeout)


def getRandomString(str_size):
    allowed_chars = string.ascii_letters
    return ''.join(random.choice(allowed_chars) for x in range(str_size))


def getRandomSpecialCharsString(str_size):
    allowed_chars = string.ascii_letters + string.punctuation
    return ''.join(random.choice(allowed_chars) for x in range(str_size))


def getRandomNumberStr(str_size):
    allowed_chars = string.digits
    return ''.join(random.choice(allowed_chars) for x in range(str_size))


def getRandomFromList(ls):
    return random.choice(ls)


def sendTypingAction(update, context, timeout=1.0):
    context.bot.send_chat_action(chat_id=update.effective_message.chat_id, action=ChatAction.TYPING)
    randomSleep(timeout / 1.3)


def getMessageFromUpdate(update):
    if hasattr(update, 'callback_query') and update.callback_query is not None:
        query = update.callback_query
        message = query.message
    elif hasattr(update, 'message') and update.message is not None:
        message = update.message
    else:
        # TODO Handle error
        message = None

    return message


def sha1(s):
    return hashlib.sha1(s.encode('utf-8')).hexdigest()


def sha1File(fname):
    # hash_md5 = hashlib.md5()
    hash_sha1 = hashlib.sha1()
    with open(fname, "rb") as f:
        for chunk in iter(lambda: f.read(2 ** 20), b""):
            # hash_md5.update(chunk)
            hash_sha1.update(chunk)
    return hash_sha1.hexdigest()


def getOrderStatusStr(contentObj, status):
    key = status + '_str'
    if key in contentObj:
        return contentObj[key]

    if status == 'pending':
        return 'PENDING'
    elif status == 'accepted':
        return 'PENDING DELIVERY'
    elif status == 'cancelled':
        return 'CANCELLED'
    elif status == 'completed':
        return 'COMPLETED'

    return 'UNKNOWN'


def getOrderText(contentObj, order):
    orderText = contentObj['order_str'] + ' <b>' + order['orderNo'] + '</b>\n\n'
    orderText += contentObj['customer_details_str'] + ': <b>' + \
                 order['customerDetails']['name'] + \
                 '</b>, ' + order['customerDetails']['address'] + ', ' + \
                 str(int(order['customerDetails']['pincode'])) + '\n'
    orderText += contentObj['phone_str'] + ': <b>' + \
                 str(order['customerDetails']['phone']) + '</b>\n\n'

    orderText += contentObj['items_str'] + ':\n'
    itemIndex = 0
    totalAmount = 0
    for item in order['items']:
        itemIndex += 1
        itemTotal = item['price'] * item['qty']
        totalAmount += itemTotal
        orderText += str(itemIndex) + '. <b>' + item['name'] + '</b> (' + item['type'] + ')\n'
        orderText += '    ' + str(item['qty']) + ' kg. @ ₹' + str(item['price']) + '/- per kg.\n'
        orderText += '    ' + contentObj['total_str'] + ': ₹' + str(int(itemTotal)) + '/-\n\n'

    orderText += contentObj['amount_str'] + ': <b>₹' + str(int(totalAmount)) + '/-</b>\n\n'
    orderText += contentObj['status_str'] + ': <b>' + getOrderStatusStr(contentObj, order['currentStatus']) + '</b>'
    return orderText
