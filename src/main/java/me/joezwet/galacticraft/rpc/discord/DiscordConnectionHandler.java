/*
 * Copyright (c) 2019 Joe van der Zwet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.joezwet.galacticraft.rpc.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.OffsetDateTime;


public class DiscordConnectionHandler {

    private String appId = "540788137266380811";
    private Logger logger;
    private Thread callbackThread;


    public DiscordConnectionHandler() {
        logger = LogManager.getLogger("Galactiraft RPC");
        init();
    }

    private void init() {
        logger.info("Initializing RPC");

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler((discordUser -> {
                    String avatar_url = "https://discordapp.com/api/avatars/";
                    if(discordUser.avatar == null) {
                        avatar_url += (Integer.parseInt(discordUser.discriminator) % 5) + ".png";
                    } else if(discordUser.avatar.startsWith("a_")) {
                        avatar_url += discordUser.userId + "/" + discordUser.avatar + ".gif";
                    } else {
                        avatar_url += discordUser.userId + "/" + discordUser.avatar + ".png";
                    }
                   System.out.println(String.format("RPC Ready. Registered user %s#%s (ID: %s, avatar: %s)", discordUser.username, discordUser.discriminator, discordUser.userId, avatar_url));
                   logger.info(String.format("RPC Ready. Registered user %s#%s (ID: %s, avatar: %s)", discordUser.username, discordUser.discriminator, discordUser.userId, avatar_url));
                })).build();

        DiscordRPC.discordInitialize(appId, handlers, true);

        DiscordRichPresence richPresence = new DiscordRichPresence.Builder("Loading Minecraft...")
                .setBigImage("planet_pluto", "")
                .setStartTimestamps(OffsetDateTime.now().toEpochSecond())
                .build();
        DiscordRPC.discordUpdatePresence(richPresence);

        startThread();
    }

    private void startThread() {
        if(callbackThread != null) {
            logger.info("Starting callback thread.");
            callbackThread = new Thread(() -> {
                while (true) {
                    try {
                        DiscordRPC.discordRunCallbacks();
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "Discord-Callback");
        }
    }
}
