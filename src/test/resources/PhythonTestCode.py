import XY

# Python program to display all the prime numbers within an interval
class PrimeNumbers:
    lower = 900
    upper = 1000
    """
    test = "should not be detected"
    #migrated should also not be detected
    """

# #migrated this should not be detected
    print("Prime numbers between", lower, "and", upper, "are:")
#END

    #Migrated comment comment comment
    for num in range(lower, upper + 1):
        # all prime numbers are greater than 1
        if num > 1:
            for i in range(2, num):
             """
            multiline comment
            """
                if (num % i) == 0:
                    break
            else:
                print(num)
    #End
    print("Hello Itestra!")
    # LoC = 12 Migrated = 7 Unmarked = 5