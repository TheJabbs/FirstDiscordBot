/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quarantinemanager;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class ReadyEventListener implements EventListener {
    private final long GENERAL_CHANNEL_ID = 1005761813838835782L;
    private TextChannel adminZone;

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof ReadyEvent) {
            ReadyEvent readyEvent = (ReadyEvent) event;
            System.out.println("Bot is ready and connected to Discord!");
            System.out.println("Logged in as: " + readyEvent.getJDA().getSelfUser().getAsTag());
        } else if (event instanceof GuildMemberJoinEvent) {
            handleMemberJoin((GuildMemberJoinEvent) event);
        } else if (event instanceof GuildVoiceJoinEvent) {
            System.out.println("in11");
            onGuildVoiceJoin((GuildVoiceJoinEvent) event);
        } else if (event instanceof GuildVoiceLeaveEvent) {
            onGuildVoiceLeave((GuildVoiceLeaveEvent) event);
        }
    }

    private void handleMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        String userId = member.getId();
        DataBaseConnection connect = new DataBaseConnection();
        System.out.println("Joined");

        if (connect.idExists(userId)) {
            String storedNickname = connect.getNickname(userId);
            System.out.println("123");
            event.getGuild().modifyNickname(member, storedNickname).queue(); // Set the stored nickname for the member
            event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(969900706385510420L)).queue(); // Add the quarantine role
            event.getGuild().removeRoleFromMember(member, event.getGuild().getRoleById(953777314125209601L)).queue(); // Remove the friend role
        }
    }

    private void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        System.out.println("in");
        if (event.getChannelJoined().getIdLong() == GENERAL_CHANNEL_ID) {
            adminZone = event.getGuild().getTextChannelById(969707275419779092L);
            adminZone.sendMessage(event.getMember().getAsMention() + " is lurking in staff.").queue();
        }
    }

    private void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getChannelLeft().getIdLong() == GENERAL_CHANNEL_ID) {
            adminZone.sendMessage(event.getMember().getAsMention() + " left staff.").queue();
        }
    }
}
