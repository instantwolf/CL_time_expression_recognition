#!/usr/bin/env python

import requests
import json
import datetime
from pyarrow import nulls
from dateutil.parser import parse

import xml.etree.ElementTree as ET

from ComparisonInstance import ComparisonInstance
from InstanceCollection import InstanceCollection
from output_dataset_test_profht import load_dataset_pht
from timex import Timex

"""
The UWTIME stand-alone app is written in java, saved within dependencies/uwtime-standalone 
"""
SERVER = 'http://localhost'
PORT = 10001

def query_parse_server(text, dct, domain):
    r = requests.post(url='%s:%d' % (SERVER, PORT), data={"query":text, "dct":dct, "domain":domain})
    if r.status_code == requests.codes.ok:
        return r
    else:
       # r.raise_for_status()
        return False



def extract_date_portion(datestring):
    # Parse the text into a datetime object
    dt_object = parse(datestring)
    # Extract only the date portion
    date_only = dt_object.date()
    return date_only

def convert_server_response_to_timex(timex_tag):
    tid = timex_tag.attrib.get("tid")
    timex_type = timex_tag.attrib.get("type")
    value = timex_tag.attrib.get("value")
    text = timex_tag.text
    timex_object = Timex(tid=tid, timex_type=timex_type, value=value, text=text)
    return timex_object

def get_timex_tags(response):

    response_json = response.json()
    if "timeml" not in response_json:
        print("The 'timeml' key is missing in the response.")
        return []
    else:
        try:
            tags = ET.fromstring(response.json()["timeml"])
            timexes = tags.findall(".//TIMEX3")
            filtered_times = timexes[min(1,len(timexes)):len(timexes)]
            timex_objects = []

            for timex in filtered_times:
                timexobj = convert_server_response_to_timex(timex)
                timex_objects.append(timexobj)
        except Exception as e:
            print(f"An error occurred while processing the XML: {e}")
            timex_objects = []
        return timex_objects

def output_timexs(response):
    timexes = get_timex_tags(response)
    for timex in timexes:
        tid = timex.attrib.get("tid")
        timex_type = timex.attrib.get("type")
        value = timex.attrib.get("value")
        text_content = timex.text
        print(f"TID: {tid}, Type: {timex_type}, Value: {value}, Text: {text_content}")


def get_timex_tags_from_source(data, i):
    timexdata = data["timexs"][i]
    timexlist = convert_source_to_timex(timexdata)
    return timexlist


def convert_source_to_timex(timexdata):
    timexcollection = []
    for row in timexdata:
        tid = row["tid"]
        timex_type = row["type"]
        value = row["value"]
        text = row["text"]
        timexobj = Timex(tid,timex_type,value,text)
        timexcollection.append(timexobj)
    return timexcollection


def read_tempEx():
    val = input("Enter any text that may contain temporal expressions: \n")
    return val

if __name__ == "__main__":

   while True:
        tempEx = read_tempEx()
        response = query_parse_server(tempEx, extract_date_portion("2025-01-17"),"other")

        if response == False or response.status_code != 200:
            print("UWTime returned an erroneous response .. try again \n ")
        else:
            timexresponse = get_timex_tags(response)
            print(timexresponse)
