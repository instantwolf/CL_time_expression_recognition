autumn :- N : (nth:<n,<s,s>> 4:n season:s)
spring :- N : (nth:<n,<s,s>> 2:n season:s)
fall :- N : (nth:<n,<s,s>> 4:n season:s)
winter :- N : (nth:<n,<s,s>> 1:n season:s)
summer :- N : (nth:<n,<s,s>> 3:n season:s)
autumns :- N : (every:<s,s> (nth:<n,<s,s>> 4:n season:s))
springs :- N : (every:<s,s> (nth:<n,<s,s>> 2:n season:s))
falls :- N : (every:<s,s> (nth:<n,<s,s>> 4:n season:s))
winters :- N : (every:<s,s> (nth:<n,<s,s>> 1:n season:s))
summers :- N : (every:<s,s> (nth:<n,<s,s>> 3:n season:s))
