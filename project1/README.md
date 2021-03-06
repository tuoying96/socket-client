### README for Project 1

[Project1: Simple Client](https://course.ccs.neu.edu/cs5700sp21/project1.html)

#### high-level approach 
The work house of this project is *Client.java*. The program is very simple and easy to understand. It first initilizes the system by creating the socket and wrapping the input output stream to *PrintWrite* and *BufferReader*, respectively. Then the program sends out a hello message and wait for the challenges. After finishing all the challenges from server, the program will wait for the secret flag and then exit. We used a pattern matcher to check if the messages are in a good format to make sure that corrupted messages won't crash the program.

#### Challenges
- The fisrt challeng is eveluate the complex tree expressions in Java. In Python, this issue can be solved just by using one-line code eval() to get the solution, while in Java, there is no such method. Therefore, I coded the python file(eval.py) and call it in my Client.java, and read the outout of this function. Finally, I can get the solution of the tree expression.
- Another chanllenge I come across was the extra point SSL implementation. SSL Socket in **Java** cannot valid and trust the self-signed server's certificate(`simple-service.ccs.neu.edu: 27998`). I firstly tried *X509TrustManager* tp by pass the SSL validation of `simple-service.ccs.neu.edu: 27998`, but it seems simply bypass all SSL configuration and will crash sometime. Finally, we find this [solution](https://blogs.oracle.com/gc/entry/unable_to_find_valid_certification) and used a [Java program](https://github.com/escline/InstallCert) to read and configure for the SSL server, and get the  certificate(`jssecacerts`). Therefore, I can use a System.setProperty() method in my Client.java for configuration. Then the SSL version works on this SSL port.
