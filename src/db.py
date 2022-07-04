from bson import ObjectId
from datetime import datetime

import pymongo
import re

from cfg import application as config
from data import randomUsers
from exceptions.DatabaseException import DatabaseException
from exceptions.QueryException import QueryException
from lib import getRandomNumberStr, getRandomFromList
from loggers.database import logger


class Database(object):
    def __init__(self):
        self.client = None
        self.db = None
        self.is_connected = False
        self.content_collection = None
        self.languages_collection = None
        self.messages_collection = None
        self.orders_collection = None
        self.products_collection = None
        self.ref_products_collection = None
        self.temp_products_collection = None
        self.user_profiles_collection = None
        self.connect()

    def connect(self):
        try:
            self.client = pymongo.MongoClient(config['mongo']['uri'], maxPoolSize=300)
            self.db = self.client[config['mongo']['dbname']]
            self.is_connected = True
            self.content_collection = self.db['messages_content']
            self.languages_collection = self.db['languages']
            self.messages_collection = self.db['messages_raw']
            self.orders_collection = self.db['orders']
            self.products_collection = self.db['products']
            self.ref_products_collection = self.db['products_reference']
            self.temp_products_collection = self.db['products_temp']
            self.user_profiles_collection = self.db['userprofiles']
        except (pymongo.errors.ServerSelectionTimeoutError, pymongo.errors.AutoReconnect) as ex:
            logger.exception('Failed to establish database connection')
            self.is_connected = False
            self.content_collection = None
            self.languages_collection = None
            self.messages_collection = None
            self.orders_collection = None
            self.products_collection = None
            self.ref_products_collection = None
            self.temp_products_collection = None
            self.user_profiles_collection = None
        except:
            logger.exception('Failed to establish connection with Mongo database')

    def disconnect(self):
        if self.client is None or self.db is None:
            return

        self.client.close()

    def ensureConnection(self):
        if not self.is_connected:
            self.connect()

        if not self.is_connected:
            raise DatabaseException()

    def getUserProfile(self, user, message, insert_if_new=True):
        self.ensureConnection()

        try:
            now = datetime.now()
            user_chat_obj = self.user_profiles_collection.find_one({
                'telegramUserId': user.id,
            })

            profile_data = {}

            if message is not None and message.chat is not None:
                profile_data['chat'] = message.chat.to_dict()

            if user_chat_obj is None:
                if not insert_if_new:
                    return None

                is_new_user = True
                profile_data['telegramUserId'] = user.id
                profile_data['user'] = user.to_dict()
                profile_data['botStage'] = 'mainMenu'
                profile_data['status'] = ''
                profile_data['isActive'] = True
                profile_data['createdAt'] = now
                profile_data['updatedAt'] = now
                self.user_profiles_collection.insert_one(profile_data)
            else:
                is_new_user = False

                if 'user' not in user_chat_obj or user_chat_obj['user'] is None or 'id' not in user_chat_obj['user']:
                    profile_data['user'] = user.to_dict()

                profile_data['isActive'] = True
                profile_data['updatedAt'] = now

                self.user_profiles_collection.update_one({
                    'telegramUserId': user.id,
                }, {
                    '$set': profile_data,
                })

            user_chat_obj = self.user_profiles_collection.find_one({
                'telegramUserId': user.id,
            })

            return user_chat_obj, is_new_user
        except:
            logger.exception('Failed to execute query')
            raise QueryException()

    def updateUserProfile(self, chat_obj, update_data):
        update_data['updatedAt'] = datetime.now()

        self.user_profiles_collection.update_one({
            '_id': chat_obj['_id'],
        }, {
            '$set': update_data,
        })

    def save_message(self, message, chat, callbackQuery, user):
        self.ensureConnection()
        message_obj = self.messages_collection.find_one({
            'message.message_id': message['message_id'],
        })

        now = datetime.now()

        if message_obj is None:
            message_obj = {
                'chat': chat,
                'message': message,
                'callbackQuery': callbackQuery,
                'user': user,
                'isActive': True,
                'createdAt': now,
                'updatedAt': now,
            }

            self.messages_collection.insert_one(message_obj)
        else:
            message_data = {
                'chat': chat,
                'message': message,
                'callbackQuery': callbackQuery,
                'user': user,
                'updatedAt': now,
            }

            self.messages_collection.update_one({
                'message.message_id': message['message_id'],
            }, {
                '$set': message_data,
            })

    def getLanguages(self):
        self.ensureConnection()

        return list(
            self.languages_collection.find({'isActive': True})
            .sort([('displayOrder', pymongo.ASCENDING)])
        )

    def getMessageContent(self, languageCode, messageType, default='Content Missing'):
        self.ensureConnection()
        message_obj = self.content_collection.find_one({
            'type': messageType,
            'isActive': True
        })

        if message_obj is None:
            return default
            # return None

        if languageCode not in message_obj['content']:
            if config['defaultLanguage'] in message_obj['content']:
                return message_obj['content']['defaultLanguage']

            return default
            # return None

        return message_obj['content'][languageCode]

    def getMessageContents(self, languageCode, messageTypes):
        if len(messageTypes) == 0:
            return {}

        self.ensureConnection()
        messages = list(self.content_collection.find({
            'type': {
                '$in': messageTypes,
            },
            'isActive': True
        }))

        contentObj = {}
        for message in messages:
            messageType = message['type']
            if 'content' in message and languageCode in message['content']:
                contentObj[messageType] = message['content'][languageCode]
            elif 'content' in message and config['defaultLanguage'] in message['content']:
                contentObj[messageType] = message['content'][config['defaultLanguage']]
            else:
                contentObj[messageType] = messageType

        for messageType in messageTypes:
            if messageType not in contentObj:
                contentObj[messageType] = messageType

        return contentObj

    def insertProduct(self, userId):
        self.ensureConnection()
        # TODO
        pass

    def getProducts(self, userId, page=1, limit=10):
        self.ensureConnection()
        skip = (page - 1) * limit
        products = list(
            self.products_collection.find({
                'userId': userId,
                'isActive': True
            }).skip(skip).limit(limit).sort([('updatedAt', pymongo.DESCENDING)])
        )
        return products

    def getProductsCount(self, userId):
        self.ensureConnection()
        return self.products_collection.count_documents({
            'userId': userId,
            'isActive': True,
        })

    def getOrders(self, userId, currentStatus, page, limit):
        self.ensureConnection()
        start = (page - 1) * limit
        end = start + limit

        conds = {
            'userId': userId,
            'isActive': True
        }

        if currentStatus != '':
            conds['currentStatus'] = currentStatus

        orders = list(
            self.orders_collection.find(conds)
            .skip(start).limit(limit).sort([('createdAt', pymongo.DESCENDING)])
        )

        return orders

    def getOrdersWithoutStatus(self, userId, excludeStatus, page, limit):
        self.ensureConnection()
        start = (page - 1) * limit
        end = start + limit

        conds = {
            'userId': userId,
            'isActive': True
        }

        if excludeStatus is not None and len(excludeStatus) > 0:
            conds['currentStatus'] = {
                '$nin': excludeStatus,
            }

        orders = list(
            self.orders_collection.find(conds)
            .skip(start).limit(limit).sort([('createdAt', pymongo.DESCENDING)])
        )

        return orders

    def getOrdersWithoutStatusCount(self, userId, excludeStatus):
        self.ensureConnection()

        conds = {
            'userId': userId,
            'isActive': True
        }

        if excludeStatus is not None and len(excludeStatus) > 0:
            conds['currentStatus'] = {
                '$nin': excludeStatus,
            }

        return self.orders_collection.count_documents(conds)

    def getOrdersCount(self, userId, currentStatus):
        self.ensureConnection()

        conds = {
            'userId': userId,
            'isActive': True
        }

        if currentStatus != '':
            conds['currentStatus'] = currentStatus

        return self.orders_collection.count_documents(conds)

    def getOrder(self, orderId):
        self.ensureConnection()
        return self.orders_collection.find_one({
            '_id': orderId,
        })

    def updateProduct(self, productId, updateData):
        self.ensureConnection()
        updateData['updatedAt'] = datetime.now()
        self.products_collection.update_one({
            '_id': productId,
        }, {
            '$set': updateData,
        })

    def updateOrder(self, orderId, updateData):
        self.ensureConnection()
        updateData['updatedAt'] = datetime.now()
        self.orders_collection.update_one({
            '_id': orderId,
        }, {
            '$set': updateData,
        })

    def clearTempProduct(self, userId):
        self.temp_products_collection.delete_one({
            'userId': userId,
        })

    def saveTempProduct(self, userId, updateData):
        self.ensureConnection()
        now = datetime.now()

        tempProduct = self.temp_products_collection.find_one({
            'userId': userId,
        })

        if tempProduct is None:
            updateData['userId'] = userId
            updateData['isActive'] = True
            updateData['createdAt'] = now
            updateData['updatedAt'] = now
            self.temp_products_collection.insert_one(updateData)
        else:
            if '_id' in updateData:
                del updateData['_id']

            if 'isActive' in updateData:
                del updateData['isActive']

            if 'createdAt' in updateData:
                del updateData['createdAt']

            updateData['updatedAt'] = now
            self.temp_products_collection.update_one({
                'userId': userId,
            }, {
                '$set': updateData,
            })

    def saveProductFromTemp(self, userProfile, selectedProductId):
        if selectedProductId is None:
            # New product
            # Move temp product to Products collection
            tempProduct = self.getTempProduct(userProfile['_id'])
            productId = db.saveProduct(userProfile['_id'], tempProduct)
            return productId
        else:
            tempProduct = self.getTempProduct(userProfile['_id'])
            del tempProduct['_id']
            del tempProduct['createdAt']
            db.updateProduct(selectedProductId, tempProduct)
            return selectedProductId

    def getTempProduct(self, userId):
        self.ensureConnection()
        tempProduct = self.temp_products_collection.find_one({
            'userId': userId,
        })
        return tempProduct

    def getProduct(self, productId):
        self.ensureConnection()
        return self.products_collection.find_one({
            '_id': productId,
        })

    def saveProduct(self, userId, updateData):
        now = datetime.now()
        del updateData['_id']
        updateData['userId'] = userId
        updateData['isActive'] = True
        updateData['createdAt'] = now
        updateData['updatedAt'] = now
        v = self.products_collection.insert_one(updateData)
        return v.inserted_id

    def findReferenceProduct(self, query):
        self.ensureConnection()
        refProduct = self.ref_products_collection.find_one({
            'aliasList': query.lower(),
            'isActive': True
        })
        return refProduct

    def insertSampleOrder(self, userId):
        randomOrderNo = '#' + getRandomNumberStr(6)
        now = datetime.now()
        randomUser = getRandomFromList(randomUsers)

        orderData = {
            "userId": userId,
            "orderNo": randomOrderNo,
            "currentStatus": "pending",
            "items": [
                {
                    "name": "Potato",
                    "type": "Small",
                    "qty": 25.0,
                    "price": 40.0,
                    "organic": True,
                    "imagePath": ""
                },
                {
                    "name": "Onion",
                    "type": "Regular",
                    "qty": 60.0,
                    "price": 55.0,
                    "organic": False,
                    "imagePath": ""
                }
            ],
            "customerDetails": randomUser,
            "notes": "",
            "isActive": True,
            "createdAt": now,
            "updatedAt": now,
        }
        v = self.orders_collection.insert_one(orderData)
        return v.inserted_id


db = Database()
