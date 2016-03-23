Collection of trial of big data technologies:

<h3><a href="https://github.com/ekosusil/BigDataHelloWorlds/tree/master/ForexAnalyticsModel">ForexAnalyticsModel</a></h3>
The data model, interfaces, abstract DAO for the ForexAnalytics project. Also the business logic for querying is implemented in this part. This project will be referenced by the other project which will use technologies for database manipulation(such as Hbase, Cassandra, MongoDB) and data stream analytics framework (Spark and Storm)

<h3><a href="https://github.com/ekosusil/BigDataHelloWorlds/tree/master/ForexAnalyticsWithHBase">ForexAnalyticsWithHbase</a></h3>
This project contains the hbase implementation for CRUD operation for ForexAnalytics project. Such as put, get, scan, delete. This project will/can be referenced by project using various data stream technologies (spark or storm)


<h3><a href="https://github.com/ekosusil/BigDataHelloWorlds/tree/master/ForexAnalysisStorm">ForexAnalysisStorm</a></h3>
The project containing storm topology for streaming ForexData (obtained from Oanda), then performing simple analytics. For now the project directly store the data obtained from Oanda's rest endpoint to HBase. In future, basic trading decisions will be implemented. 

<h3>ForexAnalysisSpark</h3>

<h3>SpringHbaseForexAnalysis</h3>
Exprimenting framework Spring data for accessing HBase.
