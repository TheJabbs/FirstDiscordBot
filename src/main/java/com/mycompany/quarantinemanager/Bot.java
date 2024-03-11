/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quarantinemanager;

import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

/**
 *
 * @author johnn
 */
public class Bot {

    private JDA jda;
    private final String TOKEN = "MTEzMjM3OTc0NDM2NTcxOTY2Mg.GoPQOj.mnDUiQ_8AUsgwUegR_kuClT096KRUDygFPMXMk",
            GUILD_ID = "953701599795109973";
    private Guild guild;
    private final DataBaseConnection connect = new DataBaseConnection();

    public void StartBot() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(TOKEN)
                .setActivity(Activity.watching("Carnivores beg for forgiveness."))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new ReadyEventListener(), new BotCommands(this, connect))
                .build().awaitReady();

        guild = jda.getGuildById(GUILD_ID);
//        Scheduler scheduler = new Scheduler();
//        scheduler.scheduleTask(this::runAutoRegisterFriend, 0, 1, TimeUnit.HOURS);

    }
//    public void runAutoRegisterFriend() {
//        Guild guild = jda.getGuildById(GUILD_ID);
//        Role friendRole = guild.getRoleById(953777314125209601L);
//        Role quarantineRole = guild.getRoleById(969900706385510420L);
//        Role mutedRole = guild.getRoleById(1080554033372217365L);
//        TextChannel staff = jda.getTextChannelById(1005761813838835782L);
//        
//        for (Member member : guild.getMembers()) {
//            if (!member.getRoles().contains(friendRole)
//                    && !member.getRoles().contains(quarantineRole)
//                    && !member.getRoles().contains(mutedRole)) {
//                guild.addRoleToMember(member, friendRole).queue();
//            }
//        }
//        staff.sendMessage("Auto friend register has been executed!").queue();
//    }

    public Guild getGuild() {
        return guild;
    }
}
