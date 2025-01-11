#!/usr/bin/env python

import requests
import json
import datetime

SERVER = 'http://localhost'
PORT = 10001

def query_parse_server(text, dct, domain):
    r = requests.post(url='%s:%d' % (SERVER, PORT), data={"query":text, "dct":dct, "domain":domain})
    if r.status_code == requests.codes.ok:
        return r
    else:
        r.raise_for_status()

if __name__ == "__main__":
    while True:
        text = input("Document text: ")
        dct = input("Document creation time (YYYY-MM-DD): ")
        dct = dct if dct else datetime.date.today().isoformat()
        domain = input("Domain (newswire | narrative | other): ")
        domain = domain if domain else "other"
        print("================================================================================")
        print(query_parse_server(text, dct, domain).json()["timeml"])
        print ("================================================================================")
