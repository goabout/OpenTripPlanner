# CSV export

Is used when running OTP. To run the CSV export run org.opentripplanner.standalone.OTPMain in otp-core project with argument --csvexport 

From OpenTripPlanner folder run

``java -Xmx2G -jar otp-core/target/otp.jar --csvexport``

## CsvExporter.java

### Functionality of CsvExporter class

* creates three new csv files in `./otp-core/exporters/'time In milliseconds'` folder;    
 example : 1388677582613_vertices.csv, 1388677582613_edges.csv and 1388677582613_externalids.csv.
 in folder `./otp-core/exporters/1388677582613`

* after creating csv, it computes the number of edges added/changed/removed in the new csv file compared to the latest uploaded csv (in csvDiff.java)

* displays the percentage of edges changed from latest uploaded data

* later it create adapticon csv 

## adapticonCsv.java

### Functionality of adapticonCsv class

* creates the new csv file in `./otp-core/exporters/adapticon/output/'time In milliseconds'_adapticon.csv` 

* this csv file contains (adapticon_segment_id, internal_id)

* (external_id, internal_id) & (adapticon_segment_id, external_id) => (adapticon_segment_id, internal_id)
 
* (external_id, internal_id) of the latest (based on 'time In milliseconds' in the file name) uploaded csv file from `./opt-core/exportes/'time In milliseconds'_uploaded/externalids.csv` file

* (adapticon_segment_id, external_id) of the latest (based on 'time In milliseconds' in the file name) csv file from `./otp-core/exporters/adapticon/'timeInmilliseconds'_adapticon.csv` file

### Essentials

* make sure the `_adapticon.csv` file is named as 'time In milliseconds `_adapticon.csv`  and placed in the above mentioned folder. example: `./otp-core/exporters/adapticon/output/1388677582613_adapticon.csv`

## csvDiff.java

### Functionality csvDiff class

* gives a list of new added edges in the latest created csv file

* compares the latest csv file from `./otp-core/exporters/'time In milliseconds'/_edges.csv` file with the latest `generated _edges.csv` file

