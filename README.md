# IRCLib
Java library for handle a connection to Internet Relay Chat.

- Easy to use.
- Event based system.
- You can create custom event.

## Exemples
### Hello World !
````Java
IRCConnection conn = new IRCConnection("hostname.exemple.com", "MyUserName");
Channel c = conn.joinChannel("channel");
c.sendMessage("Hello World !");
````
