from lark import Lark

from .grammer import QUERY_GRAMMAR


def query_parser():
    return Lark(QUERY_GRAMMAR)
