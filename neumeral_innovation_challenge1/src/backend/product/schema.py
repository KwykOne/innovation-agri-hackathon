import json
from typing import TypedDict


class ProductSchemaDict(TypedDict):
    name: str
    category: str
    subcategory: str
    description: str
    image_url: str
    ingredients: str
    benefits: str
    side_effects: str
    sustainability_score: float
    price: float
    used_on: str
    used_during: str
    purposes: str
    usages: str
    usage_codes: list[str]
    purpose_coes: list[str]


class ProductSchema:
    """
    The Product Schema contains the following:

    Product details
        name
        description
        image
        ingredients
        benefits
        side_effects,
        product_type,
        product_subtype
        sustainability_score,
        pricing
        used_on
        used_during
        purposes []
        usages []

    Purpose details
        standard list of purpose codes & matching of
        one or more purpose code with a product;
    Usage details
        standard list of usage codes & matching of
        one or more usage codes with a product.
        Usage details could include how the product could be used
        (e.g. sprayed through drones, manually sprayed from tractor, etc),
        minimum quantity for use (either fixed or in some proportion, etc);
    """

    def get():
        with open("product_schema.json", "r") as file:
            schema = json.load(file)
        return schema
