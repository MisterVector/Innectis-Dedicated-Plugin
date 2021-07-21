## Innectis Dedicated Plugin Source Code

This repository contains the IDP source code along with the plugin and its libraries.

The source code in this repository was built for Minecraft 1.12.2, using a modified version of the Paper Minecraft Server project found here: https://github.com/PaperMC/Paper and no download is available for legal reasons.

A gitstats report has been generated for this repository here: http://archives.codespeak.org/innectis/information/gitstats_report/index.html

Note that the data from this report comes from the original IDP repository which is not housed here.

The sections below will explain how to setup the IDP so that you can edit the source code as well as run a copy of the IDP.

## Assumptions

This guide assumes you already know how to use a Java IDE (such as NetBeans), know how to set up a MySQL server, and setup a Bukkit-compatible Minecraft server.

### Database Server Support

The IDP works with the following databases:

* MySQL: 5.5, 5.6, 8.0
* MariaDB: 5.5

The versions of these databases are confirmed to work. Other versions may work but unless they are shown here they are not confirmed to work.

## Setting up the IDP Source

First download this repository at https://github.com/MisterVector/Innectis-Dedicated-Plugin/archive/master.zip

After you have downloaded the repository, import everything inside the IDP Source\src folder into a new project in your favorite Java IDE along with all the libraries in the IDP Source\libs folder.

## Setting up the database

With your MySQL manager of choice import the IDP Database Schema.sql file into MySQL to setup the database and structure. Next, create a new user with the following credentials:

Username: craft@localhost  
Password: gR3ns9xs

Note that it is not necessary to grant any global permissions. Once the account has been made, go into permission management and assign craft@localhost all permissions to innectis_db except the create and drop permissions.

## Running the IDP alongside Papyrus

If you don't already have a build of Papyrus for Minecraft 1.12.2, you can request a copy of it. Copy the Innectis Dedicated Plugin.jar file to your server's plugins folder, optionally the plugins in IDP Source\libs.

Start the server, and IDP should start up. In the console, type in "setgroup `<username>` 1" to set yourself to an admin and then type in "op `<username>`" to op yourself.

You should be good to go!