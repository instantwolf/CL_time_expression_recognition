class ComparisonInstance:
        def __init__(self, solution_list, prediction_list):
            """
            Initialize the comparer with two TimexCollection objects.
            :param collection1: The first TimexCollection (e.g., ground truth).
            :param collection2: The second TimexCollection (e.g., predicted tags).
            """
            self.truth = set(solution_list)  # Ground truth
            self.prediction = set(prediction_list)  # Predicted collection

            self.result = self.eval_instance();

        #truth and prediction are equal
        def positive(self):
            return self.truth == self.prediction

        #truth > prediction -> prediction missing values, false negative
        def false_negative(self):
            return self.truth != self.prediction and self.truth.issuperset(self.prediction)

        #truth < prediction -> predicted too many, false positive
        def false_positive(self):
            return self.truth != self.prediction and self.prediction.issuperset(self.truth)

        def negative(self):
            return not self.positive() and not self.false_negative() and not self.false_positive()


        def eval_instance(self):
            if self.positive():
                return 'P'
            elif self.false_positive():
                return 'FP'
            elif self.false_negative():
                return 'FN'
            elif self.negative():
                return 'N'
            else:
                return 'O'

        def __repr__(self):
            """String representation of the Instance object."""
            return f"\n Instance: truth: {self.truth} \n prediction: {self.prediction} \n res: {self.result}"


