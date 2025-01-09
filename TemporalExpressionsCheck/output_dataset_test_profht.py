import datasets


def load_dataset_pht(dataset_name="hugosousa/ProfessorHeidelTime", language_set_name = "english"):
    """
    Displays elements of a dataset in batches of 5, pausing for user input between batches.
    """
    print(f"Loading the dataset '{dataset_name}' from Hugging Face...")

    # Load the dataset from Hugging Face
    dataset = datasets.load_dataset(dataset_name, language_set_name)
    # Access the "train" split or adjust as necessary
    data = dataset["train"]
    return data

def display_batch_pht(data, start_i= 0, items_to_read = 100):
    # Batch size
    batch_size = min(items_to_read, len(data))

    batch = data[start_i:start_i + batch_size]

    total_items = len(batch["text"])

    # Get the current batch

    # Loop through the dataset in batches
    for i in range(0, total_items):
        # Display the current batch
        print(f"\nDisplaying elements {i + 1} to {min(i + batch_size, total_items)}:")

        """display fields
        for idx, item in enumerate(batch, start=i + 1):
            print(f"{idx}: {item}")
        """

        print(f"TEXT: \n: {data[i]['text']} \n \n")
        print(f"Timexs: \n {data[i]['timexs']} \n")

        # Wait for user input to continue
        if i < total_items-1:
            input("\nPress Enter to display the next batch...")
        else:
            print("\nEnd of dataset reached.")




# Run the function
if __name__ == "__main__":
    data = load_dataset_pht()
    display_batch_pht(data,0,10)

