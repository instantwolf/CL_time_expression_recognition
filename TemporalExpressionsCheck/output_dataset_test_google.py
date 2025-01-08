import datasets

"""
Note:
    Before trying to run/load the batches ensure that you have installed/updated your SSL certificates,
    or else the dataset won't load and errors will be thrown.
    
    Commands:
        python3 --version
        open /Applications/Python\ 3.x/Install\ Certificates.command
"""

def display_batches(dataset_name="google-research-datasets/time_dial"):
    """
    Displays elements of a dataset in batches of 5, pausing for user input between batches.
    """
    print(f"Loading the dataset '{dataset_name}' from Hugging Face...")

    # Load the dataset from Hugging Face
    dataset = datasets.load_dataset("google-research-datasets/time_dial", trust_remote_code=True)

    # Access the "test" split, as this dataset only provides a test split
    data = dataset["test"]

    print(f"Loaded dataset: {dataset_name}. Total elements: {len(data)}")

    # Batch size
    batch_size = 5
    total_items = len(data)

    # Loop through the dataset in batches
    for i in range(0, total_items, batch_size):
        # Get the current batch
        batch = data[i:i + batch_size]

        # Display the current batch
        print(f"\nDisplaying elements {i + 1} to {min(i + batch_size, total_items)}:")
        for idx, item in enumerate(batch, start=i + 1):
            print(f"{idx}: {item}")

        print(f"CONVERSATION: \n: {data[i]['conversation']} \n \n")
        print(f"CORRECT1: \n {data[i]['correct1']} \n")
        print(f"CORRECT2: \n {data[i]['correct2']} \n")
        print(f"INCORRECT1: \n {data[i]['incorrect1']} \n")
        print(f"INCORRECT1_RULE: \n {data[i]['incorrect1_rule']} \n")
        print(f"INCORRECT2: \n {data[i]['incorrect2']} \n")
        print(f"INCORRECT2_RULE: \n {data[i]['incorrect2_rule']} \n")

        # Wait for user input to continue
        if i + batch_size < total_items:
            input("\nPress Enter to display the next batch...")
        else:
            print("\nEnd of dataset reached.")


# Run the function
if __name__ == "__main__":
    display_batches()
