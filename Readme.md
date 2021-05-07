# Run below command to compile java files
javac *.java

## Create jar file of all java class 
jar cfe Gnutella.jar Gnutella *.class

# Run Gnutella network using below command
It takes 2 arguments
1st argument is peer id
2nd argument is Shared directory
Run Peer 1
java -jar Gnutella.jar  1 "LocalDir2"
Run Peer 2
java -jar Gnutella.jar  2 "LocalDir1"

## Topology
Topology is defined in topology.txt
Here each peer can act and a client as well as as a server. 
How they are connected in defined in next attribute of each peer.
Periodically after 20 seconds PING PONG message will be sent from the new peer which has joined the network.

# To Search 
I created two thread.
First thread will periodically send ping to know which all peers are active.
Second thread it to seach for file. This is not periodic. 
Provide the files from the local directories to search and download
Local directories are LocalDir1, LocalDir2, LocalDir3, LocalDir4

Please check Demo.png for demo of the searching and downloading file.




