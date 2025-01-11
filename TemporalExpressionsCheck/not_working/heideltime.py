from python_heideltime import Heideltime
from output_dataset_test_profht import load_dataset_pht

ht = Heideltime()

# Configure Heideltime
ht.set_language("english")
#ht.set_treetagger_home("<path_to_treetagger>")  # If required
#ht.set_document_type("narrative")
#ht.set_output_format("timex")

data = load_dataset_pht()

for i in range(0,len(data)):
    text = data[i]["text"]
    datetime = data[i]["dct"]

    ht.set_document_type('NEWS')
    ht.set_document_time(datetime)
    tagged_text = ht.parse(text)

    print(f"Original Text: {text}")
    print(f"Tagged Text: {tagged_text}")

    if i < len(data) - 1:
        input("\nPress Enter to display the next batch...")