# MinecraftJavaSelfmade

A Minecraft client written in Java from scratch.

For support, discussions, information exchange, join the Discord-Server: https://discord.gg/tmZVkc You may talk to Ecconia there, about issues which do not belong on Github.

It was one of the days again, when MC pissed me off... Broken chat, weird graphics, well you know what 1.13.2 has to offer.

Well its nicer to use a system where every mistake is the fault of someone you can talk to. A system where one can fix every bug. A system which can be improved on demand.

## Start and progress

This project stated with a very messy git. But that had been cleaned up.

The first features had been the connection to online servers. The next logical step was the reading of packets and establishing a chat to talk to the other players.

To use this client as a replacement its required to see the world, single chunks can be viewed already.
The next thing to do is the positioning of the camera and adding the player and move it with the keyboard.

## Furture plans

* The most essential thing is the chat feature.
    - Better GUI
    - Tabcompletion/History
    - on click/hover
* 3D will be added, in a simple version and slowly improving.
    - There won't be a main menu, but a main SP world, maybe a floating island.
* For debugging/logging a special GUI with tabs, if possible allow moving the tabs.
    - Chunkmap, has to show which chunks are unloaded, and the age of each chunk.
* For debugging an overview over the data collected.
* Offline playing if connection is lost, never dump chunks and other data.
    - While at it, just save the whole server world too.
    - Disconnect player from actual position.
* errr...

## Current Features

* Connecting with (any) online-mode server.
  * Compression
  * Encryption
  * Keep-Alive auto-sending
* Chat-Window
  - History of all chat messages (which can be parsed) in a scrollPane.
  - Primive input field.
  - Client commands, for internal events.
* Client with multiple TABs for different topics.
  - Looks like Chrome, works quite well - throws Exception though.
* World/Chunks
  - Currently are all saved as objects (waiting for mem error).
  - A Map shows all chunks which are stored.
* 3D bad -> good
  - (JOGL, cause AWT and it just works)
  - The current Chunk can be viewd in 3D.
  - Uses a random color for each block type.
  - Primitive controls to move the chunk.

##### As most of Ecconias YOLO projects, its a learning project.

You may contribute if you are interested. No guidlines yet though.

