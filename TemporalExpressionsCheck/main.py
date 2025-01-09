"""
The main module of this application.

functions:
    perform_analysis(): handles getting the input data and processes it to the openai_api module.
    print_result(): handles printing the result provided by the OpenAI API.
"""
import openai_api


def perform_analysis():
    """
    Collects the datasets from external sources and giving it to the OpenAI API for further processing.
    """
    # datasets which will be analyzed
    data = ""  # TODO: implementation of providing the data from external sources

    # conduct the processing via OpenAI API
    result = openai_api.extract_datetime_from_text(data)

    print_result(result)


def print_result(result):
    """
    Prints the result(s) of the analysis from OpenAI API.
    """
    print(result)


if __name__ == "__main__":
    perform_analysis()
