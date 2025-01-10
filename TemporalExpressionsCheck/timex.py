class Timex:
    def __init__(self, tid, timex_type, value, text):
        self.tid = tid  # Optional identifier
        self.type = timex_type
        self.value = value
        self.text = text

    def __eq__(self, other):
        """Define equality based on type, value, and text."""
        if isinstance(other, Timex):
            return (self.type == other.type and
                    self.value == other.value and
                    self.text == other.text)
        return False

    def __lt__(self, other):
        """Define ordering based on type, value, and text."""
        if isinstance(other, Timex):
            return ((self.type, self.value, self.text) <
                    (other.type, other.value, other.text))
        return NotImplemented

    def __repr__(self):
        """String representation of the Timex object."""
        return f"Timex(tid='{self.tid}', type='{self.type}', value='{self.value}', text='{self.text}')"

    def __hash__(self):
        """Allows Timex to be used in sets and as dictionary keys."""
        return hash((self.type, self.value, self.text))