
# CSV export

## CsvExporter.java

creates three new csv files in `./otp-core/exporters/'time In milliseconds'` folder, vertices.csv, edges.csv and externalids.csv.

## adapticonCsv.java

* creates the new csv file in `./otp-core/exporters/adapticon/output/'time In milliseconds'_adapticon.csv` file
this csv contains (adapticon_segment_id, internal_id)
* (external_id, internal_id) & (adapticon_segment_id, external_id) => (adapticon_segment_id, internal_id)
* (external_id, internal_id) of the latest (based on 'time In milliseconds' in the file name) uploaded csv file from `./opt-core/exportes/'time In milliseconds'_uploaded/externalids.csv` file
* (adapticon_segment_id, external_id) of the latest (based on 'time In milliseconds' in the file name) csv file from `./otp-core/exporters/adapticon/'timeInmilliseconds'_adapticon.csv` file
make sure the `_adapticon.csv` file is named as 'time In milliseconds `_adapticon.csv` and placed in the above mentioned folder.

## csvDiff.java

gives a list of new added edges in the latest created csv file

compares the latest csv file from `./otp-core/exporters/'time In milliseconds'/_edges.csv` file with the latest `generated _edges.csv` file






