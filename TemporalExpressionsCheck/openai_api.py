"""
This module is responsible for sending the data and receiving the datetime data to and from the OpenAI API,
as well as comparing the results from ChatGPT/OpenAI API to the results of the source material.

functions:
    perform_comparison(): Compares the actual with the predicted timex tag.
    extract_datetime_from_text(input_data): Handles the requests / responses from the OpenAI API.
    load_data(): Loads the Professor HeidelTime datasets
    get_timex_tags_from_response(response): Takes the timex3 strings and converts them into Timex objects.
    convert_response_to_timex(timex_tag):  Takes the timex tag and parses it to a timex object.
    get_timex_tags_from_source(data, i): Extracts the actual Timex tags provided by Professor HeidelTime.
    convert_source_to_timex(timex_data): Takes the timex tag and parses it to a timex object.
"""
from openai import OpenAI
import api_key
from TemporalExpressionsCheck.ComparisonInstance import ComparisonInstance
from TemporalExpressionsCheck.timex import Timex
from output_dataset_test_profht import load_dataset_pht
import xml.etree.ElementTree as ET

# set your API Key to enable requests
client = OpenAI(api_key=api_key.API_KEY)
# model to use
gpt_model = "gpt-4o-mini"

# prompt for OpenAI API
task = ("You are a temporal tagger. Given the following text, find temporal expressions. As answer provide only a list "
        "of timex3 xml tags (<TIMEX3>...</TIMEX3) including the properties text, tid, type like DATE, DURATION and so "
        "on and value, which is the mapped timex value like P1D and so on. Be as precise as possible:")


def perform_comparison():
    """
    Performs the comparison of the evaluated timex3 tags from ChatGPT/OpenAI API with the timex3 tags from
    the source.
    """
    data = load_data()
    batch_size = 5  # len(data) - 1

    # picking out a few samples of the dataset
    batch = data[0:batch_size]

    for i in range(0, batch_size):
        response = extract_datetime_from_text(data[i])

        # extracting the timex3 tags for comparison
        timex_response = get_timex_tags_from_response(response)
        timex_solution = get_timex_tags_from_source(batch, i)

        # comparing source and predicted timex3 tags with one another
        result = ComparisonInstance(timex_solution, timex_response)

        print(result)


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


def load_data():
    """
    Loads the datasets of Professor HeidelTime using the output_dataset_test_profht module.

    returns:
        list: Loaded datasets.
    """
    data = load_dataset_pht()
    return data


"""
=========================================
            Response Parser
=========================================
"""


def get_timex_tags_from_response(response):
    """
    Takes the timex3 strings and converts them into Timex objects.

    args:
        response (string): Timex3 tags extracted by the API.
    returns:
        list: The converted timex objects.
    """
    # parse the XML response
    root = ET.fromstring(f"<root>{response}</root>")
    # find all TIMEX3 tags
    timex_collection = root.findall(".//TIMEX3")

    timex_objects = []

    for timex in timex_collection:
        # convert the timex3 tags into timex objects
        timex_object = convert_response_to_timex(timex)
        timex_objects.append(timex_object)

    return timex_objects


def convert_response_to_timex(timex_tag):
    """
    Takes the timex tag and parses it to a timex object.

    args:
        timex_tag (ET): The timex tag in ElementTree format.
    returns:
        Timex: The parsed timex tag.
    """
    tid = timex_tag.attrib.get("tid")
    timex_type = timex_tag.attrib.get("type")
    value = timex_tag.attrib.get("value")
    text = timex_tag.attrib.get("text")
    timex_object = Timex(tid=tid, timex_type=timex_type, value=value, text=text)

    return timex_object


"""
=========================================
            Source Parser
=========================================
"""


def get_timex_tags_from_source(data, i):
    """
    Extracts the actual Timex tags provided by Professor HeidelTime and converts them.

    args:
        data (list): The Professor HeidelTime dataset.
        i (int): Index
    returns:
        list: The converted timex objects.
    """
    # extract the "timexs" split of the current data
    timex_data = data["timexs"][i]
    timex_list = convert_source_to_timex(timex_data)
    return timex_list


def convert_source_to_timex(timex_data):
    """
    Takes the timex tag and parses it to a timex object.

    args:
        timex_tag: The timex tag.
    returns:
        Timex: The parsed timex tag.
    """
    timex_collection = []
    for row in timex_data:
        tid = row["tid"]
        timex_type = row["type"]
        value = row["value"]
        text = row["text"]
        timex_object = Timex(tid, timex_type, value, text)
        timex_collection.append(timex_object)
    return timex_collection

