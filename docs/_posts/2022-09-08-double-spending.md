---
layout: post
title: What Is the Double Spend Problem?
tags: [Blockchain]
category: FINALIZED
color: rgb(197, 160, 99)
feature-img: "assets/img/post-cover/19-cover.png"
thumbnail: "assets/img/post-cover/19-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

The Double Spend Problem describes the difficulty of ensuring digital money is not easily duplicated. Trusted third 
parties such as banks prevent double spends by privately verifying each transaction. The Bitcoin Network prevents double 
spends by allowing every member to verify every transaction.

<!--more-->

* TOC
{:toc}


The Double Spend Problem
------------------------

Digital objects like files and text are easy to duplicate. With the click of a button or a few keys, any number of files 
can be copied from one location to another. This makes digital devices very simple and useful for the average user.

However, costless duplication is not a desirable trait in money. It would not be desirable to have a monetary system
which allowed anyone to duplicate their money at will. This is the Double Spend Problem: _how can a receiver of digital 
money be sure that the money they were sent was not simultaneously sent to someone else_? How can all members of a
monetary network be sure others are not duplicating their money at will?

### Physical Money

When money is physical, the Double Spend Problem is of no concern. The same physical bill or coin cannot be in two
places at once. The Double Spend Problem only occurs in digital systems, where the same file or data can be present in
two places at once. With this in mind, the Double Spend Problem was not always an issue. Rather, it was "unsolved" by
the digitization of money.


Solutions
---------

Two types of solutions to the Double Spend Problem have arisen to maintain trust in digital money. The first relies on 
trusted central authorities to prevent double spends and other types of fraud. The second rejects central authorities
and allows any individual to check all transactions for double spends.

### Trusted Third Parties

In order to prevent fraudulent transactions such as double spends, certain institutions are entrusted to verify all 
transactions privately. These institutions include payment processors, banks, Automated Clearing Houses, and ultimately 
central banks. Each institution maintains its own private ledger and applies its own rules to verifying the
transactions.

Banks and payment processors are accountable to the local central bank and government. For this reason, they are usually 
honest. However, the costs imposed by this system are many. Most financial institutions charge fees and impose limits on 
the size, type, and number of transactions a client can execute. Additionally, transactions can take anywhere from 30-90 
days to settle depending on the transaction type.

### A Distributed Ledger

Bitcoin solves the double spend problem by using a **decentralized ledger**, which all users can access. Because all
members of the Bitcoin network can examine the full history of transactions, they can be sure that neither their coins
nor any other coins have been double spent.

When one user sends bitcoin to another, they destroy the coin they own and create a new coin owned by the receiver. The 
destruction of the sender's coin is recorded for all to see, so that they can never send it to someone else.

Bitcoin is an open system, meaning that anyone can start using the network without paying entry or maintenance fees. 
Likewise, there are no restrictions on the number or size of transactions. Additionally, thanks to the immutability of
the blockchain, Bitcoin transactions can achieve final settlement in as little as one hour.
