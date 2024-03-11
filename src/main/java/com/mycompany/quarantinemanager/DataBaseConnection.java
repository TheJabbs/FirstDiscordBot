/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quarantinemanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author johnn
 */
class DataBaseConnection {

    private static final String url = "jdbc:mariadb://localhost:3306/Quarantine?useUnicode=true&characterEncoding=UTF-8";
    private static final String username = "root";
    private static final String password = "";

    // Method to add a record to the quarantine table
    public void addRecord(String discordId, String nickname) {
        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "INSERT INTO quarantine (discord_id, nickname) VALUES (?, ?)";
            System.out.println("Nickname before insert: " + nickname);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, discordId);
            preparedStatement.setString(2, nickname);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a record from the quarantine table by Discord ID
    public void deleteRecord(String discordId) {
        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "DELETE FROM quarantine WHERE discord_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, discordId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get the nickname from the quarantine table by Discord ID
    public String getNickname(String discordId) {
        String nickname = null;
        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT nickname FROM quarantine WHERE discord_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, discordId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                nickname = resultSet.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nickname;
    }

    public boolean idExists(String discordId) {
        boolean exists = false;
        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT COUNT(*) AS count FROM quarantine WHERE discord_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, discordId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                exists = count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
    
    public void updateNickname(String discordId, String newNickname) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "UPDATE quarantine SET nickname = ? WHERE discord_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, newNickname);
            preparedStatement.setString(2, discordId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCounterValue(int newValue) {
        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "UPDATE counter SET value = ? WHERE id = 1";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newValue);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCounterValue() {
        int counterValue = 0;
        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT value FROM counter WHERE id = 1";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                counterValue = resultSet.getInt("value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counterValue;
    }
    
    

}
