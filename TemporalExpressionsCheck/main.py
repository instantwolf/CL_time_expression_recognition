"""
The main module of this application.

functions:
    perform_analysis(): Performs the analysis of the timestamp evaluation.
"""
import openai_api


def perform_analysis():
    """
    Performs the analysis of the timestamp evaluation.
    """
    openai_api.perform_comparison()


if __name__ == "__main__":
    perform_analysis()

