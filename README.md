# CL_time_expression_recognition


##Samples
### Goal
The goal is to gather samples of time expressions as they occur in written correspondence. To avoid any sort of bias in creating such a dataset , personal chat histories shall serve as basis for further extraction. As the data is mainly derived from unfiltered chats between friends, its needs to be manually filtered to reduce the set to relevant entries. In this regard, also a set of entries that do not contain references to a certain time and date shall be considered of interest. 
### Gathering method

Chats have been exported from Whatsapp History with certain contacts as test file.
The textfile is chronologically sorted, where each message is represented as an entry within the file with the following structure: [Timestamp] - [Sender]: [Textmessage]
    
Example: 17.03.24, 17:05 - Cathrin Dbtech Schachner: ah moment


### Update to gathering method

A predefined dataset (Professor Heideltime:german -training) has been used to test temporal tagging.

### Comparison

The data was used to compare the performance between UWTime and ChatGPT in temporal tagging. 



#How to use 

##UWTime Testing

Clone UWTime repo (https://bitbucket.org/kentonl/uwtime-standalone/src/master/) to any folder or just use the local forc (dependencies/localcopy_uwtime) of the repository.
Open Project in InteliJ and run src/main/edu/uw/cs/lil/uwtime/TemporalMain.java or build jar file and execute.
Verify that the server is running at predefined port (10001). 
If you change the port, you need to adapt the python script uwtime.py and set the port to the same deviating number.

Once the server is up and running , run uwtime.py , it´ll automatically fetch the heideltime dataset (https://huggingface.co/datasets/hugosousa/ProfessorHeidelTime) from huggingface and test instance by instance. 

###batch size
The predefined batch size is 10 (since the process takes a while to complete)
If you wish, you might set batch size to 100 or even 1000 or higher. 




