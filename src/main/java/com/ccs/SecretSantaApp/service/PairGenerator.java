package com.ccs.SecretSantaApp.service;

import com.ccs.SecretSantaApp.dao.SecretSantaUser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class PairGenerator {
    public HashMap<SecretSantaUser, SecretSantaUser> generatePairs(ArrayList<SecretSantaUser> participants) {
        HashMap<SecretSantaUser, SecretSantaUser> pairs = new HashMap<>();

        for(int i = 0; i < participants.size(); i++){
            Random rand = new Random();
            int randomSpot = rand.nextInt(participants.size());
            swap(participants, i, randomSpot);
        }

        for(int i = 0; i < participants.size(); i++){
            if(i == participants.size() - 1){
                pairs.put(participants.get(i) , participants.get(0));
            }else{
                pairs.put(participants.get(i), participants.get(i+1));
            }

        }

        printPairs(pairs);
        return pairs;
    }

    private void swap(ArrayList<SecretSantaUser> receivers, int x, int y){
        SecretSantaUser temp = receivers.get(x);
        receivers.set(x , receivers.get(y));
        receivers.set(y, temp);
    }

    private void printPairs(HashMap<SecretSantaUser, SecretSantaUser> pairs){
        for(Map.Entry<SecretSantaUser, SecretSantaUser> entry : pairs.entrySet()){
            System.out.println(entry.getKey().getFirstName() + " -> " + entry.getValue().getFirstName());
        }
    }
}
