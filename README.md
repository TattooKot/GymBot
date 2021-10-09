# GymBot
Two Telegram bots, written on java(crud application), with using:</br>
1.Telegram Bot Api</br>
2.Maven</br>
3.SpringBoot</br>
4.SpringData</br>
5.PostgreSql</br>
</br>
First bot was written for me, to help me control customers in my gym
(count of training sessions, payments, visits, etc.)
Its like small crm for gym
</br>
</br>
Second bot was written for my customers.</br>
They always asked me about payments, when gym membership ends, how many trainings they have, why, etc.</br>
</br>
First bot called - AdminBot, I'm using like admin panel,</br>
where I can change, add, remove, some information about customers
</br>
From admin bot I can send notifications to all customers, who used UserBot.</br>
So if something goes wrong, and gym been closed today, all customers know about it from their bots. 
</br>
AdminBot - it's a regular crud application.
</br></br>
Second bot called - UserBot.</br>
This bot can only send notifications for customers, and know information about each of customer who used them.</br>
Customer can use command /sho_tam, and bot give customer all information about this customer from db.

