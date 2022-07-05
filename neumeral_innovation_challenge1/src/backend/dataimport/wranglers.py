from abc import ABC, abstractmethod
from random import random
from typing import TypedDict

import spacy
from product.purpose_codes import PURPOSE_CODES
from product.schema import ProductSchemaDict
from product.usage_codes import USASE_CODES


class ProductInputDict(TypedDict):
    id: str
    category: str
    provider: str
    name: str
    image_url: str
    price: float
    description: str
    data: str


class BaseWrangler(ABC):
    @abstractmethod
    def clean(self, input_dict: ProductInputDict):
        raise NotImplementedError("Method not implemented!")


class SimpleWrangler(BaseWrangler):
    def _clean_category(self, value: str):
        if value == "Crop Protection":
            """
            This can be further refined to fungicide, insecticide etc
            Keeping this as pesticide for now
            """
            return "pesticide"
        if value == "Crop Nutrition":
            return "fertilizer"
        if value == "Seeds":
            return "seed"

    def _generate_subcategory(self, data: str):
        """
        Takes the full data of the product as a string,
        and decides what should be the subcategory.
        """
        return ""

    def _generate_ingredients(self, data: str):
        """
        For now we just return the data as-is.
        Since Elastic search does keyword based search,
        there will be matches for this in corresponding field
        """
        return data

    def _generate_benefits(self, data: str):
        """
        For now we just return the data as-is.
        Since Elastic search does keyword based search,
        there will be matches for this in corresponding field
        """
        return data

    def _generate_side_effects(self, data: str):
        """
        For now we just return the data as-is.
        Since Elastic search does keyword based search,
        there will be matches for this in corresponding field
        """
        return data

    def _generate_sustainability_score(self, data: str):
        """
        Return a random value for now
        """
        return round(random() * 10)

    def _generate_purpose(self, data: str):
        """
        For now we just return the data as-is.
        Since Elastic search does keyword based search,
        there will be matches for this in corresponding field
        """
        return data

    def _generate_usage(self, data: str):
        """
        For now we just return the data as-is.
        Since Elastic search does keyword based search,
        there will be matches for this in corresponding field
        """
        return data

    def _generate_purpose_code_list(self, data: str, category: str):
        """
        For now, we just return a pre-defined list based on category
        """
        if category == "Crop Nutrition":
            return ["P0001", "P0002", "P0003"]
        if category == "Crop Protection":
            return ["P0011", "P0012", "P0013"]

        return []

    def _generate_usage_code_list(self, data: str, category: str):
        """
        For now, we just return a pre-defined list based on category
        """
        if category == "Crop Nutrition":
            return ["U0004", "U0005", "U0006"]
        if category == "Crop Protection":
            return ["U0002", "U0003"]

        return []

    def _generate_used_on(self, data: str):
        """
        For now we just return the data as-is.
        Since Elastic search does keyword based search,
        there will be matches for this in corresponding field
        """
        return data

    def _generate_used_during(self, data: str):
        """
        For now we just return the data as-is.
        Since Elastic search does keyword based search,
        there will be matches for this in corresponding field
        """
        return data

    def clean(self, input_dict: ProductInputDict) -> ProductSchemaDict:

        full_data = input_dict.get("data", "")
        category = input_dict.get("category", "")
        provider = input_dict.get("provider", "")
        name = input_dict.get("name", "")
        image_url = input_dict.get("image_url", "")
        price = input_dict.get("price", 0)
        description = input_dict.get("description", "")

        return {
            "name": name,
            "category": self._clean_category(category),
            "subcategory": self._generate_subcategory(full_data),
            "provider": provider,
            "description": description,
            "image_url": image_url,
            "price": price,
            "ingredients": self._generate_ingredients(full_data),
            "benefits": self._generate_benefits(full_data),
            "side_effects": self._generate_side_effects(full_data),
            "sustainability_score": self._generate_sustainability_score(full_data),
            "purposes": self._generate_purpose(full_data),
            "usages": self._generate_usage(full_data),
            "usage_codes": self._generate_usage_code_list(full_data, category),
            "purpose_codes": self._generate_purpose_code_list(full_data, category),
            "used_on": self._generate_used_on(full_data),
            "used_during": self._generate_used_during(full_data),
            "data": full_data,
        }


class NLPBasedWrangler(SimpleWrangler):

    """
    Uses spacy text similarity score to find the purpose and usage codes
    """

    def __init__(self):
        self.nlp = spacy.load("en_core_web_md")

    def _generate_usage_code_list(self, data: str):
        if self.nlp:
            base = self.nlp(data)

            def usage_extract(code, usage_val):
                compare = self.nlp(usage_val)
                score = base.similarity(compare)
                if score > 0.5:
                    return code

            codes = []
            for usage in USASE_CODES.items():
                code, value = usage
                ext_code = usage_extract(code, value)
                if ext_code:
                    codes.append(ext_code)

            return codes
        else:
            return super()._generate_usage_code_list()

    def _generate_purpose_code_list(self, data: str):
        if self.nlp:
            base = self.nlp(data)

            def purpose_extract(code, purpose_val):
                compare = self.nlp(purpose_val)
                score = base.similarity(compare)
                if score > 0.5:
                    return code

            codes = []
            for purpose in PURPOSE_CODES.items():
                code, value = purpose
                ext_code = purpose_extract(code, value)
                if ext_code:
                    codes.append(ext_code)

            return codes
        else:
            return super()._generate_purpose_code_list()
