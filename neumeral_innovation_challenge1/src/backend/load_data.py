import os
from pathlib import Path

from dataimport.loader import ESDataLoader
from dataimport.wranglers import SimpleWrangler
from elasticsearch import Elasticsearch

ELASTIC_HOST = os.getenv("ELASTIC_HOST", "http://localhost:9200")
ELASTIC_API_KEY_ID = os.getenv("ELASTIC_API_KEY_ID", "")
ELASTIC_API_KEY_VALUE = os.getenv("ELASTIC_API_KEY_VALUE", "")
ELASTIC_SEARCH_INDEX = os.getenv("ELASTIC_SEARCH_INDEX", "farm-products")


delete_index_flag = os.getenv("DELETE", False)
stats_flag = os.getenv("STATS", False)

es_client = Elasticsearch([ELASTIC_HOST], api_key=(ELASTIC_API_KEY_ID, ELASTIC_API_KEY_VALUE))

PROJECT_DIR = Path(__file__).parent.parent.parent.resolve()

csv_file = PROJECT_DIR / "data" / "farm_input_hackathon.csv"
wrangler = SimpleWrangler()
loader = ESDataLoader(csv_file, es_client, wrangler=wrangler)

if stats_flag:
    index_name, doc_count = loader.index_stats()
    print(f"index: {index_name}; docs: {doc_count} ")
    exit()

if delete_index_flag:
    loader.delete_index()
    print(f"Deleted existing index - {ELASTIC_SEARCH_INDEX}")

docs_indexed, err_count = loader.load_data()

print(f"Total {docs_indexed} docs indexed, {err_count} errors.")

index_name, doc_count = loader.index_stats()
print(f"index: {index_name}; docs: {doc_count} ")
