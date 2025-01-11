from ComparisonInstance import ComparisonInstance

class InstanceCollection:
    def __init__(self):
        self.instances = set()

    def addinstance(self, i):
        if isinstance(i, ComparisonInstance):
            self.instances.add(i)
        else:
            raise TypeError(f"Expected an instance of ComparisonInstance, got {type(i).__name__} instead.")

    def tp(self):
        return sum(1 for instance in self.instances if instance.result == 'P')  # True Positives

    def fp(self):
        return sum(1 for instance in self.instances if instance.result == 'FP')  # False Positives

    def fn(self):
        return sum(1 for instance in self.instances if instance.result == 'FN')  # False Negatives

    def precision(self):
        return self.tp() / (self.tp() + self.fp()) if (self.tp() + self.tp()) > 0 else 0

    def recall(self):
        return self.tp() / (self.tp() + self.fn()) if (self.tp() + self.fn()) > 0 else 0

    def f1(self):
        return (2 * self.precision() * self.recall()) / (self.precision() + self.recall()) if (self.precision() + self.recall()) > 0 else 0