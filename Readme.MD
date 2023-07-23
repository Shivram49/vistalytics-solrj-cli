
Overview
---------
A prototype system to explore and do forensic analysis on a corpus of text.

Tech stack
-----------
Solr 8.11.2

Java JDK 17.0.1

Mallet library(Topic Modelling)


Setting up solr in local
-------------------------

1. Download solr 8.11.2 binary file from the portal
2. Go inside the downloaded file parent location
3. Run the following commands

   `sudo bin/solr start -force`
4. Create a core name updated_enron using the following command
    
    `bin/solr create -c updated_enron`
5. Load emails_cleaned.csv provided to this core using the following command
   
    `bin/solr post -c updated_enron path/to/emails_cleaned.csv`
6. Go to solr admin and query to find out if the data has been loaded


Setting up the project in local
--------------------------------
1. Clone the repo
2. Open intellij and import the project
3. Make sure to use Module SDK Java >= 17

Running the project in local
-----------------------------
1. Run the LDATester code first, this will train the model, serialize and store it in src/main/resrouces(Troubleshooting tips: make sure the module sdk >= 17)
2. Now run the QueryTool, and do as prompted
3. You would be able to see the following
   1. Number of documents matched with the keyword
   2. Emails from and to
   3. Top k topics


Test cases
-----------
1. Give a search term after the program starts running
   Search term = "exam"

![img.png](img.png)


2. Give the choice 1 for number of documents
3. Give the choice 2 for from and to emails
4. Give choice 3 for query in context
5. Give choice 4 for top k topics


Test case 2 : search term = Lorry