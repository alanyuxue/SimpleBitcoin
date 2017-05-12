# Simple Bitcoin

Cryptocurrencies are considered the future of money for some but for others it is the underlying technologies that present the biggest opportunity. [Bitcoin](https://en.wikipedia.org/wiki/Bitcoin), one of the first of these currencies introduced the blockchain, a public, append-only database, that provides a very secure mechanism for recording transactions.

Rather than try and explain how Bitcoin works (you are encouraged to research this yourself but there is a good set of Khan Academy [videos](https://www.khanacademy.org/economics-finance-domain/core-finance/money-and-banking/bitcoin/v/bitcoin-cryptographic-hash-function) that give you a basic view and an more [in depth view](http://www.righto.com/2014/02/bitcoins-hard-way-using-raw-bitcoin.html) of Bitcoin messages and another on [the mining](http://www.righto.com/2014/02/bitcoin-mining-hard-way-algorithms.html) process), we are going to create a simple protocol that incorporates some of the basic elements.

Alice wants to send Bob 50 “chriscoins”. To do this, she will create a message that includes the public portion of a public/private key that she owns. Alice will then add the public key that Bob has given her to send the chriscoins to. Once she has the 3 elements, she digitally signs the message and appends that signature to the message.

The message is not sent directly to Bob however. It first needs to be validated and put onto the “blockchain” by a 3rd party called a “miner”. That process involves doing some cryptographic work, called a “proof of work”, to find a particular number. What the miner will do is to take the transaction that Alice sent and combine it with a randomly generated string (a “nonce”) so that when the two are combined and cryptographically hashed, it produces a result that has the first 8 bits all set to zero.

Once the miner has done this, the transaction is added to the blockchain along with the successful nonce that generated the correct hash. 

The miner then sends the details of the transaction to Alice and Bob. 

Obviously, chriscoins are a great deal (actually it is vastly so) simpler than bitcoins but the underlying principles involve some of the steps involved in bitcoin transactions.

For the project, you will need to write:

1. Digital wallet software for Alice and Bob to store chriscoins. The digital wallet will simply be a file that has in it a series of records that are successful bitcoin transactions as they were recorded on the chriscoin blockchain. How you store transactions is up to you but remember that records and their individual components are of variable length (you could use something like JSON). 

The digital wallet needs to generate a public and private key using RSA of 2048 bits for the key size. Public and private keys will be handled as PEM formatted text. All hashes and signatures will be SHA256

The digital wallet software will generate a message using Alice’s public key, Bob’s public key (we will assume you got this somehow), the amount of chriscoins being sent. It will then sign this message using Alice’s private key and add the signature to the message. 

The message will then be sent to the miner.

2. Miner software. The miner will receive messages from Alice and will then do a “proof of work”. It will do this by taking Alice’s message and adding a “nonce”, a number or piece of random text. It will then calculate and SHA256 hash and do this repeatedly until it gets a hash that has the first byte of the hash == 0 (if this proves too hard, then you can check if the first nibble == 0). Once this has been successfully done, the nonce is added to the transaction and it is written to the “blockchain”. The successful transaction is sent to Alice and Bob. 

2.a Put timers in the Miner software and alter the difficulty level of the hashing test until it takes on average 2 minutes of processing before a result is successful. What strategies can you employ to do this? (Hint: have a look at how it is done by Bitcoin).

3. Both Alice and Bob update their digital wallets and recalculate the balance.

The constraints of the project require that:

1. you will need to write 2 distinct pieces of software: the digital wallet that Alice and Bob use and the Miner software.
2. your project must execute on/across at least three different networked computers (not simply on a single computer, although it may be developed on just one).
3. your project may be written in Python, Java, C99, or C++ (or a combination) or NodeJS (Javascript). and may be developed for Linux, Windows, or Mac-OSX (or a combination).
4. all network traffic must be encrypted using SSL. 