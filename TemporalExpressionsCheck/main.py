"""
The main module of this application.

functions:
    perform_analysis(): handles getting the input data and processes it to the openai_api module.
    print_result(): handles printing the result provided by the OpenAI API.
"""
import openai_api
from output_dataset_test_profht import load_dataset_pht


def perform_analysis():
    """
    Collects the datasets from external sources and giving it to the OpenAI API for further processing.
    """
    # datasets from ProfessorHeidelTime which will be analyzed
    data = load_dataset_pht()

    for i in range(5):  # TODO: add functionality to handle the complete dataset processing
        # conduct the processing via OpenAI API
        result = openai_api.extract_datetime_from_text(data[i]['text'])

        print_result(result)

    # TODO: further processing of the results


def print_result(result):
    """
    Prints the result(s) of the analysis from OpenAI API.
    """
    print(result)


if __name__ == "__main__":
    perform_analysis()
