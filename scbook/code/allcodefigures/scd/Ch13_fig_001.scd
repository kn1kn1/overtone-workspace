﻿// figure 13.1 - Loading the data
(
q = q ? ();
q.execdata = ();
q.execdata.years = (1977 .. 2008); 
	// data is: [total for each region, 1977 ... 2007];
q.execdata.regions = (
	Total: [1099,1,0,2,0,1,2,5,21,18,18,25,11,16,23,14,31,38,31,
			56,45,74,68,98,85,66,71,65,59,60,53,42,37],
	Northeast: [4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,1,0,0,0],
	Midwest: [129,0,0,0,0,1,0,0,0,1,0,0,0,1,5,1,1,4,3,11,9,10,5,12,5,10,9,7,7,14,6,5,2],
	South: [933,0,0,1,0,0,2,5,21,16,18,24,10,13,17,13,26,30,26,41,29,60,
		55,74,76,50,61,57,50,43,44,36,35],
	West: [67,1,0,1,0,0,0,0,0,1,0,1,1,2,1,0,4,4,2,2,7,4,8,11,4,4,1,0,2,2,3,1,0],
	Texas: [423,0,0,0,0,0,1,0,3,6,10,6,3,4,4,5,12,17,14,19,3,37,20,35,40,17,33,24,23,19,24,26,18]
);
q.getReg = { |q, regName| q.execdata.regions[regName].drop(1) };
)

