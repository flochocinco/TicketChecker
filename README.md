Started from ZxingView-master
https://github.com/fanrunqi/ZxingView

# Introduction
I'm still learning android dev, so feel free to report issue, enhancement and better way to work

Android App for a ticket checking (cinema, museum, etc...)

# Use case
The app read a qr code with phone camera; and check if ticket has already been used.
It also add the ticket into distant server to ensure unicity.

# Modules
it contains two modules:
 - TicketCheckerModule which is the code I wrote
 - ZxingViewLib library for QR Code reading
 
 Basicaly it will read a QR Code with library, read used ID from Google Sheet, and update the sheet if needed
 
# Prerequesites
In order to work, one needs to create Google Cloud Platform (free) client ID for android. read this page:
https://developers.google.com/fit/android/get-api-key
 
# How to use ?
Download or pull repo and open it with Android Studio IDE
