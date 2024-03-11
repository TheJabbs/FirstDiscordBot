/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.quarantinemanager;

import javax.security.auth.login.LoginException;

/**
 *
 * @author johnn
 */
public class QuarantineManager {

    public static void main(String[] args) throws LoginException, InterruptedException {
        Bot bo = new Bot();
        bo.StartBot();
    }
}
