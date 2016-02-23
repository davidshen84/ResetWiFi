# ResetWiFi

[![Stories in Ready](https://badge.waffle.io/davidshen84/ResetWiFi.svg?label=ready&title=Ready)](http://waffle.io/davidshen84/ResetWiFi)

On Nexus 5 with CM 13 CAF, WiFi has many strange problems:

- Unable to connect to a saved network
- Unable to see a saved but hidden network
- Will not connect to a network if it *does not have Internet connection*

I found deleting the `/data/misc/wifi/networkHistory.txt` file and toggle the WiFi state can resolve thest issues.

For this app to work, it requires:

- A rooted phone
- Access and change WiFi state
