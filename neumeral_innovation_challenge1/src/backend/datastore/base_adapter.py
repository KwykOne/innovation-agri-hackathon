from abc import ABC, abstractmethod


class BaseAdapter(ABC):
    """
    BaseAdapter abstract class which should be implemented by the datastore
    backends that implements the actual search
    """

    @abstractmethod
    def query_search_terms(self, value=None):
        raise NotImplementedError("Method not implemented!")

    @abstractmethod
    def query_product_type(self, value=None):
        raise NotImplementedError("Method not implemented!")

    @abstractmethod
    def query_product_subtype(self, value=None):
        raise NotImplementedError("Method not implemented!")

    @abstractmethod
    def query_product_ingredients(self, value=None):
        raise NotImplementedError("Method not implemented!")

    @abstractmethod
    def query_product_used_on(self, value=None):
        raise NotImplementedError("Method not implemented!")

    @abstractmethod
    def query_product_purpose(self, value=None):
        raise NotImplementedError("Method not implemented!")

    @abstractmethod
    def query_product_used_during(self, value=None):
        raise NotImplementedError("Method not implemented!")

    @abstractmethod
    def search(self):
        raise NotImplementedError("Method not implemented!")
