# analyzer


Analyzer is a scriptable version of the core app: Network Analyzer. It provides the functionality to execute algorithms to compute shortest paths, centrality measurements, self-loops, clustering coefficients, etc. for both directed and undirected graphs. Directed graphs show separate inward and outward degrees, as well as the combined Edge Count.

Analyzer omits the display of graphs to show distributions and scatter plots. This functionality is provided in a new app, cyChart, which augments the graph functionality with the ability to create filters interactively.

To invoke Analyzer, select the command Analyze Network from the Tools menu. The app will ask if the network should be treated as directed or undirected. Upon execution, the app will add a Results Panel showing the network level statistics, and will add columns to the node and edge tables for the attributes it computes.

The algorithms used are the same as Network Analyzer and is found here. https://med.bioinf.mpi-inf.mpg.de/netanalyzer/help/2.7/
