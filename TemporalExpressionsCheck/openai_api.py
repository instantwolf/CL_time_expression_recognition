"""
This module is responsible for sending the data and receiving the datetime data to and from the OpenAI API.

functions:
    extract_datetime_from_text(input_data): handles the requests / responses from the OpenAI API.
"""
from openai import OpenAI
from datetime import datetime
import api_key

# set your API Key to enable requests
client = OpenAI(api_key=api_key.API_KEY)
# model to use
gpt_model = "gpt-4o-mini"

# prompt for OpenAI API (split up for better readability)
task = ("Du bist ein hilfreicher Assistent. "
        "Deine Aufgabe ist es, Zeitangaben aus Texten zu extrahieren und sie in ein vollständiges Datum mit Uhrzeit "
        "zu konvertieren."
        "Zusätzlich sollst du diese auch noch in eine Timex Klasse bringen wie z.B. P1W.")
output_format = ("Liste alle Angaben in dieser Struktur auf: [fullDate: YYYY-MM-DD, time: HH:MM:SS, timexClass: TimeX "
                 "Klasse].")
additional_info = "Verwende das heutige Datum, falls nötig."


def extract_datetime_from_text(input_data):
    """
    Sends a request with a prior prompt to the ChatGPT API which extracts datetime data from the input data.
    The API will then return the data in [fullDate: YYYY-MM-DD, time: HH:MM:SS, timexClass: timeXClass] format.

    args:
        input_data (string): Data to be processed.
    returns:
        string: The found datetime data in [fullDate: YYYY-MM-DD, time: HH:MM:SS, timexClass: timeXClass] format.
    """
    # current date for reference purposes
    current_date = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # request to ChatGPT API
    response = client.chat.completions.create(model=gpt_model,
                                              messages=[
                                                  {"role": "system",
                                                   "content": f"{task}{output_format}{additional_info}"},
                                                  {"role": "user",
                                                   "content": f"{input_data}. Das heutige Datum ist {current_date}."}
                                              ])

    # extract the response of ChatGPT
    return response.choices[0].message.content
