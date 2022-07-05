import csv
import os
import re
import uuid
from pathlib import Path

from dataimport.wranglers import SimpleWrangler
from elasticsearch import Elasticsearch, helpers

ELASTIC_HOST = os.getenv("ELASTIC_HOST", "http://localhost:9200")
ELASTIC_API_KEY_ID = os.getenv("ELASTIC_API_KEY_ID", "")
ELASTIC_API_KEY_VALUE = os.getenv("ELASTIC_API_KEY_VALUE", "")
ELASTIC_SEARCH_INDEX = os.getenv("ELASTIC_SEARCH_INDEX", "farm-products")

LOAD_CATEGORIES = ["Seeds", "Crop Nutrition", "Crop Protection"]


class ESDataLoader:
    """
    Loads CSV data into ElasticSearch

    Expects file path of a CSV with the following headers:
    # Category,Provider,Product ID,Product Name,Image URL,Product Price,Quantity,Product Description
    """

    def __init__(self, file_path, es_client, wrangler=None):
        self.file_path = file_path
        self.client = es_client
        self.wrangler = wrangler

    def delete_index(self):
        """
        Deletes the index in ES
        """
        return self.client.indices.delete(index=ELASTIC_SEARCH_INDEX, ignore=[400, 404])

    def generate_docs(self):
        with open(self.file_path, "r") as fi:
            reader = csv.DictReader(fi, delimiter=",")

            for row in reader:
                product_uuid = uuid.uuid4()

                # Make the data semantic, if it has to be processed by a text nlp transformer
                if row["Category"] not in LOAD_CATEGORIES:
                    continue

                name_semantic = f"The name of the product is {row['Product Name']}."
                provider_semantic = f"And is sold by {row['Provider']}."
                category_semantic = f"This product belongs to the category - {row['Category']}."
                description_semantic = f"{row['Product Description']}"

                product_data = " ".join([name_semantic, provider_semantic, category_semantic, description_semantic])
                product_price = re.sub("[^0-9.]", "", row["Product Price"])
                # "_index": ELASTIC_SEARCH_INDEX,
                # "_type": "FARM_INPUT",
                # "_id": product_uuid,
                source_doc = {
                    "id": row["Product ID"],
                    "category": row["Category"],
                    "provider": row["Provider"],
                    "name": row["Product Name"],
                    "image_url": row["Image URL"],
                    "price": float(product_price),
                    "description": row["Product Description"],
                    "data": product_data,
                }

                if self.wrangler:
                    source_doc = self.wrangler.clean(source_doc)

                doc = {"_index": ELASTIC_SEARCH_INDEX, "_type": "FARM_INPUT", "_id": product_uuid, **source_doc}

                yield doc

    def load_data(self):
        """
        Uses ElasticSearch bulk document indexer, to load data into ES
        """
        indexed_count, _error_list = helpers.bulk(
            self.client, self.generate_docs(), chunk_size=1000, request_timeout=200
        )
        return (indexed_count, len(_error_list))

    def index_stats(self):
        index_count_result = self.client.count(index=ELASTIC_SEARCH_INDEX)
        index_count = index_count_result.get("count", -1)
        return (ELASTIC_SEARCH_INDEX, index_count)
