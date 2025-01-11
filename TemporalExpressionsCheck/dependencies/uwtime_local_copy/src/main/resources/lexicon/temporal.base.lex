's :- N\NP/NP : (lambda $0:s (lambda $1:s (intersect:<s,<s,s>> $0 $1)))
of :- N\NP/NP : (lambda $0:s (lambda $1:s (intersect:<s,<s,s>> $0 $1)))
, :- N\NP/NP : (lambda $0:s (lambda $1:s (intersect:<s,<s,s>> $0 $1)))
more :- N\C/N : (lambda $0:d (lambda $1:n (*:<n,<d,d>> $1 $0)))
before :- N\NP/NP : (lambda $0:s (lambda $1:d (shift:<s,<d,s>> $0 (*:<n,<d,d>> -1:n $1))))
after :- N\NP/NP : (lambda $0:s (lambda $1:d (shift:<s,<d,s>> $0 (*:<n,<d,d>> 1:n $1))))
or :- N\N/N : (lambda $0:d (lambda $1:d (id:<d,d> $0)))
a :- NP/N : (lambda $0:d (*:<n,<d,d>> 1:n $0))
a :- NP/N : (lambda $0:d (id:<d,d> $0))
an :- NP/N : (lambda $0:d (*:<n,<d,d>> 1:n $0))
an :- NP/N : (lambda $0:d (id:<d,d> $0))
the :- NP/N : (lambda $0:s (id:<s,s> $0))
the :- NP/N : (lambda $0:a (id:<a,a> $0))
the :- NP/N : (lambda $0:d (id:<d,d> $0))
every :- NP/N : (lambda $0:s (every:<s,s> $0)
each :- NP/N : (lambda $0:s (every:<s,s> $0)
several :- NP/N : (lambda $0:d (some:<d,d> $0))
current :- N/N :(lambda $0:s (this:<s,<r,r>> $0 ref_time:r))
couple of :- N/N :(lambda $0:d (*:<n,<d,d>> 2:n $0))
few :- N/N : (lambda $0:d (some:<d,d> $0))
past :- N/N : (lambda $0:s (preceding:<s,<r,r>> $0 ref_time:r)
last :- N/N : (lambda $0:s (previous:<s,<r,r>> $0 ref_time:r))
last :- N/N : (lambda $0:s (preceding:<s,<r,r>> $0 ref_time:r))
previous :- N/N : (lambda $0:s (previous:<s,<r,r>> $0 ref_time:r))
next :- N/N : (lambda $0:s (next:<s,<r,r>> $0 ref_time:r))
following :- N/N : (lambda $0:s (next:<s,<r,r>> $0 ref_time:r))
coming :- N/N : (lambda $0:s (following:<s,<r,r>> $0 ref_time:r))
past :- N/N : (lambda $0:d (id:<d,d> $0))
recent :- N/N : (lambda $0:d (id:<d,d> $0))
coming :- N/N : (lambda $0:d (id:<d,d> $0))
fiscal :- N/N : (lambda $0:s (id:<s,s> $0))
full :- N/N : (lambda $0:s (id:<s,s> $0))
same :- N/N : (lambda $0:s (id:<s,s> $0))
late :- N/N : (lambda $0:s (end:<s,s> $0))
early :- N/N : (lambda $0:s (start:<s,s> $0))
mid :- N/N : (lambda $0:s (mid:<s,s> $0))
this :- N/N : (lambda $0:s (this:<s,<r,r>> $0 ref_time:r))
that :- N/N : (lambda $0:s (this:<s,<r,r>> $0 ref_time:r))
next :- N/N : (lambda $0:d (id:<d,d> $0))
last :- N/N : (lambda $0:d (id:<d,d> $0))
first :- N/N : (lambda $0:d (id:<d,d> $0))
latest :- N/N : (lambda $0:s (preceding:<s,<r,r>> $0 ref_time:r))
earlier :- N/N : (lambda $0:s (preceding:<s,<r,r>> $0 ref_time:r))
later :- N/N : (lambda $0:s (following:<s,<r,r>> $0 ref_time:r))
nearly :- NP/NP : (lambda $0:d (less:<d,d> $0))
about :- NP/NP : (lambda $0:d (approx:<d,d> $0))
approximately :- NP/NP : (lambda $0:d (approx:<d,d> $0))
almost :- NP/NP : (lambda $0:d (less:<d,d> $0))
more than :- NP/NP : (lambda $0:d (more:<d,d> $0))
less than :- NP/NP : (lambda $0:d (less:<d,d> $0))
no more than :- NP/NP : (lambda $0:d (more_e:<d,d> $0))
no less than :- NP/NP : (lambda $0:d (less_e:<d,d> $0))
up to :- NP/NP : (lambda $0:d (less_e:<d,d> $0))
at least :- NP/NP : (lambda $0:d (more_e:<d,d> $0))
at most :- NP/NP : (lambda $0:d (less_e:<d,d> $0))
over :- NP/NP : (lambda $0:d (more:<d,d> $0))
within :- NP/NP : (lambda $0:d (less_e:<d,d> $0))
around :- NP/NP : (lambda $0:d (approx:<d,d> $0))
around :- NP/NP : (lambda $0:s (approx:<s,s> $0))
each of :- NP/NP : (lambda $0:s (every:<s,s> $0)
earlier :- NP/NP : (lambda $0:s (start:<s,s> $0))
later :- NP/NP : (lambda $0:s (end:<s,s> $0))
the beginning of :- NP/NP : (lambda $0:s (start:<s,s> $0))
the start of :- NP/NP : (lambda $0:s (start:<s,s> $0))
the middle of :- NP/NP : (lambda $0:s (mid:<s,s> $0))
the end of :- NP/NP : (lambda $0:s (end:<s,s> $0))
post :- NP/NP : (lambda $0:s (after:<s,s> $0))
pre :- NP/NP : (lambda $0:s (before:<s,s> $0))
ago :- NP\NP : (lambda $0:d (shift:<s,<d,s>> ref_time:r (*:<n,<d,d>> -1:n $0)))
or so :- NP\NP : (lambda $0:d (approx:<d,d> $0))
or so :- NP\NP : (lambda $0:s (approx:<s,s> $0))
in :- NP/NP : (lambda $0:d (shift:<s,<d,s>> ref_time:r $0))
earlier :- NP\NP : (lambda $0:d (shift:<s,<d,s>> ref_time:r (*:<n,<d,d>> -1:n $0)))
before :- NP\NP : (lambda $0:d (shift:<s,<d,s>> ref_time:r (*:<n,<d,d>> -1:n $0)))
later :- NP\NP : (lambda $0:d (shift:<s,<d,s>> ref_time:r (*:<n,<d,d>> 1:n $0)))
following :- NP\NP : (lambda $0:d (shift:<s,<d,s>> ref_time:r (*:<n,<d,d>> 1:n $0)))
long :- NP\NP : (lambda $0:d (id:<d,d> $0))
period :- NP\NP : (lambda $0:d (id:<d,d> $0))
term :- NP\NP : (lambda $0:d (id:<d,d> $0))
century :- N : century:s
decade :- N : decade:s
year :- N : year:s
quarter :- N : quarter:s
period :- N : quarter:s
month :- N : month:s
weekend :- N : weekend:s
week :- N : week:s
day :- N : day:s
hour :- N : hour:s
minute :- N : minute:s
century :- N : century:d
decade :- N : decade:d
year :- N : year:d
quarter :- N : quarter:d
period :- N : quarter:d
month :- N : month:d
weekend :- N : weekend:d
week :- N : week:d
day :- N : day:d
hour :- N : hour:d
minute :- N : minute:d
centuries :- N : (some:<d,d> century:d)
decades :- N : (some:<d,d> decade:d)
years :- N : (some:<d,d> year:d)
quarters :- N : (some:<d,d> quarter:d)
periods :- N : (some:<d,d> quarter:d)
months :- N : (some:<d,d>  month:d)
weekends :- N : (some:<d,d> weekend:d)
weeks :- N : (some:<d,d> week:d)
days :- N : (some:<d,d> day:d)
hours :- N : (some:<d,d> hour:d)
minutes :- N : (some:<d,d> minute:d)
today :- N : ref_time:r
yesterday :- N : (shift:<s,<d,s>> ref_time:r (*:<n,<d,d>> -1:n day:d))
tomorrow :- N : (shift:<s,<d,s>> ref_time:r (*:<n,<d,d>> 1:n day:d))
hourly :- AP : (every:<s,s> hour:s)
daily :- AP : (every:<s,s> day:s)
weekly :- AP : (every:<s,s> week:s)
monthly :- AP : (every:<s,s> month:s)
quarterly :- AP : (every:<s,s> quarter:s)
annually :- AP : (every:<s,s> year:s)
yearly :- AP : (every:<s,s> year:s)
past :- N : past_ref:a
former :- N : past_ref:a
previously :- AP : past_ref:a
recently :- AP : past_ref:a
earlier :- AP : past_ref:a
once :- AP : past_ref:a
recent :- ADJ : past_ref:a
currently :- AP : present_ref:a
current :- ADJ : present_ref:a
present :- ADJ : present_ref:a
now :- AP : present_ref:a
today :- N : present_ref:a
moment :- N : present_ref:a
already :- N : present_ref:a
future :- N : future_ref:a
near term :- N : future_ref:a
later date :- N : future_ref:a
short term :- N : future_ref:a
future :- N/N : (lambda $0:d future_ref:a)
soon :- AP : future_ref:a
lengthy :- N/N : (lambda $0:d unknown:a)
a while :- N : unknown:a
some time :- N : unknown:a
then :- N : ref_time:r
time:- N : ref_time:r
same time:- N : ref_time:r
est :- N\N : (lambda $0:s (id:<s,s> $0))
pst :- N\N : (lambda $0:s (id:<s,s> $0))
edt :- N\N : (lambda $0:s (id:<s,s> $0))
tonight :- N : (intersect:<s,<s,s>> ref_time:r (nth:<n,<s,s>> 4:n timeofday:s))
overnight :- N : (intersect:<s,<s,s>> ref_time:r (nth:<n,<s,s>> 4:n timeofday:s))
noon :- N : (intersect:<s,<s,s>> (nth:<n,<s,s>> 13:n hour:s) (nth:<n,<s,s>> 1:n minute:s))
midday :- N : (intersect:<s,<s,s>> (nth:<n,<s,s>> 13:n hour:s) (nth:<n,<s,s>> 1:n minute:s))
midnight :- N : (intersect:<s,<s,s>> (nth:<n,<s,s>> 1:n hour:s) (nth:<n,<s,s>> 1:n minute:s))
election day :- N : (shift:<s,<d,s>> (intersect:<s,<s,s>> (nth:<n,<s,s>> 1:n (nth:<n,<s,s>> 1:n (intersect:<s,<s,s>> day:s week:s))) (nth:<n,<s,s>> 11:n month:s)) (*:<n,<d,d>> 1:n day:d))
thanksgiving :- N : (intersect:<s,<s,s>> (nth:<n,<s,s>> 4:n (nth:<n,<s,s>> 4:n (intersect:<s,<s,s>> day:s week:s))) (nth:<n,<s,s>> 11:n month:s))
christmas :- N : (intersect:<s,<s,s>> (nth:<n,<s,s>> 25:n (intersect:<s,<s,s>> day:s month:s)) (nth:<n,<s,s>> 12:n month:s))
day :- N\N : (lambda $0:s (id:<s,s> $0))
hundred :- C\C : (lambda $0:n (*:<n,<n,n>> $0 100:n))
thousand :- C\C : (lambda $0:n (*:<n,<n,n>> $0 1000:n))
million :- C\C : (lambda $0:n (*:<n,<n,n>> $0 1000000:n))
a hundred :- C : 100:n
a thousand :- C : 1000:n
a million :- C : 1000000:n
a.m. :- NP\NP : (lambda $0:s (id:<s,s> $0))
am :- NP\NP : (lambda $0:s (id:<s,s> $0))
p.m. :- NP\NP : (lambda $0:s (shift:<s,<d,s>> $0 (*:<n,<d,d>> 12:n hour:d)))
pm :- NP\NP : (lambda $0:s (shift:<s,<d,s>> $0 (*:<n,<d,d>> 12:n hour:d)))