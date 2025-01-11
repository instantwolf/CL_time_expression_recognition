#!/usr/bin/env python
import utils
import sys

days_of_week = ('monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday', 'sunday')
months = ('january', 'february', 'march', 'april', 'may', 'june', 'july', 'august', 'september', 'october', 'november', 'december')
short_months = ('jan.', 'feb.', 'mar.', 'april', 'may', 'june', 'july', 'aug.', 'sept.', 'oct.', 'nov.', 'dec.')
english_ordinal_numbers = ('first', 'second', 'third', 'fourth', 'fifth', 'sixth', 'seventh', 'eighth', 'ninth', 'tenth',\
                           'eleventh', 'twelfth', 'thirteenth', 'fourteenth', 'fifteenth', 'sixteenth', 'seventeenth', 'eighteenth', 'nineteenth', 'twentieth')
seasons = {'winter':1, 'spring':2, 'summer':3, 'fall':4, 'autumn':4}
times_of_day = {'morning':1, 'afternoon':2, 'evening':3, 'night':4}
english_decades = ('twenties', 'thirties', 'fourties', 'fifties', 'sixties', 'seventies', 'eighties', 'nineties')

def make_days_of_week(names):
    print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n (intersect:<s,<s,s>> day:s week:s))'%(name, i + 1) for i, name in enumerate(names))

def make_months(names):
    print '\n'.join('%s :- N  : (nth:<n,<s,s>> %d:n month:s)'%(name, i + 1) for i, name in enumerate(names))

def make_decades(english_decades):
    print '\n'.join('%ds :- N : (nth:<n,<s,s>> %d:n decade:s)' % (i * 10, i) for i in range(192, 200))
    print '\n'.join('\'%ds :- N : (nth:<n,<s,s>> %d:n decade:s)' % (i * 10, 190 + i) for i in range(2, 10))
    print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n decade:s)' % (s, 192 + i) for i,s in enumerate(english_decades))

def make_years():
    print '\n'.join('%d :- N : (nth:<n,<s,s>> %d:n year:s)' % (i, i) for i in range(1001, 3000))

def make_bc_years():
    print '\n'.join('%d bc :- N : (bc:<s,s> (nth:<n,<s,s>> %d:n year:s))' % (i, i) for i in range(1, 3000))

def make_english_years():
    print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n year:s)' % (utils.int2word(19) + utils.int2word(i), 1900 + i) for i in range(10, 100))
    print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n year:s)' % (utils.int2word(20) + utils.int2word(i), 2000 + i) for i in range(10, 100))
    print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n year:s)' % (utils.int2word(i), i) for i in range(2000, 2100))

def make_seasons(names):
    print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n season:s)' % (name, i) for name, i in names.items())
    print '\n'.join('%s :- N : (every:<s,s> (nth:<n,<s,s>> %d:n season:s))' % (name + 's', i) for name, i in names.items())

def make_times_of_day(names):
    print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n timeofday:s)' % (name, i) for name, i in names.items())
    print '\n'.join('%s :- N : (every:<s,s> (nth:<n,<s,s>> %d:n timeofday:s))' % (name + 's', i) for name, i in names.items())

def make_hours():
    format_string = '%s :- N : (intersect:<s,<s,s>> (nth:<n,<s,s>> %d:n hour:s) (nth:<n,<s,s>> 1:n minute:s))'
    print format_string % ('12 a.m.', 1)
    print '\n'.join(format_string % ('%d a.m.' % (i - 1), i) for i in range(2, 13)) # 1 a.m. is the second hour
    print format_string % ('12 am', 1)
    print '\n'.join(format_string % ('%d am' % (i - 1), i) for i in range(2, 13)) # 1 a.m. is the second hour
    print format_string % ('12 p.m.', 13)
    print '\n'.join(format_string % ('%d p.m.' % (i - 1 - 12), i) for i in range(14, 25))
    print format_string % ('12 pm', 13)
    print '\n'.join(format_string % ('%d pm' % (i - 1 - 12), i) for i in range(14, 25))

def make_english_numbers():
    format_string = '%s :- C : %d:n'
    print '\n'.join(format_string % (utils.int2word(i + 1), i + 1) for i in range(19)) # units
    print '\n'.join(format_string % (utils.int2word(i), i) for i in (20, 30, 40, 50, 60, 70, 80, 90)) # tens

def make_ordinal_numbers():
    print '\n'.join('%s :- N/N : (lambda $0:s (nth:<n,<s,s>> %d:n $0))' % (utils.ordinal_int(i + 1), i + 1) for i in range(25))

def make_ordinal_days_of_month():
    print '\n'.join('%s :- N\N : (lambda $0:s (intersect:<s,<s,s>> $0 (nth:<n,<s,s>> %d:n day:s)))' % (utils.ordinal_int(i + 1), i + 1) for i in range(31))
    print '\n'.join('%s :- N/N : (lambda $0:s (intersect:<s,<s,s>> $0 (nth:<n,<s,s>> %d:n day:s)))' % (utils.ordinal_int(i + 1), i + 1) for i in range(31))
    print '\n'.join('%s of :- N/N : (lambda $0:s (intersect:<s,<s,s>> $0 (nth:<n,<s,s>> %d:n day:s)))' % (utils.ordinal_int(i + 1), i + 1) for i in range(31))
    #print '\n'.join('%s :- N : (nth:<n,<s,s>> %d:n day:s)' % (utils.ordinal_int(i + 1), i + 1) for i in range(31))

def make_days_of_month():
    #print '\n'.join('%d :- N : (nth:<n,<s,s>> %d:n day:s)' % (i + 1, i + 1) for i in range(31))
    print '\n'.join('%d :- N\N : (lambda $0:s (intersect:<s,<s,s>> $0 (nth:<n,<s,s>> %d:n day:s)))' % (i + 1, i + 1) for i in range(31))
    print '\n'.join('%d :- N/N : (lambda $0:s (intersect:<s,<s,s>> $0 (nth:<n,<s,s>> %d:n day:s)))' % (i + 1, i + 1) for i in range(31))

def make_english_ordinal_numbers(names):
    print '\n'.join('%s :- N/N : (lambda $0:s (nth:<n,<s,s>> %d:n $0))' % (name, i + 1) for i, name in enumerate(names))

if __name__ == '__main__':
    sys.stdout = open("temporal.daysofweek.lex", "w")
    make_days_of_week(days_of_week)

    sys.stdout = open("temporal.months.lex", "w")
    make_months(months)
    make_months(short_months)

    sys.stdout = open("temporal.seasons.lex", "w")
    make_seasons(seasons)

    sys.stdout = open("temporal.timesofday.lex", "w")
    make_times_of_day(times_of_day)

    sys.stdout = open("temporal.decades.lex", "w")
    make_decades(english_decades)

    sys.stdout = open("temporal.daysofmonth.lex", "w")
    make_days_of_month() 
    make_ordinal_days_of_month()
                   
    sys.stdout = open("temporal.years.lex", "w")
    make_years()
    make_bc_years()
    make_english_years()

    sys.stdout = open("temporal.hours.lex", "w")
    make_hours()

    sys.stdout = open("temporal.numbers.lex", "w")
    make_english_numbers()

    sys.stdout = open("temporal.ordinalnumbers.lex", "w")
    make_ordinal_numbers()
    make_english_ordinal_numbers(english_ordinal_numbers)
