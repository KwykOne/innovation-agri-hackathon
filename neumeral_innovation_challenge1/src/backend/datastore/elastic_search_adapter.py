import os

from elasticsearch import Elasticsearch

from .base_adapter import BaseAdapter

ELASTIC_HOST = os.getenv("ELASTIC_HOST", "http://localhost:9200")
ELASTIC_API_KEY_ID = os.getenv("ELASTIC_API_KEY_ID", "")
ELASTIC_API_KEY_VALUE = os.getenv("ELASTIC_API_KEY_VALUE", "")
ELASTIC_SEARCH_INDEX = os.getenv("ELASTIC_SEARCH_INDEX", "farm-products")

client = Elasticsearch([ELASTIC_HOST], api_key=(ELASTIC_API_KEY_ID, ELASTIC_API_KEY_VALUE))


class ElasticSearchAdapter(BaseAdapter):
    def __init__(self):
        self.must_matches = []
        self.filters = []

    def query_search_terms(self, value=None):
        if value:
            self.must_matches.append({"match": {"data": value}})

    def query_product_type(self, value=None):
        if value:
            self.filters.append({"match": {"category": value}})

    def query_product_subtype(self, value=None):
        if value:
            self.filters.append({"match": {"subcategory": value}})

    def query_product_ingredients(self, value=None):
        if value:
            self.filters.append({"match": {"ingredients": value}})

    def query_product_used_on(self, value=None):
        if value:
            self.filters.append({"match": {"used_on": value}})

    def query_product_purpose(self, value=None):
        if value:
            self.filters.append({"match": {"purposes": value}})

    def query_product_used_during(self, value=None):
        if value:
            self.filters.append({"match": {"used_during": value}})

    def _must_match_query(self):
        if self.must_matches:
            return {"must": self.must_matches}
        else:
            return {}

    def _filter_query(self):
        if self.filters:
            return {"filter": self.filters}
        else:
            return {}

    def full_query(self):
        filters = self._filter_query()
        must_match = self._must_match_query()

        query = {**must_match, **filters}
        return {"query": {"bool": query}}

    def search(self):
        query = self.full_query()
        print("Query", query)
        result = client.search(index=ELASTIC_SEARCH_INDEX, body=query)

        hits = result.get("hits")

        result_items = hits.get("hits")
        result_count = hits.get("total", {}).get("value", 0)
        return (result_count, result_items)
