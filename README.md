# Simple Bitcoin

Cryptocurrencies are considered the future of money for some but for others it is the underlying technologies that present the biggest opportunity. [Bitcoin](https://en.wikipedia.org/wiki/Bitcoin), one of the first of these currencies introduced the blockchain, a public, append-only database, that provides a very secure mechanism for recording transactions.

Rather than try and explain how Bitcoin works (there is a good set of Khan Academy [videos](https://www.khanacademy.org/economics-finance-domain/core-finance/money-and-banking/bitcoin/v/bitcoin-cryptographic-hash-function) that give you a basic view and an more [in depth view](http://www.righto.com/2014/02/bitcoins-hard-way-using-raw-bitcoin.html) of Bitcoin messages and another on [the mining](http://www.righto.com/2014/02/bitcoin-mining-hard-way-algorithms.html) process), we are going to create a simple protocol that incorporates some of the basic elements.

## Program Instructions

This software was created by Kilian Giczey, Srdjan Kusmuk, Xue Yu and Zhiwei Huang.It is written in Java. It requires Java version higher than 1.6.

### Commands to Compile the Files:
```
javac Account.java
javac DigitalWallet.java
javac Miner.java
javac Server.java
```
### Commands to Run the Program:
```
java Server <portNumber>
java DigitalWallet <Host Name> < Port Number> <Sender’s Name> <Receiver's Name> (This will Acts as the Sender in the transactions)
java DigitalWallet <Host Name> <Port Number> <Receiver’s Name> (This will act as the receiver in the transactions)
```
### Instructions to Complete the Transaction
1. Within the Sender terminal type the transaction amount you wish to send
2. Wait as the server completes the mining process and verifies your transaction

#### Procedure of Transaction

Alice wants to send Bob 50 Bitcoins. To do this, she will create a message that includes the public portion of a public/private key that she owns. Alice will then add the public key that Bob has given her to send the bitcoins to. Once she has the 3 elements, she digitally signs the message and appends that signature to the message.

The message is not sent directly to Bob however. It first needs to be validated and put onto the “blockchain” by a 3rd party called a “miner”. That process involves doing some cryptographic work, called a “proof of work”, to find a particular number. What the miner will do is to take the transaction that Alice sent and combine it with a randomly generated string (a “nonce”) so that when the two are combined and cryptographically hashed, it produces a result that has the first 8 bits all set to zero.

Once the miner has done this, the transaction is added to the blockchain along with the successful nonce that generated the correct hash. 

The miner then sends the details of the transaction to Alice and Bob. 

### Operation Process Completed by the Program

1. The Miner is initiated and the Server is set up in the process
2. Both the Sender’s Digital Wallet and the Receiver’s Digital Wallet are connected to the Server
3. If the Program has not been used before the Sender’s account will automatically be set up with 1000 coins in order to demonstrate the operations 
4. The Sender will then make a transaction request to the Server
5. The Server will begin the mining process as it searches for a suitable nonce that allows for the first nibble to equal ‘0’.
6. Once the mining process has been completed the Server will reply with a successful transaction message to both the Sender and Receiver of the funds.
7. The balance details are then stored locally on each client of the Digital Wallet in a text file. The Miner will also store a copy of each client’s balance.
8. Upon completion of the transaction, the Sender is automatically forced to transmit its balance to the Server so that the verification process may begin.
9. The Server will then compare the alleged balance with the recorded balance contained within the Server.
10. The Sender is then alerted whether the verification was successful or if the balance has been tampered with.

## Program Analysis

### Assumptions

There were a number of assumptions we made before starting to make the programs in order to simplify or to constrain them. The following key assumptions were made:

1. We assumed the miner was always right and therefore all verification was made within the miner. 
2. If any alteration to your account balance were made the miner, the miner notifies the users of a failed verification. The miner will not correct the account inconsistency and therefore we assume that the users are honest and follow the proper procedure.
3. The amount of Bitcoins in circulation is not limited and is not regulated. The amount loaded into the accounts at creation determines how many are in the digital economy, and this maximum number can increase if a new account is loaded in.  

### Security

For the connections among Alice, Bob, and Miner are all secured by certificates. The certificate is generated using following command:   
```
	Keytool - genkeypair -alias sha256 -keyalg RSA -keysize 2048 \
		-sigalg SHA256withRSA -keystore serverkeystore -validity 365
```
All connections use ssl sockets and one certificate “serverkeystore”. Because only one certificate is used, the level security will be lower comparing with multi-certificate connections.   

However, the certificate generated satisfies the requirements of using SHA256 hashing, RSA algorithm, and a key size of 2048 bit.   

To improve the security level, more certificates should be generated and imported into server’s keystore and clients only have one server’s certificate in their truststores. Moreover, a CA can be implemented on server side to keep a record of certificates that clients may use.

### Miner

Objectives: the miner software provided the “proof of work” function and confirmation of transactions to the wallet software. 

The proof of work function was implemented similar to how real Bitcoin proof of work functions operate. It took a message containing the sender’s account, the receiver's account, the amount of Chriscoin being sent, the timestamp as a date, and the previous hash, then combined it with a “nonce” value. This nonce was iteratively set in a loop, and after each new nonce was found, the combined string was hashed using a SHA256 hashing function. After each iteration the hash was tested if it was within the given difficulty limit and if the first nibble (the first 4 bits) were equal to zero, which were the necessary values to find a valid nonce. 

The difficulty setting was implemented by following Bitcoin’s difficulty strategy, and in essence limiting the number of valid hashes allowed. The difficulty calculation is a simple division of all possible SHA256 hashes by an arbitrary number to limit and therefore make it more difficult to find a valid hash. The optimal difficulty value (the variable diffDivisor) we found to get an average hashing time of about 1.5 - 2 minutes was 16777216. To make it more difficult (i.e time consuming) you simply make this number larger. 

This arbitrary process is done to use up computational power and make confirming a transaction in the blockchain a more time consuming process. 

Limitations:
* In order to make the hashing function take on average 2 minutes we could not check the first byte (8 bits) of the hashes, we had to resort to only the first nibble. When the first byte was attempted it took about 15 - 17 minutes on a powerful desktop.
* Due to the fact that we weren’t making a real blockchain, the hashes and nonce values found were only trivial and not used for verification. In practice, each hash would only be valid if it used the prior transactions hash, in turn this would provide a way to insure a transaction was not fraudulent. 

### Verification Process

Objectives:
To ensure the client’s balances are legitimate and haven’t been tampered with

Limitations:
* The verification process takes place after the transaction has been approved by the miner. This therefore does not prevent client’s altering their balance but does however alert the Miner.
* The verification process only acts as an indicator of integrity of the accounts rather than a prevention of fraud.
* The verification process only checks the sender’s balance. It is therefore reasonable that anyone may change their balance however they will not be able to make any further transactions, as the Miner will detect alterations during any future transactions.

Improvements
* The verification process may be improved by also verifying the account balances before and after the transaction has been completed.
* The verification process may also be strengthened by granting it the ability to reverse a transaction if it detects alterations.
* Verifying the receiver’s account may also help strengthen the entire system however this is still covered by the current verification process.
* Implementation of a full blockchain which would allow for a secure storage of client’s balance’s in an environment that wouldn’t be modifiable.

Practical Applications 
  
To become a practical application that truly reflects the bitcoin system the program would have to implement a more peer-2-peer system which doesn’t rely on a central Miner to act as the Server. This may be possibly implemented by combining both the client’s Digit Wallets and Miner’s Server functionality together to allow for each client to act as a Miner for many other clients in a large network operation.

### Digital Wallet

Objectives:
1. Provide a client user interface to transact electronic funds.
2. Make sure transaction is secure and the amount of coins transferred is verified
3. Separation between the functions of a Sender and a Receiver

The digital wallet is the primary tool used by clients to transfer funds, it allows both the sender and receiver to connect to the Miner through a server function and follow the transaction and verification in the process.

### Account Files
Objectives
1. Provide a simple storage facility for the account details for both the Digital Wallet and the the Miner.
2. Provide an easy to read format

Limitations
* Can be modified by clients for fraudulent purposes. This issue is however overcome by the verification process of the Miner which compares the alleged values provided by the client’s account files and the saved files within the Miner.

Improvements
* Further versions of the program may be able to implement an encrypted file system that is far more difficult to modify, however this would then need some sort of extension to allow for easy viewing of account balances. 
