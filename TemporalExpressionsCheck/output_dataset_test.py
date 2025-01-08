import datasets

def display_batches(dataset_name="hugosousa/ProfessorHeidelTime"):
    """
    Displays elements of a dataset in batches of 10, pausing for user input between batches.
    """
    print(f"Loading the dataset '{dataset_name}' from Hugging Face...")

    # Load the dataset from Hugging Face
    dataset = datasets.load_dataset("hugosousa/ProfessorHeidelTime", "german")

    # Access the "train" split or adjust as necessary
    data = dataset["train"]

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

        print(f"TEXT: \n: {data[i]['text']} \n \n")
        print(f"Timexs: \n {data[i]['timexs']} \n")

        # Wait for user input to continue
        if i + batch_size < total_items:
            input("\nPress Enter to display the next batch...")
        else:
            print("\nEnd of dataset reached.")


# Run the function
if __name__ == "__main__":
    display_batches()