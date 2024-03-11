/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quarantinemanager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.Message;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class BotCommands extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "?";
    private final Bot bot;
    private final DataBaseConnection connect;
    private static int counter = 0;

    public BotCommands(Bot bot, DataBaseConnection connect) {
        this.bot = bot;
        this.connect = connect;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        String content = message.getContentRaw();
        EmbedBuilder embed = new EmbedBuilder();

        // Check if the message starts with the command prefix
        if (content.startsWith(COMMAND_PREFIX) && hasStaff(event.getMember())) {
            String[] commandArgs = content.split(" ");
            if (commandArgs.length == 2 && commandArgs[0].equalsIgnoreCase(COMMAND_PREFIX + "quarantine")) {
                String userId = commandArgs[1].replaceAll("[^0-9]", ""); // Get the user ID from the mention
                Member member = event.getGuild().getMemberById(userId);
                event.getMessage().delete().queue();

                if (connect.idExists(userId)) {
                    try {
                        String originalNickname = connect.getNickname(userId);
                        connect.deleteRecord(userId);
                        embed.setFooter((member.getNickname() + " has been released!"));
                        member.modifyNickname(originalNickname).queue(); // Reset the nickname to the original one
                        member.getGuild().removeRoleFromMember(member, event.getGuild().getRoleById(969900706385510420L)).queue(); // Remove the quarantine role
                        embed.setColor(new Color(0, 252, 17));
                        event.getChannel().sendMessageEmbeds(embed.build()).queue();
                        embed.clear();
                    } catch (Exception ex) {
                        Logger.getLogger(BotCommands.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    counter = connect.getCounterValue();
                    int increment = (int) ((Math.random() * 3) + 1);
                    String oldNickname = isNull(member.getNickname()) ? member.getEffectiveName() : member.getNickname();
                    String newNickname = number();

                    embed.setTitle(oldNickname + " has been sent to the quarantine!!!");
                    member.modifyNickname(newNickname).queue(after -> {
                        // This code will be executed after the nickname has been updated and the cache has been refreshed
                        connect.updateCounterValue((counter += increment));
                        // Add a delay of 1 second (1000 milliseconds) before modifying roles
                        member.getGuild().addRoleToMember(member, event.getGuild().getRoleById(969900706385510420L)).queueAfter(1, TimeUnit.SECONDS);
                        member.getGuild().removeRoleFromMember(member, event.getGuild().getRoleById(953777314125209601L)).queueAfter(2, TimeUnit.SECONDS);
                        embed.setColor(Color.YELLOW);
                        // Add a delay of 2 seconds (2000 milliseconds) before sending the embed message
                        event.getChannel().sendMessageEmbeds(embed.build()).queueAfter(2, TimeUnit.SECONDS);
                        embed.clear();
                        connect.addRecord(userId, newNickname); // Store the updated nickname in the database
                    });

                }
            } else if (commandArgs.length == 2 && hasWarden(event.getMember()) && commandArgs[0].equalsIgnoreCase(COMMAND_PREFIX + "set")) {
                event.getMessage().delete().queue();
                try {
                    int newCounterValue = Integer.parseInt(commandArgs[1]);
                    setCounter(newCounterValue);
                    event.getChannel().sendMessage("Database counter has been updated to: " + newCounterValue).queue();
                } catch (NumberFormatException ex) {
                    event.getChannel().sendMessage("Invalid number format. Please use a valid integer for the counter.").queue();
                }
            } else if (commandArgs.length == 1 && hasWarden(event.getMember()) && commandArgs[0].equalsIgnoreCase(COMMAND_PREFIX + "register")) {
                event.getMessage().delete().queue();
                registerMembersWithQuarantineRole(event);
            } else if (content.equalsIgnoreCase(COMMAND_PREFIX + "syncnames") && hasStaff(event.getMember())) {
                event.getMessage().delete().queue();
                syncNamesWithDatabase(event.getGuild());
                embed.setFooter("All nicknames have been synchronized with the database.");
                event.getChannel().sendMessageEmbeds(embed.build()).queue();
                embed.clear();
            } else if (content.equalsIgnoreCase(COMMAND_PREFIX + "modifynames") && hasStaff(event.getMember())) {
                event.getMessage().delete().queue();
                modifyNamesBasedOnDatabase(event.getGuild());
                event.getChannel().sendMessage("All nicknames have been modified based on the database.").queue();
            } else if (content.equalsIgnoreCase(COMMAND_PREFIX + "addFriend") && hasStaff(event.getMember())) {
                event.getMessage().delete().queue();
                registerFriend(event);
                event.getChannel().sendMessage("Operation successful.").queue();
            }

        }
        if (content.startsWith(COMMAND_PREFIX) && hasStaff(event.getMember())) {
            String[] commandArgs = content.split(" ");
            if (commandArgs.length == 1 && commandArgs[0].equalsIgnoreCase(COMMAND_PREFIX + "help")) {
                event.getMessage().delete().queue();
                sendHelpEmbed(event);
            }
        }
    }

    public boolean hasStaff(Member member) {
        return member.getRoles().stream().anyMatch(role
                -> role.getName().equals("Quarantine Security")
                || role.getName().equals("Quarantine Lieutenant Warden")
                || role.getName().equals("Quarantine Warden Captain")
                || role.getName().equals("Animal control")
                || role.getId().equals("953779393224912947")
                || role.getName().equals("Manager")
        );
    }

    public boolean hasWarden(Member member) {
        return member.getRoles().stream().anyMatch(role
                -> role.getName().equals("Quarantine Warden Captain")
                || role.getId().equals("953779393224912947")
        );
    }

    private static String number() {
        return String.format("%04d", counter);
    }

    private static void setCounter(int count) {
        DataBaseConnection connect = new DataBaseConnection();
        connect.updateCounterValue(count);
    }

    private static boolean isNull(String s) {
        if (s == null) {
            return true;
        } else {
            return false;
        }
    }

    private void registerMembersWithQuarantineRole(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Role quarantineRole = guild.getRoleById(969900706385510420L);
        List<Member> membersWithQuarantineRole = new ArrayList<>();

        // Manually fetch all members to ensure they are loaded in the cache
        guild.loadMembers().onSuccess(v -> {
            for (Member member : guild.getMembers()) {
                if (member.getRoles().contains(quarantineRole)) {
                    membersWithQuarantineRole.add(member);
                }
            }

            System.out.println("Members with quarantine role count: " + membersWithQuarantineRole.size());

            for (Member member : membersWithQuarantineRole) {
                if (connect.idExists(member.getId())) {
                    continue;
                }
                String userId = member.getId();
                String nickname = isNull(member.getNickname()) ? member.getEffectiveName() : member.getNickname();

                System.out.println("Registering data for Discord ID: " + userId + ", Nickname: " + nickname);
                connect.addRecord(userId, nickname);
            }

            event.getChannel().sendMessage("Successfully registered data for all members with the quarantine role.").queue();
        }).onError(error -> {
            System.out.println("Error fetching members: " + error.getMessage());
            event.getChannel().sendMessage("Failed to fetch members. Please try again later.").queue();
        });
    }

    private void registerFriend(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Role friendRole = guild.getRoleById(953777314125209601L);
        Role quarantineRole = guild.getRoleById(969900706385510420L);
        Role mutedRole = guild.getRoleById(1080554033372217365L);
        List<Member> memberWithoutFriend = new ArrayList<>();

        guild.loadMembers().onSuccess(v -> {
            for (Member member : guild.getMembers()) {
                if (!member.getRoles().contains(friendRole)
                        && !member.getRoles().contains(quarantineRole)
                        && !member.getRoles().contains(mutedRole)) {
                    memberWithoutFriend.add(member);
                }
            }

            System.out.println("Members with quarantine role count: " + memberWithoutFriend.size());

            for (Member member : memberWithoutFriend) {
                List<Role> roles = new ArrayList<>(member.getRoles());
                roles.add(friendRole);
                guild.modifyMemberRoles(member, roles).queue();
            }

            event.getChannel().sendMessage("Successfully added the friend role.").queue();
        }).onError(error -> {
            System.out.println("Error fetching members: " + error.getMessage());
            event.getChannel().sendMessage("Failed to fetch members. Please try again later.").queue();
        });
    }

    private void syncNamesWithDatabase(Guild guild) {
        DataBaseConnection connect = new DataBaseConnection();

        guild.loadMembers().onSuccess(members -> {
            List<Member> membersWithQuarantineRole = guild.getMembersWithRoles(guild.getRoleById(969900706385510420L));
            for (Member member : membersWithQuarantineRole) {
                String userId = member.getId();
                String currentNickname = member.getEffectiveName();
                String storedNickname = connect.getNickname(userId);

                if (storedNickname != null && !currentNickname.equals(storedNickname)) {
                    // Update the stored nickname in the database with the current nickname
                    connect.updateNickname(userId, currentNickname);
                }
            }
        }).onError(error -> {
            System.err.println("Failed to load members: " + error.getMessage());
        });
    }

    private void modifyNamesBasedOnDatabase(Guild guild) {
        DataBaseConnection connect = new DataBaseConnection();

        guild.loadMembers().onSuccess(members -> {
            for (Member member : members) {
                String userId = member.getId();
                String storedNickname = connect.getNickname(userId);

                if (storedNickname != null) {
                    // Set the stored nickname for the member
                    guild.modifyNickname(member, storedNickname).queue();
                }
            }
        }).onError(error -> {
            System.err.println("Failed to load members: " + error.getMessage());
        });
    }

    private void sendHelpEmbed(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Bot Commands Help");
        embed.setDescription("Here is a list of available bot commands with brief explanations:");

        // Add commands and explanations here
        embed.addField("?" + "quarantine <user_mention>", "Quarantine a user and store their nickname in the database.", false);
        embed.addField("?" + "set <number>", "Update the database counter value.", false);
        embed.addField("?" + "register", "Register data for all members with the quarantine role.", false);
        embed.addField("?" + "syncnames", "Sync nicknames in the database with current nicknames.", false);
        embed.addField("?" + "modifynames", "Modify nicknames of all members based on the database.", false);
        // Add more commands and explanations as needed

        // Set the color for the embed
        embed.setColor(event.getMember().getColor());

        // Send the embed message
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

}
