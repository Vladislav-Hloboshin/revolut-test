# revolut-test

For run this project use zip file from release section of this repository.<br>
Choose the appropriate executable file:<br>
<b>bin/RevolutTestApp</b> - *NIX OS<br>
<b>bin/RevolutTestApp.bat</b> - WINDOWS OS

Or use this command for run project from source:<br>
<b>mvn compile exec:java</b>

By default there are two accounts with ids 0 and 1.

Check balance of the first account:<br>
<b>curl -X GET http://127.0.0.1:4567/accounts/0/balance</b>

Transfer money between accounts:<br>
<b>curl -X POST -H 'Content-Type: application/x-www-form-urlencoded' -d 'accountIdFrom=0&accountIdTo=1&amount=100' http://127.0.0.1:4567/transaction</b>
