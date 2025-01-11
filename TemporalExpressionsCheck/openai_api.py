"""
This module is responsible for sending the data and receiving the datetime data to and from the OpenAI API.

functions:
    extract_datetime_from_text(input_data): Handles the requests / responses from the OpenAI API.
"""
from openai import OpenAI
import api_key

# set your API Key to enable requests
client = OpenAI(api_key=api_key.API_KEY)
# model to use
gpt_model = "gpt-4o-mini"

# prompt for OpenAI API
task = ("You are a temporal tagger. Given the following text, find temporal expressions. As answer provide only a list "
        "of timex3 xml tags including the properties text, tid, type like DATE, DURATION and so on and value, "
        "which is the mapped timex value like P1D and so on. Be as precise as possible:")


def extract_datetime_from_text(input_data):
    """
    Sends a request with a prior prompt to the ChatGPT API which extracts datetime data from the input data.
    The API will then return the data in timex3 xml format.

    args:
        input_data (string): Data to be processed.
    returns:
        string: The found datetime data in timex3 xml format.
    """
    # request to ChatGPT API
    response = client.chat.completions.create(model=gpt_model,
                                              messages=[
                                                  {"role": "system",
                                                   "content": f"{task}"},
                                                  {"role": "user",
                                                   "content": f"{input_data}"}
                                              ],
                                              temperature=0
                                              )

    # extract the response of ChatGPT
    return response.choices[0].message.content
