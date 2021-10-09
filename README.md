# GymBot
## Here we have two telegram bots, connected with each other
### Two Telegram bots, written on java(CRUD application), with using:</br>
1.Telegram Bot Api</br>
2.Maven</br>
3.SpringBoot</br>
4.SpringData</br>
5.MVC</br>
6.PostgreSql</br>
7.StreamApi
## AdminBot
I'm using this bot like **admin panel**, where I can change, add, remove, some information about customers</br>
Also, from admin bot I can **send notifications to all customers, who used UserBot.**</br>
So if something goes wrong, and the gym been closed today, all customers will know about it from their bots. 
</br>
**AdminBot** — it's a regular CRUD application.
</br>
</br>
## UserBot
**UserBot** was written for my customers.</br>
They always asked me about payments, when gym membership ends, how many trainings they have, why, etc.</br>
So I decided to write a bot for customers.</br>
Each customer can read information only about one person, so no one can saw information about other customers</br>
This bot can only send notifications for customers, and give information about each of customer who used them.</br>
Customer can use command **/sho_tam**, and bot gives customer all information about this customer from db.
