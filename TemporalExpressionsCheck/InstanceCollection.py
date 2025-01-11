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

    def tn(self):
        return sum(1 for instance in self.instances if instance.result == 'N')  # True Positives

    def fp(self):
        return sum(1 for instance in self.instances if instance.result == 'FP')  # False Positives

    def fn(self):
        return sum(1 for instance in self.instances if instance.result == 'FN')  # False Negatives

    def accuracy(self):
        denom = (self.tp() + self.tn() + self.fp() + self.fn())
        return (self.tp() + self.tn()) / denom if denom > 0 else 0

    def precision(self):
        denom = (self.tp() + self.fp())
        return self.tp() / denom if denom > 0 else 0

    def recall(self):
        denom = (self.tp() + self.fn())
        return self.tp() / denom if denom > 0 else 0

    def f1(self):
        prec = self.precision()
        rec = self.recall()
        denom = prec + rec
        return (2 * prec * rec) / denom if denom > 0 else 0

    def print_stats(self):
        print(f"Calculating statistics for instance set")
        print(f"Amount of instances tested: #{len(self.instances)} ")
        print(f"Precision: {self.precision()}")
        print(f"F-Score: {self.f1()}")
        print(f"Recall: {self.recall()}")
        print(f"Accuracy: {self.accuracy()}")