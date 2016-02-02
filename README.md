# ResetWiFi

On Nexus 5 with CM 13 CAF, WiFi has many strang problems:

- Unable to connect to a saved network
- Unable to see a saved but hidden network
- Will not connect to a network if it *does not have Internet connection*

I found deleting the `/data/misc/wifi/networkHistory.txt` file and toggle the WiFi state can resolve the issues.
