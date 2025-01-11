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




